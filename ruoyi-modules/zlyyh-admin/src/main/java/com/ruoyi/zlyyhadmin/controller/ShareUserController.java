package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.validate.EditGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import com.ruoyi.zlyyhadmin.service.IShareUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 分销员控制器
 * 前端访问路由地址为:/zlyyh/shareUser
 *
 * @author yzg
 * @date 2023-11-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/shareUser")
public class ShareUserController extends BaseController {

    private final IShareUserService iShareUserService;

    /**
     * 查询分销员列表
     */
    @SaCheckPermission("zlyyh:shareUser:list")
    @GetMapping("/pageList")
    public TableDataInfo<ShareUserVo> pageList(ShareUserBo bo, PageQuery pageQuery) {
        return iShareUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询分销员列表
     */
    @SaCheckPermission("zlyyh:shareUser:list")
    @GetMapping("/list")
    public R<List<ShareUserVo>> list(ShareUserBo bo) {
        List<ShareUserVo> list = iShareUserService.queryList(bo);
        return R.ok(list);
    }

    /**
     * 导出分销员列表
     */
    @SaCheckPermission("zlyyh:shareUser:export")
    @Log(title = "分销员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ShareUserBo bo, HttpServletResponse response) {
        List<ShareUserVo> list = iShareUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "分销员", ShareUserVo.class, response);
    }

    /**
     * 获取分销员详细信息
     *
     * @param userId 主键
     */
    @SaCheckPermission("zlyyh:shareUser:query")
    @GetMapping("/{userId}")
    public R<ShareUserVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long userId) {
        return R.ok(iShareUserService.queryById(userId));
    }

    /**
     * 新增分销员
     */
    @SaCheckPermission("zlyyh:shareUser:add")
    @Log(title = "分销员", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ShareUserBo bo) {
        return toAjax(iShareUserService.insertByBo(bo));
    }

    /**
     * 修改分销员
     */
    @SaCheckPermission("zlyyh:shareUser:edit")
    @Log(title = "分销员", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ShareUserBo bo) {
        return toAjax(iShareUserService.updateByBo(bo));
    }

    /**
     * 删除分销员
     *
     * @param userIds 主键串
     */
    @SaCheckPermission("zlyyh:shareUser:remove")
    @Log(title = "分销员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] userIds) {
        return toAjax(iShareUserService.deleteWithValidByIds(Arrays.asList(userIds), true));
    }
}
