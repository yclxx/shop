package com.ruoyi.zlyyhmobile.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.http.HttpStatus;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.validate.QueryGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.Page;
import com.ruoyi.zlyyhmobile.service.IUserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UserAddressVo;
import com.ruoyi.zlyyh.domain.bo.UserAddressBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
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
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController {

    private final IUserAddressService iUserAddressService;


    /**
     * 查询用户地址列表
     */

    @GetMapping("/list")
    public TableDataInfo<UserAddressVo> list(@Validated(QueryGroup.class) UserAddressBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        TableDataInfo<UserAddressVo> userAddressVoTableDataInfo = iUserAddressService.queryPageList(bo);
        for (UserAddressVo addressVo : userAddressVoTableDataInfo.getRows()) {
            addressVo.setTel(DesensitizedUtil.mobilePhone(addressVo.getTel()));
        }
        return userAddressVoTableDataInfo;
    }

    @GetMapping("/getUserAddressByUserId")
    public R<UserAddressVo> getUserAddressByUserId() {
        return R.ok(iUserAddressService.queryByUserId(LoginHelper.getUserId()));
    }

    /**
     * 获取用户地址详细信息
     *
     * @param userAddressId 主键
     */
    @GetMapping("/{userAddressId}")
    public R<UserAddressVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userAddressId) {
        return R.ok(iUserAddressService.queryById(userAddressId));
    }

    /**
     * 新增用户地址
     */
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@Validated(AddGroup.class) UserAddressBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return toAjax(iUserAddressService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改用户地址
     */
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public R<Void> edit(@Validated(EditGroup.class) UserAddressBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        UserAddressVo userAddressVo = iUserAddressService.queryById(bo.getUserAddressId());
        if (null == userAddressVo || !userAddressVo.getUserId().equals(bo.getUserId())) {
            throw new ServiceException("用户信息不匹配,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return toAjax(iUserAddressService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 修改用户地址
     */
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/editDefault")
    public R<Void> editDefault(UserAddressBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        UserAddressVo userAddressVo = iUserAddressService.queryById(bo.getUserAddressId());
        if (null == userAddressVo || !userAddressVo.getUserId().equals(bo.getUserId())) {
            throw new ServiceException("用户信息不匹配,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return toAjax(iUserAddressService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除用户地址
     *
     * @param userAddressId 主键串
     */
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
    @PostMapping("/del/{userAddressId}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Integer userAddressId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_address_id", userAddressId);
        map.put("user_id", LoginHelper.getUserId());
        return toAjax(iUserAddressService.removeByMap(map)? 1 : 0);
    }
}
