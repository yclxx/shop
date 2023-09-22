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
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyhmobile.service.IUserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.UserAddressBo;
import com.ruoyi.zlyyh.domain.vo.UserAddressVo;
import com.ruoyi.zlyyh.domain.UserAddress;
import com.ruoyi.zlyyh.mapper.UserAddressMapper;


import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 用户地址Service业务层处理
 *
 * @author yzg
 * @date 2023-09-15
 */
@RequiredArgsConstructor
@Service
public class UserAddressServiceImpl implements IUserAddressService {

    private final UserAddressMapper baseMapper;

    /**
     * 查询用户地址
     */
    @Override
    public UserAddressVo queryById(Long userAddressId){
        return baseMapper.selectVoById(userAddressId);
    }

    /**
     * 查询用户地址列表
     */
    @Override
    public TableDataInfo<UserAddressVo> queryPageList(UserAddressBo bo) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(20);
        LambdaQueryWrapper<UserAddress> lqw = buildQueryWrapper(bo);
        Page<UserAddressVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询用户地址列表
     */
    @Override
    public List<UserAddressVo> queryList(UserAddressBo bo) {
        LambdaQueryWrapper<UserAddress> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UserAddress> buildQueryWrapper(UserAddressBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, UserAddress::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), UserAddress::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getTel()), UserAddress::getTel, bo.getTel());
        lqw.eq(StringUtils.isNotBlank(bo.getIsDefault()), UserAddress::getIsDefault, bo.getIsDefault());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), UserAddress::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getAreaId()), UserAddress::getAreaId, bo.getAreaId());
        lqw.eq(StringUtils.isNotBlank(bo.getAddressInfo()), UserAddress::getAddressInfo, bo.getAddressInfo());
        return lqw;
    }

    /**
     * 新增用户地址
     */
    @Override
    public Boolean insertByBo(UserAddressBo bo) {
        UserAddress add = BeanUtil.toBean(bo, UserAddress.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUserAddressId(add.getUserAddressId());
        }
        return flag;
    }

    /**
     * 修改用户地址
     */
    @Override
    public Boolean updateByBo(UserAddressBo bo) {
        UserAddress update = BeanUtil.toBean(bo, UserAddress.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UserAddress entity){
        //TODO 做一些数据校验,如唯一约束
        if (ObjectUtil.isNull(entity.getUserAddressId())) {
            UserAddressBo a = new UserAddressBo();
            a.setUserId(entity.getUserId());
            long count = baseMapper.selectCount(buildQueryWrapper(a));
            if (count >= 20) {
                throw new ServiceException("最多保存20个收货地址");
            }
        } else {
            UserAddressVo userAddressVo = queryById(entity.getUserAddressId());
            if (null == userAddressVo || !userAddressVo.getUserId().equals(entity.getUserId())) {
                throw new ServiceException("登录超时，请退出重试！");
            }
        }
        // 如果地址是默认地址，修改用户所有地址为不默认
        if ("0".equals(entity.getIsDefault())) {
            // 清除默认地址
            UserAddressBo a = new UserAddressBo();
            a.setUserId(entity.getUserId());
            UserAddress updateEntity = new UserAddress();
            updateEntity.setIsDefault("1");
            baseMapper.update(updateEntity, buildQueryWrapper(a));
        }
    }

    /**
     * 批量删除用户地址
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean removeByMap(Map<String, Object> map) {
        return baseMapper.deleteByMap(map) > 0;
    }

    @Override
    public UserAddressVo queryByUserId(Long userId) {
        // 查询用户收货地址
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserAddress::getUserId, userId);
        lqw.orderByAsc(UserAddress::getIsDefault).orderByDesc(UserAddress::getUpdateTime);
        List<UserAddressVo> userAddressVos = baseMapper.selectVoList(lqw);
        if (ObjectUtil.isEmpty(userAddressVos)) {
            return null;
        }
        return userAddressVos.get(0);
    }
}
