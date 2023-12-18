package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteDictService;
import com.ruoyi.system.api.domain.SysDictData;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dict")
public class DictController {

    @DubboReference
    private RemoteDictService remoteDictService;

    /**
     * 查询字典列表
     */
    @GetMapping("/ignore/list/{dictType}")
    public R<List<SysDictData>> list(@PathVariable String dictType) {
        return R.ok(remoteDictService.selectDictDataByType(dictType));
    }
}
