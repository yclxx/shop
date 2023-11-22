package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import com.ruoyi.zlyyhmobile.service.IVerifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IVerifierServiceImpl implements IVerifierService {
    private final VerifierMapper baseMapper;
    private final VerifierShopMapper verifierShopMapper;
    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final ProductInfoMapper productInfoMapper;
    private final ShopProductMapper shopProductMapper;
    private final IProductService productService;
    private final IShopService shopService;

    @Cacheable(cacheNames = CacheNames.M_VERIFIER, key = "#bo.getId()")
    @Override
    public VerifierVo info(VerifierBo bo) {
        VerifierVo verifierVo = baseMapper.selectVoOne(new LambdaQueryWrapper<Verifier>().eq(Verifier::getId, bo.getId()).eq(Verifier::getPlatformKey, bo.getPlatformKey()));
        // 手机号脱敏
        if (StringUtils.isNotEmpty(verifierVo.getMobile())) {
            verifierVo.setMobile(DesensitizedUtil.mobilePhone(verifierVo.getMobile()));
        }
        return verifierVo;
    }

    @Override
    public List<ShopVo> queryShopList(VerifierBo bo) {
        LambdaQueryWrapper<Shop> lqw = Wrappers.lambdaQuery();
        lqw.eq(Shop::getStatus, "0");
        lqw.last("AND shop_id IN(SELECT shop_id FROM t_verifier_shop WHERE verifier_id = " + bo.getId() + ")");
        List<ShopVo> result = shopMapper.selectVoList(lqw);
        return result;
    }

    @Override
    public List<VerifierVo> verifierList(Long shopId) {
        LambdaQueryWrapper<Verifier> lqw = Wrappers.lambdaQuery();
        //lqw.eq(Verifier::getPlatformKey, bo.getPlatformKey());
        lqw.last("AND id IN(SELECT shop_id FROM t_verifier_shop WHERE shop_id = " + shopId + ")");
        List<VerifierVo> result = baseMapper.selectVoList(lqw);
        return result;
    }

    @Override
    public TableDataInfo<ShopVo> queryShopPageList(VerifierBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Shop> lqw = Wrappers.lambdaQuery();
        lqw.eq(Shop::getStatus, "0");
        lqw.last("AND shop_id IN(SELECT shop_id FROM t_verifier_shop WHERE verifier_id = " + bo.getId() + ") ORDER BY create_time DESC");
        Page<ShopVo> result = shopMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public Boolean updateShopById(ShopBo bo) {
        return shopService.updateShopById(bo);
    }

    @Override
    public Boolean updateShopMerchantById(List<ShopMerchantBo> bos) {
        return shopService.updateShopMerchantById(bos);
    }

    @Override
    public TableDataInfo<Product> queryProductPageList(VerifierBo bo, PageQuery pageQuery) {
        // 查询核销人员门店信息
        List<ShopVo> shopVos = queryShopList(bo);
        if (ObjectUtil.isNotEmpty(shopVos)) {
            List<Long> shopIds = shopVos.stream().map(ShopVo::getShopId).collect(Collectors.toList());
            //LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
            Page<Product> result = productMapper.selectPageByShopId(pageQuery.build(), shopIds);
            return TableDataInfo.build(result);
        }
        return TableDataInfo.build();
    }

    /**
     * 修改商品状态
     */
    @Override
    public void productStatus(ProductBo bo) {
        if (ObjectUtil.isEmpty(bo)) throw new ServiceException("商品信息异常");
        Product product = productMapper.selectById(bo.getProductId());
        if (product.getStatus().equals("0")) {
            product.setStatus("1");
        } else if (product.getStatus().equals("1")) {
            product.setStatus("0");
        }
        productMapper.updateById(product);
    }

    @Override
    public TableDataInfo<VerifierVo> getVerifierListByShop(CodeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Verifier> lqw = Wrappers.lambdaQuery();
        lqw.ne(Verifier::getId, bo.getVerifierId());
        lqw.last("AND id IN(SELECT verifier_id FROM t_verifier_shop WHERE shop_id = " + bo.getShopId() + ")");
        Page<VerifierVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public Boolean cancelVerifier(CodeBo bo) {
        LambdaQueryWrapper<VerifierShop> lqw = Wrappers.lambdaQuery();
        lqw.eq(VerifierShop::getVerifierId, bo.getVerifierId());
        lqw.eq(VerifierShop::getShopId, bo.getShopId());
        return verifierShopMapper.delete(lqw) > 0;
    }

    @Override
    public Boolean addVerifier(CodeBo bo) {
        if (StringUtils.isEmpty(bo.getVerifierMobile())) return false;
        // 核销人员查询
        LambdaQueryWrapper<Verifier> lqw = Wrappers.lambdaQuery();
        lqw.eq(Verifier::getMobile, bo.getVerifierMobile());
        VerifierVo verifierVo = baseMapper.selectVoOne(lqw);
        // 判断核销人员是否存在
        if (ObjectUtil.isNotEmpty(verifierVo)) {
            //// 判断核销人员是否与添加人为上下级关系
            //if (!verifierVo.getSuperiorId().equals(LoginHelper.getUserId())) {
            //    return false;
            //}
            // 绑定核销人员与门店
            LambdaQueryWrapper<VerifierShop> lqw2 = Wrappers.lambdaQuery();
            lqw2.eq(VerifierShop::getVerifierId, verifierVo.getId());
            lqw2.eq(VerifierShop::getShopId, bo.getShopId());
            // 判断是否已经是核销人员
            if (verifierShopMapper.selectCount(lqw2) > 0) {
                return true;
            }
            return handleVerifierShop(verifierVo.getId(), bo.getShopId());
        } else {
            // 新增核销人员
            Verifier verifier = new Verifier();
            verifier.setId(IdUtil.getSnowflakeNextId());
            verifier.setPlatformKey(ZlyyhUtils.getPlatformId());
            verifier.setMobile(bo.getVerifierMobile());
            verifier.setVerifierType("verifier");
            verifier.setStatus("0");
            //verifier.setSuperiorId(LoginHelper.getUserId());
            if (baseMapper.insert(verifier) <= 0) return false;
            // 核销人员绑定门店
            return handleVerifierShop(verifier.getId(), bo.getShopId());
        }
    }

    private Boolean handleVerifierShop(Long verifierId, Long shopId) {
        LambdaQueryWrapper<VerifierShop> lqw = Wrappers.lambdaQuery();
        lqw.eq(VerifierShop::getVerifierId, verifierId);
        lqw.eq(VerifierShop::getShopId, shopId);
        if (verifierShopMapper.selectCount(lqw) > 0) {
            return false;
        } else {
            VerifierShop verifierShop = new VerifierShop();
            verifierShop.setVerifierId(verifierId);
            verifierShop.setShopId(shopId);
            return verifierShopMapper.insert(verifierShop) > 0;
        }
    }

    @Override
    public Boolean updateProductById(ProductBo bo) {
        return productService.updateProductById(bo);
    }

    @Override
    public Boolean insertProduct(ProductBo bo) {
        Shop shop = shopMapper.selectById(bo.getShopId());
        if (shop != null) {
            Product product = BeanCopyUtils.copy(bo, Product.class);
            if (product != null) {
                product.setStatus("0");
                product.setProductAffiliation("0");
                product.setPickupMethod("1");
                product.setPlatformKey(shop.getPlatformKey());
                product.setProductId(IdUtil.getSnowflakeNextId());
                ProductInfo productInfo = new ProductInfo();
                productInfo.setProductId(product.getProductId());
                productInfo.setItemId(product.getProductId().toString());
                productInfo.setDiscount("0.00");
                productInfo.setStock(bo.getTotalCount());
                productInfo.setItemPrice(bo.getProductInfo().getItemPrice());
                productInfoMapper.insert(productInfo);
                int insert = productMapper.insert(product);
                if (insert > 0) {
                    ShopProduct shopProduct = new ShopProduct();
                    shopProduct.setId(IdUtil.getSnowflakeNextId());
                    shopProduct.setProductId(product.getProductId());
                    shopProduct.setShopId(shop.getShopId());
                    shopProduct.setSort(99L);
                    shopProductMapper.insert(shopProduct);
                    return true;
                }
            }
        }
        return false;
    }
}
