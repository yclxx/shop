package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.ShopMerchantBo;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyh.domain.bo.ShopTourLogBo;
import com.ruoyi.zlyyh.domain.bo.ShopTourLsMerchantBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyhmobile.service.IShopTourService;
import lombok.RequiredArgsConstructor;
import org.redisson.misc.LogHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private final CityMapper cityMapper;
    private final BusinessDistrictMapper businessDistrictMapper;
    private final CommercialTenantMapper commercialTenantMapper;
    private final ShopTourRewardMapper shopTourRewardMapper;
    private final ShopTourActivityMapper shopTourActivityMapper;
    private final ShopMerchantMapper shopMerchantMapper;
    private final ShopTourLogMapper shopTourLogMapper;
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
        LambdaQueryWrapper<ShopTour> lqw = buildQueryWrapper(bo);
        Page<ShopTourVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<ShopTourVo> dataInfo = TableDataInfo.build(result);
        for (ShopTourVo row : dataInfo.getRows()) {
            ShopVo shopVo = shopMapper.selectVoById(row.getShopId());
            if (ObjectUtil.isNotEmpty(shopVo)) {
                row.setShopName(shopVo.getShopName());
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
        lqw.eq(bo.getShopId() != null, ShopTour::getShopId, bo.getShopId());
        lqw.eq(bo.getVerifierId() != null, ShopTour::getVerifierId, bo.getVerifierId());
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
        return baseMapper.updateById(update) > 0;
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
     * @param shopIds
     */
    @Override
    public void changeTourShop(List<Long> shopIds) {
        if (ObjectUtil.isNotEmpty(shopIds)) {
            for (Long shopId : shopIds) {
                List<ShopTourVo> shopTourVos = baseMapper.selectVoList(new LambdaQueryWrapper<ShopTour>().eq(ShopTour::getShopId, shopId));
                if (ObjectUtil.isNotEmpty(shopTourVos)) {
                    continue;
                } else {
                    ShopTour shopTour = new ShopTour();
                    shopTour.setShopId(shopId);
                    baseMapper.insert(shopTour);
                }
            }
        }
    }

    /**
     * 获取城市列表
     * @param cityCode
     * @return
     */
    @Override
    public List<CityVo> getCityList(String cityCode) {
        List<CityVo> cityVos = cityMapper.selectVoList(new LambdaQueryWrapper<City>().eq(City::getParentCode, cityCode));
        List<CityVo> list = new ArrayList<>();
        CityVo cityVo = new CityVo();
        cityVo.setAdcode(null);
        cityVo.setAreaName("全部");
        list.add(cityVo);
        list.addAll(cityVos);
        return list;
    }

    /**
     * 获取商圈列表
     * @param adcode
     * @return
     */
    @Override
    public List<BusinessDistrictVo> getBusinessDistrictList(String adcode) {
        List<BusinessDistrictVo> districtVos = businessDistrictMapper.selectVoList(new LambdaQueryWrapper<BusinessDistrict>().eq(BusinessDistrict::getAdcode, adcode).eq(BusinessDistrict::getStatus, "0"));
        List<BusinessDistrictVo> list = new ArrayList<>();
        BusinessDistrictVo districtVo = new BusinessDistrictVo();
        districtVo.setBusinessDistrictName("全部");
        list.add(districtVo);
        list.addAll(districtVos);
        return list;
    }

    @Override
    public TableDataInfo<ShopTourVo> queryPageTourList(ShopTourBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopTourActivity> wrapper = new LambdaQueryWrapper<>();
        String nowDate = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
        wrapper.eq(ShopTourActivity::getStatus,"0");
        wrapper.le(ShopTourActivity::getStartDate, nowDate);
        wrapper.gt(ShopTourActivity::getEndDate,nowDate);
        List<ShopTourActivityVo> tourActivityVos = shopTourActivityMapper.selectVoList(wrapper);
        List<Long> activityIds = null;
        if (ObjectUtil.isNotEmpty(tourActivityVos)) {
            activityIds = tourActivityVos.stream().map(ShopTourActivityVo::getTourActivityId).collect(Collectors.toList());
            bo.setActivityIds(activityIds);
            Page<ShopTourVo> result = baseMapper.queryPageTourList(bo, pageQuery.build());
            return TableDataInfo.build(result);
        } else {
            return TableDataInfo.build(new ArrayList<>());
        }
    }

    @Override
    public TableDataInfo<ShopTourVo> queryPageNearTourList(ShopTourBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopTourActivity> wrapper = new LambdaQueryWrapper<>();
        String nowDate = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
        wrapper.eq(ShopTourActivity::getStatus,"0");
        wrapper.le(ShopTourActivity::getStartDate, nowDate);
        wrapper.gt(ShopTourActivity::getEndDate,nowDate);
        List<ShopTourActivityVo> tourActivityVos = shopTourActivityMapper.selectVoList(wrapper);
        List<Long> activityIds = null;
        if (ObjectUtil.isNotEmpty(tourActivityVos)) {
            activityIds = tourActivityVos.stream().map(ShopTourActivityVo::getTourActivityId).collect(Collectors.toList());
            bo.setActivityIds(activityIds);
            Page<ShopTourVo> result = baseMapper.queryPageNearTourList(bo, pageQuery.build());
            return TableDataInfo.build(result);
        } else {
            return TableDataInfo.build(new ArrayList<>());
        }
    }

    /**
     * 预约巡检商户门店
     */
    @Override
    public String reserveTourShop(Long tourShopId,Long userId) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<ShopTour>().eq(ShopTour::getVerifierId, userId).eq(ShopTour::getIsReserve, "1").eq(ShopTour::getStatus,"1"));
        if (count >= 6) {
            return "1";
        }
        ShopTour shopTour = new ShopTour();
        shopTour.setId(tourShopId);
        shopTour.setVerifierId(userId);
        shopTour.setIsReserve("1");
        shopTour.setStatus("1");
        shopTour.setReserveDate(DateUtils.getNowDate());
        shopTour.setReserveValidity(DateUtils.getSecondEndTime(shopTour.getReserveDate()));
        int i = baseMapper.updateById(shopTour);

        if (i > 0) {
            ShopTourVo shopTourVo = baseMapper.selectVoById(tourShopId);
            if (ObjectUtil.isNotEmpty(shopTourVo)) {
                ShopTourLog tourLog = new ShopTourLog();
                tourLog.setTourId(tourShopId);
                tourLog.setVerifierId(userId);
                tourLog.setOperType("1");
                tourLog.setShopId(shopTourVo.getShopId());
                shopTourLogMapper.insert(tourLog);
            }
        }

        return "0";
    }

    /**
     * 取消预约
     * @param tourShopId
     */
    @Override
    public void cancelReserveTourShop(Long tourShopId,String tourType) {
        ShopTourVo shopTourVo = baseMapper.selectVoById(tourShopId);
        if (ObjectUtil.isEmpty(shopTourVo)) {
            return;
        }
        ShopTourLog tourLog = new ShopTourLog();
        tourLog.setTourId(tourShopId);
        tourLog.setVerifierId(shopTourVo.getVerifierId());
        if (tourType.equals("1")) {
            tourLog.setOperType("3");
        } else if (tourType.equals("2")) {
            tourLog.setOperType("4");
        } else if (tourType.equals("7")) {
            tourLog.setOperType("7");
        }
        tourLog.setShopId(shopTourVo.getShopId());
        shopTourLogMapper.insert(tourLog);

        ShopTour shopTour = new ShopTour();
        shopTour.setId(tourShopId);
        shopTour.setVerifierId(null);
        shopTour.setIsReserve("0");
        shopTour.setStatus("0");
        shopTour.setReserveDate(null);
        shopTour.setReserveValidity(null);
        shopTour.setCheckRemark(null);
        shopTour.setShopStatus("0");
        shopTour.setVerifierImage(null);
        shopTour.setGoodsImage(null);
        shopTour.setShopImage(null);
        shopTour.setTourRemark(null);
        shopTour.setMerchantNo(null);
        shopTour.setOldMerchantNo(null);
        shopTour.setMerchantType(null);
        shopTour.setIsActivity("1");
        shopTour.setNoActivityRemark(null);
        shopTour.setIsClose("1");
        shopTour.setCloseRemark(null);
        LambdaUpdateWrapper<ShopTour> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ShopTour::getId,shopTour.getId());
        wrapper.set(ShopTour::getVerifierId,null);
        wrapper.set(ShopTour::getReserveDate,null);
        wrapper.set(ShopTour::getReserveValidity,null);
        wrapper.set(ShopTour::getCheckRemark,null);
        wrapper.set(ShopTour::getVerifierImage,null);
        wrapper.set(ShopTour::getGoodsImage,null);
        wrapper.set(ShopTour::getShopImage,null);
        wrapper.set(ShopTour::getTourRemark,null);
        wrapper.set(ShopTour::getMerchantNo,null);
        wrapper.set(ShopTour::getOldMerchantNo,null);
        wrapper.set(ShopTour::getMerchantType,null);
        wrapper.set(ShopTour::getNoActivityRemark,null);
        wrapper.set(ShopTour::getCloseRemark,null);
        baseMapper.update(shopTour, wrapper);

        LambdaQueryWrapper<ShopTourLsMerchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopTourLsMerchant::getTourId,tourShopId);
        queryWrapper.eq(ShopTourLsMerchant::getVerifierId,shopTourVo.getVerifierId());
        shopTourLsMerchantMapper.delete(queryWrapper);

        RedisUtils.deleteObject("tourShop" + tourShopId + "user" + shopTourVo.getVerifierId());
    }

    /**
     * 获取预约商户门店
     * @return
     */
    @Override
    public TableDataInfo<ShopTourVo> getReserveShopList(ShopTourBo bo, PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        bo.setVerifierId(userId);
        Page<ShopTourVo> result = baseMapper.queryReserveShopList(bo, pageQuery.build());
        return TableDataInfo.build(result);
    }

    /**
     * 获取巡检商户门店信息
     * @param tourId
     * @return
     */
    @Override
    public ShopTourVo getTourShopInfo(Long tourId,String type) {
        Long userId = LoginHelper.getUserId();
        ShopTourBo bo = RedisUtils.getCacheObject("tourShop" + tourId + "user" + userId);
        if (ObjectUtil.isNotEmpty(bo)) {
            ShopTourVo shopTourVo = BeanUtil.toBean(bo, ShopTourVo.class);
            shopTourVo.setRedisFlag("1");
            return shopTourVo;
        }
        ShopTourVo shopTourVo = baseMapper.selectVoById(tourId);
        if (ObjectUtil.isNotEmpty(shopTourVo)) {
            if (StringUtils.isNotEmpty(type) && type.equals("1")) {
                //开始巡检
                ShopVo shopVo = shopMapper.selectVoById(shopTourVo.getShopId());
                if (ObjectUtil.isNotEmpty(shopVo)) {
                    shopTourVo.setShopName(shopVo.getShopName());
                    shopTourVo.setAddress(shopVo.getAddress());
                    if (ObjectUtil.isNotEmpty(shopVo.getCommercialTenantId())) {
                        CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoById(shopVo.getCommercialTenantId());
                        if (ObjectUtil.isNotEmpty(commercialTenantVo)) {
                            shopTourVo.setAdminMobile(commercialTenantVo.getAdminMobile());
                        }
                    }
                }
            } else if (StringUtils.isNotEmpty(type) && type.equals("2")) {
                //补充资料
                List<ShopTourLogVo> shopTourLogVos = shopTourLogMapper.selectVoList(new LambdaQueryWrapper<ShopTourLog>().eq(ShopTourLog::getTourId, tourId).eq(ShopTourLog::getVerifierId, shopTourVo.getVerifierId()).eq(ShopTourLog::getOperType, "2").orderByDesc(ShopTourLog::getCreateTime));
                if (ObjectUtil.isNotEmpty(shopTourLogVos)) {
                    ShopTourLogVo tourLogVo = shopTourLogVos.get(0);
                    shopTourVo.setShopName(tourLogVo.getShopName());
                    shopTourVo.setAddress(tourLogVo.getAddress());
                    shopTourVo.setAdminMobile(tourLogVo.getAdminMobile());
                }
            }
            //List<ShopTourLogVo> shopTourLogVos = shopTourLogMapper.selectVoList(new LambdaQueryWrapper<ShopTourLog>().eq(ShopTourLog::getTourId, tourId).eq(ShopTourLog::getVerifierId, shopTourVo.getVerifierId()).eq(ShopTourLog::getOperType, "2").orderByDesc(ShopTourLog::getCreateTime));
            //if (ObjectUtil.isNotEmpty(shopTourLogVos)) {
            //    ShopTourLogVo tourLogVo = shopTourLogVos.get(0);
            //    shopTourVo.setShopName(tourLogVo.getShopName());
            //    shopTourVo.setAddress(tourLogVo.getAddress());
            //    shopTourVo.setAdminMobile(tourLogVo.getAdminMobile());
            //} else {
            //    ShopVo shopVo = shopMapper.selectVoById(shopTourVo.getShopId());
            //    if (ObjectUtil.isNotEmpty(shopVo)) {
            //        shopTourVo.setShopName(shopVo.getShopName());
            //        shopTourVo.setAddress(shopVo.getAddress());
            //        if (ObjectUtil.isNotEmpty(shopVo.getCommercialTenantId())) {
            //            CommercialTenantVo commercialTenantVo = commercialTenantMapper.selectVoById(shopVo.getCommercialTenantId());
            //            if (ObjectUtil.isNotEmpty(commercialTenantVo)) {
            //                shopTourVo.setAdminMobile(commercialTenantVo.getAdminMobile());
            //            }
            //        }
            //    }
            //}
        }
        return shopTourVo;
    }

    /**
     * 暂存
     * @param bo
     */
    @Override
    public void tourZanCun(ShopTourBo bo) {
        Long userId = LoginHelper.getUserId();
        ShopTourBo shopTourBo = RedisUtils.getCacheObject("tourShop" + bo.getId() + "user" + userId);
        if (ObjectUtil.isNotEmpty(shopTourBo)) {
            RedisUtils.deleteObject("tourShop" + bo.getId() + "user" + userId);
        }
        RedisUtils.setCacheObject("tourShop" + bo.getId() + "user" + userId,bo);
    }

    /**
     * 巡检提交
     * @param bo
     */
    @Override
    public void tourSubmit(ShopTourBo bo) {
        Boolean aBoolean = updateByBo(bo);
        if (aBoolean) {
            ShopTourLog tourLog = new ShopTourLog();
            tourLog.setTourId(bo.getId());
            tourLog.setVerifierId(bo.getVerifierId());
            tourLog.setOperType("2");
            tourLog.setShopId(bo.getShopId());
            tourLog.setShopName(bo.getShopName());
            tourLog.setAddress(bo.getAddress());
            tourLog.setAdminMobile(bo.getAdminMobile());
            tourLog.setShopStatus(bo.getShopStatus());
            tourLog.setVerifierImage(bo.getVerifierImage());
            tourLog.setGoodsImage(bo.getGoodsImage());
            tourLog.setShopImage(bo.getShopImage());
            tourLog.setTourRemark(bo.getTourRemark());
            tourLog.setOldMerchantNo(bo.getOldMerchantNo());
            tourLog.setMerchantType(bo.getMerchantType());
            tourLog.setMerchantNo(bo.getMerchantNo());
            tourLog.setIsActivity(bo.getIsActivity());
            tourLog.setIsClose(bo.getIsClose());
            shopTourLogMapper.insert(tourLog);
            Long userId = LoginHelper.getUserId();
            RedisUtils.deleteObject("tourShop" + bo.getId() + "user" + userId);
        }
    }

    /**
     * 获取巡检奖励
     */
    @Override
    public ShopTourRewardVo getTourReward(Long userId) {
        return shopTourRewardMapper.selectVoOne(new LambdaQueryWrapper<ShopTourReward>().eq(ShopTourReward::getVerifierId,userId).last("limit 1"));
    }

    /**
     * 核验有效期
     */
    @Override
    public String verifyDate(Long tourShopId) {
        ShopTourVo shopTourVo = baseMapper.selectVoById(tourShopId);
        if (ObjectUtil.isNotEmpty(shopTourVo)) {
            if (DateUtils.compare(shopTourVo.getReserveValidity()) < 0) {
                cancelReserveTourShop(tourShopId,"7");
                return "0";
            }
        }
        return "";
    }

    /**
     * 获取商户号
     */
    @Override
    public List<ShopMerchantVo> getShopMerchantNoList(Long shopId) {
        return shopMerchantMapper.selectVoList(new LambdaQueryWrapper<ShopMerchant>().eq(ShopMerchant::getShopId, shopId));
    }

    /**
     * 获取商户号信息
     */
    @Override
    public List<ShopMerchantVo> getShopMerchantNoInfo(Long shopId, String merchantType) {
        return shopMerchantMapper.selectVoList(new LambdaQueryWrapper<ShopMerchant>().eq(ShopMerchant::getShopId, shopId).eq(ShopMerchant::getMerchantType,merchantType));
    }

    /**
     * 巡检商户号变更提交
     */
    @Override
    public void updateMerchantNo(ShopTourLsMerchantBo bo) {
        Long userId = LoginHelper.getUserId();
        if (ObjectUtil.isNotEmpty(bo.getShopMerchantBos())) {
            shopTourLsMerchantMapper.delete(new LambdaQueryWrapper<ShopTourLsMerchant>().eq(ShopTourLsMerchant::getTourId,bo.getTourId()).eq(ShopTourLsMerchant::getVerifierId,userId));
            for (ShopMerchantBo shopMerchantBo : bo.getShopMerchantBos()) {
                ShopTourLsMerchant lsMerchant = new ShopTourLsMerchant();
                lsMerchant.setTourId(bo.getTourId());
                lsMerchant.setVerifierId(userId);
                lsMerchant.setShopId(bo.getShopId());
                lsMerchant.setMerchantNo(shopMerchantBo.getMerchantNo());
                lsMerchant.setMerchantType(shopMerchantBo.getMerchantType());
                lsMerchant.setPaymentMethod(shopMerchantBo.getPaymentMethod());
                lsMerchant.setAcquirer(shopMerchantBo.getAcquirer());
                lsMerchant.setTerminalNo(shopMerchantBo.getTerminalNo());
                lsMerchant.setMerchantImg(shopMerchantBo.getMerchantImg());
                lsMerchant.setYcMerchant(shopMerchantBo.getYcMerchant());
                if (StringUtils.isNotEmpty(shopMerchantBo.getOldMerchantNo())) {
                    lsMerchant.setOldMerchantNo(shopMerchantBo.getOldMerchantNo());
                }
                if (StringUtils.isNotEmpty(shopMerchantBo.getIsUpdate())) {
                    lsMerchant.setIsUpdate(shopMerchantBo.getIsUpdate());
                }
                shopTourLsMerchantMapper.insert(lsMerchant);
            }
        }
    }

    /**
     * 获取临时商户号信息
     */
    @Override
    public List<ShopTourLsMerchantVo> getLsShopMerchantNoList(Long tourId, Long shopId) {
        Long userId = LoginHelper.getUserId();
        return shopTourLsMerchantMapper.selectVoList(new LambdaQueryWrapper<ShopTourLsMerchant>().eq(ShopTourLsMerchant::getTourId,tourId).eq(ShopTourLsMerchant::getShopId,shopId).eq(ShopTourLsMerchant::getVerifierId,userId));
    }

    /**
     * 获取失效预约记录列表
     */
    @Override
    public TableDataInfo<ShopTourLogVo> getInvalidTourList(ShopTourLogBo bo, PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        bo.setVerifierId(userId);
        Page<ShopTourLogVo> result = shopTourLogMapper.getInvalidTourList(bo, pageQuery.build());
        return TableDataInfo.build(result);
    }
}
