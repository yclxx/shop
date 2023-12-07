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
import com.ruoyi.zlyyh.domain.ShareUserAccount;
import com.ruoyi.zlyyh.domain.ShareUserRecord;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.ShareUserAccountMapper;
import com.ruoyi.zlyyh.mapper.ShareUserRecordMapper;
import com.ruoyi.zlyyhadmin.service.IShareUserRecordService;
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
 * 分销记录Service业务层处理
 *
 * @author yzg
 * @date 2023-11-09
 */
@RequiredArgsConstructor
@Service
public class ShareUserRecordServiceImpl implements IShareUserRecordService {

    private final ShareUserRecordMapper baseMapper;
    private final ShareUserAccountMapper shareUserAccountMapper;
    private final IUserService userService;

    /**
     * 查询分销记录
     */
    @Override
    public ShareUserRecordVo queryById(Long recordId) {
        return baseMapper.selectVoById(recordId);
    }

    /**
     * 查询分销记录列表
     */
    @Override
    public TableDataInfo<ShareUserRecordVo> queryPageList(ShareUserRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ShareUserRecord> lqw = buildQueryWrapper(bo);
        if (null == lqw) {
            return TableDataInfo.build(new ArrayList<>());
        }
        Page<ShareUserRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        this.setUserMobile(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询分销记录列表
     */
    @Override
    public List<ShareUserRecordVo> queryList(ShareUserRecordBo bo) {
        LambdaQueryWrapper<ShareUserRecord> lqw = buildQueryWrapper(bo);
        if (null == lqw) {
            return new ArrayList<>();
        }
        List<ShareUserRecordVo> shareUserRecordVos = baseMapper.selectVoList(lqw);
        this.setUserMobile(shareUserRecordVos);
        return shareUserRecordVos;
    }

    private void setUserMobile(List<ShareUserRecordVo> shareUserRecordVos) {
        for (ShareUserRecordVo shareUserRecordVo : shareUserRecordVos) {
            if (null != shareUserRecordVo.getUserId()) {
                UserVo userVo = userService.queryById(shareUserRecordVo.getUserId());
                if (null != userVo) {
                    shareUserRecordVo.setUserMobile(userVo.getMobile());
                }
            }
            if (null != shareUserRecordVo.getInviteeUserId()) {
                UserVo userVo = userService.queryById(shareUserRecordVo.getInviteeUserId());
                if (null != userVo) {
                    shareUserRecordVo.setInviteeUserMobile(userVo.getMobile());
                }
            }
        }
    }

    private LambdaQueryWrapper<ShareUserRecord> buildQueryWrapper(ShareUserRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ShareUserRecord> lqw = Wrappers.lambdaQuery();
        if (null != bo.getUserId()) {
            if (bo.getUserId().toString().length() == 11) {
                UserBo userBo = new UserBo();
                userBo.setMobile(bo.getUserId().toString());
                List<UserVo> userVos = userService.queryList(userBo);
                if (ObjectUtil.isEmpty(userVos)) {
                    return null;
                }
                lqw.in(ShareUserRecord::getUserId, userVos.stream().map(UserVo::getUserId).collect(Collectors.toSet()));
            } else {
                lqw.eq(ShareUserRecord::getUserId, bo.getUserId());
            }
        }
        if (null != bo.getInviteeUserId()) {
            if (bo.getInviteeUserId().toString().length() == 11) {
                UserBo userBo = new UserBo();
                userBo.setMobile(bo.getInviteeUserId().toString());
                List<UserVo> userVos = userService.queryList(userBo);
                if (ObjectUtil.isEmpty(userVos)) {
                    return null;
                }
                lqw.in(ShareUserRecord::getInviteeUserId, userVos.stream().map(UserVo::getUserId).collect(Collectors.toSet()));
            } else {
                lqw.eq(ShareUserRecord::getInviteeUserId, bo.getInviteeUserId());
            }
        }
        lqw.eq(bo.getNumber() != null, ShareUserRecord::getNumber, bo.getNumber());
        lqw.eq(bo.getOrderUsedTime() != null, ShareUserRecord::getOrderUsedTime, bo.getOrderUsedTime());
        lqw.eq(bo.getAwardAmount() != null, ShareUserRecord::getAwardAmount, bo.getAwardAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getInviteeStatus()), ShareUserRecord::getInviteeStatus, bo.getInviteeStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAwardStatus()), ShareUserRecord::getAwardStatus, bo.getAwardStatus());
        lqw.eq(bo.getAwardTime() != null, ShareUserRecord::getAwardTime, bo.getAwardTime());
        lqw.eq(StringUtils.isNotBlank(bo.getAwardAccount()), ShareUserRecord::getAwardAccount, bo.getAwardAccount());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            ShareUserRecord::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增分销记录
     */
    @Override
    public Boolean insertByBo(ShareUserRecordBo bo) {
        ShareUserRecord add = BeanUtil.toBean(bo, ShareUserRecord.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setRecordId(add.getRecordId());
        }
        return flag;
    }

    /**
     * 修改分销记录
     */
    @Transactional
    @Override
    public Boolean updateByBo(ShareUserRecordBo bo) {
        ShareUserRecord update = BeanUtil.toBean(bo, ShareUserRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ShareUserRecord entity) {
        ShareUserAccount shareUserAccount = shareUserAccountMapper.selectById(entity.getUserId());
        if (null == shareUserAccount) {
            throw new ServiceException("分销用户账户不存在");
        }
        // 操作邀请人用户账户
        if (null != entity.getRecordId()) {
            ShareUserRecordVo shareUserRecordVo = baseMapper.selectVoById(entity.getRecordId());
            if (null == shareUserRecordVo) {
                return;
            }
            if (shareUserRecordVo.getInviteeStatus().equals(entity.getInviteeStatus())) {
                return;
            }
            if (entity.getInviteeStatus().equals("0") || entity.getInviteeStatus().equals("1")) {
                return;
            }
            if ("3".equals(entity.getInviteeStatus()) || "4".equals(entity.getInviteeStatus()) || "5".equals(entity.getInviteeStatus())) {
                shareUserAccount.setFreezeBalance(shareUserAccount.getFreezeBalance().subtract(entity.getAwardAmount()));
                shareUserAccountMapper.updateById(shareUserAccount);
            }
        }
    }

    /**
     * 批量删除分销记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
