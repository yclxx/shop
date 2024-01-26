package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.MissionGroupProduct;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.vo.MissionGroupProductVo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.enumd.DateType;
import com.ruoyi.zlyyh.mapper.MissionGroupProductMapper;
import com.ruoyi.zlyyh.mapper.MissionMapper;
import com.ruoyi.zlyyh.mapper.MissionUserRecordMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhmobile.service.IMissionUserDrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MissionUserDrawServiceImpl implements IMissionUserDrawService {
    private final MissionGroupProductMapper missionGroupProductMapper;
    private final MissionMapper missionMapper;
    private final MissionUserRecordMapper missionUserRecordMapper;

    @Override
    public String sendDrawCount(Long userId, Long productId,String exProductId) {
        MissionVo missionVo = null;
        if(NumberUtil.isLong(exProductId)){
            missionVo = missionMapper.selectVoById(Long.valueOf(exProductId));
        }

        if(null == missionVo){
            List<MissionGroupProductVo> missionGroupProductVos = missionGroupProductMapper.selectVoList(new LambdaQueryWrapper<MissionGroupProduct>().eq(MissionGroupProduct::getProductId, productId));
            if (ObjectUtil.isEmpty(missionGroupProductVos)) {
                throw new ServiceException("未找到产品对应任务信息");
            }
            MissionGroupProductVo missionGroupProductVo = missionGroupProductVos.get(missionGroupProductVos.size() - 1);
            if (null != missionGroupProductVo.getMissionId()) {
                missionVo = missionMapper.selectVoById(missionGroupProductVo.getMissionId());
            }
        }
        if (null == missionVo) {
            throw new ServiceException("任务不存在");
        }
        return sendRecord(missionVo, userId).toString();
    }

    public Long sendRecord(MissionVo missionVo, Long userId) {
        if ("0".equals(missionVo.getMissionAwardType())) {
            MissionUserRecord add = new MissionUserRecord();
            add.setMissionId(missionVo.getMissionId());
            add.setMissionGroupId(missionVo.getMissionGroupId());
            add.setMissionUserId(userId);
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
                        if (ObjectUtil.isNotEmpty(missionVo.getMissionTime()) && missionVo.getMissionTime().contains("-")){
                            String[] split = missionVo.getMissionTime().split("-");
                            if (split.length == 2) {
                                String str2 = split[1];
                                if (StringUtils.isNotBlank(str2) && str2.length() > 4) {
                                    // 结束时间
                                    expiryDate = DateUtils.parseDate(DateUtil.today() + " " + str2);
                                }
                            }
                        }else {
                            expiryDate = DateUtil.endOfDay(new Date()).offset(DateField.MILLISECOND, -999).toJdkDate();
                        }
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
            boolean flag = missionUserRecordMapper.insert(add) > 0;
            if (!flag) {
                log.error("赠送用户抽奖机会失败，活动用户Id：{}，任务信息：{}", userId, missionVo);
            }
            return add.getMissionUserRecordId();
        }
        throw new ServiceException("暂不支持该奖励类型");
    }

    private Long getUserRecordCount(Long userId, Long missionGroupId, DateType dateType) {
        LambdaQueryWrapper<MissionUserRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionUserRecord::getMissionUserId, userId);
        lqw.eq(MissionUserRecord::getMissionGroupId, missionGroupId);
        lqw.isNotNull(MissionUserRecord::getCreateTime);
        if (null != dateType) {
            if (dateType == DateType.DAY) {
                lqw.apply("date(create_time) = curdate()");
            } else if (dateType == DateType.WEEK) {
                lqw.apply("YEARWEEK(date_format(create_time,'%Y-%m-%d'),1) = YEARWEEK(now(),1)");
            } else if (dateType == DateType.MONTH) {
                lqw.apply("DATE_FORMAT(create_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')");
            }
        }
        return missionUserRecordMapper.selectCount(lqw);
    }
}
