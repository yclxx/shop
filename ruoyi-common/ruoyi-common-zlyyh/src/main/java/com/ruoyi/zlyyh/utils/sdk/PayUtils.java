package com.ruoyi.zlyyh.utils.sdk;

import cn.hutool.core.codec.Base64;
import com.ruoyi.common.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 支付帮助类
 *
 * @author xiexi
 * @date 2020-11-27
 */
public class PayUtils {

    /**
     * 云闪付支付
     *
     * @param number      订单号
     * @param txnAmt      支付金额，单位分 不能带小数点
     * @param backUrl     支付成功回调地址
     * @param merchantNo  支付商户号
     * @param merchantKey 证书密码
     * @param certName    证书路径名称
     * @return 支付tn号
     */
    public static String pay(String number, String txnAmt, String backUrl, String merchantNo, String merchantKey, String certName, String acqAddnData, Date payTimeOut) {
        Map<String, String> contentData = new HashMap<>();
        // 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改
        // 版本号 全渠道默认值
        contentData.put("version", UnionpayBese.version);
        // 字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put("encoding", UnionpayBese.encoding);
        // 签名方法
        contentData.put("signMethod", SDKConfig.getSignMethod());
        // 交易类型 01:消费
        contentData.put("txnType", "01");
        // 交易子类 01：消费
        contentData.put("txnSubType", "01");
        // 填写000201
        contentData.put("bizType", "000201");
        // 渠道类型 08手机
        contentData.put("channelType", "08");
        // 商户接入参数
        // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put("merId", merchantNo);
        // 接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
        contentData.put("accessType", "0");
        // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put("orderId", number);
        // 订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put("txnTime", YinLianUtil.getDateFormYYYYMMDDhhmmss());
        // 支付超时时间，格式为YYYYMMDDhhmmss
        if (null != payTimeOut) {
            contentData.put("payTimeout", YinLianUtil.getDateFormYYYYMMDDhhmmss(payTimeOut));
        }
        // 账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
        contentData.put("accType", "01");
        // 交易金额 单位为分，不能带小数点
        contentData.put("txnAmt", txnAmt);
        // 境内商户固定 156 人民币
        contentData.put("currencyCode", "156");
        if (StringUtils.isNotEmpty(acqAddnData)) {
            LogUtil.writeLog("单品营销参数:" + acqAddnData);
            // 订单详细信息（支持单品）
            contentData.put("acqAddnData", Base64.encode(acqAddnData));
        }
        /* 请求方保留域，
         透传字段，查询、通知、对账文件中均会原样出现，如有需要请启用并修改自己希望透传的数据。
         出现部分特殊字符时可能影响解析，请按下面建议的方式填写：
         1. 如果能确定内容不会出现&={}[]"'等符号时，可以直接填写数据，建议的方法如下。
         contentData.put("reqReserved",  "透传信息1|透传信息2|透传信息3");
         2. 内容可能出现&={}[]"'符号时：
         1) 如果需要对账文件里能显示，可将字符替换成全角＆＝｛｝【】“‘字符（自己写代码，此处不演示）；
         2) 如果对账文件没有显示要求，可做一下base64（如下）。
          注意控制数据长度，实际传输的数据长度不能超过1024位。
          查询、通知等接口解析时使用new  String(Base64.decodeBase64(reqReserved), UnionpayBese.encoding);解base64后再对数据做后续解析。
         contentData.put("reqReserved",  Base64.encodeBase64String("任意格式的信息都可以".toString().getBytes(UnionpayBese.encoding)));
         后台通知地址（需设置为外网能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，【支付失败的交易银联不会发送后台通知】
         后台通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 网关支付产品接口规范 消费交易 商户通知
         注意:1.需设置为外网能访问，否则收不到通知 2.http  https均可 3.收单后台通知后需要10秒内返回http200或302状态码
         4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200或302，那么银联会间隔一段时间再次发送。总共发送5次，银联后续间隔1、2、4、5 分钟后会再次通知。
         5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
        */
        contentData.put("backUrl", backUrl);
        // 前台通知
        contentData.put("frontUrl", UnionpayBese.frontUrl);
        /* 对请求参数进行签名并发送http post请求，接收同步应答报文*/
        Map<String, String> reqData = AcpService.signByCertInfo(contentData, certName, merchantKey, UnionpayBese.encoding); //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestAppUrl = SDKConfig.getAppTransUrl();  //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestAppUrl, UnionpayBese.encoding); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        /* 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->*/
        // 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionpayBese.encoding, false)) {
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    //成功,获取tn号
                    String tn = rspData.get("tn");
                    LogUtil.writeLog("银联云闪付支付【" + merchantNo + "】，返回tn号：" + tn);
                    return tn;
                } else {
                    //其他应答码为失败请排查原因或做失败处理
                    LogUtil.writeLog(rspData.toString());
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        return null;
    }

