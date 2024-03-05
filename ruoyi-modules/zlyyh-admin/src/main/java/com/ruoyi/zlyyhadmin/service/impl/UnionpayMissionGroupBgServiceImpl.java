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
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionGroupBgBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionGroupBgVo;
import com.ruoyi.zlyyh.domain.UnionpayMissionGroupBg;
import com.ruoyi.zlyyh.mapper.UnionpayMissionGroupBgMapper;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionGroupBgService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 任务组背景Service业务层处理
 *
 * @author yzg
 * @date 2024-03-02
 */
@RequiredArgsConstructor
@Service
public class UnionpayMissionGroupBgServiceImpl implements IUnionpayMissionGroupBgService {

    private final UnionpayMissionGroupBgMapper baseMapper;

    /**
     * 查询任务组背景
     */
    @Override
    public UnionpayMissionGroupBgVo queryById(Long missionBgId){
        return baseMapper.selectVoById(missionBgId);
    }

    /**
     * 查询任务组背景列表
     */
    @Override
    public TableDataInfo<UnionpayMissionGroupBgVo> queryPageList(UnionpayMissionGroupBgBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<UnionpayMissionGroupBg> lqw = buildQueryWrapper(bo);
        Page<UnionpayMissionGroupBgVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询任务组背景列表
     */
    @Override
    public List<UnionpayMissionGroupBgVo> queryList(UnionpayMissionGroupBgBo bo) {
        LambdaQueryWrapper<UnionpayMissionGroupBg> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UnionpayMissionGroupBg> buildQueryWrapper(UnionpayMissionGroupBgBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UnionpayMissionGroupBg> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getMissionGroupId() != null, UnionpayMissionGroupBg::getMissionGroupId, bo.getMissionGroupId());
        lqw.eq(StringUtils.isNotBlank(bo.getBgImg()), UnionpayMissionGroupBg::getBgImg, bo.getBgImg());
        lqw.eq(StringUtils.isNotBlank(bo.getImgType()), UnionpayMissionGroupBg::getImgType, bo.getImgType());
        lqw.eq(bo.getPlatformKey() != null, UnionpayMissionGroupBg::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), UnionpayMissionGroupBg::getStatus, bo.getStatus());
        lqw.eq(bo.getSort() != null, UnionpayMissionGroupBg::getSort, bo.getSort());
        return lqw;
    }

    /**
     * 新增任务组背景
     */
    @Override
    public Boolean insertByBo(UnionpayMissionGroupBgBo bo) {
        UnionpayMissionGroupBg add = BeanUtil.toBean(bo, UnionpayMissionGroupBg.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMissionBgId(add.getMissionBgId());
        }
        return flag;
    }

    /**
     * 修改任务组背景
     */
    @Override
    public Boolean updateByBo(UnionpayMissionGroupBgBo bo) {
        UnionpayMissionGroupBg update = BeanUtil.toBean(bo, UnionpayMissionGroupBg.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UnionpayMissionGroupBg entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除任务组背景
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
