package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IPlatformUserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.PlatformUserGroupBo;
import com.ruoyi.zlyyh.domain.vo.PlatformUserGroupVo;
import com.ruoyi.zlyyh.domain.PlatformUserGroup;
import com.ruoyi.zlyyh.mapper.PlatformUserGroupMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 平台城市企业微信用户来源Service业务层处理
 *
 * @author yzg
 * @date 2024-03-06
 */
@RequiredArgsConstructor
@Service
public class PlatformUserGroupServiceImpl implements IPlatformUserGroupService {

    private final PlatformUserGroupMapper baseMapper;

    /**
     * 查询平台城市企业微信用户来源
     */
    @Override
    public PlatformUserGroupVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询平台城市企业微信用户来源列表
     */
    @Override
    public TableDataInfo<PlatformUserGroupVo> queryPageList(PlatformUserGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PlatformUserGroup> lqw = buildQueryWrapper(bo);
        Page<PlatformUserGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询平台城市企业微信用户来源列表
     */
    @Override
    public List<PlatformUserGroupVo> queryList(PlatformUserGroupBo bo) {
        LambdaQueryWrapper<PlatformUserGroup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PlatformUserGroup> buildQueryWrapper(PlatformUserGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PlatformUserGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, PlatformUserGroup::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), PlatformUserGroup::getType, bo.getType());
        lqw.eq(bo.getUserId() != null, PlatformUserGroup::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PlatformUserGroup::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增平台城市企业微信用户来源
     */
    @Override
    public Boolean insertByBo(PlatformUserGroupBo bo) {
        PlatformUserGroup add = BeanUtil.toBean(bo, PlatformUserGroup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改平台城市企业微信用户来源
     */
    @Override
    public Boolean updateByBo(PlatformUserGroupBo bo) {
        PlatformUserGroup update = BeanUtil.toBean(bo, PlatformUserGroup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(PlatformUserGroup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除平台城市企业微信用户来源
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
