package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.ExtensionServiceProvider;
import com.ruoyi.zlyyh.domain.MerchantApproval;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.ShopMerchant;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyh.mapper.ExtensionServiceProviderMapper;
import com.ruoyi.zlyyh.mapper.MerchantApprovalMapper;
import com.ruoyi.zlyyh.mapper.ShopMapper;
import com.ruoyi.zlyyh.mapper.ShopMerchantMapper;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 门店Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class ShopServiceImpl implements IShopService {

    private final ShopMapper baseMapper;
    private final ShopMerchantMapper shopMerchantMapper;
    private final MerchantApprovalMapper merchantApprovalMapper;
    private final ExtensionServiceProviderMapper extensionServiceProviderMapper;

    /**
     * 查询门店
     */
    @Cacheable(cacheNames = CacheNames.SHOP, key = "#shopId")
    @Override
    public ShopVo queryById(Long shopId) {
        return baseMapper.selectVoById(shopId);
    }

    @Override
    public Boolean updateShopById(ShopBo bo) {
        if (ObjectUtil.isNotEmpty(bo)) {
            this.getAddressCode(bo);
            Shop shop = BeanCopyUtils.copy(bo, Shop.class);
            return baseMapper.updateById(shop) > 0;
        } else {
            throw new ServiceException("编辑失败。");
        }
    }

    public Boolean updateShopMerchantById(Long shopId, List<ShopMerchant> bos) {
        Shop shop = baseMapper.selectById(shopId);
        if (ObjectUtil.isNotEmpty(bos)) {
            List<ShopMerchant> insert = new ArrayList<>();
            List<ShopMerchant> update = new ArrayList<>();
            bos.forEach(o -> {
                if (ObjectUtil.isNotEmpty(o.getId())) {
                    if (ObjectUtil.isNotEmpty(shop)) {
                        o.setShopId(shop.getShopId());
                    } else {
                        o.setShopId(shopId);
                    }
                    update.add(o);
                } else {
                    o.setId(IdUtil.getSnowflakeNextId());
                    o.setStatus("0");
                    if (ObjectUtil.isNotEmpty(shop)) {
                        o.setShopId(shop.getShopId());
                    } else {
                        o.setShopId(shopId);
                    }
                    insert.add(o);
                }
            });
            if (ObjectUtil.isNotEmpty(update)) {
                List<Long> updateIds = update.stream().map(ShopMerchant::getId).collect(Collectors.toList());
                shopMerchantMapper.delete(new LambdaQueryWrapper<ShopMerchant>().notIn(ShopMerchant::getId, updateIds).eq(ShopMerchant::getMerchantType, bos.get(0).getMerchantType()));
                shopMerchantMapper.updateBatchById(update);
            }
            if (ObjectUtil.isNotEmpty(insert)) shopMerchantMapper.insertBatch(insert);
            return true;
        } else {
            throw new ServiceException("门店商编异常");
        }
    }

    /**
     * 查询门店列表
     */
    @Override
    public TableDataInfo<ShopVo> queryPageList(ShopBo bo, PageQuery pageQuery) {
        pageQuery.setOrderByColumn(null);
        pageQuery.setIsAsc(null);
        Page<ShopVo> result = baseMapper.selectShopList(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询门店列表
     */
    @Override
    public TableDataInfo<ShopVo> getShopListByProductId(ShopBo bo, PageQuery pageQuery) {
        pageQuery.setOrderByColumn(null);
        pageQuery.setIsAsc(null);
        Page<ShopVo> result = baseMapper.selectShopListByProductId(pageQuery.build(), bo);
        if (result.getTotal() == 0) {
            bo.setCitycode(null);
            result = baseMapper.selectShopListByProductId(pageQuery.build(), bo);
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询门店列表
     */
    @Override
    public TableDataInfo<ShopVo> selectShopListByCommercialTenantId(ShopBo bo, PageQuery pageQuery) {
        pageQuery.setOrderByColumn(null);
        pageQuery.setIsAsc(null);
        Page<ShopVo> result = baseMapper.selectShopListByCommercialTenantId(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商户门店
     *
     * @param commercialIds 商户ID
     * @return 门店集合
     */
    @Override
    public List<ShopVo> queryListByCommercialIds(List<Long> commercialIds, String cityCode) {
        if (ObjectUtil.isEmpty(commercialIds)) {
            return new ArrayList<>();
        }
        return baseMapper.selectVoList(new LambdaQueryWrapper<Shop>().in(Shop::getCommercialTenantId, commercialIds).eq(Shop::getCitycode, cityCode));
    }

    @Override
    public List<Shop> queryListByCommercialId(Long commercialId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Shop>().eq(Shop::getCommercialTenantId, commercialId));
    }

    public boolean addApproval(MerchantApprovalBo bo) {
        if (StringUtils.isEmpty(bo.getBrandMobile())) throw new ServiceException("管理员手机号为空");
        LambdaQueryWrapper<MerchantApproval> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantApproval::getBrandMobile, bo.getBrandMobile());
        lqw.in(MerchantApproval::getApprovalStatus, "0", "1");
        Long l = merchantApprovalMapper.selectCount(lqw);
        if (l > 0) {
            throw new ServiceException("此管理员手机号已申请商户");
        }
        if (StringUtils.isNotEmpty(bo.getExtend())) {
            ExtensionServiceProvider extensionServiceProvider = extensionServiceProviderMapper.selectById(bo.getExtend());
            if (ObjectUtil.isEmpty(extensionServiceProvider)) {
                throw new ServiceException("扩展服务商号不存在");
            }
        }
        bo.setApprovalStatus("0");
        MerchantApproval merchantApproval = BeanCopyUtils.copy(bo, MerchantApproval.class);
        return merchantApprovalMapper.insert(merchantApproval) > 0;
    }

    /**
     * 查询商户门店数量
     *
     * @param commercialTenantId 商户Id
     * @return 门店数量
     */
    public Long selectCountByCommercialTenantId(Long commercialTenantId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<Shop>().eq(Shop::getCommercialTenantId, commercialTenantId));
    }

    private void getAddressCode(ShopBo bo) {
        JSONObject addressInfo;
        String key;
        if (StringUtils.isBlank(bo.getAddress())) {
            return;
        }
        key = "importShop:" + bo.getAddress();
        addressInfo = RedisUtils.getCacheObject(key);
        if (ObjectUtil.isEmpty(addressInfo)) {
            addressInfo = AddressUtils.getAddressInfo(bo.getAddress());
        }
        if (ObjectUtil.isNotEmpty(addressInfo)) {
            bo.setFormattedAddress(addressInfo.getString("formatted_address"));
            bo.setProvince(addressInfo.getString("province"));
            bo.setCity(addressInfo.getString("city"));
            bo.setDistrict(addressInfo.getString("district"));
            String adcode = addressInfo.getString("adcode");
            String procode = adcode.substring(0, 2) + "0000";
            String citycode = adcode.substring(0, 4) + "00";
            bo.setProcode(procode);
            bo.setCitycode(citycode);
            bo.setAdcode(adcode);
            if (ObjectUtil.isEmpty(bo.getLatitude()) || ObjectUtil.isEmpty(bo.getLongitude())) {
                String location = addressInfo.getString("location");
                String[] split = location.split(",");
                String longitude = split[0];
                String latitude = split[1];
                bo.setLongitude(new BigDecimal(longitude));
                bo.setLatitude(new BigDecimal(latitude));
            }
            if (StringUtils.isNotBlank(key)) {
                RedisUtils.setCacheObject(key, addressInfo, Duration.ofDays(5));
            }
        }
    }
}