    /**
     * 查询支付订单
     *
     * @param number      订单号
     * @param txnTime     订单发送时间，格式yyyyMMddHHmmss
     * @param merchantNo  商户号
     * @param merchantKey 证书密码
     * @param certName    证书路径名称
     * @return 返回结果
     */
    public static Map<String, String> queryOrder(String number, String txnTime, String merchantNo, String merchantKey, String certName) {
        Map<String, String> data = new HashMap<>();
        /* 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 */
        // 版本号
        data.put("version", UnionpayBese.version);
        // 字符集编码
        data.put("encoding", UnionpayBese.encoding);
        // 签名方法
        data.put("signMethod", SDKConfig.getSignMethod());
        // 交易类型
        data.put("txnType", "00");
        // 交易子类型
        data.put("txnSubType", "00");
        // 业务类型
        data.put("bizType", "000000");
        /* 商户接入参数 */
        // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("merId", merchantNo);
        // 接入类型，商户接入固定填0，不需修改
        data.put("accessType", "0");
        /* 要调通交易以下字段必须修改 */
        // ****商户订单号，每次发交易测试需修改为被查询的交易的订单号
        data.put("orderId", number);
        // ****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间
        data.put("txnTime", txnTime);
        /* 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> */
        // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        Map<String, String> reqData = AcpService.signByCertInfo(data, certName, merchantKey, UnionpayBese.encoding);
        // 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
        String url = SDKConfig.getSingleQueryUrl();
        // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        Map<String, String> rspData = AcpService.post(reqData, url, UnionpayBese.encoding);
        String reqMessage = UnionpayBese.genHtmlResult(reqData);
        String rspMessage = UnionpayBese.genHtmlResult(rspData);
        LogUtil.writeErrorLog("请求报文:<br> " + reqMessage + "<br> " + "应答报文:<br> " + rspMessage + "<br> ");
        /* 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> */
        //应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionpayBese.encoding, false)) {
                LogUtil.writeLog("验证签名成功");
                if (("00").equals(rspData.get("respCode"))) { //如果查询交易成功
                    return rspData;
                } else if (("34").equals(rspData.get("respCode"))) {
                    //订单不存在，可认为交易状态未明，需要稍后发起交易状态查询，或依据对账结果为准
                    LogUtil.writeErrorLog("订单不存在。<br> ");
                } else {//查询交易本身失败，如应答码10/11检查查询报文是否正确
                    LogUtil.writeErrorLog("查询订单失败：" + rspData.get("respMsg") + "。<br> ");
                }
            } else {
                LogUtil.writeErrorLog("验证签名失败");
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        return null;
    }

//    public static void main(String[] args) {
//        Map<String, String> data = new HashMap<String, String>();
//        /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
//        data.put("version", "5.1.0"); //版本号
//        data.put("encoding", "UTF-8"); //字符集编码
//        data.put("signMethod", "01"); //签名方法
//        data.put("txnType", "00"); //交易类型
//        data.put("txnSubType", "00"); //交易子类型
//        data.put("bizType", "000000"); //业务类型
//        /***商户接入参数***/
//        data.put("merId", "898991148166607");  //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
//        data.put("accessType", "0"); //接入类型，商户接入固定填0，不需修改
//        /***要调通交易以下字段必须修改***/
//        data.put("orderId", "1679680315372453888"); //****商户订单号，每次发交易测试需修改为被查询的交易的订单号
//        data.put("txnTime", "20230714103349"); //****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间
//        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->**/
//        Map<String, String> reqData = AcpService.signByCertInfo(data, "https://zjyhjy.95516.com/zlyyh/zlyyh-gateway/resource/static/2023/04/14/2f6f7799-490f-4893-ac23-171af583d5e9.pfx", "190190", "UTF-8"); //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
//        String url = "https://gateway.95516.com/gateway/api/queryTrans.do"; //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
//        Map<String, String> rspData = AcpService.post(reqData, url, "UTF-8"); //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
//        System.out.println(rspData);
//    }

