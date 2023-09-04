package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.MissionUserRecord;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 活动记录Mapper接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface MissionUserRecordMapper extends BaseMapperPlus<MissionUserRecordMapper, MissionUserRecord, MissionUserRecordVo> {

    /**
     * 查询任务发放额度
     *
     * @param wrapper 查询条件
     * @return 任务发放额度
     */
    BigDecimal sumMissionQuota(@Param(Constants.WRAPPER) Wrapper<MissionUserRecord> wrapper);
}
