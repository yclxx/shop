package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteLianLianProductService;
import com.ruoyi.zlyyh.domain.CategorySupplier;
import com.ruoyi.zlyyh.domain.LianlianCity;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.ProductInfo;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.param.LianLianParam;
import com.ruoyi.zlyyh.utils.LianLianUtils;
import com.ruoyi.zlyyhadmin.domain.vo.LianLianProductItem;
import com.ruoyi.zlyyhadmin.domain.vo.LianLianProductVo;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 联联产品服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteLianLianProductServiceImpl implements RemoteLianLianProductService {
    // 云闪付参数配置表
    private final IYsfConfigService ysfConfigService;
    private final IProductService productService;
    private final ICategorySupplierService categorySupplierService;
    private final ICategoryProductService categoryProductService;
    private final IProductInfoService productInfoService;
    private final IShopService shopService;
    private final IShopProductService shopProductService;
    private final ILianlianCityService lianlianCityService;
    private final BigDecimal HUNDRED = new BigDecimal(100);

    /**
     * 获取联联产品数据
     * @param platformKey
     */
    @Override
    public void selectLianLianProductList(Long platformKey) {
        // 账户id
        String channelId = ysfConfigService.queryValueByKey(platformKey, "LianLian.channelId");
        // 密钥
        String secret = ysfConfigService.queryValueByKey(platformKey, "LianLian.secret");
        // 域名
        String basePath = ysfConfigService.queryValueByKey(platformKey, "LianLian.basePath");
        // 产品列表地址
        String queryProductList = ysfConfigService.queryValueByKey(platformKey, "LianLian.queryProductList");
        // 单个产品详情信息
        String queryProductByCondition = ysfConfigService.queryValueByKey(platformKey, "LianLian.queryProductByCondition");
        // 产品图文详情
        String detailHtml = ysfConfigService.queryValueByKey(platformKey, "LianLian.detailHtml");
        log.info("开始执行联联产品列表定时任务.");
        int cityPageNum = 0;
        try {
            String productList = basePath + queryProductList;
            String productDetail = basePath + queryProductByCondition;
            String productDetailHtml = basePath + detailHtml;
            while (true) {
                // 查询本地城市列表
                int PAGESIZE = 20;
                Page<LianlianCity> cityCodes = lianlianCityService.selectLlianCityCodeList("0", cityPageNum, PAGESIZE);
                if (!cityCodes.getRecords().isEmpty()) {
                    for (LianlianCity city : cityCodes.getRecords()) {
                        int pageNum = 1;
                        while (true) {
                            //分页 请求产品列表
                            JSONObject decryptedData = LianLianUtils.getProductList(channelId, secret, productList, city.getCityCode(), pageNum);
                            if (decryptedData == null) {
                                //Thread.sleep(1000);
                                log.info("获取联联产品接口失败");
                                break;
                            }
                            JSONArray list = decryptedData.getJSONArray("list");
                            if (list == null) {
                                log.info("获取联联产品接口数据列表失败");
                                break;
                            }
                            for (int i = 0; i < list.size(); i++) {
                                this.saveLianLianProduct(channelId, secret, productDetailHtml, productDetail, platformKey, JSONObject.parseObject(list.get(i).toString()));
                            }
                            BigDecimal total = decryptedData.getBigDecimal("total");
                            if (total == null) {
                                break;
                            }
                            //还有下一页，则继续循环请求，pageNum + 1
                            //没有则跳出循环
                            if (BigDecimal.valueOf(pageNum).multiply(BigDecimal.TEN).compareTo(total) > 0) {
                                break;
                            } else {
                                TimeUnit.SECONDS.sleep(1);
                            }
                            pageNum += 1;
                        }
                    }
                }
                if (cityCodes.getRecords().size() < PAGESIZE) break;
                cityPageNum += PAGESIZE;
            }
        } catch (Exception e) {
            log.info("联联商品更新定时任务异常:{}", e.getMessage());
        }
        log.info("结束执行联联产品列表定时任务.");
    }

    public static void main(String[] args) {
        //Integer total = 64;
        //BigDecimal pageNumDecimal = BigDecimal.valueOf(7);
        //System.out.println(BigDecimal.valueOf(total).divide(pageNumDecimal, RoundingMode.DOWN));
        System.out.println();
    }

    /**
     * 插入商品
     */
    void saveLianLianProduct(String channelId, String secret, String productDetailHtml, String productDetail, Long platformKey, JSONObject jsonObject) {
        LianLianProductVo lianProductVo = jsonObject.toJavaObject(LianLianProductVo.class);
        String productKey = "productLianLian:";
        Object cacheObject = RedisUtils.getCacheObject(productKey + lianProductVo.getProductId());
        // 若redis缓存为空,则设置新缓存并处理数据,若有则直接跳过,执行下一个数据
        if (cacheObject == null) {
            RedisUtils.setCacheObject(productKey + lianProductVo.getProductId(), 1, Duration.ofHours(5));
            CategorySupplier categorySupplier = null;
            //获取联联商品分类
            if (StringUtils.isNotEmpty(lianProductVo.getCategoryPath())) {
                //查询分类表
                categorySupplier = categorySupplierService.queryBySupplierId("14", lianProductVo.getCategoryPath());
                if (ObjectUtil.isNull(categorySupplier)) {
                    categorySupplier = new CategorySupplier();
                    categorySupplier.setSupplierId("14");
                    categorySupplier.setSupplierName("联联产品");
                    categorySupplier.setFullName(lianProductVo.getCategoryPath());
                    categorySupplierService.insert(categorySupplier);
                }
            }

            //得到产品详情（图文详情）
            String htmlContent = null;
            String spxz = null;
            JSONObject htmlObject = LianLianUtils.getProductDetail(channelId, secret, productDetailHtml, lianProductVo.getProductId().toString());
            if (htmlObject != null) {
                htmlContent = htmlObject.getString("htmlContent");
                if (StringUtils.isNotEmpty(htmlContent)) { // 获取商品须知
                    Document doc = Jsoup.parse(htmlContent);
                    Elements elementsByClass = doc.getElementsByClass("body-center body-center-border-bottom");
                    Element body = doc.body();
                    body.html(elementsByClass.html());
                    // 获取联联商品购买须知内容
                    spxz = doc.outerHtml();
                }
            }


            JSONArray jsonArray = JSONArray.parseArray(lianProductVo.getItemList());
            List<LianLianProductItem> productItems = jsonArray.toJavaList(LianLianProductItem.class);
            //处理一下 productItems 中的价格，原来价格是分，处理为元
            //拿到套餐中价格最低的套餐
            //LianLianProductItem item = productItems.stream().min(Comparator.comparing(LianLianProductItem::getSalePrice)).get();
            for (LianLianProductItem item : productItems) {
                item.setChannelPrice(item.getChannelPrice().divide(HUNDRED, 2, RoundingMode.HALF_UP));
                item.setSalePrice(item.getSalePrice().divide(HUNDRED, 2, RoundingMode.HALF_UP));
                item.setOriginPrice(item.getOriginPrice().divide(HUNDRED, 2, RoundingMode.HALF_UP));
                // 根据第三方产品编号查询产品是否存在，如果存在则更新，不存在新增
                Product product = productService.queryByExternalProductId(lianProductVo.getProductId().toString(), "14", platformKey);
                //这里计算利润是否高于0.5元
                if (item.getSalePrice().subtract(item.getChannelPrice()).compareTo(new BigDecimal("0.5")) < 0) {
                    if (ObjectUtil.isNotNull(product)) {
                        product.setStatus("0");
                        ProductBo productBo = BeanUtil.toBean(product, ProductBo.class);
                        productService.updateByBo(productBo);
                    }
                    return;
                }
                if (product == null) {
                    product = new Product();
                }

                //主体信息
                product.setPlatformKey(platformKey);
                product.setProductAffiliation("0");
                product.setProductType("14");
                product.setPickupMethod("1");
                product.setProductName(lianProductVo.getOnlyName() + " " + item.getSubTitle());//产品名称
                product.setProductImg(lianProductVo.getFaceImg());//产品图片
                product.setProductSubhead(lianProductVo.getTitle());//产品副标题
                product.setShowOriginalAmount("1");
                product.setOriginalAmount(item.getOriginPrice());//产品原价
                product.setSellAmount(item.getSalePrice());//产品售价
                if (lianProductVo.getItemStock().equals(0)) {
                    product.setTotalCount(Long.valueOf(lianProductVo.getStockAmount()));//库存
                } else if (lianProductVo.getItemStock().equals(1)) {
                    product.setTotalCount(item.getStock());//库存
                }
                product.setDescription(spxz);
                product.setVipAmount(item.getChannelPrice());
                ProductInfo productInfo = new ProductInfo();
                //productInfo.setProductId(product.getProductId());
                productInfo.setTitle(lianProductVo.getTitle());
                productInfo.setMainPicture(lianProductVo.getFaceImg());
                productInfo.setActivityPriceCent(item.getSalePrice());
                productInfo.setOriginalPriceCent(item.getOriginPrice());
                productInfo.setStock(product.getTotalCount());
                productInfo.setDiscount(item.getSalePrice().divide(item.getOriginPrice(), 2, RoundingMode.HALF_UP).toString());
                //productInfo.setFxPrice(item.getChannelPrice());//结算价
                // 使用时间
                productInfo.setTicketTimeRule(lianProductVo.getValidBeginDate() + "-" + lianProductVo.getValidEndDate());
                productInfo.setBrandName(lianProductVo.getOnlyName());
                //productInfo.setRemark(htmlContent);
                productInfo.setItemBuyNote(spxz);
                productInfo.setItemContentGroup(JSONObject.toJSONString(productItems).replace("&", ""));
                // 将原先海报地址改为套餐图片
                productInfo.setItemContentImage(lianProductVo.getFaceImg());
                productInfo.setBuyLimit(Long.valueOf(lianProductVo.getSingleMax()));
                //productInfo.setSource("营商宝");
                //productInfo.setBookingShowAddress(lianProductVo.getBookingShowAddress()); 是否需要填写配送地址
                //productInfo.setOrderShowIdCard(lianProductVo.getOrderShowIdCard()); 是否需要身份证
                //productInfo.setOrderShowDate(lianProductVo.getOrderShowDate()); 是否需要填写使用日期
                productInfo.setReserveDesc(lianProductVo.getAttention()); // 订单注意事项配置补充说明
                //String appointMent = lianProductVo.getBookingType().equals("0") ? "1" : lianProductVo.getBookingType().equals("1") ? "3" : "2";
                //productInfo.setAppointMent(appointMent);
                // 如果需要填身份证，日期，配送地址 设置产品为下架状态
                if (StringUtils.isNotEmpty(lianProductVo.getBeginTime())) {
                    product.setShowStartDate(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, lianProductVo.getBeginTime()));
                    productInfo.setSaleStartTime(lianProductVo.getBeginTime());
                }
                if (StringUtils.isNotEmpty(lianProductVo.getEndTime())) {
                    product.setShowEndDate(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, lianProductVo.getEndTime()));
                    productInfo.setSaleEndTime(lianProductVo.getEndTime());
                }
                if (!"0".equals(lianProductVo.getBookingShowAddress()) || !"0".equals(lianProductVo.getOrderShowDate()) || !"0".equals(lianProductVo.getOrderShowIdCard())) {
                    //将状态改成下架
                    product.setStatus("1");
                }
                // 商品扩展信息
                //commodity.setCommodityInfo(commodityInfo);
                //不存在则新增
                if (product.getProductId() == null) {
                    product.setProductId(IdUtil.getSnowflakeNextId());
                    product.setProductType("14");
                    product.setExternalProductId(lianProductVo.getProductId().toString());
                    productInfo.setItemId(item.getItemId().toString());
                    product.setStatus("0");//上架
                    if (lianProductVo.getSingleMin() > 1) {//如果单次最小购买量大于1
                        //将状态改成下架
                        product.setStatus("1");
                    }
                    ProductBo productBo = BeanUtil.toBean(product, ProductBo.class);
                    productService.insert(productBo);
                    productInfo.setProductId(productBo.getProductId());
                    ProductInfoBo productInfoBo = BeanUtil.toBean(productInfo, ProductInfoBo.class);
                    productInfoService.insertByBo(productInfoBo);
                } else {
                    ProductBo productBo = BeanUtil.toBean(product, ProductBo.class);
                    //存在则修改
                    productService.updateByBo(productBo);
                    ProductInfoBo productInfoBo = BeanUtil.toBean(productInfo, ProductInfoBo.class);
                    productInfoService.updateByBo(productInfoBo);
                }
                //插入商家
                int i = 0;
                JSONObject productInfos = LianLianUtils.getProductShop(channelId, secret, productDetail, lianProductVo.getProductId().toString());
                if (ObjectUtil.isNotNull(productInfos)) {
                    String shops = productInfos.getString("shopList");
                    if (StringUtils.isNotEmpty(shops)) {
                        i = this.saveShop(shops, product.getProductId(), platformKey);
                    }
                }
                if (categorySupplier != null && StringUtils.isNotEmpty(categorySupplier.getCategoryId())) {
                    if (ObjectUtil.isNotNull(categorySupplier) && ObjectUtil.isNotNull(categorySupplier.getCategoryId()) && i == 0 && Long.parseLong(lianProductVo.getValidEndDate()) >= Long.parseLong(lianProductVo.getEndTime())) {
                        String categoryId = categorySupplier.getCategoryId();
                        String[] categorySplit = categoryId.split(",");
                        for (int n = 0; n < categorySplit.length; n++) {
                            Long l = Long.valueOf(categorySplit[n]);
                            Long categoryProduct = categoryProductService.queryByCategoryAndProduct(l, product.getProductId());
                            if (ObjectUtil.isNull(categoryProduct) || categoryProduct == 0) {
                                CategoryProductBo categoryProductBo = new CategoryProductBo();
                                categoryProductBo.setProductId(productInfo.getProductId());
                                categoryProductBo.setCategoryId(l);
                                categoryProductService.insertByBo(categoryProductBo);
                            }
                        }
                    }
                }
            }
        }
    }

    private int saveShop(String shops, Long productId, Long platformKey) {
        int questionStore = 0;
        JSONArray jsonArray = JSONArray.parseArray(shops);
        List<LianLianParam.ShopList> shopsList = jsonArray.toJavaList(LianLianParam.ShopList.class);
        for (LianLianParam.ShopList shop : shopsList) {
            //判断店铺是否有电话或者省市
            String address = shop.getAddress();
            String s = StringUtils.isEmpty(checkCellphone(shop.getPhoneNumber())) ? checkTelephone(shop.getPhoneNumber()) : checkCellphone(shop.getPhoneNumber());
            if (StringUtils.isEmpty(shop.getPhoneNumber()) || StringUtils.isEmpty(address) || StringUtils.isEmpty(s)) {
                questionStore++;
            }
            // 无经纬度，跳过
            if (StringUtils.isEmpty(shop.getLongitude()) || StringUtils.isEmpty(shop.getLatitude()) || "0".equals(shop.getLongitude()) || "0".equals(shop.getLatitude())) {
                continue;
            }
            BigDecimal longitude = new BigDecimal(shop.getLongitude());
            BigDecimal latitude = new BigDecimal(shop.getLatitude());
            //先查询是不是已经存在了店铺
            List<ShopVo> shopVos = shopService.queryByCommercialTenantId(shop.getId(), platformKey, longitude, latitude);
            if (!shopVos.isEmpty()) { // 有店则跳过
                continue;
            }

            ShopBo shopBo = new ShopBo();
            shopBo.setShopId(IdUtil.getSnowflakeNextId());
            shopBo.setCommercialTenantId(shop.getId());
            shopBo.setShopName(shop.getName());
            shopBo.setShopTel(StringUtils.isEmpty(checkCellphone(shop.getPhoneNumber())) ? checkTelephone(shop.getPhoneNumber()) : checkCellphone(shop.getPhoneNumber()));
            shopBo.setFormattedAddress(shop.getAddress());
            shopBo.setPlatformKey(platformKey);
            shopBo.setCitycode(shop.getCityCode());
            shopBo.setLongitude(longitude);
            shopBo.setLatitude(latitude);
            shopService.insertByBo(shopBo);

            ShopProductBo shopProductBo = new ShopProductBo();
            shopProductBo.setProductId(productId);
            shopProductBo.setShopId(shopBo.getShopId());
            shopProductService.insertByBo(shopProductBo);
        }
        return questionStore;
    }

    public static String checkTelephone(String str) {
        str = str.replaceAll("\\s*|\t|\r|\n", "");
        str = str.replaceAll("\\<|\\>|\\(|\\)|\\（|\\）|\\[|\\]|\\【|\\】|\\{|\\}", "");
        // 将给定的正则表达式编译到模式中
        Pattern pattern = Pattern.compile("\\d{3,4}[-]\\d{7,8}|\\d{3}\\d{3,4}\\d{4}|\\d{3}[-]\\d{3,4}[-]\\d{4}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        if (matcher.find()) {
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }

    public static String checkCellphone(String str) {
        // 将给定的正则表达式编译到模式中,号段增加了，有13,14,15,16,17,18,19打头的
        Pattern pattern = Pattern.compile("((13[0-9])|(14[0-9])|(15([0-9]))|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}");
        // 创建匹配给定输入与此模式的匹配器。
        Matcher matcher = pattern.matcher(str);
        //查找字符串中是否有符合的子字符串
        if (matcher.find()) {
            //查找到符合的即输出
            return matcher.group();
        }
        return null;
    }
}
