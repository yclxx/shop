package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.ShareUser;
import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import com.ruoyi.zlyyh.mapper.ShareUserMapper;
import com.ruoyi.zlyyhadmin.service.IShareUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

    /**
     * 查询分销员
     */
    @Override
    public ShareUserVo queryById(Long userId){
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
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            ShareUser::getCreateTime ,params.get("beginCreateTime"), params.get("endCreateTime"));
        return lqw;
    }

    /**
     * 新增分销员
     */
    @Override
    public Boolean insertByBo(ShareUserBo bo) {
        ShareUser add = BeanUtil.toBean(bo, ShareUser.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUserId(add.getUserId());
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
    private void validEntityBeforeSave(ShareUser entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除分销员
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
