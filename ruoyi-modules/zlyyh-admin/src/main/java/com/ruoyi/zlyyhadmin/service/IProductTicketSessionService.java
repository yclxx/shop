package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ProductTicketSessionBo;
import com.ruoyi.zlyyh.domain.vo.ProductTicketSessionVo;

import java.util.Collection;
import java.util.List;

/**
 * 演出场次Service接口
 *
 * @author yzg
 * @date 2023-09-12
 */
public interface IProductTicketSessionService {

    /**
     * 查询演出场次
     */
    ProductTicketSessionVo queryById(Long priceId);

    /**
     * 查询演出场次列表
     */
    TableDataInfo<ProductTicketSessionVo> queryPageList(ProductTicketSessionBo bo, PageQuery pageQuery);

    /**
     * 查询演出场次列表
     */
    List<ProductTicketSessionVo> queryList(ProductTicketSessionBo bo);

    /**
     * 查询演出场次与票种列表
     */
    List<ProductTicketSessionVo> queryLists(ProductTicketSessionBo bo);

    /**
     * 修改演出场次
     */
    Boolean insertByBo(ProductTicketSessionBo bo);

    /**
     * 插入新场次与票种
     */
    void insertByBoList(List<ProductTicketSessionBo> bos, Long productId);

    /**
     * 修改演出场次
     */
    Boolean updateByBo(ProductTicketSessionBo bo);

    /**
     * 修改演出场次
     */
    Boolean updateByBoList(List<ProductTicketSessionBo> bo,Long productId);

    /**
     * 校验并批量删除演出场次信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
