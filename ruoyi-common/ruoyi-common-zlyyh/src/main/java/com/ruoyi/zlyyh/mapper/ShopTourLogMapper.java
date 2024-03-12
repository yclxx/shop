package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.ShopTourLog;
import com.ruoyi.zlyyh.domain.bo.ShopTourLogBo;
import com.ruoyi.zlyyh.domain.vo.ShopTourLogVo;
import org.apache.ibatis.annotations.Param;

/**
 * 巡检记录Mapper接口
 *
 * @author yzg
 * @date 2024-03-06
 */
public interface ShopTourLogMapper extends BaseMapperPlus<ShopTourLogMapper, ShopTourLog, ShopTourLogVo> {

    Page<ShopTourLogVo> getInvalidTourList(@Param("bo") ShopTourLogBo bo, Page page);
}
