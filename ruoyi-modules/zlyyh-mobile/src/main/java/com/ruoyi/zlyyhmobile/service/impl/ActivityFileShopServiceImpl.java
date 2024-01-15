package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.ChineseCharacterUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.TreeBuildUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.ActivityFileShop;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.City;
import com.ruoyi.zlyyh.domain.MerchantType;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.ActivityFileShopMapper;
import com.ruoyi.zlyyh.mapper.CityMapper;
import com.ruoyi.zlyyh.mapper.FileImportLogMapper;
import com.ruoyi.zlyyh.mapper.MerchantTypeMapper;
import com.ruoyi.zlyyhmobile.service.IActivityFileShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
    private final CityMapper cityMapper;


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
        List<AreaVo> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(areaVos)) {
            List<AreaVo> result = areaVos.stream().filter(res -> res.getAdcode() != 0).collect(Collectors.toList());
            AreaVo areaVo = new AreaVo();
            areaVo.setAreaName("全部城市");
            list.add(areaVo);
            list.addAll(result);
        }
        return list;
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
            if(ObjectUtil.isNotEmpty(typeVoList)){
                MerchantTypeVo merchantTypeVo = new MerchantTypeVo();
                merchantTypeVo.setTypeName("全部");
                merchantTypeVo.setStatus("0");
                list.add(merchantTypeVo);
                if (ObjectUtil.isNotEmpty(typeVoList)) {
                    list.addAll(typeVoList);
                }
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

    @Override
    public void getDistrict(String adcode) {
        String url = "https://restapi.amap.com/v3/config/district";
        String key = "ed8652067f054abb156de40a110a08ea";
        String subdistrict = "2";
        String query = "key=" + key + "&keywords=" + adcode + "&subdistrict=" + subdistrict;
        String s = HttpUtil.createGet(url).body(query).execute().body();
        if (StringUtils.isNotEmpty(s)) {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (0 == jsonObject.getIntValue("status")) {
                return;
            }
            JSONArray districts = jsonObject.getJSONArray("districts");
            if (ObjectUtil.isEmpty(districts)) {
                return;
            }
            JSONObject provinceJson = districts.getJSONObject(0);
            if (provinceJson.getString("level").equals("province")) {
                City city = new City();
                city.setAdcode(Long.valueOf(provinceJson.getString("adcode")));
                city.setAreaName(provinceJson.getString("name"));
                city.setLevel(provinceJson.getString("level"));
                String firstLetter = ChineseCharacterUtils.getSpells(String.valueOf(city.getAreaName().charAt(0)).toUpperCase());
                city.setFirstLetter(firstLetter);
                cityMapper.insert(city);
                JSONArray provinceDistricts = provinceJson.getJSONArray("districts");
                if (ObjectUtil.isNotEmpty(provinceDistricts)) {
                    for (int i = 0; i < provinceDistricts.size(); i++) {
                        JSONObject cityJson = provinceDistricts.getJSONObject(i);
                        if (cityJson.getString("level").equals("city")) {
                            City cityCity = new City();
                            cityCity.setAdcode(Long.valueOf(cityJson.getString("adcode")));
                            cityCity.setCitycode(cityJson.getString("citycode"));
                            cityCity.setAreaName(cityJson.getString("name"));
                            cityCity.setLevel(cityJson.getString("level"));
                            cityCity.setParentCode(Long.valueOf(provinceJson.getString("adcode")));
                            String firstLetter1 = ChineseCharacterUtils.getSpells(String.valueOf(cityCity.getAreaName().charAt(0)).toUpperCase());
                            cityCity.setFirstLetter(firstLetter1);
                            cityMapper.insert(cityCity);

                            JSONArray cityDistricts = cityJson.getJSONArray("districts");
                            if (ObjectUtil.isNotEmpty(cityDistricts)) {
                                for (int j = 0; j < cityDistricts.size(); j++) {
                                    JSONObject districtJson = cityDistricts.getJSONObject(j);
                                    if (districtJson.getString("level").equals("district")) {
                                        City cityDistrict = new City();
                                        cityDistrict.setAdcode(Long.valueOf(districtJson.getString("adcode")));
                                        cityDistrict.setCitycode(districtJson.getString("citycode"));
                                        cityDistrict.setAreaName(districtJson.getString("name"));
                                        cityDistrict.setLevel(districtJson.getString("level"));
                                        cityDistrict.setParentCode(Long.valueOf(cityJson.getString("adcode")));
                                        String firstLetter2 = ChineseCharacterUtils.getSpells(String.valueOf(cityDistrict.getAreaName().charAt(0)).toUpperCase());
                                        cityDistrict.setFirstLetter(firstLetter2);
                                        cityMapper.insert(cityDistrict);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取省市区列表
     */
    //@Override
    //public List<CityVo> getCityDistrictList() {
    //    List<CityVo> provinceList = cityMapper.selectVoList(new LambdaQueryWrapper<City>().eq(City::getLevel, "province"));
    //    if (ObjectUtil.isNotEmpty(provinceList)) {
    //        for (CityVo province : provinceList) {
    //            List<CityVo> cities = cityMapper.selectVoList(new LambdaQueryWrapper<City>().eq(City::getParentCode, province.getAdcode()));
    //            if (ObjectUtil.isNotEmpty(cities)) {
    //                province.setAreas(cities);
    //                for (CityVo city : cities) {
    //                    List<CityVo> districts = cityMapper.selectVoList(new LambdaQueryWrapper<City>().eq(City::getParentCode, city.getAdcode()));
    //                    if (ObjectUtil.isNotEmpty(districts)) {
    //                        CityVo allCity = new CityVo();
    //                        allCity.setAdcode(Long.valueOf(province.getAdcode().toString().substring(0, 2)));
    //                        allCity.setAreaName("全部");
    //                        districts.add(0,allCity);
    //                        city.setAreas(districts);
    //                    }
    //                }
    //            }
    //        }
    //    }
    //    return provinceList;
    //}

    /**
     * 下拉树
     */
    @Override
    @Cacheable(cacheNames = CacheNames.CITY_AREA_LIST, key = "'city_area_list'")
    public List<Tree<Long>> getCityDistrictList() {
        List<CityVo> cityVos = cityMapper.selectVoList(new LambdaQueryWrapper<City>());
        if (CollUtil.isEmpty(cityVos)) {
            return CollUtil.newArrayList();
        }

        return TreeBuildUtils.builds(cityVos, (city, tree) ->
            tree.setId(city.getAdcode())
                .setParentId(city.getParentCode())
                .setName(city.getAreaName()));
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
