package com.ruoyi.zlyyhadmin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.validate.AddGroup;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.RemoteAppOrderService;
import com.ruoyi.zlyyh.domain.bo.MissionUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.MissionUserRecordVo;
import com.ruoyi.zlyyhadmin.service.IMissionUserRecordService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 活动记录控制器
 * 前端访问路由地址为:/zlyyh/missionUserRecord
 *
 * @author yzg
 * @date 2023-05-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/missionUserRecord")
public class MissionUserRecordController extends BaseController {

    private final IMissionUserRecordService iMissionUserRecordService;
    @DubboReference(retries = 0)
    private RemoteAppOrderService appOrderService;

    /**
     * 查询活动记录列表
     */
    @SaCheckPermission("zlyyh:missionUserRecord:list")
    @GetMapping("/list")
    public TableDataInfo<MissionUserRecordVo> list(MissionUserRecordBo bo, PageQuery pageQuery) {
        return iMissionUserRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出活动记录列表
     */
    @SaCheckPermission("zlyyh:missionUserRecord:export")
    @Log(title = "活动记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MissionUserRecordBo bo, HttpServletResponse response) {
        List<MissionUserRecordVo> list = iMissionUserRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "活动记录", MissionUserRecordVo.class, response);
    }

    /**
     * 获取活动记录详细信息
     *
     * @param missionUserRecordId 主键
     */
    @SaCheckPermission("zlyyh:missionUserRecord:query")
    @GetMapping("/{missionUserRecordId}")
    public R<MissionUserRecordVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long missionUserRecordId) {
        return R.ok(iMissionUserRecordService.queryById(missionUserRecordId));
    }

    /**
     * 新增活动记录
     */
    @SaCheckPermission("zlyyh:missionUserRecord:add")
    @Log(title = "活动记录", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MissionUserRecordBo bo) {
        return toAjax(iMissionUserRecordService.insertByBo(bo));
    }

    /**
     * 作废抽奖机会
     *
     * @param ids 主键串
     */
    @SaCheckPermission("zlyyh:missionUserRecord:edit")
    @Log(title = "活动订单取码记录", businessType = BusinessType.UPDATE)
    @PutMapping("/expiry/{ids}")
    public R<Void> expiry(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(iMissionUserRecordService.expiry(Arrays.asList(ids)));
    }

    /**
     * 订单补发
     *
     * @param missionUserRecordId 主键
     */
    @SaCheckPermission("zlyyh:missionUserRecord:reissue")
    @GetMapping("/reissue/{missionUserRecordId}")
    public void reissue(@PathVariable Long missionUserRecordId) {
        appOrderService.sendDraw(missionUserRecordId);
    }
}
