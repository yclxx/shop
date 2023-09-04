package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.RecordLog;
import com.ruoyi.zlyyh.domain.vo.RecordLogVo;
import org.apache.ibatis.annotations.Param;

/**
 * 记录日志Mapper接口
 *
 * @author yzg
 * @date 2023-08-01
 */
public interface RecordLogMapper extends BaseMapperPlus<RecordLogMapper, RecordLog, RecordLogVo> {
    RecordLog selectRecord(@Param("platformKey") Long platformKey, @Param("source") String source, @Param("recordDate") String recordDate);
}
