package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.PageBlockColumn;
import com.ruoyi.zlyyh.domain.vo.PageBlockColumnVo;
import com.ruoyi.zlyyh.domain.bo.PageBlockColumnBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 版块模板字段Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IPageBlockColumnService {

    /**
     * 查询版块模板字段
     */
    PageBlockColumnVo queryById(Long columnId);

    /**
     * 查询版块模板字段列表
     */
    TableDataInfo<PageBlockColumnVo> queryPageList(PageBlockColumnBo bo, PageQuery pageQuery);

    /**
     * 查询版块模板字段列表
     */
    List<PageBlockColumnVo> queryList(PageBlockColumnBo bo);

    /**
     * 修改版块模板字段
     */
    Boolean insertByBo(PageBlockColumnBo bo);

    /**
     * 修改版块模板字段
     */
    Boolean updateByBo(PageBlockColumnBo bo);

    /**
     * 校验并批量删除版块模板字段信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 查询模板字段
     * @param blockId 模板编号
     */
    List<PageBlockColumnVo> selectListByBlockId(Long blockId);
}
