package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyhmobile.service.IActivityFileShopService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 门店Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class ActivityFileShopServiceImpl implements IActivityFileShopService {

    private final ActivityFileShopMapper baseMapper;
    private final MerchantTypeMapper merchantTypeMapper;
    private final FileImportLogMapper fileImportLogMapper;


    /**
     * 获取商户列表
     */
    @Override
    public TableDataInfo<ActivityFileShopVo> queryPageList(ActivityFileShopBo bo, PageQuery pageQuery) {
        Page<ActivityFileShopVo> result = baseMapper.selectFileShopList(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    /**
     * 获取当前查询批次城市列表
     */
    @Cacheable(cacheNames = CacheNames.ACTIVITY_MERCHANT, key = "'fileMerchantCitys' + #fileId")
    @Override
    public List<AreaVo> getCityList(String fileId) {
        List<AreaVo> areaVos = baseMapper.selectCityList(fileId);
        if (ObjectUtil.isNotEmpty(areaVos)) {
            for (AreaVo areaVo : areaVos) {
                if (areaVo.getAdcode() == 0) {
                    areaVo.setAreaName("全部城市");
                }
            }
        }
        return areaVos;
    }

    /**
     * 获取商户类别列表
     */
    @Cacheable(cacheNames = CacheNames.MERCHANT_TYPE, key = "'merchantType' + #fileId")
    @Override
    public List<MerchantTypeVo> getMerTypeList(String fileId) {
        List<Long> typeIds = baseMapper.selectTypeByFileId(fileId);
        List<MerchantTypeVo> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(typeIds)) {
            List<MerchantTypeVo> typeVoList = merchantTypeMapper.selectVoList(new LambdaQueryWrapper<MerchantType>().eq(MerchantType::getStatus, "0").in(MerchantType::getMerchantTypeId, typeIds));
            MerchantTypeVo merchantTypeVo = new MerchantTypeVo();
            merchantTypeVo.setTypeName("全部");
            merchantTypeVo.setStatus("0");
            list.add(merchantTypeVo);
            if (ObjectUtil.isNotEmpty(typeVoList)) {
                list.addAll(typeVoList);
            }
        }
        return list;
    }

    /**
     * 获取导入文件信息
     */
    @Override
    public FileImportLogVo getFileInfo(String fileId) {
        return fileImportLogMapper.selectVoById(fileId);
    }

    private LambdaQueryWrapper<ActivityFileShop> buildQueryWrapper(ActivityFileShopBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ActivityFileShop> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getActivityShopName()), ActivityFileShop::getActivityShopName, bo.getActivityShopName());
        lqw.eq(StringUtils.isNotBlank(bo.getAddress()), ActivityFileShop::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ActivityFileShop::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getFormattedAddress()), ActivityFileShop::getFormattedAddress, bo.getFormattedAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getProvince()), ActivityFileShop::getProvince, bo.getProvince());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), ActivityFileShop::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getDistrict()), ActivityFileShop::getDistrict, bo.getDistrict());
        lqw.eq(StringUtils.isNotBlank(bo.getProcode()), ActivityFileShop::getProcode, bo.getProcode());
        lqw.eq(StringUtils.isNotBlank(bo.getCitycode()), ActivityFileShop::getCitycode, bo.getCitycode());
        lqw.eq(StringUtils.isNotBlank(bo.getAdcode()), ActivityFileShop::getAdcode, bo.getAdcode());
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), ActivityFileShop::getFileName, bo.getFileName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileId()), ActivityFileShop::getFileId, bo.getFileId());
        lqw.eq(StringUtils.isNotBlank(bo.getIndexUrl()), ActivityFileShop::getIndexUrl, bo.getIndexUrl());
        lqw.eq(bo.getLongitude() != null, ActivityFileShop::getLongitude, bo.getLongitude());
        lqw.eq(bo.getLatitude() != null, ActivityFileShop::getLatitude, bo.getLatitude());
        lqw.eq(bo.getSort() != null, ActivityFileShop::getSort, bo.getSort());
        return lqw;
    }
}
