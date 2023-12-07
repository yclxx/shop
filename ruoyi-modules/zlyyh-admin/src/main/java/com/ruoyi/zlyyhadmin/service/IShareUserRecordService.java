package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;

import java.util.Collection;
import java.util.List;

/**
 * 分销记录Service接口
 *
 * @author yzg
 * @date 2023-11-09
 */
public interface IShareUserRecordService {

    /**
     * 查询分销记录
     */
    ShareUserRecordVo queryById(Long recordId);

    /**
     * 查询分销记录列表
     */
    TableDataInfo<ShareUserRecordVo> queryPageList(ShareUserRecordBo bo, PageQuery pageQuery);

    /**
     * 查询分销记录列表
     */
    List<ShareUserRecordVo> queryList(ShareUserRecordBo bo);

    /**
     * 修改分销记录
     */
    Boolean insertByBo(ShareUserRecordBo bo);

    /**
     * 修改分销记录
     */
    Boolean updateByBo(ShareUserRecordBo bo);

    /**
     * 校验并批量删除分销记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
