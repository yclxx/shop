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
import com.ruoyi.zlyyh.domain.BusinessDistrict;
import com.ruoyi.zlyyh.domain.City;
import com.ruoyi.zlyyh.domain.ShopTour;
import com.ruoyi.zlyyh.domain.ShopTourReward;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
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
        return cityMapper.selectVoList(new LambdaQueryWrapper<City>().eq(City::getParentCode, cityCode));
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
        Page<ShopTourVo> result = baseMapper.queryPageTourList(bo, pageQuery.build());
        return TableDataInfo.build(result);
    }

    @Override
    public TableDataInfo<ShopTourVo> queryPageNearTourList(ShopTourBo bo, PageQuery pageQuery) {
        Page<ShopTourVo> result = baseMapper.queryPageNearTourList(bo, pageQuery.build());
        return TableDataInfo.build(result);
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
        baseMapper.updateById(shopTour);
        return "0";
    }

    /**
     * 取消预约
     * @param tourShopId
     */
    @Override
    public void cancelReserveTourShop(Long tourShopId) {
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
        wrapper.set(ShopTour::getNoActivityRemark,null);
        wrapper.set(ShopTour::getCloseRemark,null);
        baseMapper.update(shopTour,wrapper);
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
    public ShopTourVo getTourShopInfo(Long tourId) {
        Long userId = LoginHelper.getUserId();
        ShopTourBo bo = RedisUtils.getCacheObject("tourShop" + tourId + "user" + userId);
        if (ObjectUtil.isNotEmpty(bo)) {
            return BeanUtil.toBean(bo,ShopTourVo.class);
        }
        ShopTourVo shopTourVo = baseMapper.selectVoById(tourId);
        if (ObjectUtil.isNotEmpty(shopTourVo)) {
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
                cancelReserveTourShop(tourShopId);
                return "0";
            }
        }
        return "";
    }
}
