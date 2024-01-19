package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.FileImportLogBo;
import com.ruoyi.zlyyh.domain.vo.FileImportLogVo;
import com.ruoyi.zlyyh.domain.FileImportLog;
import com.ruoyi.zlyyh.mapper.FileImportLogMapper;
import com.ruoyi.zlyyhadmin.service.IFileImportLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 文件导入记录Service业务层处理
 *
 * @author yzg
 * @date 2024-01-04
 */
@RequiredArgsConstructor
@Service
public class FileImportLogServiceImpl implements IFileImportLogService {

    private final FileImportLogMapper baseMapper;

    /**
     * 查询文件导入记录
     */
    @Override
    public FileImportLogVo queryById(Long importLogId){
        return baseMapper.selectVoById(importLogId);
    }

    /**
     * 查询文件导入记录列表
     */
    @Override
    public TableDataInfo<FileImportLogVo> queryPageList(FileImportLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<FileImportLog> lqw = buildQueryWrapper(bo);
        Page<FileImportLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询文件导入记录列表
     */
    @Override
    public List<FileImportLogVo> queryList(FileImportLogBo bo) {
        LambdaQueryWrapper<FileImportLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<FileImportLog> buildQueryWrapper(FileImportLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<FileImportLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getUrl()), FileImportLog::getUrl, bo.getUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getName()), FileImportLog::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getPageTitle()), FileImportLog::getPageTitle, bo.getPageTitle());
        lqw.eq(bo.getCount() != null, FileImportLog::getCount, bo.getCount());
        return lqw;
    }

    /**
     * 新增文件导入记录
     */
    @Override
    public Boolean insertByBo(FileImportLogBo bo) {
        FileImportLog add = BeanUtil.toBean(bo, FileImportLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setImportLogId(add.getImportLogId());
        }
        return flag;
    }

    /**
     * 修改文件导入记录
     */
    @Override
    public Boolean updateByBo(FileImportLogBo bo) {
        FileImportLog update = BeanUtil.toBean(bo, FileImportLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(FileImportLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除文件导入记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
