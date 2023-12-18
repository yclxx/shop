package com.ruoyi.zlyyhmobile.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.SendDyInfoBo;
import com.ruoyi.zlyyh.domain.vo.SendDyInfoVo;
import com.ruoyi.zlyyh.domain.vo.UserVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ISendDyInfoService;
import com.ruoyi.zlyyhmobile.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户订阅控制器
 * 前端访问路由地址为:/zlyyh-mobile/sendDyInfo
 *
 * @author yzg
 * @date 2023-12-07
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/sendDyInfo")
public class SendDyInfoController extends BaseController {

    private final ISendDyInfoService iSendDyInfoService;
    private final IUserService userService;

    /**
     * 新增用户订阅
     */
    @PostMapping("/ignore/add")
    public R<Void> add(@RequestBody SendDyInfoBo bo) {
        Long userId;
        try {
            userId = LoginHelper.getUserId();
        } catch (Exception e) {
            log.info("用户订阅消息失败，用户未登录或登录超时：", e);
            return R.ok();
        }
        UserVo userVo = userService.queryById(userId, ZlyyhUtils.getPlatformChannel());
        if (null == userVo || StringUtils.isBlank(userVo.getOpenId())) {
            log.info("用户订阅消息失败，用户不存在或openId缺失,{}", userVo);
            return R.ok();
        }
        bo.setPlatformKey(ZlyyhUtils.getPlatformId());
        bo.setUserId(userId);
        bo.setOpenId(userVo.getOpenId());
        List<SendDyInfoVo> sendDyInfoVos = iSendDyInfoService.queryList(bo);
        if (ObjectUtil.isEmpty(sendDyInfoVos)) {
            bo.setDyCount(1L);
            return toAjax(iSendDyInfoService.insertByBo(bo));
        } else {
            SendDyInfoVo sendDyInfoVo = sendDyInfoVos.get(sendDyInfoVos.size() - 1);
            bo.setId(sendDyInfoVo.getId());
            bo.setDyCount(sendDyInfoVo.getDyCount() + 1);
            return toAjax(iSendDyInfoService.updateByBo(bo));
        }
    }
}
