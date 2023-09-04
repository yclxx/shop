package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.DrawBo;
import com.ruoyi.zlyyh.domain.vo.DrawVo;

import java.util.Collection;
import java.util.List;

/**
 * 奖品管理Service接口
 *
 * @author yzg
 * @date 2023-05-10
 */
public interface IDrawService {

    /**
     * 查询奖品管理
     */
    DrawVo queryById(Long drawId);

    /**
     * 查询奖品管理列表
     */
    TableDataInfo<DrawVo> queryPageList(DrawBo bo, PageQuery pageQuery);

    /**
     * 查询奖品管理列表
     */
    List<DrawVo> queryList(DrawBo bo);

    /**
     * 修改奖品管理
     */
    Boolean insertByBo(DrawBo bo);

    /**
     * 修改奖品管理
     */
    Boolean updateByBo(DrawBo bo);

    /**
     * 校验并批量删除奖品管理信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
