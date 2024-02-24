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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserVo;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserBo;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionUserService;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Arrays;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpServletResponse;

/**
 * 银联任务用户控制器
 * 前端访问路由地址为:/zlyyh/unionpayMissionUser
 *
 * @author yzg
 * @date 2024-02-21
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/unionpayMissionUser")
public class UnionpayMissionUserController extends BaseController {

    private final IUnionpayMissionUserService iUnionpayMissionUserService;

    /**
     * 查询银联任务用户列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionUser:list")
    @GetMapping("/list")
    public TableDataInfo<UnionpayMissionUserVo> list(UnionpayMissionUserBo bo, PageQuery pageQuery) {
        return iUnionpayMissionUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出银联任务用户列表
     */
    @SaCheckPermission("zlyyh:unionpayMissionUser:export")
    @Log(title = "银联任务用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(UnionpayMissionUserBo bo, HttpServletResponse response) {
        List<UnionpayMissionUserVo> list = iUnionpayMissionUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "银联任务用户", UnionpayMissionUserVo.class, response);
    }

    /**
     * 获取银联任务用户详细信息
     *
     * @param upMissionUserId 主键
     */
    @SaCheckPermission("zlyyh:unionpayMissionUser:query")
    @GetMapping("/{upMissionUserId}")
    public R<UnionpayMissionUserVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long upMissionUserId) {
        return R.ok(iUnionpayMissionUserService.queryById(upMissionUserId));
    }

    /**
     * 新增银联任务用户
     */
    @SaCheckPermission("zlyyh:unionpayMissionUser:add")
    @Log(title = "银联任务用户", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody UnionpayMissionUserBo bo) {
        return toAjax(iUnionpayMissionUserService.insertByBo(bo));
    }

    /**
     * 修改银联任务用户
     */
    @SaCheckPermission("zlyyh:unionpayMissionUser:edit")
    @Log(title = "银联任务用户", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody UnionpayMissionUserBo bo) {
        return toAjax(iUnionpayMissionUserService.updateByBo(bo));
    }

    /**
     * 删除银联任务用户
     *
     * @param upMissionUserIds 主键串
     */
    @SaCheckPermission("zlyyh:unionpayMissionUser:remove")
    @Log(title = "银联任务用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{upMissionUserIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] upMissionUserIds) {
        return toAjax(iUnionpayMissionUserService.deleteWithValidByIds(Arrays.asList(upMissionUserIds), true));
    }
}
