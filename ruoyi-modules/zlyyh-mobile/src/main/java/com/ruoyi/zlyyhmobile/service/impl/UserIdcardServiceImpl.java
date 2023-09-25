package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyhmobile.service.IUserIdcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.UserIdcardBo;
import com.ruoyi.zlyyh.domain.vo.UserIdcardVo;
import com.ruoyi.zlyyh.domain.UserIdcard;
import com.ruoyi.zlyyh.mapper.UserIdcardMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 观影用户信息Service业务层处理
 *
 * @author yzg
 * @date 2023-09-15
 */
@RequiredArgsConstructor
@Service
public class UserIdcardServiceImpl implements IUserIdcardService {

    private final UserIdcardMapper baseMapper;

    /**
     * 查询观影用户信息
     */
    @Override
    public UserIdcardVo queryById(Long userIdcardId) {
        return baseMapper.selectVoById(userIdcardId);
    }

    /**
     * 查询观影用户信息列表
     */
    @Override
    public TableDataInfo<UserIdcardVo> queryPageList(UserIdcardBo bo) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(20);
        LambdaQueryWrapper<UserIdcard> lqw = buildQueryWrapper(bo);
        Page<UserIdcardVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询观影用户信息列表
     */
    @Override
    public List<UserIdcardVo> queryList(UserIdcardBo bo) {
        LambdaQueryWrapper<UserIdcard> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询观影用户信息列表
     */
    @Override
    public List<UserIdcardVo> queryListByIds(List<Long> ids) {
        LambdaQueryWrapper<UserIdcard> lqw = Wrappers.lambdaQuery();
        lqw.in(UserIdcard::getUserIdcardId, ids);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UserIdcard> buildQueryWrapper(UserIdcardBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UserIdcard> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, UserIdcard::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), UserIdcard::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCardType()), UserIdcard::getCardType, bo.getCardType());
        lqw.eq(StringUtils.isNotBlank(bo.getIdCard()), UserIdcard::getIdCard, bo.getIdCard());
        return lqw;
    }

    /**
     * 新增观影用户信息
     */
    @Override
    public Boolean insertByBo(UserIdcardBo bo) {
        UserIdcard add = BeanUtil.toBean(bo, UserIdcard.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUserIdcardId(add.getUserIdcardId());
        }
        return flag;
    }

    /**
     * 修改观影用户信息
     */
    @Override
    public Boolean updateByBo(UserIdcardBo bo) {
        UserIdcard update = BeanUtil.toBean(bo, UserIdcard.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UserIdcard entity) {
        //TODO 做一些数据校验,如唯一约束
        if (ObjectUtil.isNull(entity.getUserIdcardId())) {
            UserIdcardBo a = new UserIdcardBo();
            a.setUserId(entity.getUserId());
            long count = baseMapper.selectCount(buildQueryWrapper(a));
            if (count >= 20) {
                throw new ServiceException("最多保存20个身份信息");
            }
        } else {
            UserIdcardVo userIdcardVo = queryById(entity.getUserIdcardId());
            if (null == userIdcardVo || !userIdcardVo.getUserId().equals(entity.getUserId())) {
                throw new ServiceException("登录超时，请退出重试！");
            }
        }

    }

    /**
     * 批量删除观影用户信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean removeByMap(Map<String, Object> map) {
        return baseMapper.deleteByMap(map) > 0;
    }

}
