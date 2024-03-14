package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.UnionpayMissionUserLog;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserLogBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserLogVo;
import org.apache.ibatis.annotations.Param;

/**
 * 银联任务奖励发放记录Mapper接口
 *
 * @author yzg
 * @date 2024-02-21
 */
public interface UnionpayMissionUserLogMapper extends BaseMapperPlus<UnionpayMissionUserLogMapper, UnionpayMissionUserLog, UnionpayMissionUserLogVo> {

    /**
     * 查询当前周数量
     */
    Long selectListByWeek(@Param("bo") UnionpayMissionUserLogBo bo);

    /**
     * 查询天周数量
     */
    Long selectListByToday(@Param("bo") UnionpayMissionUserLogBo logBo);
}
