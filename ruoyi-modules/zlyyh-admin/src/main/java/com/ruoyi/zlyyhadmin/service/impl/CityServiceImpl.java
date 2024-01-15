package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.ChineseCharacterUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.CityBo;
import com.ruoyi.zlyyh.domain.vo.CityVo;
import com.ruoyi.zlyyh.domain.City;
import com.ruoyi.zlyyh.mapper.CityMapper;
import com.ruoyi.zlyyhadmin.service.ICityService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 行政区Service业务层处理
 *
 * @author yzg
 * @date 2024-01-12
 */
@RequiredArgsConstructor
@Service
public class CityServiceImpl implements ICityService {

    private final CityMapper baseMapper;

    /**
     * 查询行政区
     */
    @Override
    public CityVo queryById(Long adcode){
        return baseMapper.selectVoById(adcode);
    }


    /**
     * 查询行政区列表
     */
    @Override
    public List<CityVo> queryList(CityBo bo) {
        LambdaQueryWrapper<City> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<City> buildQueryWrapper(CityBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<City> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getCitycode()), City::getCitycode, bo.getCitycode());
        lqw.like(StringUtils.isNotBlank(bo.getAreaName()), City::getAreaName, bo.getAreaName());
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), City::getLevel, bo.getLevel());
        lqw.eq(bo.getParentCode() != null, City::getParentCode, bo.getParentCode());
        lqw.eq(StringUtils.isNotBlank(bo.getFirstLetter()), City::getFirstLetter, bo.getFirstLetter());
        return lqw;
    }

    /**
     * 新增行政区
     */
    @Override
    public Boolean insertByBo(CityBo bo) {
        City add = BeanUtil.toBean(bo, City.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setAdcode(add.getAdcode());
        }
        return flag;
    }

    /**
     * 修改行政区
     */
    @Override
    public Boolean updateByBo(CityBo bo) {
        City update = BeanUtil.toBean(bo, City.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(City entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除行政区
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
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
                baseMapper.insert(city);
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
                            baseMapper.insert(cityCity);

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
                                        baseMapper.insert(cityDistrict);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
