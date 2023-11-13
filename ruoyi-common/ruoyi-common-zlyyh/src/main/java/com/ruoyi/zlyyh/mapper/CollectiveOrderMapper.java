package com.ruoyi.zlyyh.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.CollectiveOrder;
import com.ruoyi.zlyyh.domain.vo.CollectiveOrderVo;
import org.apache.ibatis.annotations.Param;

/**
 * 大订单Mapper接口
 *
 * @author yzg
 * @date 2023-10-16
 */
public interface CollectiveOrderMapper extends BaseMapperPlus<CollectiveOrderMapper, CollectiveOrder, CollectiveOrderVo> {


    /**
     * 物理删除大订单信息
     */
    Long deleteByCollectiveNumber(@Param("collectiveNumber") String collectiveNumber);
}
