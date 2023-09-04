package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ProductComputeDayBo;
import com.ruoyi.zlyyh.domain.vo.ProductComputeDayVo;

import java.util.Collection;
import java.util.List;

/**
 * 订单数据统计（每天）Service接口
 *
 * @author yzg
 * @date 2023-07-12
 */
public interface IProductComputeDayService {

    /**
     * 查询订单数据统计（每天）
     */
    ProductComputeDayVo queryById(Long id);

    /**
     * 查询订单数据统计（每天）列表
     */
    TableDataInfo<ProductComputeDayVo> queryPageList(ProductComputeDayBo bo, PageQuery pageQuery);

    /**
     * 查询订单数据统计（每天）列表
     */
    List<ProductComputeDayVo> queryList(ProductComputeDayBo bo);

    /**
     * 修改订单数据统计（每天）
     */
    Boolean insertByBo(ProductComputeDayBo bo);

    /**
     * 修改订单数据统计（每天）
     */
    Boolean updateByBo(ProductComputeDayBo bo);

    /**
     * 校验并批量删除订单数据统计（每天）信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
