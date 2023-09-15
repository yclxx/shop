package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.domain.GrabPeriod;
import com.ruoyi.zlyyh.domain.bo.GrabPeriodBo;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodVo;
import com.ruoyi.zlyyh.mapper.GrabPeriodMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IGrabPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 秒杀配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class GrabPeriodServiceImpl implements IGrabPeriodService {

    private final GrabPeriodMapper baseMapper;

    /**
     * 查询秒杀配置
     */
    @Override
    public GrabPeriodVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询秒杀配置列表
     */
    @Override
    public TableDataInfo<GrabPeriodVo> queryPageList(GrabPeriodBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<GrabPeriod> lqw = buildQueryWrapper(bo);
        Page<GrabPeriodVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询秒杀配置列表
     */
    @Override
    public List<GrabPeriodVo> queryList(GrabPeriodBo bo) {
        LambdaQueryWrapper<GrabPeriod> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<GrabPeriod> buildQueryWrapper(GrabPeriodBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<GrabPeriod> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != bo.getId(), GrabPeriod::getId, bo.getId());
        lqw.like(StringUtils.isNotBlank(bo.getGrabPeriodName()), GrabPeriod::getGrabPeriodName, bo.getGrabPeriodName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), GrabPeriod::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getTopBjImg()), GrabPeriod::getTopBjImg, bo.getTopBjImg());
        lqw.eq(StringUtils.isNotBlank(bo.getSellStartTime()), GrabPeriod::getSellStartTime, bo.getSellStartTime());
        lqw.eq(StringUtils.isNotBlank(bo.getSellEndTime()), GrabPeriod::getSellEndTime, bo.getSellEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getDateType()), GrabPeriod::getDateType, bo.getDateType());
        lqw.eq(StringUtils.isNotBlank(bo.getDateList()), GrabPeriod::getDateList, bo.getDateList());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), GrabPeriod::getDescription, bo.getDescription());
        lqw.eq(bo.getPlatformKey() != null, GrabPeriod::getPlatformKey, bo.getPlatformKey());
        lqw.between(params.get("beginStartDate") != null && params.get("endStartDate") != null,
            GrabPeriod::getStartDate, params.get("beginStartDate"), params.get("endStartDate"));
        lqw.between(params.get("beginEndDate") != null && params.get("endEndDate") != null,
            GrabPeriod::getEndDate, params.get("beginEndDate"), params.get("endEndDate"));
        return lqw;
    }

    /**
     * 新增秒杀配置
     */
    @Override
    public Boolean insertByBo(GrabPeriodBo bo) {
        GrabPeriod add = BeanUtil.toBean(bo, GrabPeriod.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改秒杀配置
     */
    @CacheEvict(cacheNames = CacheNames.GRAB_PERIOD, key = "#bo.getId()")
    @Override
    public Boolean updateByBo(GrabPeriodBo bo) {
        GrabPeriod update = BeanUtil.toBean(bo, GrabPeriod.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(GrabPeriod entity) {
        if (StringUtils.isNotBlank(entity.getSellStartTime()) && entity.getSellStartTime().length() < 7) {
            entity.setSellStartTime(entity.getSellStartTime() + ":00");
        }
        if (StringUtils.isNotBlank(entity.getSellEndTime()) && entity.getSellEndTime().length() < 7) {
            entity.setSellEndTime(entity.getSellEndTime() + ":00");
        }
        PermissionUtils.setPlatformDeptIdAndUserId(entity, entity.getPlatformKey(), null == entity.getId(), false);
    }

    /**
     * 批量删除秒杀配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.GRAB_PERIOD, id);
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
