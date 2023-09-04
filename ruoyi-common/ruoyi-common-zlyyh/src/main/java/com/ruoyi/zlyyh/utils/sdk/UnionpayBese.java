package com.ruoyi.zlyyh.utils.sdk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * 名称： demo中用到的方法<br>
 * 日期： 2015-09<br>
 * 版权： 中国银联<br>
 * 声明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码，性能，规范性等方面的保障<br>
 */
public class UnionpayBese {

    /**
     * 默认配置的是UTF-8
     */
    public static String encoding = "UTF-8";

    /**
     * 全渠道固定值
     */
    public static String version = SDKConfig.getVersion();

    /**
     * 后台服务对应的写法参照 FrontRcvResponse.java
     */
    public static String frontUrl = SDKConfig.getFrontUrl();

    /**
     * 商户发送交易时间 格式:yyyyMMddHHmmss
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 组装请求，返回报文字符串用于显示
     *
     * @param data 数据集合
     * @return 结果
     */
    public static String genHtmlResult(Map<String, String> data) {

        TreeMap<String, String> tree = new TreeMap<>();
        Iterator<Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuilder sf = new StringBuilder();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            if ("respCode".equals(key)) {
                sf.append("<b>").append(key).append(SDKConstants.EQUAL).append(value).append("</br></b>");
            } else {
                sf.append(key).append(SDKConstants.EQUAL).append(value).append("</br>");
            }
        }
        return sf.toString();
    }
}
