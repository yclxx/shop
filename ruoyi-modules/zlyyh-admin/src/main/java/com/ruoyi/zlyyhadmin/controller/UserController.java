package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.DesensitizedUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.UserBo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyhadmin.service.AsyncService;
import com.ruoyi.zlyyhadmin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 用户信息控制器
 * 前端访问路由地址为:/zlyyh/user
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private final IUserService iUserService;
    private final AsyncService asyncService;

    /**
     * 查询用户信息列表
     */
    @SaCheckPermission("zlyyh:user:list")
    @GetMapping("/list")
    public TableDataInfo<UserVo> list(UserBo bo, PageQuery pageQuery) {
        return iUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户信息列表
     */
    @SaCheckPermission("zlyyh:user:export")
    @Log(title = "用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UserBo bo, HttpServletResponse response) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(1);
        TableDataInfo<UserVo> userVoTableDataInfo = iUserService.queryPageList(bo, pageQuery);
        if (userVoTableDataInfo.getTotal() > 2000) {
            throw new ServiceException("导出数据过多，不允许导出");
        }
        List<UserVo> list = iUserService.queryList(bo);
        for (UserVo userVo : list) {
            if (StringUtils.isNotBlank(userVo.getMobile())) {
                userVo.setMobile(DesensitizedUtil.mobilePhone(userVo.getMobile()));
            }
        }
        ExcelUtil.exportExcel(list, "用户信息", UserVo.class, response);
    }

    /**
     * 获取用户信息详细信息
     *
     * @param userId 主键
     */
    @SaCheckPermission("zlyyh:user:query")
    @GetMapping("/{userId}")
    public R<UserVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userId) {
        return R.ok(iUserService.queryById(userId));
    }

    /**
     * 新增用户信息
     */
    @SaCheckPermission("zlyyh:user:add")
    @Log(title = "用户信息", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UserBo bo) {
        return toAjax(iUserService.insertByBo(bo));
    }

    /**
     * 修改用户信息
     */
    @SaCheckPermission("zlyyh:user:edit")
    @Log(title = "用户信息", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UserBo bo) {
        return toAjax(iUserService.updateByBo(bo));
    }

    /**
     * 删除用户信息
     *
     * @param userIds 主键串
     */
    @SaCheckPermission("zlyyh:user:remove")
    @Log(title = "用户信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] userIds) {
        return toAjax(iUserService.deleteWithValidByIds(Arrays.asList(userIds), true));
    }

    /**
     * 同步用户数据
     */
    @SaCheckPermission("zlyyh:user:refresh")
    @PostMapping("/selectAll")
    public R<Void> selectAll(UserBo bo) {
        if (null == bo.getPlatformKey()) {
            throw new ServiceException("请先选择平台");
        }
        if (null == bo.getBeginStartDate() || null == bo.getEndStartDate()) {
            throw new ServiceException("请选择创建时间");
        }
        asyncService.syncUserData(bo.getBeginStartDate(), bo.getEndStartDate(), bo.getPlatformKey());
        return R.ok();
    }
}
