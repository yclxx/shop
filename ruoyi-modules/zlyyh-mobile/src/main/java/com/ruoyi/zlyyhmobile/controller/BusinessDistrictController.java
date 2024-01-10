package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.ColumnUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.SelectListEntity;
import com.ruoyi.zlyyh.domain.bo.BusinessDistrictBo;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictVo;
import com.ruoyi.zlyyhmobile.service.IBusinessDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商圈控制器
 * 前端访问路由地址为:/zlyyh/businessDistrict
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/businessDistrict")
public class BusinessDistrictController extends BaseController {

    private final IBusinessDistrictService iBusinessDistrictService;

    /**
     * 查询商户下拉列表
     */
    @GetMapping("/selectListBusinessDistrict")
    public R<List<SelectListEntity>> selectListBusinessDistrict(BusinessDistrictBo bo){
        List<BusinessDistrictVo> businessDistrictVos = iBusinessDistrictService.queryList(bo);
        return R.ok(BeanCopyUtils.copyListToSelectListVo(businessDistrictVos, ColumnUtil.getFieldName(BusinessDistrictVo::getBusinessDistrictId),ColumnUtil.getFieldName(BusinessDistrictVo::getBusinessDistrictName),null));
    }
}
