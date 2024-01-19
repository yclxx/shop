package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.FileImportLog;
import com.ruoyi.zlyyh.domain.vo.FileImportLogVo;
import com.ruoyi.zlyyh.domain.bo.FileImportLogBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 文件导入记录Service接口
 *
 * @author yzg
 * @date 2024-01-04
 */
public interface IFileImportLogService {

    /**
     * 查询文件导入记录
     */
    FileImportLogVo queryById(Long importLogId);

    /**
     * 查询文件导入记录列表
     */
    TableDataInfo<FileImportLogVo> queryPageList(FileImportLogBo bo, PageQuery pageQuery);

    /**
     * 查询文件导入记录列表
     */
    List<FileImportLogVo> queryList(FileImportLogBo bo);

    /**
     * 修改文件导入记录
     */
    Boolean insertByBo(FileImportLogBo bo);

    /**
     * 修改文件导入记录
     */
    Boolean updateByBo(FileImportLogBo bo);

    /**
     * 校验并批量删除文件导入记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
