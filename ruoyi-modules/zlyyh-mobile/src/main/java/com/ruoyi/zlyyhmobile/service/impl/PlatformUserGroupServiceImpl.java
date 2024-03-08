package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.zlyyh.domain.PlatformUserGroup;
import com.ruoyi.zlyyh.domain.bo.PlatformUserGroupBo;
import com.ruoyi.zlyyh.mapper.PlatformUserGroupMapper;
import com.ruoyi.zlyyhmobile.service.IPlatformUserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 企业微信用户来源Service业务层处理
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@Service
public class PlatformUserGroupServiceImpl implements IPlatformUserGroupService {

    private final PlatformUserGroupMapper baseMapper;

    /**
     * 新增
     */
    @Override
    public void insert(PlatformUserGroupBo bo) {
        PlatformUserGroup platformUserGroup = BeanUtil.toBean(bo, PlatformUserGroup.class);
        baseMapper.insert(platformUserGroup);
    }
}
