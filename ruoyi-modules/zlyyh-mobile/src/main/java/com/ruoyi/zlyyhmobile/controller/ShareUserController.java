package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.codec.Base64;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.ShareUserBo;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.properties.WxProperties;
import com.ruoyi.zlyyh.utils.WxUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.GenWxQrCodeBo;
import com.ruoyi.zlyyhmobile.domain.vo.ShareUserInfoVo;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final IShareUserAccountService shareUserAccountService;
    private final IShareUserRecordService shareUserRecordService;
    private final IUserService userService;
    private final WxProperties wxProperties;
    private final IPlatformService platformService;

    /**
     * 获取分销员详细信息
     */
    @GetMapping("/info")
    public R<ShareUserInfoVo> getInfo() {
        ShareUserInfoVo shareUserInfoVo = new ShareUserInfoVo();
        Long userId = LoginHelper.getUserId();
        ShareUserVo shareUserVo = iShareUserService.queryById(userId);
        if (null == shareUserVo) {
            return R.ok();
        }
        shareUserInfoVo.setShareUserVo(shareUserVo);
        ShareUserAccountVo shareUserAccountVo = shareUserAccountService.queryById(userId);
        shareUserInfoVo.setShareUserAccountVo(shareUserAccountVo);
        return R.ok(shareUserInfoVo);
    }

    /**
     * 新增分销员
     */
    @RepeatSubmit
    @Log(title = "分销员", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@RequestBody ShareUserBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        return toAjax(iShareUserService.insertByBo(bo));
    }

    /**
     * 查询顶级上级分销
     */
    @GetMapping("/ignore/shareList")
    public R<List<ShareUserVo>> shareList() {
        ShareUserBo shareUserBo = new ShareUserBo();
        shareUserBo.setPlatformKey(ZlyyhUtils.getPlatformId());
        shareUserBo.setStatus("0");
        shareUserBo.setAuditStatus("1");
        shareUserBo.setParentId(0L);
        List<ShareUserVo> shareUserVos = iShareUserService.queryList(shareUserBo);
        return R.ok(shareUserVos);
    }

    /**
     * 查询推广次数
     */
    @GetMapping("/shareCount")
    public R<Long> shareCount() {
        return R.ok(shareUserRecordService.shareCount(LoginHelper.getUserId()));
    }

    /**
     * 查询推广记录
     */
    @GetMapping("/shareLogList")
    public TableDataInfo<ShareUserRecordVo> shareLogList(ShareUserRecordBo bo, PageQuery pageQuery) {
        bo.setUserId(LoginHelper.getUserId());
        TableDataInfo<ShareUserRecordVo> shareUserRecordVoTableDataInfo = shareUserRecordService.queryPageList(bo, pageQuery);
        for (ShareUserRecordVo row : shareUserRecordVoTableDataInfo.getRows()) {
            UserVo userVo = userService.queryById(row.getInviteeUserId(), ZlyyhUtils.getPlatformChannel());
            if (null != userVo) {
                row.setInviteeUserMobile(userVo.getMobile());
            }
        }
        return shareUserRecordVoTableDataInfo;
    }

    /**
     * 生成微信二维码
     */
    @GetMapping("/genShareQrCode")
    public R<String> genShareQrCode(GenWxQrCodeBo genWxQrCodeBo) {
        // 生成key
        PlatformVo platformVo = platformService.queryById(ZlyyhUtils.getPlatformId(), PlatformEnumd.MP_WX.getChannel());
        if (null == platformVo) {
            return R.fail("访问错误[platform]");
        }
        String accessToken = WxUtils.getAccessToken(platformVo.getAppId(), platformVo.getSecret(), wxProperties.getAccessTokenUrl());
        if (StringUtils.isBlank(accessToken)) {
            return R.fail("系统繁忙，请稍后重试");
        }
        byte[] bytes = WxUtils.genQrCode(accessToken, genWxQrCodeBo.getScene(), genWxQrCodeBo.getPage(), genWxQrCodeBo.getEnv_version());
        return R.ok("操作成功", Base64.encode(bytes));
    }
}
