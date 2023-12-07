package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.CodeBo;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyhmobile.service.ICodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 核销信息表
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
public class CodeController extends BaseController {
    private final ICodeService codeService;

    /**
     * 查询核销码
     */
    @GetMapping("/queryByNo")
    public R<Map<String, Object>> queryByNo(CodeBo bo) {
        Long userId = LoginHelper.getUserId();
        return R.ok(codeService.queryVerifierByCodeNo(bo, userId));
    }

    /**
     * 开始核销
     */
    @PostMapping("/verifierCode")
    public R verifierCode(@RequestBody CodeBo bo) {
        return R.ok(codeService.usedCodes(bo));
    }


    /**
     * 今日核销，预约查询
     */
    @GetMapping("/getCodeTimeCount")
    public R<Map<String, Long>> getCodeTimeCount() {
        return R.ok(codeService.getCodeTimeCount());
    }

    /**
     * 核销码明细查询查询
     */
    @GetMapping("/getCodeListByVerifier")
    public TableDataInfo<CodeVo> getCodeListByVerifier(CodeBo bo, PageQuery pageQuery) {
        bo.setVerifierId(LoginHelper.getUserId());
        return codeService.getCodeListByVerifier(bo, pageQuery);
    }

    /**
     * 核销统计查询
     */
    @PostMapping("/statistics")
    public R statistics(@RequestBody CodeBo bo) {
        bo.setVerifierId(LoginHelper.getUserId());
        List<CodeVo> codeVoList = codeService.statistics(bo);
        return R.ok(codeVoList);
    }
}
