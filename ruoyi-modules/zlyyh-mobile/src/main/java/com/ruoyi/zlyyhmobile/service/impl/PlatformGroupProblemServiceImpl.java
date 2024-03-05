package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.zlyyh.domain.PlatformGroupProblem;
import com.ruoyi.zlyyh.domain.bo.PlatformGroupProblemBo;
import com.ruoyi.zlyyh.mapper.PlatformGroupProblemMapper;
import com.ruoyi.zlyyhmobile.service.IPlatformGroupProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 企业微信二维码Service业务层处理
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@Service
public class PlatformGroupProblemServiceImpl implements IPlatformGroupProblemService {

    private final PlatformGroupProblemMapper platformGroupProblemMapper;

    /**
     * 新增
     */
    @Override
    public void insertByBo(PlatformGroupProblemBo bo) {
        PlatformGroupProblem platformGroupProblem = BeanUtil.toBean(bo, PlatformGroupProblem.class);
        platformGroupProblemMapper.insert(platformGroupProblem);
    }
}
