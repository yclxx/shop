package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.GrabPeriodProduct;
import com.ruoyi.zlyyh.domain.bo.GrabPeriodProductBo;
import com.ruoyi.zlyyh.domain.vo.GrabPeriodProductVo;
import com.ruoyi.zlyyh.mapper.GrabPeriodProductMapper;
import com.ruoyi.zlyyhmobile.service.IGrabPeriodProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 秒杀商品配置Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class GrabPeriodProductServiceImpl implements IGrabPeriodProductService {

    private final GrabPeriodProductMapper baseMapper;

    /**
     * 查询秒杀商品配置
     */
    @Override
    public GrabPeriodProductVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询秒杀商品配置列表
     */
    @Override
    public TableDataInfo<GrabPeriodProductVo> queryPageList(GrabPeriodProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<GrabPeriodProduct> lqw = buildQueryWrapper(bo);
        Page<GrabPeriodProductVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询秒杀商品配置列表
     */
    @Override
    public List<GrabPeriodProductVo> queryList(GrabPeriodProductBo bo) {
        LambdaQueryWrapper<GrabPeriodProduct> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<GrabPeriodProduct> buildQueryWrapper(GrabPeriodProductBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<GrabPeriodProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getGrabPeriodId() != null, GrabPeriodProduct::getGrabPeriodId, bo.getGrabPeriodId());
        lqw.eq(bo.getProductId() != null, GrabPeriodProduct::getProductId, bo.getProductId());
        lqw.eq(bo.getSort() != null, GrabPeriodProduct::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增秒杀商品配置
     */
    @Override
    public Boolean insertByBo(GrabPeriodProductBo bo) {
        GrabPeriodProduct add = BeanUtil.toBean(bo, GrabPeriodProduct.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改秒杀商品配置
     */
    @Override
    public Boolean updateByBo(GrabPeriodProductBo bo) {
        GrabPeriodProduct update = BeanUtil.toBean(bo, GrabPeriodProduct.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(GrabPeriodProduct entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除秒杀商品配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
