package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.resource.api.RemoteFileService;
import com.ruoyi.resource.api.domain.SysFile;
import com.ruoyi.system.api.RemoteLianLianProductService;
import com.ruoyi.zlyyh.domain.CategorySupplier;
import com.ruoyi.zlyyh.domain.LianlianCity;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.ProductInfo;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.param.LianLianParam;
import com.ruoyi.zlyyh.utils.LianLianUtils;
import com.ruoyi.zlyyhadmin.domain.vo.LianLianProductItem;
import com.ruoyi.zlyyhadmin.domain.vo.LianLianProductVo;
import com.ruoyi.zlyyhadmin.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
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
    @DubboReference
    private RemoteFileService remoteFileService;
    private final BigDecimal HUNDRED = new BigDecimal(100);

    /**
     * 获取联联产品数据
     *
     * @param platformKey
     */
    @Async
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
        TimeInterval timer = DateUtil.timer();
        int cityPageNum = 0;
        int pageSize = 20;

        String productList = basePath + queryProductList;
        String productDetail = basePath + queryProductByCondition;
        String productDetailHtml = basePath + detailHtml;
        while (true) {
            try {
                // 查询本地城市列表
                Page<LianlianCity> cityCodes = lianlianCityService.selectLlianCityCodeList("0", cityPageNum, pageSize);
                if (!cityCodes.getRecords().isEmpty()) {
                    for (LianlianCity city : cityCodes.getRecords()) {
                        int pageNum = 1;
                        while (true) {
                            //分页 请求产品列表
                            JSONObject decryptedData = LianLianUtils.getProductList(channelId, secret, productList, city.getCityCode(), pageNum);
                            if (decryptedData == null) {
                                log.info("获取联联产品接口失败");
                                break;
                            }
                            JSONArray list = decryptedData.getJSONArray("list");
                            if (list == null) {
                                log.info("获取联联产品接口数据列表失败");
                                break;
                            }
                            for (int i = 0; i < list.size(); i++) {
                                try {
                                    this.saveLianLianProduct(channelId, secret, productDetailHtml, productDetail, platformKey, JSONObject.parseObject(list.get(i).toString()));
                                } catch (Exception e) {
                                    log.error("联联产品，保存单个产品异常，", e);
                                }
                            }
                            BigDecimal total = decryptedData.getBigDecimal("total");
                            if (total == null) {
                                break;
                            }
                            //还有下一页，则继续循环请求，pageNum + 1
                            //没有则跳出循环
                            if (BigDecimal.valueOf(pageNum).multiply(BigDecimal.TEN).compareTo(total) > 0) {
                                break;
                            }
                            pageNum += 1;
                        }
                    }
                }
                if (cityCodes.getRecords().size() < pageSize) break;
                cityPageNum += pageSize;
            } catch (Exception e) {
                log.error("联联商品更新定时任务异常:", e);
            }
        }
        log.info("结束执行联联产品列表定时任务,耗时：{}分钟", timer.intervalMinute());
    }

    /**
     * 获取联联商品
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

            JSONArray jsonArray = JSONArray.parseArray(lianProductVo.getItemList());
            List<LianLianProductItem> productItems = jsonArray.toJavaList(LianLianProductItem.class);
            for (LianLianProductItem item : productItems) {
                // 处理价格单位
                item.setChannelPrice(item.getChannelPrice().divide(HUNDRED, 2, RoundingMode.HALF_UP)); // 渠道结算价
                item.setSalePrice(item.getSalePrice().divide(HUNDRED, 2, RoundingMode.HALF_UP)); // 售价
                item.setOriginPrice(item.getOriginPrice().divide(HUNDRED, 2, RoundingMode.HALF_UP));// 原价
                // 根据第三方产品编号查询产品是否存在，如果存在则更新，不存在新增
                Product product = productService.queryByExternalProductId(lianProductVo.getProductId().toString(), "14", platformKey);
                // 计算产品利润是否高于0.5元
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
                    product.setProductAffiliation("0");
                    product.setProductType("14");
                    product.setPickupMethod("1");
                    product.setShowOriginalAmount("1");
                }

                //主体信息

                if (lianProductVo.getOnlyName().equals(item.getSubTitle())) {
                    product.setProductName(lianProductVo.getOnlyName());//产品名称
                } else {
                    product.setProductName(lianProductVo.getOnlyName() + " " + item.getSubTitle());//产品名称
                }
                if (lianProductVo.getItemStock().equals(0)) {
                    product.setTotalCount(Long.valueOf(lianProductVo.getStockAmount()));//库存
                } else if (lianProductVo.getItemStock().equals(1)) {
                    product.setTotalCount(item.getStock());//库存
                }
                product.setProductImg(lianProductVo.getFaceImg());//产品图片
                product.setProductSubhead(lianProductVo.getTitle());//产品副标题
                product.setOriginalAmount(item.getOriginPrice());//产品原价
                product.setSellAmount(item.getSalePrice());//产品售价
                //product.setVipAmount(item.getChannelPrice());
                ProductInfo productInfo = new ProductInfo();
                productInfo.setTitle(lianProductVo.getTitle());
                productInfo.setMainPicture(lianProductVo.getFaceImg());
                productInfo.setActivityPriceCent(item.getSalePrice());
                productInfo.setOriginalPriceCent(item.getOriginPrice());
                productInfo.setStock(product.getTotalCount());
                productInfo.setDiscount(item.getSalePrice().divide(item.getOriginPrice(), 2, RoundingMode.HALF_UP).toString());
                // 使用时间
                //productInfo.setTicketTimeRule(lianProductVo.getValidBeginDate() + "-" + lianProductVo.getValidEndDate());
                productInfo.setBrandName(lianProductVo.getOnlyName());
                //productInfo.setItemBuyNote(spxz);
                productInfo.setItemContentGroup(JSONObject.toJSONString(productItems).replace("&", ""));
                // 将原先海报地址改为套餐图片
                //productInfo.setItemContentImage(lianProductVo.getFaceImg());
                productInfo.setBuyLimit(Long.valueOf(lianProductVo.getSingleMax()));
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
                // 查询产品图文详情(文案)
                String htmlContent = null;
                String spxz = null;
                JSONObject htmlObject = LianLianUtils.getProductDetail(channelId, secret, productDetailHtml, lianProductVo.getProductId().toString());
                if (htmlObject != null) {
                    htmlContent = htmlObject.getString("htmlContent");
                    if (StringUtils.isNotEmpty(htmlContent)) { // 获取商品须知
                        //Document doc = Jsoup.parse(htmlContent);
                        //Elements elementsByClass = doc.getElementsByClass("body-center body-center-border-bottom");
                        //Element body = doc.body();
                        //body.html(elementsByClass.html());
                        //// 获取联联商品购买须知内容
                        //spxz = doc.outerHtml();
                        String fileName = product.getProductId() + ".html";
                        InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());
                        byte[] bytes = IoUtil.readBytes(inputStream);
                        SysFile upload = remoteFileService.upload(fileName, fileName, "text/html;charset:utf-8", bytes);
                        spxz = upload.getUrl();
                    }
                }
                product.setDescription(spxz);
                //购物车内仅添加一个商品
                product.setLineUpperLimit(1L);
                product.setSupplier("1717473385180033026");
                // 商品扩展信息
                //commodity.setCommodityInfo(commodityInfo);
                //不存在则新增
                if (product.getProductId() == null) {
                    product.setProductId(IdUtil.getSnowflakeNextId());
                    product.setProductType("14");
                    // 联联产品id
                    product.setExternalProductId(lianProductVo.getProductId().toString());
                    // 联联套餐id
                    productInfo.setItemId(item.getItemId().toString());
                    productInfo.setItemPrice(item.getChannelPrice());
                    product.setStatus("0");//上架
                    if (lianProductVo.getSingleMin() > 1) {//如果单次最小购买量大于1
                        //将状态改成下架
                        product.setStatus("1");
                    }
                    ProductBo productBo = BeanUtil.toBean(product, ProductBo.class);
                    productService.insertByBo(productBo);
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
                if (ObjectUtil.isNotEmpty(product.getProductId())) {
                    productService.setProductCity(product.getProductId());
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
            // 删除原有门店
            shopService.deleteByCommercialTenantId(shop.getId()); // 此方法下次使用之后，必须删除
            shopService.deleteBySupplierShopId(shop.getId());

            // 新增门店
            ShopBo shopBo = new ShopBo();
            shopBo.setShopId(IdUtil.getSnowflakeNextId());
            shopBo.setSupplierShopId(shop.getId().toString());
            shopBo.setShopName(shop.getName());
            shopBo.setShopTel(StringUtils.isEmpty(checkCellphone(shop.getPhoneNumber())) ? checkTelephone(shop.getPhoneNumber()) : checkCellphone(shop.getPhoneNumber()));
            shopBo.setFormattedAddress(shop.getAddress());
            shopBo.setCitycode(shop.getCityCode());
            shopBo.setAddress(shop.getAddress());
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
