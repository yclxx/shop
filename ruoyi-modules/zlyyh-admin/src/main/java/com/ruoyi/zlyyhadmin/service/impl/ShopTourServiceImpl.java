package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyhadmin.service.IShopTourService;

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
            if (bo.getIsClose().equals("0")) {
                Shop shop = new Shop();
                shop.setShopId(bo.getShopId());
                shop.setStatus("1");
                shopMapper.updateById(shop);
            }
            if (StringUtils.isNotEmpty(bo.getMerchantNo())) {
                if (StringUtils.isNotEmpty(bo.getOldMerchantNo())) {
                    List<ShopMerchantVo> merchantVos = shopMerchantMapper.selectVoList(new LambdaQueryWrapper<ShopMerchant>().eq(ShopMerchant::getShopId, bo.getShopId()).eq(ShopMerchant::getMerchantNo, bo.getOldMerchantNo()).eq(ShopMerchant::getMerchantType, bo.getMerchantType()));
                    if (ObjectUtil.isNotEmpty(merchantVos)) {
                        for (ShopMerchantVo merchantVo : merchantVos) {
                            ShopMerchant shopMerchant = new ShopMerchant();
                            shopMerchant.setId(merchantVo.getId());
                            shopMerchant.setMerchantNo(bo.getMerchantNo());
                            shopMerchantMapper.updateById(shopMerchant);
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
}
