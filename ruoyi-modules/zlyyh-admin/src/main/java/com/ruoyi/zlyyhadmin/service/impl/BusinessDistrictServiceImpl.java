package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhadmin.service.IBusinessDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.BusinessDistrictBo;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictVo;
import com.ruoyi.zlyyh.domain.BusinessDistrict;
import com.ruoyi.zlyyh.mapper.BusinessDistrictMapper;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商圈Service业务层处理
 *
 * @author yzg
 * @date 2023-09-15
 */
@RequiredArgsConstructor
@Service
public class BusinessDistrictServiceImpl implements IBusinessDistrictService {

    private final BusinessDistrictMapper baseMapper;

    /**
     * 查询商圈
     */
    @Override
    public BusinessDistrictVo queryById(Long businessDistrictId){
        return baseMapper.selectVoById(businessDistrictId);
    }

    /**
     * 查询商圈列表
     */
    @Override
    public TableDataInfo<BusinessDistrictVo> queryPageList(BusinessDistrictBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BusinessDistrict> lqw = buildQueryWrapper(bo);
        Page<BusinessDistrictVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商圈列表
     */
    @Override
    public List<BusinessDistrictVo> queryList(BusinessDistrictBo bo) {
        LambdaQueryWrapper<BusinessDistrict> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BusinessDistrict> buildQueryWrapper(BusinessDistrictBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BusinessDistrict> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBusinessDistrictName()), BusinessDistrict::getBusinessDistrictName, bo.getBusinessDistrictName());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessDistrictImg()), BusinessDistrict::getBusinessDistrictImg, bo.getBusinessDistrictImg());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessMobile()), BusinessDistrict::getBusinessMobile, bo.getBusinessMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessHours()), BusinessDistrict::getBusinessHours, bo.getBusinessHours());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), BusinessDistrict::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BusinessDistrict::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getFormattedAddress()), BusinessDistrict::getFormattedAddress, bo.getFormattedAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getProvince()), BusinessDistrict::getProvince, bo.getProvince());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), BusinessDistrict::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getDistrict()), BusinessDistrict::getDistrict, bo.getDistrict());
        lqw.eq(StringUtils.isNotBlank(bo.getProcode()), BusinessDistrict::getProcode, bo.getProcode());
        lqw.eq(StringUtils.isNotBlank(bo.getCitycode()), BusinessDistrict::getCitycode, bo.getCitycode());
        lqw.eq(StringUtils.isNotBlank(bo.getAdcode()), BusinessDistrict::getAdcode, bo.getAdcode());
        lqw.eq(bo.getLongitude() != null, BusinessDistrict::getLongitude, bo.getLongitude());
        lqw.eq(bo.getLatitude() != null, BusinessDistrict::getLatitude, bo.getLatitude());
        lqw.eq(StringUtils.isNotBlank(bo.getBusinessDistrictScope()), BusinessDistrict::getBusinessDistrictScope, bo.getBusinessDistrictScope());
        lqw.eq(bo.getPlatformKey() != null, BusinessDistrict::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getSysDeptId() != null, BusinessDistrict::getSysDeptId, bo.getSysDeptId());
        lqw.eq(bo.getSysUserId() != null, BusinessDistrict::getSysUserId, bo.getSysUserId());
        return lqw;
    }

    /**
     * 新增商圈
     */
    @Override
    public Boolean insertByBo(BusinessDistrictBo bo) {
        BusinessDistrict add = BeanUtil.toBean(bo, BusinessDistrict.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setBusinessDistrictId(add.getBusinessDistrictId());
        }
        return flag;
    }

    /**
     * 修改商圈
     */
    @Override
    public Boolean updateByBo(BusinessDistrictBo bo) {
        BusinessDistrict update = BeanUtil.toBean(bo, BusinessDistrict.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BusinessDistrict entity){
        //TODO 做一些数据校验,如唯一约束
        PermissionUtils.setPlatformDeptIdAndUserId(entity, entity.getPlatformKey(), true, true);
    }

    /**
     * 批量删除商圈
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
