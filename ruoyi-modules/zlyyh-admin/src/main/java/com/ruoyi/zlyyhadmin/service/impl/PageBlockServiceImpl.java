package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.IPageBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.PageBlockBo;
import com.ruoyi.zlyyh.domain.vo.PageBlockVo;
import com.ruoyi.zlyyh.domain.PageBlock;
import com.ruoyi.zlyyh.mapper.PageBlockMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 版块模板Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class PageBlockServiceImpl implements IPageBlockService {

    private final PageBlockMapper baseMapper;

    /**
     * 查询版块模板
     */
    @Override
    public PageBlockVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询版块模板列表
     */
    @Override
    public TableDataInfo<PageBlockVo> queryPageList(PageBlockBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PageBlock> lqw = buildQueryWrapper(bo);
        Page<PageBlockVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询版块模板列表
     */
    @Override
    public List<PageBlockVo> queryList(PageBlockBo bo) {
        LambdaQueryWrapper<PageBlock> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<PageBlock> buildQueryWrapper(PageBlockBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<PageBlock> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBlockName()), PageBlock::getBlockName, bo.getBlockName());
        lqw.like(StringUtils.isNotBlank(bo.getMainField()), PageBlock::getMainField,bo.getMainField());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), PageBlock::getStatus, bo.getStatus());
        lqw.eq(bo.getSort() != null, PageBlock::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增版块模板
     */
    @Override
    public Boolean insertByBo(PageBlockBo bo) {
        PageBlock add = BeanUtil.toBean(bo, PageBlock.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改版块模板
     */
    @Override
    public Boolean updateByBo(PageBlockBo bo) {
        PageBlock update = BeanUtil.toBean(bo, PageBlock.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除版块模板
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
