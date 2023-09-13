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
import com.ruoyi.zlyyh.domain.MissionGroup;
import com.ruoyi.zlyyh.domain.bo.MissionGroupBo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupVo;
import com.ruoyi.zlyyh.mapper.MissionGroupMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IMissionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 任务组Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class MissionGroupServiceImpl implements IMissionGroupService {

    private final MissionGroupMapper baseMapper;

    /**
     * 查询任务组
     */
    @Override
    public MissionGroupVo queryById(Long missionGroupId) {
        return baseMapper.selectVoById(missionGroupId);
    }

    /**
     * 查询任务组列表
     */
    @Override
    public TableDataInfo<MissionGroupVo> queryPageList(MissionGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MissionGroup> lqw = buildQueryWrapper(bo);
        Page<MissionGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询任务组列表
     */
    @Override
    public List<MissionGroupVo> queryList(MissionGroupBo bo) {
        LambdaQueryWrapper<MissionGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MissionGroup> buildQueryWrapper(MissionGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MissionGroup> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getMissionGroupName()), MissionGroup::getMissionGroupName, bo.getMissionGroupName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), MissionGroup::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, MissionGroup::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }

    /**
     * 新增任务组
     */
    @Override
    public Boolean insertByBo(MissionGroupBo bo) {
        MissionGroup add = BeanUtil.toBean(bo, MissionGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMissionGroupId(add.getMissionGroupId());
        }
        return flag;
    }

    /**
     * 修改任务组
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_GROUP, key = "#bo.getMissionGroupId()")
    @Override
    public Boolean updateByBo(MissionGroupBo bo) {
        MissionGroup update = BeanUtil.toBean(bo, MissionGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MissionGroup entity) {
        PermissionUtils.setPlatformDeptIdAndUserId(entity, entity.getPlatformKey(), null == entity.getMissionGroupId());
    }

    /**
     * 批量删除任务组
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.MISSION_GROUP, id);
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
