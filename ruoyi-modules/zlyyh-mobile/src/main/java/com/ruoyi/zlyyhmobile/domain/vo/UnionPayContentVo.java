package com.ruoyi.zlyyhmobile.domain.vo;

import com.ruoyi.zlyyh.domain.vo.DistributorVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 25487
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnionPayContentVo {
    private String version;
    private String appId;
    private String bizMethod;
    private String sign;
    private String signMethod;
    private String reqId;
    private DistributorVo distributorVo;
    private String body;
    private String signValue;
}
