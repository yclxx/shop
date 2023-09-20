package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.Code;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyh.domain.bo.CodeBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商品券码Service接口
 *
 * @author yzg
 * @date 2023-09-20
 */
public interface ICodeService {

    /**
     * 查询商品券码
     */
    CodeVo queryById(Long id);

    /**
     * 查询商品券码列表
     */
    TableDataInfo<CodeVo> queryPageList(CodeBo bo, PageQuery pageQuery);

    /**
     * 查询商品券码列表
     */
    List<CodeVo> queryList(CodeBo bo);

    /**
     * 修改商品券码
     */
    Boolean insertByBo(CodeBo bo);

    /**
     * 修改商品券码
     */
    Boolean updateByBo(CodeBo bo);

    /**
     * 校验并批量删除商品券码信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
