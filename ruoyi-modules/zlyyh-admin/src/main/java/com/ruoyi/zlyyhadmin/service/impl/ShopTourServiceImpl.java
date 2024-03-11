package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyhadmin.service.IShopTourService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 巡检商户Service业务层处理
 *
 * @author yzg
 * @date 2024-01-28
 */
@RequiredArgsConstructor
@Service
public class ShopTourServiceImpl implements IShopTourService {

    private final ShopTourMapper baseMapper;
    private final ShopMapper shopMapper;
    private final VerifierMapper verifierMapper;
    private final ShopTourRewardMapper shopTourRewardMapper;
    private final ShopMerchantMapper shopMerchantMapper;
    private final ShopTourLogMapper shopTourLogMapper;
    private final CommercialTenantMapper commercialTenantMapper;
    private final ShopTourLsMerchantMapper shopTourLsMerchantMapper;

    /**
     * 查询巡检商户
     */
    @Override
    public ShopTourVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询巡检商户列表
     */
    @Override
    public TableDataInfo<ShopTourVo> queryPageList(ShopTourBo bo, PageQuery pageQuery) {
        if (StringUtils.isNotEmpty(bo.getShopName())) {
            List<ShopVo> shopVos = shopMapper.selectVoList(new LambdaQueryWrapper<Shop>().like(Shop::getShopName, bo.getShopName()));
            if (ObjectUtil.isNotEmpty(shopVos)) {
                bo.setShopsIds(shopVos.stream().map(ShopVo::getShopId).collect(Collectors.toList()));
            } else {
                return TableDataInfo.build(new ArrayList<>());
            }
        }
        LambdaQueryWrapper<ShopTour> lqw = buildQueryWrapper(bo);
        Page<ShopTourVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<ShopTourVo> dataInfo = TableDataInfo.build(result);
        for (ShopTourVo row : dataInfo.getRows()) {
            ShopVo shopVo = shopMapper.selectVoById(row.getShopId());
            List<ShopTourLogVo> shopTourLogVos = shopTourLogMapper.selectVoList(new LambdaQueryWrapper<ShopTourLog>().eq(ShopTourLog::getTourId, row.getId()).eq(ShopTourLog::getVerifierId, row.getVerifierId()).eq(ShopTourLog::getOperType, "2").orderByDesc(ShopTourLog::getCreateTime));
            if (ObjectUtil.isNotEmpty(shopVo)) {
                row.setShopName(shopVo.getShopName());
            }
            if (ObjectUtil.isNotEmpty(shopTourLogVos)) {
                row.setShopTourLogVo(shopTourLogVos.get(0));
            }
        }
        return dataInfo;
    }

