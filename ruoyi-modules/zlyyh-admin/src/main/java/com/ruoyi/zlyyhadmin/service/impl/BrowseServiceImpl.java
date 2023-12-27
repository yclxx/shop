package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Browse;
import com.ruoyi.zlyyh.domain.bo.BrowseBo;
import com.ruoyi.zlyyh.domain.vo.BrowseVo;
import com.ruoyi.zlyyh.mapper.BrowseMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IBrowseService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 浏览任务Service业务层处理
 *
 * @author yzg
 * @date 2023-12-14
 */
@RequiredArgsConstructor
@Service
public class BrowseServiceImpl implements IBrowseService {

    private final BrowseMapper baseMapper;

    /**
     * 查询浏览任务
     */
    @Override
    public BrowseVo queryById(Long browseId) {
        return baseMapper.selectVoById(browseId);
    }

    /**
     * 查询浏览任务列表
     */
    @Override
    public TableDataInfo<BrowseVo> queryPageList(BrowseBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Browse> lqw = buildQueryWrapper(bo);
        Page<BrowseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询浏览任务列表
     */
    @Override
    public List<BrowseVo> queryList(BrowseBo bo) {
        LambdaQueryWrapper<Browse> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Browse> buildQueryWrapper(BrowseBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Browse> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBrowseName()), Browse::getBrowseName, bo.getBrowseName());
        lqw.eq(StringUtils.isNotBlank(bo.getToType()), Browse::getToType, bo.getToType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Browse::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, Browse::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getProductId() != null, Browse::getProductId, bo.getProductId());
        return lqw;
    }

    /**
     * 新增浏览任务
     */
    @CacheEvict(cacheNames = {CacheNames.BROWSELIST}, allEntries = true)
    @Override
    public Boolean insertByBo(BrowseBo bo) {
        Browse add = BeanUtil.toBean(bo, Browse.class);
        validEntityBeforeSave(add);
        PermissionUtils.setPlatformDeptIdAndUserId(add, bo.getPlatformKey(), true, false);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setBrowseId(add.getBrowseId());
        }
        return flag;
    }

    /**
     * 修改浏览任务
     */
    @CacheEvict(cacheNames = {CacheNames.BROWSELIST, CacheNames.BROWSEVO}, allEntries = true)
    @Override
    public Boolean updateByBo(BrowseBo bo) {
        Browse update = BeanUtil.toBean(bo, Browse.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Browse entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除浏览任务
     */
    @CacheEvict(cacheNames = {CacheNames.BROWSELIST, CacheNames.BROWSEVO}, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
