package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.GrabPeriodProduct;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodProductVo;
import com.ruoyi.zlyyh.domain.bo.GrabPeriodProductBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 秒杀商品配置Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IGrabPeriodProductService {

    /**
     * 查询秒杀商品配置
     */
    GrabPeriodProductVo queryById(Long id);

    /**
     * 查询秒杀商品配置列表
     */
    TableDataInfo<GrabPeriodProductVo> queryPageList(GrabPeriodProductBo bo, PageQuery pageQuery);

    /**
     * 查询秒杀商品配置列表
     */
    List<GrabPeriodProductVo> queryList(GrabPeriodProductBo bo);

    /**
     * 修改秒杀商品配置
     */
    Boolean insertByBo(GrabPeriodProductBo bo);

    /**
     * 修改秒杀商品配置
     */
    Boolean updateByBo(GrabPeriodProductBo bo);

    /**
     * 校验并批量删除秒杀商品配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
