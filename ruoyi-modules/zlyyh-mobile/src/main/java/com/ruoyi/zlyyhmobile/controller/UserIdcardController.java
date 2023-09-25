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
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.UserAddressBo;
import com.ruoyi.zlyyh.domain.vo.UserAddressVo;
import com.ruoyi.zlyyhmobile.service.IUserIdcardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UserIdcardVo;
import com.ruoyi.zlyyh.domain.bo.UserIdcardBo;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 观影用户信息控制器
 * 前端访问路由地址为:/zlyyh/userIdcard
 *
 * @author yzg
 * @date 2023-09-15
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/userIdcard")
public class UserIdcardController extends BaseController {

    private final IUserIdcardService iUserIdcardService;

    /**
     * 查询用户观影人列表
     */
    @GetMapping("/list")
    public TableDataInfo<UserIdcardVo> list(@Validated(QueryGroup.class) UserIdcardBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        TableDataInfo<UserIdcardVo> userIdcardVoTableDataInfo = iUserIdcardService.queryPageList(bo);
        for (UserIdcardVo userIdcardVo : userIdcardVoTableDataInfo.getRows()) {
            userIdcardVo.setIdCard(DesensitizedUtil.idCardNum(userIdcardVo.getIdCard(),3,4));
        }
        return userIdcardVoTableDataInfo;
    }

    /**
     * 获取观影用户信息详细信息
     *
     * @param userIdcardId 主键
     */
    @GetMapping("/{userIdcardId}")
    public R<UserIdcardVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userIdcardId) {
        Long userId = LoginHelper.getUserId();
        UserIdcardVo userIdcardVo = iUserIdcardService.queryById(userIdcardId);
        if (null == userIdcardVo || !userIdcardVo.getUserId().equals(userId)) {
            throw new ServiceException("用户信息不匹配,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return R.ok(iUserIdcardService.queryById(userIdcardId));
    }

    /**
     * 新增观影用户信息
     */
    @Log(title = "观影用户信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody UserIdcardBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return toAjax(iUserIdcardService.insertByBo(bo)? 1 : 0);
    }

    /**
     * 修改观影用户信息
     */
    @Log(title = "观影用户信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public R<Void> edit(@Validated @RequestBody UserIdcardBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        UserIdcardVo userIdcardVo = iUserIdcardService.queryById(bo.getUserIdcardId());
        if (null == userIdcardVo || !userIdcardVo.getUserId().equals(bo.getUserId())) {
            throw new ServiceException("用户信息不匹配,请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        return toAjax(iUserIdcardService.updateByBo(bo)? 1 : 0);
    }


    /**
     * 删除用户身份信息
     *
     * @param userIdcardId 主键串
     */
    @Log(title = "用户身份信息", businessType = BusinessType.DELETE)
    @PostMapping("/del/{userIdcardId}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable String userIdcardId) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_idcard_id", userIdcardId);
        map.put("user_id", LoginHelper.getUserId());
        return toAjax(iUserIdcardService.removeByMap(map)? 1 : 0);
    }
}
