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
import com.ruoyi.zlyyh.domain.Mission;
import com.ruoyi.zlyyh.domain.bo.MissionBo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.mapper.MissionMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 任务配置Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class MissionServiceImpl implements IMissionService {

    private final MissionMapper baseMapper;

    /**
     * 查询任务配置
     */
    @Override
    public MissionVo queryById(Long missionId) {
        return baseMapper.selectVoById(missionId);
    }

    /**
     * 查询任务配置列表
     */
    @Override
    public TableDataInfo<MissionVo> queryPageList(MissionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Mission> lqw = buildQueryWrapper(bo);
        Page<MissionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询任务配置列表
     */
    @Override
    public List<MissionVo> queryList(MissionBo bo) {
        LambdaQueryWrapper<Mission> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Mission> buildQueryWrapper(MissionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Mission> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getMissionGroupId() != null, Mission::getMissionGroupId, bo.getMissionGroupId());
        lqw.like(StringUtils.isNotBlank(bo.getMissionName()), Mission::getMissionName, bo.getMissionName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Mission::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, Mission::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getMissionAffiliation()), Mission::getMissionAffiliation, bo.getMissionAffiliation());
        return lqw;
    }

    /**
     * 新增任务配置
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_LIST, allEntries = true)
    @Override
    public Boolean insertByBo(MissionBo bo) {
        Mission add = BeanUtil.toBean(bo, Mission.class);
        PermissionUtils.setPlatformDeptIdAndUserId(add, add.getPlatformKey(), true, false);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMissionId(add.getMissionId());
        }
        return flag;
    }

    /**
     * 修改任务配置
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_LIST, allEntries = true)
    @Override
    public Boolean updateByBo(MissionBo bo) {
        Mission update = BeanUtil.toBean(bo, Mission.class);
        PermissionUtils.setPlatformDeptIdAndUserId(update, update.getPlatformKey(), false, false);
        CacheUtils.evict(CacheNames.MISSION, bo.getMissionId());
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除任务配置
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_LIST, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.MISSION, id);
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
