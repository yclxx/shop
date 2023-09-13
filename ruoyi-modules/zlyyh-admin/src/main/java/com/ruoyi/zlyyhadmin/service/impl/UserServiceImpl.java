package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.Platform;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.PlatformMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户信息Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper baseMapper;
    private final PlatformMapper platformMapper;

    /**
     * 查询用户信息
     */
    @Override
    public UserVo queryById(Long userId) {
        return baseMapper.selectVoById(userId);
    }

    /**
     * 查询用户信息列表
     */
    @Override
    public TableDataInfo<UserVo> queryPageList(UserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<User> lqw = buildQueryWrapper(bo);
        if (StringUtils.isNotBlank(bo.getMobile())) {
            List<User> users = baseMapper.selectListIncludeMobile(lqw, new User(bo.getMobile()));
            return TableDataInfo.build(BeanCopyUtils.copyList(users, UserVo.class));
        } else {
            Page<User> result = baseMapper.selectPageIncludeMobile(pageQuery.build(), lqw, new User());
            return TableDataInfo.build(result, UserVo.class);
        }
    }

    /**
     * 查询用户信息列表
     */
    @Override
    public List<UserVo> queryList(UserBo bo) {
        LambdaQueryWrapper<User> lqw = buildQueryWrapper(bo);
        List<User> users = baseMapper.selectListIncludeMobile(lqw, new User(bo.getMobile()));
        List<UserVo> userVos = BeanCopyUtils.copyList(users, UserVo.class);
        if (CollectionUtils.isNotEmpty(userVos)) {
            for (UserVo userVo : userVos) {
                Platform platform = platformMapper.selectById(userVo.getPlatformKey());
                if (null != platform) {
                    userVo.setPlatformName(platform.getPlatformName());
                }
            }
        }
        return userVos;
    }

    private LambdaQueryWrapper<User> buildQueryWrapper(UserBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(null != bo.getUserId(), User::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), User::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getUserImg()), User::getUserImg, bo.getUserImg());
        lqw.like(StringUtils.isNotBlank(bo.getOpenId()), User::getOpenId, bo.getOpenId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), User::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getReloadUser()), User::getReloadUser, bo.getReloadUser());
        lqw.eq(StringUtils.isNotBlank(bo.getVipUser()), User::getVipUser, bo.getVipUser());
        lqw.like(StringUtils.isNotBlank(bo.getRegisterCityName()), User::getRegisterCityName, bo.getRegisterCityName());
        lqw.eq(StringUtils.isNotBlank(bo.getRegisterCityCode()), User::getRegisterCityCode, bo.getRegisterCityCode());
        lqw.eq(StringUtils.isNotBlank(bo.getFollowStatus()), User::getFollowStatus, bo.getFollowStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getLoginIp()), User::getLoginIp, bo.getLoginIp());
        lqw.eq(bo.getLoginDate() != null, User::getLoginDate, bo.getLoginDate());
        lqw.like(StringUtils.isNotBlank(bo.getLoginCityName()), User::getLoginCityName, bo.getLoginCityName());
        lqw.eq(StringUtils.isNotBlank(bo.getLoginCityCode()), User::getLoginCityCode, bo.getLoginCityCode());
        lqw.eq(bo.getPlatformKey() != null, User::getPlatformKey, bo.getPlatformKey());
        lqw.isNotNull(StringUtils.isNotBlank(bo.getMobile()), User::getMobile);
        return lqw;
    }

    /**
     * 新增用户信息
     */
    @Override
    public Boolean insertByBo(UserBo bo) {
        User add = BeanUtil.toBean(bo, User.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUserId(add.getUserId());
        }
        return flag;
    }

    /**
     * 修改用户信息
     */
    @Override
    public Boolean updateByBo(UserBo bo) {
        User update = BeanUtil.toBean(bo, User.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(User entity) {
        PermissionUtils.setPlatformDeptIdAndUserId(entity, entity.getPlatformKey(), null == entity.getUserId());
    }

    /**
     * 批量删除用户信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
