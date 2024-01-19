package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.MerchantType;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.bo.FileImportLogBo;
import com.ruoyi.zlyyh.domain.vo.MerchantTypeVo;
import com.ruoyi.zlyyh.mapper.MerchantTypeMapper;
import com.ruoyi.zlyyhadmin.domain.bo.MerchantImportBo;
import com.ruoyi.zlyyhadmin.service.IActivityFileShopService;
import com.ruoyi.zlyyhadmin.service.IFileImportLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.vo.ActivityFileShopVo;
import com.ruoyi.zlyyh.domain.ActivityFileShop;
import com.ruoyi.zlyyh.mapper.ActivityFileShopMapper;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 活动商户Service业务层处理
 *
 * @author yzg
 * @date 2024-01-03
 */
@RequiredArgsConstructor
@Service
public class ActivityFileShopServiceImpl implements IActivityFileShopService {

    private final ActivityFileShopMapper baseMapper;
    private final MerchantTypeMapper merchantTypeMapper;
    private final IFileImportLogService fileImportLogService;

    /**
     * 查询活动商户
     */
    @Override
    public ActivityFileShopVo queryById(Long activityShopId){
        return baseMapper.selectVoById(activityShopId);
    }

    /**
     * 查询活动商户列表
     */
    @Override
    public TableDataInfo<ActivityFileShopVo> queryPageList(ActivityFileShopBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ActivityFileShop> lqw = buildQueryWrapper(bo);
        Page<ActivityFileShopVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询活动商户列表
     */
    @Override
    public List<ActivityFileShopVo> queryList(ActivityFileShopBo bo) {
        LambdaQueryWrapper<ActivityFileShop> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ActivityFileShop> buildQueryWrapper(ActivityFileShopBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ActivityFileShop> lqw = Wrappers.lambdaQuery();
        //lqw.like(StringUtils.isNotBlank(bo.getActivityShopName()), ActivityFileShop::getActivityShopName, bo.getActivityShopName());
        //lqw.eq(StringUtils.isNotBlank(bo.getAddress()), ActivityFileShop::getAddress, bo.getAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ActivityFileShop::getStatus, bo.getStatus());
        //lqw.eq(StringUtils.isNotBlank(bo.getFormattedAddress()), ActivityFileShop::getFormattedAddress, bo.getFormattedAddress());
        //lqw.eq(StringUtils.isNotBlank(bo.getProvince()), ActivityFileShop::getProvince, bo.getProvince());
        //lqw.eq(StringUtils.isNotBlank(bo.getCity()), ActivityFileShop::getCity, bo.getCity());
        //lqw.eq(StringUtils.isNotBlank(bo.getDistrict()), ActivityFileShop::getDistrict, bo.getDistrict());
        //lqw.eq(StringUtils.isNotBlank(bo.getProcode()), ActivityFileShop::getProcode, bo.getProcode());
        //lqw.eq(StringUtils.isNotBlank(bo.getCitycode()), ActivityFileShop::getCitycode, bo.getCitycode());
        //lqw.eq(StringUtils.isNotBlank(bo.getAdcode()), ActivityFileShop::getAdcode, bo.getAdcode());
        lqw.like(StringUtils.isNotBlank(bo.getFileName()), ActivityFileShop::getFileName, bo.getFileName());
        lqw.eq(StringUtils.isNotBlank(bo.getFileId()), ActivityFileShop::getFileId, bo.getFileId());
        lqw.eq(bo.getTypeId() != null, ActivityFileShop::getTypeId, bo.getTypeId());
        lqw.eq(StringUtils.isNotBlank(bo.getIndexUrl()), ActivityFileShop::getIndexUrl, bo.getIndexUrl());
        lqw.eq(bo.getLongitude() != null, ActivityFileShop::getLongitude, bo.getLongitude());
        lqw.eq(bo.getLatitude() != null, ActivityFileShop::getLatitude, bo.getLatitude());
        lqw.eq(bo.getSort() != null, ActivityFileShop::getSort, bo.getSort());
        if (StringUtils.isNotBlank(bo.getActivityShopName())) {
            lqw.and(lq ->
                lq.like(ActivityFileShop::getActivityShopName, bo.getActivityShopName()).or().like(ActivityFileShop::getAddress, bo.getActivityShopName())
                    .or().like(ActivityFileShop::getFormattedAddress, bo.getActivityShopName())
            );
        }
        if (StringUtils.isNotBlank(bo.getProvince())) {
            lqw.and(lq ->
                lq.like(ActivityFileShop::getProvince, bo.getProvince()).or().like(ActivityFileShop::getCity, bo.getProvince())
                    .or().like(ActivityFileShop::getDistrict, bo.getProvince())
                    .or().like(ActivityFileShop::getProcode, bo.getProvince())
                    .or().like(ActivityFileShop::getCitycode, bo.getProvince())
                    .or().like(ActivityFileShop::getAdcode, bo.getProvince())
            );
        }
        return lqw;
    }

    /**
     * 新增活动商户
     */
    @Override
    public Boolean insertByBo(ActivityFileShopBo bo) {
        if (StringUtils.isNotEmpty(bo.getAddress()) && StringUtils.isNotEmpty(bo.getCityName())) {
            JSONObject addressInfo = AddressUtils.getAddressInfo(bo.getAddress(), bo.getCityName());
            if (ObjectUtil.isNotEmpty(addressInfo)) {
                bo.setFormattedAddress(addressInfo.getString("formatted_address"));
                bo.setProvince(addressInfo.getString("province"));
                bo.setCity(addressInfo.getString("city"));
                bo.setDistrict(addressInfo.getString("district"));

                String adcode = addressInfo.getString("adcode");
                String procode = adcode.substring(0, 2) + "0000";
                String citycode = adcode.substring(0, 4) + "00";
                bo.setProcode(procode);
                bo.setCitycode(citycode);
                bo.setAdcode(adcode);

                String location = addressInfo.getString("location");
                String[] split = location.split(",");
                String longitude = split[0];
                String latitude = split[1];
                bo.setLongitude(new BigDecimal(longitude));
                bo.setLatitude(new BigDecimal(latitude));
            }
        }
        ActivityFileShop add = BeanUtil.toBean(bo, ActivityFileShop.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setActivityShopId(add.getActivityShopId());
        }
        return flag;
    }

    /**
     * 修改活动商户
     */
    @Override
    public Boolean updateByBo(ActivityFileShopBo bo) {
        ActivityFileShopVo fileShopVo = baseMapper.selectVoById(bo.getActivityShopId());
        if (ObjectUtil.isNotEmpty(fileShopVo)) {
            if (!fileShopVo.getAddress().equals(bo.getAddress())) {
                JSONObject addressInfo = AddressUtils.getAddressInfo(bo.getAddress(), bo.getCityName());
                if (ObjectUtil.isNotEmpty(addressInfo)) {
                    bo.setFormattedAddress(addressInfo.getString("formatted_address"));
                    bo.setProvince(addressInfo.getString("province"));
                    bo.setCity(addressInfo.getString("city"));
                    bo.setDistrict(addressInfo.getString("district"));

                    String adcode = addressInfo.getString("adcode");
                    String procode = adcode.substring(0, 2) + "0000";
                    String citycode = adcode.substring(0, 4) + "00";
                    bo.setProcode(procode);
                    bo.setCitycode(citycode);
                    bo.setAdcode(adcode);

                    String location = addressInfo.getString("location");
                    String[] split = location.split(",");
                    String longitude = split[0];
                    String latitude = split[1];
                    bo.setLongitude(new BigDecimal(longitude));
                    bo.setLatitude(new BigDecimal(latitude));
                }
            }
        }
        ActivityFileShop update = BeanUtil.toBean(bo, ActivityFileShop.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ActivityFileShop entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除活动商户
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 导入商户
     */
    @Override
    @Async
    public void importMerchant(MultipartFile file, FileImportLogBo logBo) throws IOException {
        List<MerchantImportBo> merchantImportBos = ExcelUtil.importExcel(file.getInputStream(), MerchantImportBo.class);
        if (ObjectUtil.isNotEmpty(merchantImportBos)) {
            logBo.setCount((long) merchantImportBos.size());
            Long importCount = 0L;
            for (MerchantImportBo merchantImportBo : merchantImportBos) {
                if (StringUtils.isEmpty(merchantImportBo.getType())) {
                    continue;
                }
                MerchantTypeVo merchantTypeVo = merchantTypeMapper.selectVoOne(new LambdaQueryWrapper<MerchantType>().eq(MerchantType::getTypeName, merchantImportBo.getType().trim()).eq(MerchantType::getStatus, "0").last("limit 1"));
                if (ObjectUtil.isEmpty(merchantTypeVo)) {
                    MerchantType merchantType = new MerchantType();
                    merchantType.setTypeName(merchantImportBo.getType().trim());
                    merchantTypeMapper.insert(merchantType);
                    merchantTypeVo = BeanUtil.toBean(merchantType, MerchantTypeVo.class);
                }
                List<ActivityFileShopVo> shopVos = baseMapper.selectVoList(new LambdaQueryWrapper<ActivityFileShop>().eq(ActivityFileShop::getAddress, merchantImportBo.getAddress().trim()).eq(ActivityFileShop::getActivityShopName, merchantImportBo.getMerName().trim()).eq(ActivityFileShop::getTypeId,merchantTypeVo.getMerchantTypeId()));
                if (ObjectUtil.isNotEmpty(shopVos)) {
                    continue;
                }
                ActivityFileShop activityFileShop = new ActivityFileShop();
                if (StringUtils.isNotEmpty(merchantImportBo.getMerName())) {
                    activityFileShop.setActivityShopName(merchantImportBo.getMerName());
                }
                if (StringUtils.isNotEmpty(merchantImportBo.getAddress())) {
                    activityFileShop.setAddress(merchantImportBo.getAddress().trim());
                }
                activityFileShop.setFileName(file.getOriginalFilename());
                activityFileShop.setFileId(String.valueOf(logBo.getImportLogId()));
                //按照地址来获取相关信息
                if (StringUtils.isBlank(merchantImportBo.getAddress())) {
                    continue;
                }
                String key = "importFileMerchant:" + merchantImportBo.getAddress();
                JSONObject addressInfo = RedisUtils.getCacheObject(key);
                if (ObjectUtil.isEmpty(addressInfo)) {
                    addressInfo = AddressUtils.getAddressInfo(merchantImportBo.getAddress(), merchantImportBo.getProvince());
                }
                if (ObjectUtil.isNotEmpty(addressInfo)) {
                    activityFileShop.setFormattedAddress(addressInfo.getString("formatted_address"));
                    activityFileShop.setProvince(addressInfo.getString("province"));
                    activityFileShop.setCity(addressInfo.getString("city"));
                    activityFileShop.setDistrict(addressInfo.getString("district"));

                    String adcode = addressInfo.getString("adcode");
                    String procode = adcode.substring(0, 2) + "0000";
                    String citycode = adcode.substring(0, 4) + "00";
                    activityFileShop.setProcode(procode);
                    activityFileShop.setCitycode(citycode);
                    activityFileShop.setAdcode(adcode);

                    String location = addressInfo.getString("location");
                    String[] split = location.split(",");
                    String longitude = split[0];
                    String latitude = split[1];
                    activityFileShop.setLongitude(new BigDecimal(longitude));
                    activityFileShop.setLatitude(new BigDecimal(latitude));

                    if (StringUtils.isNotBlank(key)) {
                        RedisUtils.setCacheObject(key, addressInfo, Duration.ofDays(5));
                    }
                }

                activityFileShop.setTypeId(merchantTypeVo.getMerchantTypeId());
                int i = baseMapper.insert(activityFileShop);
                if (i > 0) {
                    importCount++;
                }
            }
            logBo.setImportCount(importCount);
            fileImportLogService.updateByBo(logBo);
        }
    }
}
