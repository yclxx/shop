package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Supplier;
import com.ruoyi.zlyyh.domain.bo.SupplierBo;
import com.ruoyi.zlyyh.domain.vo.SupplierVo;
import com.ruoyi.zlyyh.mapper.SupplierMapper;
import com.ruoyi.zlyyhadmin.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 供应商Service业务层处理
 *
 * @author yzg
 * @date 2023-10-11
 */
@RequiredArgsConstructor
@Service
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierMapper baseMapper;

    /**
     * 查询供应商
     */
    @Override
    public SupplierVo queryById(Long supplierId){
        return baseMapper.selectVoById(supplierId);
    }

    /**
     * 查询供应商列表
     */
    @Override
    public TableDataInfo<SupplierVo> queryPageList(SupplierBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Supplier> lqw = buildQueryWrapper(bo);
        Page<SupplierVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询供应商列表
     */
    @Override
    public List<SupplierVo> queryList(SupplierBo bo) {
        LambdaQueryWrapper<Supplier> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Supplier> buildQueryWrapper(SupplierBo bo) {
        LambdaQueryWrapper<Supplier> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getSupplierName()), Supplier::getSupplierName, bo.getSupplierName());
        lqw.eq(StringUtils.isNotBlank(bo.getLinkman()), Supplier::getLinkman, bo.getLinkman());
        lqw.eq(StringUtils.isNotBlank(bo.getMobile()), Supplier::getMobile, bo.getMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getInvoiceType()), Supplier::getInvoiceType, bo.getInvoiceType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Supplier::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getWarning()), Supplier::getWarning, bo.getWarning());
        return lqw;
    }

    /**
     * 新增供应商
     */
    @Override
    public Boolean insertByBo(SupplierBo bo) {
        Supplier add = BeanUtil.toBean(bo, Supplier.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setSupplierId(add.getSupplierId());
        }
        return flag;
    }

    /**
     * 修改供应商
     */
    @Override
    public Boolean updateByBo(SupplierBo bo) {
        Supplier update = BeanUtil.toBean(bo, Supplier.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Supplier entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除供应商
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
