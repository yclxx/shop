package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.SendDyInfo;
import com.ruoyi.zlyyh.domain.bo.SendDyInfoBo;
import com.ruoyi.zlyyh.domain.vo.SendDyInfoVo;
import com.ruoyi.zlyyh.mapper.SendDyInfoMapper;
import com.ruoyi.zlyyhmobile.service.ISendDyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 用户订阅Service业务层处理
 *
 * @author yzg
 * @date 2023-12-07
 */
@RequiredArgsConstructor
@Service
public class SendDyInfoServiceImpl implements ISendDyInfoService {

    private final SendDyInfoMapper baseMapper;

    /**
     * 查询用户订阅
     */
    @Override
    public SendDyInfoVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询用户订阅列表
     */
    @Override
    public TableDataInfo<SendDyInfoVo> queryPageList(SendDyInfoBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SendDyInfo> lqw = buildQueryWrapper(bo);
        Page<SendDyInfoVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询用户订阅列表
     */
    @Override
    public List<SendDyInfoVo> queryList(SendDyInfoBo bo) {
        LambdaQueryWrapper<SendDyInfo> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SendDyInfo> buildQueryWrapper(SendDyInfoBo bo) {
        LambdaQueryWrapper<SendDyInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, SendDyInfo::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getUserId() != null, SendDyInfo::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getTmplId()), SendDyInfo::getTmplId, bo.getTmplId());
        return lqw;
    }

    /**
     * 新增用户订阅
     */
    @Override
    public Boolean insertByBo(SendDyInfoBo bo) {
        SendDyInfo add = BeanUtil.toBean(bo, SendDyInfo.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户订阅
     */
    @Override
    public Boolean updateByBo(SendDyInfoBo bo) {
        SendDyInfo update = BeanUtil.toBean(bo, SendDyInfo.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SendDyInfo entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除用户订阅
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
