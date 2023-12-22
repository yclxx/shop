package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ShareUser;
import com.ruoyi.zlyyh.domain.ShareUserAccount;
import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.ShareUserAccountMapper;
import com.ruoyi.zlyyh.mapper.ShareUserMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IShareUserService;
import com.ruoyi.zlyyhadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分销员Service业务层处理
 *
 * @author yzg
 * @date 2023-11-09
 */
@RequiredArgsConstructor
@Service
public class ShareUserServiceImpl implements IShareUserService {

    private final ShareUserMapper baseMapper;
    private final ShareUserAccountMapper shareUserAccountMapper;
    private final IUserService userService;

    /**
     * 查询分销员
     */
    @Override
    public ShareUserVo queryById(Long userId) {
        return baseMapper.selectVoById(userId);
    }

    /**
     * 查询分销员列表
     */
    public TableDataInfo<ShareUserVo> queryPageList(ShareUserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShareUser> lqw = buildQueryWrapper(bo);
        if (null == lqw) {
            return TableDataInfo.build(new ArrayList<>());
        }
        Page<ShareUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<ShareUserVo> build = TableDataInfo.build(result);
        for (ShareUserVo shareUserVo : build.getRows()) {
            UserVo userVo = userService.queryById(shareUserVo.getUserId());
            if (null != userVo) {
                shareUserVo.setUserMobile(userVo.getMobile());
            }
        }
        return build;
    }

    /**
     * 查询分销员列表
     */
    @Override
    public List<ShareUserVo> queryList(ShareUserBo bo) {
        LambdaQueryWrapper<ShareUser> lqw = buildQueryWrapper(bo);
        if (null == lqw) {
            return new ArrayList<>();
        }
        List<ShareUserVo> shareUserVos = baseMapper.selectVoList(lqw);
        for (ShareUserVo shareUserVo : shareUserVos) {
            UserVo userVo = userService.queryById(shareUserVo.getUserId());
            if (null != userVo) {
                shareUserVo.setUserMobile(userVo.getMobile());
            }
        }
        return shareUserVos;
    }

    private LambdaQueryWrapper<ShareUser> buildQueryWrapper(ShareUserBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShareUser> lqw = Wrappers.lambdaQuery();
        if (null != bo.getUserId()) {
            if (bo.getUserId().toString().length() == 11) {
                UserBo userBo = new UserBo();
                userBo.setMobile(bo.getUserId().toString());
                List<UserVo> userVos = userService.queryList(userBo);
                if (ObjectUtil.isEmpty(userVos)) {
                    return null;
                }
                lqw.in(ShareUser::getUserId, userVos.stream().map(UserVo::getUserId).collect(Collectors.toSet()));
            } else {
                lqw.eq(ShareUser::getUserId, bo.getUserId());
            }
        }
        lqw.like(StringUtils.isNotBlank(bo.getBusinessDistrictName()), ShareUser::getBusinessDistrictName, bo.getBusinessDistrictName());
        lqw.like(StringUtils.isNotBlank(bo.getCommercialTenantName()), ShareUser::getCommercialTenantName, bo.getCommercialTenantName());
        lqw.like(StringUtils.isNotBlank(bo.getShopName()), ShareUser::getShopName, bo.getShopName());
        lqw.eq(StringUtils.isNotBlank(bo.getUpMobile()), ShareUser::getUpMobile, bo.getUpMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ShareUser::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditStatus()), ShareUser::getAuditStatus, bo.getAuditStatus());
        lqw.eq(bo.getParentId() != null, ShareUser::getParentId, bo.getParentId());
        lqw.eq(bo.getPlatformKey() != null, ShareUser::getPlatformKey, bo.getPlatformKey());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            ShareUser::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增分销员
     */
    @Transactional
    @Override
    public Boolean insertByBo(ShareUserBo bo) {
        ShareUser add = BeanUtil.toBean(bo, ShareUser.class);
        validEntityBeforeSave(add);
        PermissionUtils.setPlatformDeptIdAndUserId(add, bo.getPlatformKey(), true, true);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUserId(add.getUserId());
            ShareUserAccount shareUserAccount = new ShareUserAccount();
            shareUserAccount.setUserId(add.getUserId());
            int insert = shareUserAccountMapper.insert(shareUserAccount);
            if (insert < 1) {
                throw new ServiceException("操作失败");
            }
        }
        return flag;
    }

    /**
     * 修改分销员
     */
    @Override
    public Boolean updateByBo(ShareUserBo bo) {
        ShareUser update = BeanUtil.toBean(bo, ShareUser.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShareUser entity) {
        if (null != entity.getParentId()) {
            ShareUserVo shareUserVo = baseMapper.selectVoById(entity.getParentId());
            if (null != shareUserVo) {
                entity.setBusinessDistrictName(shareUserVo.getBusinessDistrictName());
            }
        }
        if (StringUtils.isNotBlank(entity.getUpMobile()) && entity.getUpMobile().contains("*")) {
            entity.setUpMobile(null);
        }
        if (StringUtils.isNotBlank(entity.getUserName()) && entity.getUserName().contains("*")) {
            entity.setUserName(null);
        }
    }

    /**
     * 批量删除分销员
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
