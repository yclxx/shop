package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.ShareUser;
import com.ruoyi.zlyyh.domain.ShareUserAccount;
import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserAccountVo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import com.ruoyi.zlyyh.mapper.ShareUserAccountMapper;
import com.ruoyi.zlyyh.mapper.ShareUserMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhmobile.service.IShareUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    @Override
    public List<ShareUserVo> queryList(ShareUserBo bo) {
        LambdaQueryWrapper<ShareUser> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ShareUser> buildQueryWrapper(ShareUserBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShareUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, ShareUser::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getBusinessDistrictName()), ShareUser::getBusinessDistrictName, bo.getBusinessDistrictName());
        lqw.like(StringUtils.isNotBlank(bo.getCommercialTenantName()), ShareUser::getCommercialTenantName, bo.getCommercialTenantName());
        lqw.like(StringUtils.isNotBlank(bo.getShopName()), ShareUser::getShopName, bo.getShopName());
        lqw.eq(StringUtils.isNotBlank(bo.getUpMobile()), ShareUser::getUpMobile, bo.getUpMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ShareUser::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditStatus()), ShareUser::getAuditStatus, bo.getAuditStatus());
        lqw.eq(bo.getParentId() != null, ShareUser::getParentId, bo.getParentId());
        lqw.eq(bo.getPlatformKey() != null, ShareUser::getPlatformKey, bo.getPlatformKey());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null, ShareUser::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增分销员
     */
    @Transactional
    @Override
    public Boolean insertByBo(ShareUserBo bo) {
        if (ObjectUtil.isNull(bo.getUserId())) {
            return false;
        }
        if (ObjectUtil.isNull(bo.getPlatformKey())) {
            return false;
        }
        ShareUserVo shareUserVo = baseMapper.selectVoById(bo.getUserId());
        if (null != shareUserVo) {
            if ("1".equals(shareUserVo.getAuditStatus())) {
                throw new ServiceException("不可重复申请");
            } else if ("0".equals(shareUserVo.getAuditStatus())) {
                throw new ServiceException("审核中");
            }
        }
        ShareUser add = BeanUtil.toBean(bo, ShareUser.class);
        add.setAuditStatus("0");
        PermissionUtils.setPlatformDeptIdAndUserId(add, bo.getPlatformKey(), true, true);
        boolean b = baseMapper.insertOrUpdate(add);
        if (!b) {
            throw new ServiceException("操作失败");
        }
        ShareUserAccountVo shareUserAccountVo = shareUserAccountMapper.selectVoById(bo.getUserId());
        if (null == shareUserAccountVo) {
            ShareUserAccount shareUserAccount = new ShareUserAccount();
            shareUserAccount.setUserId(bo.getUserId());
            int insert1 = shareUserAccountMapper.insert(shareUserAccount);
            if (insert1 < 1) {
                throw new ServiceException("操作失败");
            }
        }
        return true;
    }
}
