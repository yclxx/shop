package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.ActivityFileShop;
import com.ruoyi.zlyyh.domain.Area;
import com.ruoyi.zlyyh.domain.MerchantType;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.utils.LocationUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IActivityFileShopService;
import com.ruoyi.zlyyhmobile.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 活动商户门店
 * @author ruoyi
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/activityMerchant/ignore")
public class ActivityMerchantController {
    private final IActivityFileShopService iActivityFileShopService;

    /**
     * 获取商户列表
     *
     * @param bo
     * @param pageQuery
     * @return
     */
    @GetMapping("/getMerchantList")
    public TableDataInfo<ActivityFileShopVo> getMerchantList(ActivityFileShopBo bo, PageQuery pageQuery) {
        //bo.setCitycode(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        return iActivityFileShopService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取导入文件信息
     */
    @GetMapping("/getFileInfo")
    public R<FileImportLogVo> getFileInfo(String fileId) {
        return R.ok(iActivityFileShopService.getFileInfo(fileId));
    }

    /**
     * 获取当前城市和行政编码
     */
    @GetMapping("/getLocationCity")
    public R<Map<String, String>> getLocationCity(String location) {
        Map<String, String> locationCity = LocationUtils.getLocationCity(location);
        if (ObjectUtil.isNotEmpty(locationCity)) {
            if (StringUtils.isEmpty(locationCity.get("city"))) {
                locationCity.put("city",locationCity.get("province"));
            }
        }
        return R.ok(locationCity);
    }

    /**
     * 获取当前查询批次城市列表
     */
    @GetMapping("/getCityList")
    public R<List<AreaVo>> getCityList(String fileId) {
        return R.ok(iActivityFileShopService.getCityList(fileId));
    }

    /**
     * 获取商户类别列表
     */
    @GetMapping("/getMerTypeList")
    public R<List<MerchantTypeVo>> getMerTypeList(String fileId) {
        return R.ok(iActivityFileShopService.getMerTypeList(fileId));
    }

    /**
     * 获取省市区列表
     */
    @GetMapping("/getCityDistrictList")
    public R<List<Tree<Long>>> getCityDistrictList() {
        List<Tree<Long>> cityDistrictList = iActivityFileShopService.getCityDistrictList();
        return R.ok(cityDistrictList);
    }

    /**
     * 查询高德省市区
     * @param adcode
     * @return
     */
    @GetMapping("/getDistrict")
    public R<Void> getDistrict(String adcode) {
        iActivityFileShopService.getDistrict(adcode);
        return R.ok();
    }
}
