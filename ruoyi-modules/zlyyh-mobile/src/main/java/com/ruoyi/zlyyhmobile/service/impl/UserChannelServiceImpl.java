package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.UserChannel;
import com.ruoyi.zlyyh.domain.bo.UserChannelBo;
import com.ruoyi.zlyyh.domain.vo.UserChannelVo;
import com.ruoyi.zlyyh.mapper.UserChannelMapper;
import com.ruoyi.zlyyhmobile.service.IUserChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户渠道信息Service业务层处理
 *
 * @author yzg
 * @date 2023-10-13
 */
@RequiredArgsConstructor
@Service
public class UserChannelServiceImpl implements IUserChannelService {

    private final UserChannelMapper baseMapper;

    /**
     * 查询用户渠道信息
     */
    @Override
    public UserChannelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询用户渠道信息列表
     */
    @Override
    public TableDataInfo<UserChannelVo> queryPageList(UserChannelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<UserChannel> lqw = buildQueryWrapper(bo);
        Page<UserChannelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询用户渠道信息列表
     */
    @Override
    public List<UserChannelVo> queryList(UserChannelBo bo) {
        LambdaQueryWrapper<UserChannel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UserChannel> buildQueryWrapper(UserChannelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UserChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, UserChannel::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), UserChannel::getUserName, bo.getUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getUserImg()), UserChannel::getUserImg, bo.getUserImg());
        lqw.eq(StringUtils.isNotBlank(bo.getOpenId()), UserChannel::getOpenId, bo.getOpenId());
        lqw.eq(StringUtils.isNotBlank(bo.getReloadUser()), UserChannel::getReloadUser, bo.getReloadUser());
        lqw.like(StringUtils.isNotBlank(bo.getRegisterCityName()), UserChannel::getRegisterCityName, bo.getRegisterCityName());
        lqw.eq(StringUtils.isNotBlank(bo.getRegisterCityCode()), UserChannel::getRegisterCityCode, bo.getRegisterCityCode());
        lqw.eq(StringUtils.isNotBlank(bo.getFollowStatus()), UserChannel::getFollowStatus, bo.getFollowStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getLoginIp()), UserChannel::getLoginIp, bo.getLoginIp());
        lqw.eq(bo.getLoginDate() != null, UserChannel::getLoginDate, bo.getLoginDate());
        lqw.like(StringUtils.isNotBlank(bo.getLoginCityName()), UserChannel::getLoginCityName, bo.getLoginCityName());
        lqw.eq(StringUtils.isNotBlank(bo.getLoginCityCode()), UserChannel::getLoginCityCode, bo.getLoginCityCode());
        lqw.eq(bo.getLastLoginDate() != null, UserChannel::getLastLoginDate, bo.getLastLoginDate());
        lqw.eq(StringUtils.isNotBlank(bo.getChannel()), UserChannel::getChannel, bo.getChannel());
        return lqw;
    }

    /**
     * 新增用户渠道信息
     */
    @Override
    public Boolean insertByBo(UserChannelBo bo) {
        UserChannel add = BeanUtil.toBean(bo, UserChannel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户渠道信息
     */
    @Override
    public Boolean updateByBo(UserChannelBo bo) {
        UserChannel update = BeanUtil.toBean(bo, UserChannel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UserChannel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除用户渠道信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
