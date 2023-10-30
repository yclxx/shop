package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.MissionUser;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.bo.MissionUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import com.ruoyi.zlyyh.domain.vo.MissionUserVo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.mapper.MissionMapper;
import com.ruoyi.zlyyh.mapper.MissionUserMapper;
import com.ruoyi.zlyyh.mapper.MissionUserRecordMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IMissionUserRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动记录Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class MissionUserRecordServiceImpl implements IMissionUserRecordService {

    private final MissionUserRecordMapper baseMapper;
    private final MissionMapper missionMapper;
    private final MissionUserMapper missionUserMapper;
    private final UserMapper userMapper;

    /**
     * 查询活动记录
     */
    @Override
    public MissionUserRecordVo queryById(Long missionUserRecordId) {
        return baseMapper.selectVoById(missionUserRecordId);
    }

    /**
     * 查询活动记录列表
     */
    @Override
    public TableDataInfo<MissionUserRecordVo> queryPageList(MissionUserRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MissionUserRecord> lqw = buildQueryWrapper(bo);
        Page<MissionUserRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询活动记录列表
     */
    @Override
    public List<MissionUserRecordVo> queryList(MissionUserRecordBo bo) {
        LambdaQueryWrapper<MissionUserRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MissionUserRecord> buildQueryWrapper(MissionUserRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
        if (bo.getMissionUserId() != null) {
            List<Long> missionUserIds = getMissionUserIds(bo.getMissionUserId(), null, bo.getMissionGroupId());
            if (ObjectUtil.isEmpty(missionUserIds)) {
                lqw.eq(MissionUserRecord::getMissionUserId, bo.getMissionUserId());
            } else {
                lqw.in(MissionUserRecord::getMissionUserId, missionUserIds);
            }
        }
        lqw.eq(bo.getMissionGroupId() != null, MissionUserRecord::getMissionGroupId, bo.getMissionGroupId());
        lqw.eq(bo.getMissionId() != null, MissionUserRecord::getMissionId, bo.getMissionId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), MissionUserRecord::getStatus, bo.getStatus());
        lqw.between(params.get("beginExpiryTime") != null && params.get("endExpiryTime") != null,
            MissionUserRecord::getExpiryTime, params.get("beginExpiryTime"), params.get("endExpiryTime"));
        lqw.eq(bo.getDrawId() != null, MissionUserRecord::getDrawId, bo.getDrawId());
        lqw.eq(StringUtils.isNotBlank(bo.getDrawType()), MissionUserRecord::getDrawType, bo.getDrawType());
        lqw.like(StringUtils.isNotBlank(bo.getDrawName()), MissionUserRecord::getDrawName, bo.getDrawName());
        lqw.eq(StringUtils.isNotBlank(bo.getSendStatus()), MissionUserRecord::getSendStatus, bo.getSendStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getSendAccount()), MissionUserRecord::getSendAccount, bo.getSendAccount());
        lqw.eq(StringUtils.isNotBlank(bo.getPushNumber()), MissionUserRecord::getPushNumber, bo.getPushNumber());
        lqw.between(params.get("beginDrawTime") != null && params.get("endDrawTime") != null,
            MissionUserRecord::getDrawTime, params.get("beginDrawTime"), params.get("endDrawTime"));
        return lqw;
    }

    /**
     * 新增活动记录
     */
    @Override
    public Boolean insertByBo(MissionUserRecordBo bo) {
        if (null == bo.getMissionId() || null == bo.getMissionUserId()) {
            throw new ServiceException("缺少必要参数");
        }
        MissionVo missionVo = missionMapper.selectVoById(bo.getMissionId());
        if (null == missionVo || !"0".equals(missionVo.getStatus())) {
            throw new ServiceException("任务不存在或已停用");
        }
        List<Long> missionUserIds = getMissionUserIds(bo.getMissionUserId(), missionVo.getPlatformKey(), missionVo.getMissionGroupId());
        if (ObjectUtil.isEmpty(missionUserIds)) {
            throw new ServiceException("用户信息不存在");
        }
        if (missionUserIds.size() != 1) {
            throw new ServiceException("用户信息存在多条，不可操作");
        }
        MissionUserRecord add = new MissionUserRecord();
        add.setMissionId(bo.getMissionId());
        add.setMissionGroupId(missionVo.getMissionGroupId());
        add.setMissionUserId(missionUserIds.get(0));
        Date expiryDate = null;
        if ("1".equals(missionVo.getAwardExpiryType())) {
            try {
                expiryDate = DateUtil.parseDate(missionVo.getAwardExpiryDate()).toJdkDate();
            } catch (Exception ignored) {
            }
        } else if ("2".equals(missionVo.getAwardExpiryType())) {
            if (NumberUtil.isLong(missionVo.getAwardExpiryDate())) {
                int addDay = Integer.parseInt(missionVo.getAwardExpiryDate());
                if (addDay < 1) {
                    // TODO 填写当天的结束时间
                    expiryDate = DateUtil.endOfDay(new Date()).offset(DateField.MILLISECOND, -999).toJdkDate();
                } else {
                    expiryDate = DateUtil.endOfDay(DateUtil.offsetDay(new Date(), addDay)).offset(DateField.MILLISECOND, -999).toJdkDate();
                }
            }
        } else if ("3".equals(missionVo.getAwardExpiryType())) {
            String format = DateUtil.format(DateUtil.offsetMonth(new Date(), 1), DatePattern.NORM_MONTH_PATTERN);
            format = format + missionVo.getAwardExpiryDate();
            try {
                expiryDate = DateUtil.parseDate(format).toJdkDate();
            } catch (Exception ignored) {
            }
        }
        add.setExpiryTime(expiryDate);
        PermissionUtils.setPlatformDeptIdAndUserId(add, missionVo.getPlatformKey(), true, false);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMissionUserRecordId(add.getMissionUserRecordId());
        }
        return flag;
    }

    /**
     * 修改活动记录
     */
    @Override
    public Boolean expiry(Collection<Long> ids) {
        MissionUserRecord add = new MissionUserRecord();
        add.setStatus("3");
        return baseMapper.update(add, new LambdaQueryWrapper<MissionUserRecord>().in(MissionUserRecord::getMissionUserRecordId, ids).eq(MissionUserRecord::getStatus, "0")) > 0;
    }

    private List<Long> getMissionUserIds(Long missionUserId, Long platformKey, Long missionGroupId) {
        if (null == missionUserId) {
            return new ArrayList<>();
        }
        if (missionUserId.toString().length() == 11) {
            List<User> users = userMapper.selectListIncludeMobile(new LambdaQueryWrapper<User>()
                .eq(null != platformKey, User::getPlatformKey, platformKey).isNotNull(User::getMobile), new User(missionUserId.toString()));
            if (ObjectUtil.isEmpty(users)) {
                return new ArrayList<>();
            }
            LambdaQueryWrapper<MissionUser> lqw = Wrappers.lambdaQuery();
            lqw.eq(missionGroupId != null, MissionUser::getMissionGroupId, missionGroupId);
            lqw.eq(platformKey != null, MissionUser::getPlatformKey, platformKey);
            lqw.in(MissionUser::getUserId, users.stream().map(User::getUserId).collect(Collectors.toSet()));
            List<MissionUserVo> missionUserVos = missionUserMapper.selectVoList(lqw);
            if (ObjectUtil.isNotEmpty(missionUserVos)) {
                return missionUserVos.stream().map(MissionUserVo::getMissionUserId).collect(Collectors.toList());
            }
        } else {
            MissionUserVo missionUserVo = missionUserMapper.selectVoById(missionUserId);
            if (null != missionUserVo) {
                List<Long> missionUserIds = new ArrayList<>();
                missionUserIds.add(missionGroupId);
                return missionUserIds;
            }
            UserVo userVo = userMapper.selectVoById(missionUserId);
            if (null != userVo) {
                LambdaQueryWrapper<MissionUser> lqw = Wrappers.lambdaQuery();
                lqw.eq(missionGroupId != null, MissionUser::getMissionGroupId, missionGroupId);
                lqw.eq(platformKey != null, MissionUser::getPlatformKey, platformKey);
                lqw.eq(MissionUser::getUserId, userVo.getUserId());
                List<MissionUserVo> missionUserVos = missionUserMapper.selectVoList(lqw);
                if (ObjectUtil.isNotEmpty(missionUserVos)) {
                    return missionUserVos.stream().map(MissionUserVo::getMissionUserId).collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }
}