    /**
     * 银联支付退货接口
     *
     * @param queryId        原始消费交易的queryId，由银联支付回调成功的queryId,或者通过查询接口查询queryId  t_order 表中的queryId
     * @param txnAmt         退货金额 单位分
     * @param newOrderNumber 退款订单号，新生成的订单号，作为退款订单号
     * @param backUrl        回调通知地址（退货回调通知地址，和支付回调通知不同）
     * @param merchantNo     支付商户号
     * @param merchantKey    证书密码
     * @param certName       证书路径名称
     * @return 返回结果
     */
    public static Map<String, String> backTransReq(String queryId, String txnAmt, String newOrderNumber, String backUrl, String merchantNo, String merchantKey, String certName, String acqAddnData) {
        Map<String, String> data = new HashMap<>();
        /* 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 */
        // 版本号
        data.put("version", UnionpayBese.version);
        // 字符集编码 可以使用UTF-8,GBK两种方式
        data.put("encoding", UnionpayBese.encoding);
        // 签名方法
        data.put("signMethod", SDKConfig.getSignMethod());
        // 交易类型 04-退货
        data.put("txnType", "04");
        // 交易子类型 默认00
        data.put("txnSubType", "00");
        // 业务类型
        data.put("bizType", "000201");
        // 渠道类型，07-PC，08-手机
        data.put("channelType", "08");
        /* 商户接入参数 */
        // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("merId", merchantNo);
        // 接入类型，商户接入固定填0，不需修改
        data.put("accessType", "0");
        // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
        data.put("orderId", newOrderNumber);
        // 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        data.put("txnTime", UnionpayBese.getCurrentTime());
        // 交易币种（境内商户一般是156 人民币）
        data.put("currencyCode", "156");
        // ****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
        data.put("txnAmt", txnAmt);
        // 后台通知地址，后台通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 网关支付产品接口规范 退货交易 商户通知,其他说明同消费交易的后台通知
        data.put("backUrl", backUrl);
        if (StringUtils.isNotEmpty(acqAddnData)) {
            LogUtil.writeLog("单品营销参数:" + acqAddnData);
            data.put("acqAddnData", Base64.encode(acqAddnData)); //订单详细信息（支持单品）
        }
        /* 要调通交易以下字段必须修改 */
        // ****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取
        data.put("origQryId", queryId);
        /*
         请求方保留域，
         透传字段，查询、通知、对账文件中均会原样出现，如有需要请启用并修改自己希望透传的数据。
         出现部分特殊字符时可能影响解析，请按下面建议的方式填写：
         1. 如果能确定内容不会出现&={}[]"'等符号时，可以直接填写数据，建议的方法如下。
         data.put("reqReserved", "透传信息1|透传信息2|透传信息3");
         2. 内容可能出现&={}[]"'符号时：
         1) 如果需要对账文件里能显示，可将字符替换成全角＆＝｛｝【】“‘字符（自己写代码，此处不演示）；
         2) 如果对账文件没有显示要求，可做一下base64（如下）。
         注意控制数据长度，实际传输的数据长度不能超过1024位。
         查询、通知等接口解析时使用new String(Base64.decodeBase64(reqReserved), DemoBase.encoding);解base64后再对数据做后续解析。
         data.put("reqReserved", Base64.encodeBase64String("任意格式的信息都可以".toString().getBytes(DemoBase.encoding)));
        */
        /* 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> */
        // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        Map<String, String> reqData = AcpService.signByCertInfo(data, certName, merchantKey, UnionpayBese.encoding);
        // 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        String url = SDKConfig.getBackTransUrl();
        // 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
        Map<String, String> rspData = AcpService.post(reqData, url, UnionpayBese.encoding);
        /* 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> */
        // 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionpayBese.encoding, false)) {
                LogUtil.writeLog("验证签名成功");
                return rspData;
            } else {
                LogUtil.writeErrorLog("验证签名失败");
            }
        } else {
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
        }
        String reqMessage = UnionpayBese.genHtmlResult(reqData);
        String rspMessage = UnionpayBese.genHtmlResult(rspData);
        LogUtil.writeLog("请求报文:<br/>" + reqMessage + "<br/>" + "应答报文:</br>" + rspMessage + "");
        return null;
    }

    /**
     * 获取请求参数中所有的信息
     *
     * @param request 请求信息
     * @return 返回结果
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request, String encoding) throws UnsupportedEncodingException {
        Map<String, String> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        LogUtil.printRequestLog(res);
        Map<String, String> valideData = null;
        if (!res.isEmpty()) {
            Iterator<Map.Entry<String, String>> it = res.entrySet().iterator();
            valideData = new HashMap<>(res.size());
            while (it.hasNext()) {
                Map.Entry<String, String> e = it.next();
                String key = e.getKey();
                String value = e.getValue();
                value = new String(value.getBytes(encoding), encoding);
                valideData.put(key, value);
            }
        }
        return valideData;
    }
}
