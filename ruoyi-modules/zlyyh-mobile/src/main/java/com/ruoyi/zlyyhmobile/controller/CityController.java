package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.zlyyh.domain.bo.CityBo;
import com.ruoyi.zlyyh.domain.vo.CityVo;
import com.ruoyi.zlyyhmobile.service.ICityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 行政区控制器
 * 前端访问路由地址为:/zlyyh/city
 *
 * @author yzg
 * @date 2024-01-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/city/ignore")
public class CityController extends BaseController {

    private final ICityService iCityService;

    /**
     * 查询行政区列表
     */
    @PostMapping("/cityList")
    public R<Map<String,Object>> cityList() {
        CityBo cityBo = new CityBo();
        cityBo.setLevel("city");
        List<CityVo> list = iCityService.queryList(cityBo);
        Map<String,Object> result = new HashMap<>();

        Map<String, List<CityVo>> cityNav = list.stream().collect(Collectors.groupingBy(CityVo::getFirstLetter));
        result.put("city_nav", cityNav);
        //result.put("hot_city", null);
        return R.ok(result);
    }


    /**
     * 获取城市信息
     * @param adcode
     * @return
     */
    @GetMapping("/getDistrict/{adcode}")
    public R<Void> getDistrict(@PathVariable String adcode) {
        iCityService.getDistrict(adcode);
        return R.ok();
    }

}
