package com.ruoyi.zlyyhmobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.zlyyh.domain.BusinessDistrict;
import com.ruoyi.zlyyh.domain.bo.BusinessDistrictBo;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictVo;
import com.ruoyi.zlyyh.mapper.BusinessDistrictMapper;
import com.ruoyi.zlyyhmobile.service.IBusinessDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
}
