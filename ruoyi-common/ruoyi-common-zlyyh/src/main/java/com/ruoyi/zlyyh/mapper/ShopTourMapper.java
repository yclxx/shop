package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ShopTour;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyh.domain.vo.ShopTourVo;
import org.apache.ibatis.annotations.Param;

/**
 * 巡检商户Mapper接口
 *
 * @author yzg
 * @date 2024-01-28
 */
public interface ShopTourMapper extends BaseMapperPlus<ShopTourMapper, ShopTour, ShopTourVo> {

    /**
     * 查询巡检商户列表
     */
    Page<ShopTourVo> queryPageTourList(@Param("bo") ShopTourBo bo,Page page);

    /**
     * 查询附近巡检商户列表
     */
    Page<ShopTourVo> queryPageNearTourList(@Param("bo")ShopTourBo bo, Page page);

    /**
     * 获取预约商户列表
     */
    Page<ShopTourVo> queryReserveShopList(@Param("bo")ShopTourBo bo, Page page);

}
