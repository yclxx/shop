package com.ruoyi.zlyyhmobile.domain.vo;

import com.ruoyi.zlyyh.domain.vo.ShareUserAccountVo;
import com.ruoyi.zlyyh.domain.vo.ShareUserVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShareUserInfoVo implements Serializable {
    private ShareUserVo shareUserVo;
    private ShareUserAccountVo shareUserAccountVo;
}
