package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.MissionUser;
import com.ruoyi.zlyyh.domain.bo.MissionUserBo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupVo;
import com.ruoyi.zlyyh.domain.vo.MissionUserVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.MissionGroupMapper;
import com.ruoyi.zlyyh.mapper.MissionUserMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IMissionUserService;
import com.ruoyi.zlyyhmobile.service.IPlatformService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 任务用户Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class MissionUserServiceImpl implements IMissionUserService {

    private final MissionUserMapper baseMapper;
    private final MissionGroupMapper missionGroupMapper;
    private final IUserService userService;
    private final IPlatformService platformService;

    /**
     * 查询任务用户
     */
    @Override
    public MissionUserVo queryById(Long missionUserId) {
        return baseMapper.selectVoById(missionUserId);
    }

    /**
     * 查询任务用户
     */
    @Cacheable(cacheNames = CacheNames.MISSION_USER, key = "#missionGroupId+'-'+#userId")
    @Override
    public MissionUserVo queryByUserIdAndGroupId(Long missionGroupId, Long userId, Long platformKey) {
        LambdaQueryWrapper<MissionUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionUser::getMissionGroupId, missionGroupId);
        lqw.eq(MissionUser::getUserId, userId);
        lqw.eq(MissionUser::getPlatformKey, platformKey);

        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 新增任务用户
     */
    @CacheEvict(cacheNames = CacheNames.MISSION_USER, key = "#bo.getMissionGroupId()+'-'+#bo.getUserId()")
    @Override
    public Boolean insertByBo(MissionUserBo bo) {
        MissionUser add = BeanUtil.toBean(bo, MissionUser.class);
        UserVo userVo = userService.queryById(bo.getUserId(), PlatformEnumd.MP_YSF.getChannel());
        if (null == userVo || StringUtils.isEmpty(userVo.getOpenId())) {
            throw new ServiceException("登录超时，请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        // 查询是否在活动城市
        MissionGroupVo missionGroupVo = missionGroupMapper.selectVoById(bo.getMissionGroupId());
        if (null == missionGroupVo || !"0".equals(missionGroupVo.getStatus())) {
            throw new ServiceException("任务不存在或已结束");
        }
        if (null != missionGroupVo.getEndDate() && DateUtils.compare(missionGroupVo.getEndDate()) < 0) {
            throw new ServiceException("任务已结束");
        }
        PlatformVo platformVo = platformService.queryById(missionGroupVo.getPlatformKey(), ZlyyhUtils.getPlatformChannel());
        if (null == platformVo) {
            throw new ServiceException("平台信息错误");
        }
        ZlyyhUtils.checkCity(missionGroupVo.getShowCity(), platformVo);
        PermissionUtils.setPlatformDeptIdAndUserId(add, missionGroupVo.getPlatformKey(), true, false);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMissionUserId(add.getMissionUserId());
        }
        // 校验是否已经报名过了
        MissionUserVo missionUserVo = this.queryByUserIdAndGroupId(bo.getMissionGroupId(), bo.getUserId(), bo.getPlatformKey());
        if (null != missionUserVo) {
            // 请求接口报名
            R<Void> r = YsfUtils.registerMission(userVo.getOpenId(), platformVo.getAppId(), platformVo.getSecret(), platformVo.getPlatformKey());
            if (R.isError(r)) {
                throw new ServiceException("报名失败[" + r.getMsg() + "]");
            }
            return true;
        }
        return flag;
    }

    /**
     * 查询任务用户列表
     */
    @Override
    public TableDataInfo<MissionUserVo> queryPageList(Long missionGroupId, PageQuery pageQuery) {
        LambdaQueryWrapper<MissionUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionUser::getMissionGroupId, missionGroupId);
        lqw.eq(MissionUser::getStatus, "0");
        Page<MissionUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }
}