    /**
     * 查询巡检商户列表
     */
    @Override
    public List<ShopTourVo> queryList(ShopTourBo bo) {
        LambdaQueryWrapper<ShopTour> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShopTour> buildQueryWrapper(ShopTourBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShopTour> lqw = Wrappers.lambdaQuery();
        //lqw.eq(bo.getShopId() != null, ShopTour::getShopId, bo.getShopId());
        lqw.eq(bo.getVerifierId() != null, ShopTour::getVerifierId, bo.getVerifierId());
        lqw.eq(bo.getTourActivityId() != null, ShopTour::getTourActivityId, bo.getTourActivityId());
        lqw.eq(bo.getRewardAmount() != null, ShopTour::getRewardAmount, bo.getRewardAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getIsReserve()), ShopTour::getIsReserve, bo.getIsReserve());
        lqw.eq(StringUtils.isNotBlank(bo.getShopStatus()), ShopTour::getShopStatus, bo.getShopStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ShopTour::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getCheckRemark()), ShopTour::getCheckRemark, bo.getCheckRemark());
        lqw.eq(StringUtils.isNotBlank(bo.getVerifierImage()), ShopTour::getVerifierImage, bo.getVerifierImage());
        lqw.eq(StringUtils.isNotBlank(bo.getGoodsImage()), ShopTour::getGoodsImage, bo.getGoodsImage());
        lqw.eq(StringUtils.isNotBlank(bo.getShopImage()), ShopTour::getShopImage, bo.getShopImage());
        lqw.eq(StringUtils.isNotBlank(bo.getTourRemark()), ShopTour::getTourRemark, bo.getTourRemark());
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantNo()), ShopTour::getMerchantNo, bo.getMerchantNo());
        lqw.eq(StringUtils.isNotBlank(bo.getIsActivity()), ShopTour::getIsActivity, bo.getIsActivity());
        lqw.eq(StringUtils.isNotBlank(bo.getIsClose()), ShopTour::getIsClose, bo.getIsClose());
        lqw.in(ObjectUtil.isNotEmpty(bo.getShopsIds()), ShopTour::getShopId, bo.getShopsIds());
        return lqw;
    }

    /**
     * 新增巡检商户
     */
    @Override
    public Boolean insertByBo(ShopTourBo bo) {
        ShopTour add = BeanUtil.toBean(bo, ShopTour.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改巡检商户
     */
    @Override
    public Boolean updateByBo(ShopTourBo bo) {
        ShopTour update = BeanUtil.toBean(bo, ShopTour.class);
        validEntityBeforeSave(update);
        boolean b = baseMapper.updateById(update) > 0;
        if (b) {
            if (StringUtils.isNotEmpty(bo.getTourType()) && bo.getTourType().equals("6")) {
                ShopTourVo shopTourVo = baseMapper.selectVoById(bo.getId());
                if (ObjectUtil.isNotEmpty(shopTourVo)) {
                    ShopTourLog tourLog = new ShopTourLog();
                    tourLog.setTourId(shopTourVo.getId());
                    tourLog.setVerifierId(shopTourVo.getVerifierId());
                    tourLog.setOperType("6");
                    tourLog.setShopId(shopTourVo.getShopId());
                    tourLog.setCheckFailReason(bo.getCheckRemark());
                    shopTourLogMapper.insert(tourLog);
                }
            }
        }
        return b;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShopTour entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除巡检商户
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 添加巡检商户
     */
    @Override
    public void changeTourShop(ShopTourBo bo) {
        if (ObjectUtil.isEmpty(bo.getShopIds())) {
            return;
        }
        for (Long shopId : bo.getShopIds()) {
            List<ShopTourVo> shopTourVos = baseMapper.selectVoList(new LambdaQueryWrapper<ShopTour>().eq(ShopTour::getShopId, shopId).eq(ShopTour::getTourActivityId,bo.getTourActivityId()));
            if (ObjectUtil.isNotEmpty(shopTourVos)) {
                continue;
            } else {
                ShopTour shopTour = new ShopTour();
                shopTour.setShopId(shopId);
                shopTour.setTourActivityId(bo.getTourActivityId());
                if (ObjectUtil.isNotEmpty(bo.getRewardAmount())) {
                    shopTour.setRewardAmount(bo.getRewardAmount());
                }
                baseMapper.insert(shopTour);
            }
        }
    }

    /**
     * 巡检审核通过
     */
    @Override
    public void tourCheckPass(ShopTourBo bo) {
        ShopTourBo shopTourBo = new ShopTourBo();
        shopTourBo.setId(bo.getId());
        shopTourBo.setStatus("3");
        Boolean aBoolean = updateByBo(shopTourBo);
        if (aBoolean) {
            ShopTourLog tourLog = new ShopTourLog();
            tourLog.setTourId(bo.getId());
            tourLog.setVerifierId(bo.getVerifierId());
            tourLog.setOperType("5");
            tourLog.setShopId(bo.getShopId());
            shopTourLogMapper.insert(tourLog);

            LambdaQueryWrapper<ShopTourLsMerchant> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShopTourLsMerchant::getTourId,bo.getId());
            wrapper.eq(ShopTourLsMerchant::getVerifierId,bo.getVerifierId());
            wrapper.in(ShopTourLsMerchant::getIsUpdate,"1","2");
            List<ShopTourLsMerchantVo> lsMerchantVos = shopTourLsMerchantMapper.selectVoList(wrapper);
            if (ObjectUtil.isNotEmpty(lsMerchantVos)) {
                for (ShopTourLsMerchantVo lsMerchantVo : lsMerchantVos) {
                    if (lsMerchantVo.getIsUpdate().equals("1")) {
                        //修改商户号
                        List<ShopMerchantVo> merchantVos = shopMerchantMapper.selectVoList(new LambdaQueryWrapper<ShopMerchant>().eq(ShopMerchant::getShopId, lsMerchantVo.getShopId()).eq(ShopMerchant::getMerchantNo, lsMerchantVo.getOldMerchantNo()).eq(ShopMerchant::getMerchantType, lsMerchantVo.getMerchantType()));
                        if (ObjectUtil.isNotEmpty(merchantVos)) {
                            for (ShopMerchantVo merchantVo : merchantVos) {
                                ShopMerchant shopMerchant = new ShopMerchant();
                                shopMerchant.setId(merchantVo.getId());
                                shopMerchant.setMerchantNo(lsMerchantVo.getMerchantNo());
                                shopMerchant.setPaymentMethod(lsMerchantVo.getPaymentMethod());
                                shopMerchant.setAcquirer(lsMerchantVo.getAcquirer());
                                shopMerchant.setTerminalNo(lsMerchantVo.getTerminalNo());
                                shopMerchant.setMerchantImg(lsMerchantVo.getMerchantImg());
                                shopMerchant.setYcMerchant(lsMerchantVo.getYcMerchant());
                                shopMerchantMapper.updateById(shopMerchant);
                            }
                        }
                    } else if (lsMerchantVo.getIsUpdate().equals("2")){
                        //新增商户号
                        ShopMerchant shopMerchant = new ShopMerchant();
                        shopMerchant.setShopId(lsMerchantVo.getShopId());
                        shopMerchant.setMerchantType(lsMerchantVo.getMerchantType());
                        shopMerchant.setMerchantNo(lsMerchantVo.getMerchantNo());
                        shopMerchant.setPaymentMethod(lsMerchantVo.getPaymentMethod());
                        shopMerchant.setAcquirer(lsMerchantVo.getAcquirer());
                        shopMerchant.setTerminalNo(lsMerchantVo.getTerminalNo());
                        shopMerchant.setMerchantImg(lsMerchantVo.getMerchantImg());
                        shopMerchant.setYcMerchant(lsMerchantVo.getYcMerchant());
                        shopMerchantMapper.insert(shopMerchant);
                    }
                }
            }
            List<ShopTourLogVo> shopTourLogVos = shopTourLogMapper.selectVoList(new LambdaQueryWrapper<ShopTourLog>().eq(ShopTourLog::getTourId, bo.getId()).eq(ShopTourLog::getVerifierId, bo.getVerifierId()).eq(ShopTourLog::getOperType, "2").orderByDesc(ShopTourLog::getCreateTime));
            if (ObjectUtil.isNotEmpty(shopTourLogVos)) {
                ShopTourLogVo tourLogVo = shopTourLogVos.get(0);
                Shop shop = new Shop();
                shop.setShopId(tourLogVo.getShopId());
                if (tourLogVo.getShopStatus().equals("2")) {
                    shop.setStatus("1");
                }
                if (StringUtils.isNotEmpty(tourLogVo.getShopName())) {
                    shop.setShopName(tourLogVo.getShopName());
                }
                if (StringUtils.isNotEmpty(tourLogVo.getAddress())) {
                    shop.setAddress(tourLogVo.getAddress());
                    getAddressCode(tourLogVo,shop);
                }
                shopMapper.updateById(shop);

                if (StringUtils.isNotEmpty(tourLogVo.getAdminMobile())) {
                    ShopVo shopVo = shopMapper.selectVoById(tourLogVo.getShopId());
                    if (ObjectUtil.isNotEmpty(shopVo)) {
                        if (ObjectUtil.isNotEmpty(shopVo.getCommercialTenantId())) {
                            CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoById(shopVo.getCommercialTenantId());
                            if (ObjectUtil.isNotEmpty(commercialTenantVo)) {
                                CommercialTenant commercialTenant = new CommercialTenant();
                                commercialTenant.setCommercialTenantId(shopVo.getCommercialTenantId());
                                commercialTenant.setAdminMobile(tourLogVo.getAdminMobile());
                                commercialTenantMapper.updateById(commercialTenant);
                            }
                        }
                    }
                }
            }
            ShopTourRewardVo rewardVo = shopTourRewardMapper.selectVoOne(new LambdaQueryWrapper<ShopTourReward>().eq(ShopTourReward::getVerifierId, bo.getVerifierId()).last("limit 1"));
            if (ObjectUtil.isNotEmpty(rewardVo)) {
                rewardVo.setAmount(rewardVo.getAmount() + bo.getRewardAmount());
                rewardVo.setCount(rewardVo.getCount() + 1);
                shopTourRewardMapper.updateById(BeanUtil.toBean(rewardVo,ShopTourReward.class));
            } else {
                ShopTourReward reward = new ShopTourReward();
                reward.setVerifierId(bo.getVerifierId());
                reward.setAmount(bo.getRewardAmount());
                reward.setCount(1L);
                shopTourRewardMapper.insert(reward);
            }
        }
    }

    @Override
    public ShopVo queryByShopId(Long shopId) {
        ShopVo shopVo = shopMapper.selectVoById(shopId);
        if (ObjectUtil.isNotEmpty(shopVo)) {
            if (ObjectUtil.isNotEmpty(shopVo.getCommercialTenantId())) {
                CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoById(shopVo.getCommercialTenantId());
                if (ObjectUtil.isNotEmpty(commercialTenantVo)) {
                    shopVo.setAdminMobile(commercialTenantVo.getAdminMobile());
                }
            }
        }
        return shopVo;
    }

    private void getAddressCode(ShopTourLogVo vo, Shop shop) {
        JSONObject addressInfo;
        String key;
        if (StringUtils.isBlank(vo.getAddress())) {
            return;
        }
        key = "updateTourShop:" + vo.getAddress();
        addressInfo = RedisUtils.getCacheObject(key);
        if (ObjectUtil.isEmpty(addressInfo)) {
            addressInfo = AddressUtils.getAddressInfo(vo.getAddress(), null);
        }
        if (ObjectUtil.isNotEmpty(addressInfo)) {
            shop.setFormattedAddress(addressInfo.getString("formatted_address"));
            shop.setProvince(addressInfo.getString("province"));
            shop.setCity(addressInfo.getString("city"));
            shop.setDistrict(addressInfo.getString("district"));
            String adcode = addressInfo.getString("adcode");
            String procode = adcode.substring(0, 2) + "0000";
            String citycode = adcode.substring(0, 4) + "00";
            shop.setProcode(procode);
            shop.setCitycode(citycode);
            shop.setAdcode(adcode);

            String location = addressInfo.getString("location");
            String[] split = location.split(",");
            String longitude = split[0];
            String latitude = split[1];
            shop.setLongitude(new BigDecimal(longitude));
            shop.setLatitude(new BigDecimal(latitude));

            if (StringUtils.isNotBlank(key)) {
                RedisUtils.setCacheObject(key, addressInfo, Duration.ofDays(5));
            }
        }
    }
}
