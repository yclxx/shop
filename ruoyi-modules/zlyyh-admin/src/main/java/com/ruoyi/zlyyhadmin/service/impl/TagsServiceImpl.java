package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhadmin.service.ITagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.TagsBo;
import com.ruoyi.zlyyh.domain.vo.TagsVo;
import com.ruoyi.zlyyh.domain.Tags;
import com.ruoyi.zlyyh.mapper.TagsMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 标签Service业务层处理
 *
 * @author yzg
 * @date 2023-10-09
 */
@RequiredArgsConstructor
@Service
public class TagsServiceImpl implements ITagsService {

    private final TagsMapper baseMapper;

    /**
     * 查询标签
     */
    @Override
    public TagsVo queryById(Long tagsId){
        return baseMapper.selectVoById(tagsId);
    }

    /**
     * 查询标签列表
     */
    @Override
    public TableDataInfo<TagsVo> queryPageList(TagsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Tags> lqw = buildQueryWrapper(bo);
        Page<TagsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询标签列表
     */
    @Override
    public List<TagsVo> queryList(TagsBo bo) {
        LambdaQueryWrapper<Tags> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Tags> buildQueryWrapper(TagsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Tags> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTagsName()), Tags::getTagsName, bo.getTagsName());
        lqw.eq(StringUtils.isNotBlank(bo.getTagsType()), Tags::getTagsType, bo.getTagsType());
        return lqw;
    }

    /**
     * 新增标签
     */
    @Override
    public Boolean insertByBo(TagsBo bo) {
        Tags add = BeanUtil.toBean(bo, Tags.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTagsId(add.getTagsId());
        }
        return flag;
    }

    /**
     * 修改标签
     */
    @Override
    public Boolean updateByBo(TagsBo bo) {
        Tags update = BeanUtil.toBean(bo, Tags.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Tags entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除标签
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
