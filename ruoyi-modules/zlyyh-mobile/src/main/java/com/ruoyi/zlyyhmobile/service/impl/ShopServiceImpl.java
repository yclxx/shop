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
import com.ruoyi.zlyyh.domain.bo.ShopMerchantBo;
import com.ruoyi.zlyyh.domain.vo.ShopMerchantVo;
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
    public Shop getShopBytId(Long shopId) {
        Shop shop = baseMapper.selectById(shopId);
        return shop;
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

    public Boolean updateShopMerchantById(List<ShopMerchantBo> bos) {
        if (ObjectUtil.isNotEmpty(bos)) {
            List<ShopMerchant> insert = new ArrayList<>();
            List<ShopMerchant> update = new ArrayList<>();
            bos.forEach(o -> {
                if (ObjectUtil.isNotEmpty(o.getId())) {
                    ShopMerchant shopMerchant = BeanCopyUtils.copy(o, ShopMerchant.class);
                    update.add(shopMerchant);
                } else {
                    ShopMerchant shopMerchant = BeanCopyUtils.copy(o, ShopMerchant.class);
                    shopMerchant.setId(IdUtil.getSnowflakeNextId());
                    shopMerchant.setStatus("0");
                    insert.add(shopMerchant);
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
            throw new ServiceException("编辑失败。");
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
    public List<ShopMerchantVo> getShopMerchantVo(ShopMerchantBo bo) {
        LambdaQueryWrapper<ShopMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShopMerchant::getShopId, bo.getShopId());
        lqw.eq(ShopMerchant::getMerchantType, bo.getMerchantType());
        return shopMerchantMapper.selectVoList(lqw);
    }

    public boolean addApproval(MerchantApprovalBo bo) {
        LambdaQueryWrapper<MerchantApproval> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantApproval::getMobile, bo.getMobile());
        if (merchantApprovalMapper.selectCount(lqw) > 0) {
            throw new ServiceException("此管理员手机号已申请");
        }
        ExtensionServiceProvider extensionServiceProvider = extensionServiceProviderMapper.selectById(bo.getExtend());
        if (ObjectUtil.isEmpty(extensionServiceProvider)) {
            throw new ServiceException("扩展服务商号不存在");
        }
        bo.setApprovalStatus("0");
        MerchantApproval merchantApproval = BeanCopyUtils.copy(bo, MerchantApproval.class);
        return merchantApprovalMapper.insert(merchantApproval) > 0;
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
