package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.zlyyhadmin.service.IUserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UserAddressVo;
import com.ruoyi.zlyyh.domain.bo.UserAddressBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户地址控制器
 * 前端访问路由地址为:/zlyyh/userAddress
 *
 * @author yzg
 * @date 2023-09-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController {

    private final IUserAddressService iUserAddressService;

    /**
     * 查询用户地址列表
     */
    @SaCheckPermission("zlyyh:userAddress:list")
    @GetMapping("/list")
    public TableDataInfo<UserAddressVo> list(UserAddressBo bo, PageQuery pageQuery) {
        return iUserAddressService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户地址列表
     */
    @SaCheckPermission("zlyyh:userAddress:export")
    @Log(title = "用户地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UserAddressBo bo, HttpServletResponse response) {
        List<UserAddressVo> list = iUserAddressService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户地址", UserAddressVo.class, response);
    }

    /**
     * 获取用户地址详细信息
     *
     * @param userAddressId 主键
     */
    @SaCheckPermission("zlyyh:userAddress:query")
    @GetMapping("/{userAddressId}")
    public R<UserAddressVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userAddressId) {
        return R.ok(iUserAddressService.queryById(userAddressId));
    }

    /**
     * 新增用户地址
     */
    @SaCheckPermission("zlyyh:userAddress:add")
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UserAddressBo bo) {
        return toAjax(iUserAddressService.insertByBo(bo));
    }

    /**
     * 修改用户地址
     */
    @SaCheckPermission("zlyyh:userAddress:edit")
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UserAddressBo bo) {
        return toAjax(iUserAddressService.updateByBo(bo));
    }

    /**
     * 删除用户地址
     *
     * @param userAddressIds 主键串
     */
    @SaCheckPermission("zlyyh:userAddress:remove")
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userAddressIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] userAddressIds) {
        return toAjax(iUserAddressService.deleteWithValidByIds(Arrays.asList(userAddressIds), true));
    }
}
