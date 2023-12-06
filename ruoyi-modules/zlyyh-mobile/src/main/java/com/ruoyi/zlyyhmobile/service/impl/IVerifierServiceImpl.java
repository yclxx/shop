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
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.ShopAndMerchantBo;
import com.ruoyi.zlyyhmobile.service.IProductService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import com.ruoyi.zlyyhmobile.service.IVerifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IVerifierServiceImpl implements IVerifierService {
    private final VerifierMapper baseMapper;
    private final VerifierShopMapper verifierShopMapper;
    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final ProductInfoMapper productInfoMapper;
    private final ShopMerchantMapper shopMerchantMapper;
    private final ShopProductMapper shopProductMapper;
    private final CommercialTenantMapper commercialTenantMapper;
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
    public Boolean updateVerifier(VerifierBo bo) {
        Verifier verifier = BeanCopyUtils.copy(bo, Verifier.class);
        boolean b = baseMapper.updateById(verifier) > 0;
        if (b) {
            CacheUtils.evict(CacheNames.M_VERIFIER, verifier.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<ShopVo> queryShopList(VerifierBo bo) {
        LambdaQueryWrapper<Shop> lqw = Wrappers.lambdaQuery();
        lqw.inSql(Shop::getShopId, "SELECT shop_id FROM t_verifier_shop WHERE verifier_id = " + bo.getId());
        List<ShopVo> result = shopMapper.selectVoList(lqw);
        return result;
    }

    @Override
    public Map<String, Object> getShopId(Long shopId) {
        Shop shop = shopMapper.selectById(shopId);
        CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoById(shop.getCommercialTenantId());
        LambdaQueryWrapper<ShopMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShopMerchant::getShopId, shopId);
        lqw.eq(ShopMerchant::getMerchantType, "1");
        List<ShopMerchantVo> ysfMerchant = shopMerchantMapper.selectVoList(lqw);
        lqw.clear();
        lqw.eq(ShopMerchant::getShopId, shopId);
        lqw.eq(ShopMerchant::getMerchantType, "0");
        List<ShopMerchantVo> wxMerchant = shopMerchantMapper.selectVoList(lqw);
        lqw.eq(ShopMerchant::getShopId, shopId);
        lqw.eq(ShopMerchant::getMerchantType, "2");
        List<ShopMerchantVo> payMerchant = shopMerchantMapper.selectVoList(lqw);

        Map<String, Object> map = new HashMap<>();
        map.put("shop", shop);
        map.put("commercialTenant", commercialTenantVo);
        map.put("ysfMerchant", ysfMerchant);
        map.put("wxMerchant", wxMerchant);
        map.put("payMerchant", payMerchant);
        return map;
    }

    @Override
    public Boolean updateShopId(ShopAndMerchantBo bo) {
        int i = 0;
        if (ObjectUtil.isNotEmpty(bo.getCommercialTenant())) {
            CommercialTenant commercialTenant = commercialTenantMapper.selectById(bo.getCommercialTenant().getCommercialTenantId());
            // 修改商户的管理员手机号与原手机号不一样处理
            if (commercialTenant.getIsCache().equals("0") || (!bo.getCommercialTenant().getAdminMobile().equals(commercialTenant.getAdminMobile()))) {
                Verifier oldVerifier = baseMapper.selectByMobile(commercialTenant.getAdminMobile());
                Verifier newVerifier = baseMapper.selectByMobile(bo.getCommercialTenant().getAdminMobile());
                if (ObjectUtil.isNotEmpty(newVerifier)) {
                    if (ObjectUtil.isNotEmpty(oldVerifier)) {
                        VerifierShop verifierShop = verifierShopMapper.selectByShopIdAndVerifierId(bo.getShop().getShopId(), oldVerifier.getId());
                        if (ObjectUtil.isNotEmpty(verifierShop)) {
                            verifierShop.setVerifierId(newVerifier.getId());
                            verifierShopMapper.updateById(verifierShop);
                        } else {
                            verifierShop = new VerifierShop();
                            verifierShop.setId(IdUtil.getSnowflakeNextId());
                            verifierShop.setSort(99L);
                            verifierShop.setShopId(bo.getShop().getShopId());
                            verifierShop.setVerifierId(newVerifier.getId());
                            verifierShopMapper.insert(verifierShop);
                        }
                        newVerifier.setIsAdmin(true);
                        baseMapper.updateById(newVerifier);
                    } else {
                        VerifierShop verifierShop = new VerifierShop();
                        verifierShop.setId(IdUtil.getSnowflakeNextId());
                        verifierShop.setSort(99L);
                        verifierShop.setShopId(bo.getShop().getShopId());
                        verifierShop.setVerifierId(newVerifier.getId());
                        verifierShopMapper.insert(verifierShop);
                    }
                } else {
                    newVerifier = new Verifier();
                    newVerifier.setId(IdUtil.getSnowflakeNextId());
                    newVerifier.setUsername(bo.getCommercialTenant().getAdminMobile());
                    newVerifier.setMobile(bo.getCommercialTenant().getAdminMobile());
                    newVerifier.setStatus("0");
                    newVerifier.setReloadUser("0");
                    newVerifier.setIsVerifier(true);
                    newVerifier.setIsAdmin(true);
                    if (ObjectUtil.isNotEmpty(oldVerifier)) {
                        VerifierShop verifierShop = verifierShopMapper.selectByShopIdAndVerifierId(bo.getShop().getShopId(), oldVerifier.getId());
                        verifierShop.setVerifierId(newVerifier.getId());
                        verifierShopMapper.updateById(verifierShop);
                        baseMapper.insert(newVerifier);
                    } else {
                        VerifierShop verifierShop = new VerifierShop();
                        verifierShop.setId(IdUtil.getSnowflakeNextId());
                        verifierShop.setSort(99L);
                        verifierShop.setShopId(bo.getShop().getShopId());
                        verifierShop.setVerifierId(newVerifier.getId());
                        verifierShopMapper.insert(verifierShop);
                    }
                }
            }
            commercialTenant.setCommercialTenantName(bo.getCommercialTenant().getCommercialTenantName());
            commercialTenant.setCommercialTenantTitle(bo.getCommercialTenant().getCommercialTenantTitle());
            commercialTenant.setCommercialTenantImg(bo.getCommercialTenant().getCommercialTenantImg());
            commercialTenant.setActivityNature(bo.getCommercialTenant().getActivityNature());
            commercialTenant.setAdminMobile(bo.getCommercialTenant().getAdminMobile());
            commercialTenant.setIsCache("1");
            commercialTenantMapper.updateById(commercialTenant);
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getShop())) {
            Shop shop = BeanCopyUtils.copy(bo.getShop(), Shop.class);
            shopMapper.updateById(shop);
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getYsfMerchant())) {
            shopService.updateShopMerchantById(bo.getShop().getShopId(), bo.getYsfMerchant());
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getWxMerchant())) {
            shopService.updateShopMerchantById(bo.getShop().getShopId(), bo.getWxMerchant());
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getPayMerchant())) {
            shopService.updateShopMerchantById(bo.getShop().getShopId(), bo.getPayMerchant());
            i++;
        }
        return i > 0;
    }

    @Override
    public Boolean updateShop(ShopBo bo) {
        Shop shop = BeanCopyUtils.copy(bo, Shop.class);
        return shopMapper.updateById(shop) > 0;
    }

    @Override
    public Boolean insertShop(ShopAndMerchantBo bo) {
        int i = 0;
        Long shopId = IdUtil.getSnowflakeNextId();
        if (ObjectUtil.isNotEmpty(bo.getCommercialTenant())) {
            Verifier newVerifier = baseMapper.selectByMobile(bo.getCommercialTenant().getAdminMobile());
            if (ObjectUtil.isNotEmpty(newVerifier)) {
                newVerifier.setIsVerifier(true);
                newVerifier.setIsAdmin(true);
                baseMapper.updateById(newVerifier);
            } else {
                newVerifier.setId(IdUtil.getSnowflakeNextId());
                newVerifier.setUsername(bo.getCommercialTenant().getAdminMobile());
                newVerifier.setMobile(bo.getCommercialTenant().getAdminMobile());
                newVerifier.setStatus("0");
                newVerifier.setReloadUser("0");
                newVerifier.setIsVerifier(true);
                newVerifier.setIsAdmin(true);
                baseMapper.insert(newVerifier);
            }
            VerifierShop verifierShop = new VerifierShop();
            verifierShop.setId(IdUtil.getSnowflakeNextId());
            verifierShop.setSort(99L);
            verifierShop.setShopId(shopId);
            verifierShop.setVerifierId(newVerifier.getId());
            verifierShopMapper.insert(verifierShop);
            CommercialTenant commercialTenant = commercialTenantMapper.selectById(bo.getCommercialTenant().getCommercialTenantId());
            commercialTenant.setCommercialTenantName(bo.getCommercialTenant().getCommercialTenantName());
            commercialTenant.setCommercialTenantTitle(bo.getCommercialTenant().getCommercialTenantTitle());
            commercialTenant.setCommercialTenantImg(bo.getCommercialTenant().getCommercialTenantImg());
            commercialTenant.setActivityNature(bo.getCommercialTenant().getActivityNature());
            commercialTenant.setIsCache("1");
            commercialTenantMapper.updateById(commercialTenant);
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getShop())) {
            Shop shop = BeanCopyUtils.copy(bo.getShop(), Shop.class);
            shop.setShopId(shopId);
            shop.setCommercialTenantId(bo.getCommercialTenant().getCommercialTenantId());
            shopMapper.insert(shop);
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getYsfMerchant())) {
            bo.getYsfMerchant().forEach(o -> {
                o.setShopId(shopId);
            });
            shopMerchantMapper.insertBatch(bo.getYsfMerchant());
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getWxMerchant())) {
            bo.getWxMerchant().forEach(o -> {
                o.setShopId(shopId);
            });
            shopMerchantMapper.insertBatch(bo.getWxMerchant());
            i++;
        }
        if (ObjectUtil.isNotEmpty(bo.getPayMerchant())) {
            bo.getPayMerchant().forEach(o -> {
                o.setShopId(shopId);
            });
            shopMerchantMapper.insertBatch(bo.getPayMerchant());
            i++;
        }
        return i > 0;
    }

    @Override
    public List<VerifierVo> verifierList(Long shopId) {
        LambdaQueryWrapper<Verifier> lqw = Wrappers.lambdaQuery();
        //lqw.eq(Verifier::getPlatformKey, bo.getPlatformKey());
        lqw.inSql(Verifier::getId, "SELECT shop_id FROM t_verifier_shop WHERE shop_id = " + shopId);
        List<VerifierVo> result = baseMapper.selectVoList(lqw);
        return result;
    }

    @Override
    public TableDataInfo<ShopVo> queryShopPageList(VerifierBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Shop> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(Shop::getCreateTime);
        if (ObjectUtil.isNotEmpty(bo.getCommercialTenantId())) {
            lqw.eq(Shop::getCommercialTenantId, bo.getCommercialTenantId());
        } else {
            lqw.inSql(Shop::getShopId, "SELECT shop_id FROM t_verifier_shop WHERE verifier_id = " + bo.getId());
        }
        Page<ShopVo> result = shopMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public Boolean updateShopById(ShopBo bo) {
        return shopService.updateShopById(bo);
    }

    @Override
    public TableDataInfo<ProductVo> queryProductPageList(VerifierBo bo, PageQuery pageQuery) {
        // 查询核销人员门店信息
        List<Long> shopIds = new ArrayList<>();
        shopIds.add(bo.getShopId());
        Page<ProductVo> result = productMapper.selectPageByShopId(pageQuery.build(), shopIds);
        return TableDataInfo.build(result);
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

    /**
     * 解除核销人员与门店关系
     */
    @Override
    public Boolean cancelVerifier(CodeBo bo) {
        LambdaQueryWrapper<VerifierShop> lqw = Wrappers.lambdaQuery();
        lqw.eq(VerifierShop::getVerifierId, bo.getVerifierId());
        lqw.eq(VerifierShop::getShopId, bo.getShopId());
        return verifierShopMapper.delete(lqw) > 0;
    }

    /**
     * 添加核销人员与门店关系
     */
    @Override
    public Boolean addVerifier(CodeBo bo) {
        if (StringUtils.isEmpty(bo.getVerifierMobile())) return false;
        // 核销人员查询
        LambdaQueryWrapper<Verifier> lqw = Wrappers.lambdaQuery();
        lqw.eq(Verifier::getMobile, bo.getVerifierMobile());
        VerifierVo verifierVo = baseMapper.selectVoOne(lqw);
        // 判断核销人员是否存在
        if (ObjectUtil.isNotEmpty(verifierVo)) {
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
            //verifier.setVerifierType("verifier");
            verifier.setIsVerifier(true);
            verifier.setIsAdmin(false);
            verifier.setIsBd(false);
            verifier.setStatus("0");
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

    /**
     * 修改商品信息
     */
    @Override
    public Boolean updateProductById(ProductBo bo) {
        return productService.updateProductById(bo);
    }

    /**
     * 添加产品
     */
    @Override
    public Boolean insertProduct(ProductBo bo) {
        Product product = BeanCopyUtils.copy(bo, Product.class);
        if (product != null) {
            product.setStatus("0");
            product.setProductAffiliation("0");
            product.setPickupMethod("1");
            product.setProductId(IdUtil.getSnowflakeNextId());
            ProductInfo productInfo = new ProductInfo();
            productInfo.setProductId(product.getProductId());
            productInfo.setItemId(product.getProductId().toString());
            if (ObjectUtil.isNotEmpty(bo.getTotalCount())) {
                productInfo.setStock(bo.getTotalCount());
            } else {
                productInfo.setStock(-1L);
            }
            productInfo.setDiscount("0.00");
            productInfo.setCommissionRate(bo.getProductInfo().getCommissionRate());
            productInfo.setShopAll(bo.getProductInfo().getShopAll());
            productInfo.setOverdue(bo.getProductInfo().getOverdue());
            productInfo.setAnyTime(bo.getProductInfo().getAnyTime());
            productInfo.setLeastPrice(bo.getProductInfo().getLeastPrice());
            productInfo.setReducePrice(bo.getProductInfo().getReducePrice());
            if (bo.getProductInfo().getShopAll()) {
                // 查询用户全部商店数据
                LambdaQueryWrapper<VerifierShop> lqw = Wrappers.lambdaQuery();
                lqw.eq(VerifierShop::getVerifierId, LoginHelper.getUserId());
                List<VerifierShopVo> verifierShopVos = verifierShopMapper.selectVoList(lqw);
                List<Long> collect = verifierShopVos.stream().map(VerifierShopVo::getShopId).collect(Collectors.toList());
                if (ObjectUtil.isNotEmpty(collect)) {
                    List<ShopProduct> shopProducts = new ArrayList<>();
                    for (Long shopId : collect) {
                        ShopProduct shopProduct = new ShopProduct();
                        shopProduct.setId(IdUtil.getSnowflakeNextId());
                        shopProduct.setProductId(product.getProductId());
                        shopProduct.setShopId(shopId);
                        shopProduct.setSort(99L);
                        shopProducts.add(shopProduct);
                    }
                    shopProductMapper.insertBatch(shopProducts);
                    Shop shop = shopMapper.selectById(collect.get(0));
                    product.setPlatformKey(shop.getPlatformKey());
                }
            } else {
                Shop shop = shopMapper.selectById(bo.getShopId());
                ShopProduct shopProduct = new ShopProduct();
                shopProduct.setId(IdUtil.getSnowflakeNextId());
                shopProduct.setProductId(product.getProductId());
                shopProduct.setShopId(shop.getShopId());
                shopProduct.setSort(99L);
                shopProductMapper.insert(shopProduct);
                product.setPlatformKey(shop.getPlatformKey());
            }
            productMapper.insert(product);
            productInfoMapper.insert(productInfo);
            return true;
        }
        return false;
    }

    /**
     * 查询门店商编
     */
    @Override
    public List<ShopMerchantVo> getShopMerchant(ShopMerchantBo bo) {
        LambdaQueryWrapper<ShopMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShopMerchant::getShopId, bo.getShopId());
        if (StringUtils.isNotEmpty(bo.getMerchantType())) {
            lqw.eq(ShopMerchant::getMerchantType, bo.getMerchantType());
        }
        lqw.orderByDesc(ShopMerchant::getSettlement);
        return shopMerchantMapper.selectVoList(lqw);
    }

    /**
     * 修改门店上百年
     */
    @Override
    public Boolean insertShopMerchant(ShopAndMerchantBo bo) {
        List<ShopMerchant> bos = new ArrayList<>();
        bos.addAll(bo.getWxMerchant());
        bos.addAll(bo.getPayMerchant());
        bos.addAll(bo.getYsfMerchant());
        return shopService.updateShopMerchantById(bo.getShop().getShopId(), bos);
    }

    /**
     * 增加门店商编
     */
    @Override
    public Boolean insertTenantShopMerchant(ShopAndMerchantBo bo) {
        Boolean isUpdate = false;
        CommercialTenant commercialTenant = BeanCopyUtils.copy(bo.getCommercialTenant(), CommercialTenant.class);
        if (ObjectUtil.isEmpty(commercialTenant.getCommercialTenantId())) {
            commercialTenant.setCommercialTenantId(IdUtil.getSnowflakeNextId());
        }
        if (commercialTenant.getIsCache().equals("0")) {
            commercialTenant.getAdminMobile();
        }
        Shop shop = BeanCopyUtils.copy(bo.getShop(), Shop.class);
        if (ObjectUtil.isNotEmpty(shop.getShopId())) {
            isUpdate = true;
        } else {
            shop.setShopId(IdUtil.getSnowflakeNextId());
        }
        shop.setCommercialTenantId(commercialTenant.getCommercialTenantId());
        if (ObjectUtil.isNotEmpty(bo.getYsfMerchant())) {
            shopService.updateShopMerchantById(shop.getShopId(), bo.getYsfMerchant());
        }
        if (ObjectUtil.isNotEmpty(bo.getWxMerchant())) {
            shopService.updateShopMerchantById(shop.getShopId(), bo.getYsfMerchant());
        }
        if (ObjectUtil.isNotEmpty(bo.getPayMerchant())) {
            shopService.updateShopMerchantById(shop.getShopId(), bo.getYsfMerchant());
        }
        if (bo.getCommercialTenant().getIsCache().equals("1")) {
            Verifier newVerifier = baseMapper.selectByMobile(bo.getCommercialTenant().getAdminMobile());
            if (ObjectUtil.isNotEmpty(newVerifier)) {
                newVerifier.setIsVerifier(true);
                newVerifier.setIsAdmin(true);
                baseMapper.updateById(newVerifier);
            } else {
                newVerifier.setId(IdUtil.getSnowflakeNextId());
                newVerifier.setUsername(bo.getCommercialTenant().getAdminMobile());
                newVerifier.setMobile(bo.getCommercialTenant().getAdminMobile());
                newVerifier.setStatus("0");
                newVerifier.setReloadUser("0");
                newVerifier.setIsVerifier(true);
                newVerifier.setIsAdmin(true);
                baseMapper.insert(newVerifier);
            }
            VerifierShop verifierShop = new VerifierShop();
            verifierShop.setId(IdUtil.getSnowflakeNextId());
            verifierShop.setSort(99L);
            verifierShop.setShopId(shop.getShopId());
            verifierShop.setVerifierId(newVerifier.getId());
            verifierShopMapper.insert(verifierShop);
        }
        if (isUpdate) {
            shopMapper.updateById(shop);
            commercialTenantMapper.updateById(commercialTenant);
        } else {
            shopMapper.insert(shop);
            commercialTenantMapper.insert(commercialTenant);
        }
        return true;
    }
}
