package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.YsfConfig;
import com.ruoyi.zlyyh.domain.vo.YsfConfigVo;
import org.apache.ibatis.annotations.Param;

/**
 * 云闪付参数配置Mapper接口
 *
 * @author yzg
 * @date 2023-07-31
 */
public interface YsfConfigMapper extends BaseMapperPlus<YsfConfigMapper, YsfConfig, YsfConfigVo> {
    String queryValueByKey(@Param("platformId") Long platformId, @Param("key") String key);
}
