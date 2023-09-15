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
import com.ruoyi.zlyyh.domain.Draw;
import com.ruoyi.zlyyh.domain.bo.DrawBo;
import com.ruoyi.zlyyh.domain.vo.DrawVo;
import com.ruoyi.zlyyh.mapper.DrawMapper;
import com.ruoyi.zlyyh.utils.DrawRedisCacheUtils;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IDrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 奖品管理Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class DrawServiceImpl implements IDrawService {

    private final DrawMapper baseMapper;

    /**
     * 查询奖品管理
     */
    @Override
    public DrawVo queryById(Long drawId) {
        return baseMapper.selectVoById(drawId);
    }

    /**
     * 查询奖品管理列表
     */
    @Override
    public TableDataInfo<DrawVo> queryPageList(DrawBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Draw> lqw = buildQueryWrapper(bo);
        Page<DrawVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询奖品管理列表
     */
    @Override
    public List<DrawVo> queryList(DrawBo bo) {
        LambdaQueryWrapper<Draw> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Draw> buildQueryWrapper(DrawBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Draw> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getDrawName())) {
            lqw.and(lw -> lw.like(Draw::getDrawName, bo.getDrawName()).or().like(Draw::getDrawSimpleName, bo.getDrawName()));
        }
        lqw.eq(null != bo.getMissionGroupId(), Draw::getMissionGroupId, bo.getMissionGroupId());
        lqw.eq(null != bo.getPlatformKey(), Draw::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Draw::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getDrawType()), Draw::getDrawType, bo.getDrawType());
        return lqw;
    }

    /**
     * 新增奖品管理
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_GROUP_DRAW, key = "#bo.getPlatformKey() + '-' + #bo.getMissionGroupId()")
    @Override
    public Boolean insertByBo(DrawBo bo) {
        Draw add = BeanUtil.toBean(bo, Draw.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setDrawId(add.getDrawId());
        }
        return flag;
    }

    /**
     * 修改奖品管理
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_GROUP_DRAW, key = "#bo.getPlatformKey() + '-' + #bo.getMissionGroupId()")
    @Override
    public Boolean updateByBo(DrawBo bo) {
        Draw update = BeanUtil.toBean(bo, Draw.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Draw entity) {
        // 删除奖品缓存
        DrawRedisCacheUtils.delDrawList(entity.getPlatformKey(), entity.getMissionGroupId());
        PermissionUtils.setPlatformDeptIdAndUserId(entity, entity.getPlatformKey(), null == entity.getDrawId(), false);
    }

    /**
     * 批量删除奖品管理
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        for (Long id : ids) {
            DrawVo drawVo = queryById(id);
            if (null != drawVo) {
                // 删除奖品缓存
                DrawRedisCacheUtils.delDrawList(drawVo.getPlatformKey(), drawVo.getMissionGroupId());
                String key = "" + drawVo.getPlatformKey() + '-' + drawVo.getMissionGroupId();
                CacheUtils.evict(CacheNames.MISSION_GROUP_DRAW, key);
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
