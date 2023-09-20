package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Code;
import com.ruoyi.zlyyh.domain.bo.CodeBo;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyh.mapper.CodeMapper;
import com.ruoyi.zlyyhadmin.service.ICodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品券码Service业务层处理
 *
 * @author yzg
 * @date 2023-09-20
 */
@RequiredArgsConstructor
@Service
public class CodeServiceImpl implements ICodeService {

    private final CodeMapper baseMapper;

    /**
     * 查询商品券码
     */
    @Override
    public CodeVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询商品券码列表
     */
    @Override
    public TableDataInfo<CodeVo> queryPageList(CodeBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Code> lqw = buildQueryWrapper(bo);
        Page<CodeVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商品券码列表
     */
    @Override
    public List<CodeVo> queryList(CodeBo bo) {
        LambdaQueryWrapper<Code> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Code> buildQueryWrapper(CodeBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProductId() != null, Code::getProductId, bo.getProductId());
        lqw.eq(bo.getProductSessionId() != null, Code::getProductSessionId, bo.getProductSessionId());
        lqw.eq(bo.getProductSkuId() != null, Code::getProductSkuId, bo.getProductSkuId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), Code::getProductName, bo.getProductName());
        lqw.like(StringUtils.isNotBlank(bo.getProductSessionName()), Code::getProductSessionName, bo.getProductSessionName());
        lqw.like(StringUtils.isNotBlank(bo.getProductSkuName()), Code::getProductSkuName, bo.getProductSkuName());
        lqw.eq(StringUtils.isNotBlank(bo.getCodeNo()), Code::getCodeNo, bo.getCodeNo());
        lqw.eq(StringUtils.isNotBlank(bo.getAllocationState()), Code::getAllocationState, bo.getAllocationState());
        lqw.eq(bo.getNumber() != null, Code::getNumber, bo.getNumber());
        lqw.eq(StringUtils.isNotBlank(bo.getUsedStatus()), Code::getUsedStatus, bo.getUsedStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getCodeType()), Code::getCodeType, bo.getCodeType());
        lqw.eq(bo.getUsedTime() != null, Code::getUsedTime, bo.getUsedTime());
        lqw.eq(bo.getShopId() != null, Code::getShopId, bo.getShopId());
        lqw.like(StringUtils.isNotBlank(bo.getShopName()), Code::getShopName, bo.getShopName());
        lqw.eq(bo.getVerifierId() != null, Code::getVerifierId, bo.getVerifierId());
        lqw.eq(StringUtils.isNotBlank(bo.getVerifierMobile()), Code::getVerifierMobile, bo.getVerifierMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getQrcodeImgUrl()), Code::getQrcodeImgUrl, bo.getQrcodeImgUrl());
        lqw.eq(bo.getAppointmentShopId() != null, Code::getAppointmentShopId, bo.getAppointmentShopId());
        lqw.like(StringUtils.isNotBlank(bo.getAppointmentShopName()), Code::getAppointmentShopName, bo.getAppointmentShopName());
        lqw.eq(bo.getAppointmentDate() != null, Code::getAppointmentDate, bo.getAppointmentDate());
        lqw.eq(StringUtils.isNotBlank(bo.getAppointmentStatus()), Code::getAppointmentStatus, bo.getAppointmentStatus());
        lqw.eq(bo.getAppointmentId() != null, Code::getAppointmentId, bo.getAppointmentId());
        lqw.eq(bo.getSysDeptId() != null, Code::getSysDeptId, bo.getSysDeptId());
        lqw.eq(bo.getSysUserId() != null, Code::getSysUserId, bo.getSysUserId());
        return lqw;
    }

    /**
     * 新增商品券码
     */
    @Override
    public Boolean insertByBo(CodeBo bo) {
        Code add = BeanUtil.toBean(bo, Code.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商品券码
     */
    @Override
    public Boolean updateByBo(CodeBo bo) {
        Code update = BeanUtil.toBean(bo, Code.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Code entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除商品券码
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
