package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyh.mapper.AreaMapper;
import com.ruoyi.zlyyhadmin.service.ICityMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.CityMerchantBo;
import com.ruoyi.zlyyh.domain.vo.CityMerchantVo;
import com.ruoyi.zlyyh.domain.CityMerchant;
import com.ruoyi.zlyyh.mapper.CityMerchantMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 城市商户Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class CityMerchantServiceImpl implements ICityMerchantService {

    private final CityMerchantMapper baseMapper;
    private final AreaMapper areaMapper;

    /**
     * 查询城市商户
     */
    @Override
    public CityMerchantVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询城市商户列表
     */
    @Override
    public TableDataInfo<CityMerchantVo> queryPageList(CityMerchantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CityMerchant> lqw = buildQueryWrapper(bo);
        Page<CityMerchantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询城市商户列表
     */
    @Override
    public List<CityMerchantVo> queryList(CityMerchantBo bo) {
        LambdaQueryWrapper<CityMerchant> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CityMerchant> buildQueryWrapper(CityMerchantBo bo) {
        LambdaQueryWrapper<CityMerchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, CityMerchant::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getAdcode()), CityMerchant::getAdcode, bo.getAdcode());
        lqw.like(StringUtils.isNotBlank(bo.getAreaName()), CityMerchant::getAreaName, bo.getAreaName());
        lqw.eq(bo.getMerchantId() != null, CityMerchant::getMerchantId, bo.getMerchantId());
        return lqw;
    }

    /**
     * 新增城市商户
     */
    @Override
    public Boolean insertByBo(CityMerchantBo bo) {
        CityMerchant add = null;
        String adcodeStr = bo.getAdcode();
        String[] adcodeArray = adcodeStr.split(",");
        for (String s : adcodeArray) {
            add = new CityMerchant();
            add.setMerchantId(bo.getMerchantId());
            add.setPlatformKey(bo.getPlatformKey());
            add.setAdcode(s);
            String areaName = process(s, add.getPlatformKey(), add.getMerchantId());
            if(StringUtils.isNotBlank(areaName)){
                add.setAreaName(areaName);
                baseMapper.insert(add);
            }
        }
        return true;
    }

    private String process(String adcode,Long platformKey,Long merchantId){
        if (StringUtils.isBlank(adcode)) {
            return null;
        }
        LambdaQueryWrapper<CityMerchant> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CityMerchant::getPlatformKey, platformKey);
        wrapper.eq(CityMerchant::getMerchantId, merchantId);
        wrapper.eq(CityMerchant::getAdcode, adcode);
        wrapper.last("Limit 1");
        CityMerchantVo cityMerchantVo = baseMapper.selectVoOne(wrapper);
        if (null != cityMerchantVo) {
            return null;
        }
        LambdaQueryWrapper<Area> areaWrapper = Wrappers.lambdaQuery();
        areaWrapper.eq(Area::getAdcode, adcode);
        areaWrapper.last("limit 1");
        AreaVo areaVo = areaMapper.selectVoOne(areaWrapper);
        if (null == areaVo) {
            return null;
        }
        return areaVo.getAreaName();
    }

    /**
     * 修改城市商户
     */
    @Override
    public Boolean updateByBo(CityMerchantBo bo) {
        LambdaQueryWrapper<CityMerchant> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CityMerchant::getPlatformKey, bo.getPlatformKey());
        wrapper.eq(CityMerchant::getMerchantId, bo.getMerchantId());
        wrapper.eq(CityMerchant::getAdcode, bo.getAdcode());
        List<CityMerchant> cityMerchants = baseMapper.selectList(wrapper);
        if(cityMerchants.size() > 0){
            throw new ServiceException("已存在一条相同数据，修改失败");
        }
        LambdaQueryWrapper<Area> areaWrapper = Wrappers.lambdaQuery();
        areaWrapper.eq(Area::getAdcode, bo.getAdcode());
        areaWrapper.last("limit 1");
        AreaVo areaVo = areaMapper.selectVoOne(areaWrapper);
        bo.setAreaName(areaVo.getAreaName());
        CityMerchant update = BeanUtil.toBean(bo, CityMerchant.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CityMerchant entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除城市商户
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
