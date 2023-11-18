package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Verifier;
import com.ruoyi.zlyyh.domain.bo.VerifierBo;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import com.ruoyi.zlyyh.mapper.VerifierMapper;
import com.ruoyi.zlyyhadmin.service.IVerifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 核销人员管理Service业务层处理
 *
 * @author yzg
 * @date 2023-11-06
 */
@RequiredArgsConstructor
@Service
public class VerifierServiceImpl implements IVerifierService {

    private final VerifierMapper baseMapper;

    /**
     * 查询核销人员管理
     */
    @Override
    public VerifierVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询核销人员管理列表
     */
    @Override
    public TableDataInfo<VerifierVo> queryPageList(VerifierBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Verifier> lqw = buildQueryWrapper(bo);
        Page<VerifierVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询核销人员管理列表
     */
    @Override
    public List<VerifierVo> queryList(VerifierBo bo) {
        LambdaQueryWrapper<Verifier> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Verifier> buildQueryWrapper(VerifierBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Verifier> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, Verifier::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getMobile()), Verifier::getMobile, bo.getMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getVerifierType()), Verifier::getVerifierType, bo.getVerifierType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Verifier::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getOpenId()), Verifier::getOpenId, bo.getOpenId());
        return lqw;
    }

    /**
     * 新增核销人员管理
     */
    @Override
    public Boolean insertByBo(VerifierBo bo) {
        Verifier add = BeanUtil.toBean(bo, Verifier.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改核销人员管理
     */
    @CacheEvict(cacheNames = CacheNames.M_VERIFIER, key = "#bo.getId()")
    @Override
    public Boolean updateByBo(VerifierBo bo) {
        Verifier update = BeanUtil.toBean(bo, Verifier.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Verifier entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除核销人员管理
     */
    @CacheEvict(cacheNames = CacheNames.M_VERIFIER, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
