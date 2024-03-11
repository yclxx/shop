package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.mapper.ShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ShopTourLsMerchantBo;
import com.ruoyi.zlyyh.domain.vo.ShopTourLsMerchantVo;
import com.ruoyi.zlyyh.domain.ShopTourLsMerchant;
import com.ruoyi.zlyyh.mapper.ShopTourLsMerchantMapper;
import com.ruoyi.zlyyhadmin.service.IShopTourLsMerchantService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 巡检商户号临时Service业务层处理
 *
 * @author yzg
 * @date 2024-03-10
 */
@RequiredArgsConstructor
@Service
public class ShopTourLsMerchantServiceImpl implements IShopTourLsMerchantService {

    private final ShopTourLsMerchantMapper baseMapper;
    private final ShopMapper shopMapper;

    /**
     * 查询巡检商户号临时
     */
    @Override
    public ShopTourLsMerchantVo queryById(Long tourMerchantLsId){
        return baseMapper.selectVoById(tourMerchantLsId);
    }

    /**
     * 查询巡检商户号临时列表
     */
    @Override
    public TableDataInfo<ShopTourLsMerchantVo> queryPageList(ShopTourLsMerchantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShopTourLsMerchant> lqw = buildQueryWrapper(bo);
        Page<ShopTourLsMerchantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<ShopTourLsMerchantVo> dataInfo = TableDataInfo.build(result);
        for (ShopTourLsMerchantVo row : dataInfo.getRows()) {
            row.setShopName(shopMapper.selectVoById(row.getShopId()).getShopName());
        }
        return dataInfo;
    }

    /**
     * 查询巡检商户号临时列表
     */
    @Override
    public List<ShopTourLsMerchantVo> queryList(ShopTourLsMerchantBo bo) {
        LambdaQueryWrapper<ShopTourLsMerchant> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShopTourLsMerchant> buildQueryWrapper(ShopTourLsMerchantBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShopTourLsMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getTourId() != null, ShopTourLsMerchant::getTourId, bo.getTourId());
        lqw.eq(bo.getTourLogId() != null, ShopTourLsMerchant::getTourLogId, bo.getTourLogId());
        lqw.eq(bo.getVerifierId() != null, ShopTourLsMerchant::getVerifierId, bo.getVerifierId());
        lqw.eq(bo.getShopId() != null, ShopTourLsMerchant::getShopId, bo.getShopId());
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantNo()), ShopTourLsMerchant::getMerchantNo, bo.getMerchantNo());
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantType()), ShopTourLsMerchant::getMerchantType, bo.getMerchantType());
        lqw.eq(StringUtils.isNotBlank(bo.getPaymentMethod()), ShopTourLsMerchant::getPaymentMethod, bo.getPaymentMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getAcquirer()), ShopTourLsMerchant::getAcquirer, bo.getAcquirer());
        lqw.eq(StringUtils.isNotBlank(bo.getTerminalNo()), ShopTourLsMerchant::getTerminalNo, bo.getTerminalNo());
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantImg()), ShopTourLsMerchant::getMerchantImg, bo.getMerchantImg());
        lqw.eq(StringUtils.isNotBlank(bo.getYcMerchant()), ShopTourLsMerchant::getYcMerchant, bo.getYcMerchant());
        lqw.eq(StringUtils.isNotBlank(bo.getIsUpdate()), ShopTourLsMerchant::getIsUpdate, bo.getIsUpdate());
        lqw.in(ObjectUtil.isNotEmpty(bo.getIsUpdateList()), ShopTourLsMerchant::getIsUpdate, bo.getIsUpdateList());
        return lqw;
    }

    /**
     * 新增巡检商户号临时
     */
    @Override
    public Boolean insertByBo(ShopTourLsMerchantBo bo) {
        ShopTourLsMerchant add = BeanUtil.toBean(bo, ShopTourLsMerchant.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTourMerchantLsId(add.getTourMerchantLsId());
        }
        return flag;
    }

    /**
     * 修改巡检商户号临时
     */
    @Override
    public Boolean updateByBo(ShopTourLsMerchantBo bo) {
        ShopTourLsMerchant update = BeanUtil.toBean(bo, ShopTourLsMerchant.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShopTourLsMerchant entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除巡检商户号临时
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
