package com.ruoyi.zlyyh.utils.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AcpService
 * @Description acpsdk接口服务类，接入商户集成请可以直接参考使用本类中的方法
 * @date 2016-7-22 下午2:44:37
 * 声明：以下代码只是为了方便接入方测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障
 */
public class AcpService {

    /**
     * 多证书签名(通过传入私钥证书路径和密码签名）<br>
     * 功能：如果有多个商户号接入银联,每个商户号对应不同的证书可以使用此方法:传入私钥证书和密码(并且在acp_sdk.properties中 配置 acpsdk.singleMode=false)<br>
     *
     * @param reqData  请求报文map<br>
     * @param certPath 签名私钥文件（带路径）<br>
     * @param certPwd  签名私钥密码<br>
     * @param encoding 上送请求报文域encoding字段的值<br>
     * @return　签名后的map对象<br>
     */
    public static Map<String, String> signByCertInfo(Map<String, String> reqData, String certPath,
                                                     String certPwd, String encoding) {
        reqData = SDKUtil.filterBlank(reqData);
        SDKUtil.signByCertInfo(reqData, certPath, certPwd, encoding);
        return reqData;
    }

    /**
     * 验证签名(SHA-1摘要算法)<br>
     *
     * @param rspData  返回报文数据<br>
     * @param encoding 上送请求报文域encoding字段的值<br>
     * @return true 通过 false 未通过<br>
     */
    public static boolean validate(Map<String, String> rspData, String encoding, boolean showLog) {
        return SDKUtil.validate(rspData, encoding, showLog);
    }

    /**
     * 功能：后台交易提交请求报文并接收同步应答报文<br>
     *
     * @param reqData      请求报文<br>
     * @param reqUrl       请求地址<br>
     * @param encoding<br>
     * @return 应答http 200返回true ,其他false<br>
     */
    public static Map<String, String> post(
        Map<String, String> reqData, String reqUrl, String encoding) {
        Map<String, String> rspData = new HashMap<>();
        LogUtil.writeLog("请求银联地址:" + reqUrl);
        //发送后台请求数据 连接超时时间，读超时时间（可自行判断，修改）
        HttpClient hc = new HttpClient(reqUrl, 30000, 30000);
        try {
            int status = hc.send(reqData, encoding);
            if (200 == status) {
                String resultString = hc.getResult();
                if (null != resultString && !"".equals(resultString)) {
                    // 将返回结果转换为map
                    Map<String, String> tmpRspData = SDKUtil.convertResultStringToMap(resultString);
                    rspData.putAll(tmpRspData);
                }
            } else {
                LogUtil.writeLog("返回http状态码[" + status + "]，请检查请求报文或者请求地址是否正确");
            }
        } catch (Exception e) {
            LogUtil.writeErrorLog(e.getMessage(), e);
        }
        return rspData;
    }
}
