package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.RemoteProductService;
import com.ruoyi.zlyyh.domain.ProductUnionPay;
import com.ruoyi.zlyyh.domain.bo.ProductBo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.enumd.UnionPay.UnionPayParams;
import com.ruoyi.zlyyh.mapper.ProductUnionPayMapper;
import com.ruoyi.zlyyh.properties.utils.YsfDistributionPropertiesUtils;
import com.ruoyi.zlyyh.utils.sdk.UnionPayDistributionUtil;
import com.ruoyi.zlyyhadmin.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;

/**
 * 订单服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteProductServiceImpl implements RemoteProductService {
    @Autowired
    private IProductService productService;
    private final ProductUnionPayMapper productUnionPayMapper;

    @Async
    @Override
    public void selectUnionPayProductList(Long platformKey) {
        if (null == platformKey) {
            return;
        }
        log.info("开始执行直销商品查询");
        // 页码
        int curPage = 1;
        String pagingIndex = null;
        while (true) {
            String s = UnionPayDistributionUtil.selectProductList(curPage, pagingIndex, platformKey, YsfDistributionPropertiesUtils.getJDAppId(platformKey), YsfDistributionPropertiesUtils.getCertPathJD(platformKey));
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (!jsonObject.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                log.info("直销商品返回提示：{}", jsonObject.get("msg"));
                break;
            }
            pagingIndex = jsonObject.getString("pagingIndex");
            JSONArray chnlProdLst = jsonObject.getJSONArray("chnlProdLst");
            for (int i = 0; i < chnlProdLst.size(); i++) {
                JSONObject productObject = chnlProdLst.getJSONObject(i);
                // 第三方数据信息
                String chnlProdId = productObject.getString("chnlProdId");
                // 获取商品详情信息
                String detail = UnionPayDistributionUtil.selectProductDetails(chnlProdId, platformKey, YsfDistributionPropertiesUtils.getJDAppId(platformKey), YsfDistributionPropertiesUtils.getCertPathJD(platformKey));
                JSONObject productDetail = JSONObject.parseObject(detail);
                this.productHandler(platformKey, productObject, productDetail);
            }
            curPage += 1;
            if (jsonObject.getString("hasNext").equals("0")) {
                break;
            }
        }
        log.info("结束执行直销商品查询");

        log.info("开始执行代销销商品查询");
        // 页码
        curPage = 1;
        pagingIndex = null;
        while (true) {
            String s = UnionPayDistributionUtil.selectProductList(curPage, pagingIndex, platformKey, YsfDistributionPropertiesUtils.getJCAppId(platformKey), YsfDistributionPropertiesUtils.getCertPathJC(platformKey));
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (!jsonObject.getString("code").equals(UnionPayParams.CodeSuccess.getStr())) {
                log.info("直销商品返回提示：{}", jsonObject.get("msg"));
                break;
            }
            pagingIndex = jsonObject.getString("pagingIndex");
            JSONArray chnlProdLst = jsonObject.getJSONArray("chnlProdLst");
            for (int i = 0; i < chnlProdLst.size(); i++) {
                JSONObject productObject = chnlProdLst.getJSONObject(i);
                // 第三方数据信息
                String chnlProdId = productObject.getString("chnlProdId");
                // 获取商品详情信息
                String detail = UnionPayDistributionUtil.selectProductDetails(chnlProdId, platformKey, YsfDistributionPropertiesUtils.getJCAppId(platformKey), YsfDistributionPropertiesUtils.getCertPathJC(platformKey));
                JSONObject productDetail = JSONObject.parseObject(detail);
                this.productHandler(platformKey, productObject, productDetail);
            }
            curPage += 1;
            if (jsonObject.getString("hasNext").equals("0")) {
                break;
            }
        }
        log.info("结束执行代销商品查询");
    }

    /**
     * @param platformKey   平台标识
     * @param productObject 商品对象
     * @param productDetail 商品详情
     */
    public void productHandler(Long platformKey, JSONObject productObject, JSONObject productDetail) {
        // 商品对象必填字段
        String chnlProdId = productObject.getString("chnlProdId"); // 渠道商品代码
        String prodNm = productObject.getString("prodNm"); // 商品名称
        String prodPrice = productObject.getString("prodPrice"); // 商品价格 单位分
        String stkMd = productObject.getString("stkMd"); // 商品库存类型 0-数量;1-状态;
        String salesStartTm = productObject.getString("salesStartTm"); // 销售起始时间
        String salesEndTm = productObject.getString("salesEndTm"); // 销售截止时间
        // 商品对象条件出现字段
        String prodValue = productObject.getString("prodValue"); // 商品面值
        String cstkAvlNum = productObject.getString("cstkAvlNum");// 库存数量
        String thnUrl = productObject.getString("thnUrl"); //缩略图链接
        String brandPicUrl = productObject.getString("brandPicUrl"); // 品牌LOGO
        String brandNm = productObject.getString("brandNm"); //品牌

        ProductUnionPay productUnionPay = JSONObject.toJavaObject(productDetail, ProductUnionPay.class);
        productUnionPay.setExternalProductId(chnlProdId);

        // 商品详情条件字段
        String prodSt = productDetail.getString("prodSt");// 商品状态
        String coopMd = productDetail.getString("coopMd");// 销售模式
        //String prodTp = productDetail.getString("prodTp");// 卡券类型 0-仅券码;1-券码+券密;2-短链;3-直充
        String singleUsrDayNumLimit = productDetail.getString("singleUsrDayNumLimit");//单个用户每日发券限制数量
        String singleUsrMonthNumLimit = productDetail.getString("singleUsrMonthNumLimit");// 单个用户每月发券限制数量

        Date startTm = DateUtils.parseDate(salesStartTm);
        Date endTm = DateUtils.parseDate(salesEndTm);

        BigDecimal bigDecimal = BigDecimal.valueOf(100);
        ProductVo productVo = productService.queryByExternalProductId(chnlProdId);
        ProductBo productBo;
        if (null == productVo) {
            productBo = new ProductBo();
        } else {
            productBo = BeanCopyUtils.copy(productVo, ProductBo.class);
        }
        productBo.setExternalProductId(chnlProdId);
        productBo.setProductName(prodNm);
        if (StringUtils.isNotEmpty(prodValue)) {
            productBo.setOriginalAmount(new BigDecimal(prodValue).divide(bigDecimal));
        }
        productBo.setSellAmount(new BigDecimal(prodPrice).divide(bigDecimal));
        if (stkMd.equals("0")) {
            productBo.setTotalCount(Long.valueOf(cstkAvlNum));
        } else {
            productBo.setTotalCount(-1L);
        }
        productBo.setProviderLogo(brandPicUrl);
        productBo.setProviderName(brandNm);
        productBo.setProductImg(thnUrl);
        productBo.setShowStartDate(startTm);
        productBo.setShowEndDate(endTm);
        productBo.setSellStartDate(startTm);
        productBo.setSellEndDate(endTm);
        productBo.setAssignDate("0");
        productBo.setShowCity("ALL");
        productBo.setPlatformKey(platformKey);
        // 判断订单状态
        if (StringUtils.isNotEmpty(productBo.getStatus())) {
            if (productBo.getStatus().equals("0")) {
                productBo.setStatus(this.equalsStatus(prodSt));
            }
        } else {
            productBo.setStatus(this.equalsStatus(prodSt));
        }
        productBo.setProductAffiliation("0");
        productBo.setToType("0");

        // 购买须知
        if (StringUtils.isNotEmpty(productUnionPay.getDetailTp())) {
            if (productUnionPay.getDetailTp().equals("0")) {
                productUnionPay.setCusIstr(base64decode(productUnionPay.getCusIstr()));
                productBo.setDescription(productUnionPay.getCusIstr());
            }
        }

        // 判断销售模式，设置产品类型
        if (coopMd.equals("3")) {
            // 直销
            productBo.setProductType("12");
        } else {
            // 代销
            productBo.setProductType("11");
        }
        if (StringUtils.isNotEmpty(singleUsrMonthNumLimit)) {
            productBo.setMonthCount(Long.valueOf(singleUsrMonthNumLimit));
        }
        if (StringUtils.isNotEmpty(singleUsrDayNumLimit)) {
            productBo.setDayCount(Long.valueOf(singleUsrDayNumLimit));
        }

        if (productBo.getProductId() != null) {
            productService.updateByBo(productBo);
        } else {
            productBo.setProductId(IdUtil.getSnowflakeNextId());
            productBo.setPickupMethod("1");
            productBo.setSearchStatus("1");
            productBo.setShowIndex("0");
            productService.insertByBo(productBo);
        }
        productUnionPay.setProductId(productBo.getProductId());
        productUnionPayMapper.insertOrUpdate(productUnionPay);
    }

    /**
     * 解密商品说明
     */
    private String base64decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    /**
     * 判断商品状态
     * @param prodSt
     * @return
     */
    private String equalsStatus(String prodSt) {
        if (prodSt.equals("1")) {
            return "0";
        } else {
            return "1";
        }
    }
}
