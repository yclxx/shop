package com.ruoyi.zlyyhmobile.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import com.ruoyi.zlyyh.domain.vo.PlatformCityIndexVo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 平台信息视图对象
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@Data
public class AppPlatformVo {

    private static final long serialVersionUID = 1L;

    /**
     * 平台标识
     */
    private Long platformKey;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 小程序标题
     */
    private String platformTitle;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * appId
     */
    private String appId;

    /**
     * 小程序ID
     */
    private String encryptAppId;

    /**
     * 云闪付62会员权限：0-无权限，1-有权限
     */
    private String unionPayVip;

    /**
     * 客服电话
     */
    @ExcelProperty(value = "客服电话")
    private String serviceTel;

    /**
     * 客服服务时间
     */
    @ExcelProperty(value = "客服服务时间")
    private String serviceTime;

    /**
     * 活动城市：ALL-全部、否则填城市行政区号，多个之间用英文逗号隔开
     */
    @ExcelProperty(value = "活动城市：ALL-全部、否则填城市行政区号，多个之间用英文逗号隔开")
    private String platformCity;

    /**
     * 默认支付商户
     */
    @ExcelProperty(value = "默认支付商户")
    private Long merchantId;

    /**
     * 活动城市
     */
    List<AreaVo> platformCityList;

    /**
     * 平台城市首页
     */
    Map<String, PlatformCityIndexVo> cityIndex;
}
