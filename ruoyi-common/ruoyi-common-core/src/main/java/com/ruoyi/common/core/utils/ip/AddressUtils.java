package com.ruoyi.common.core.utils.ip;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取地址类
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtils {

    /**
     * 获取地址的详细信息，经纬度等
     *
     * @param address 需要获取经纬度的地址信息
     * @return 返回结果
     */
    public static JSONObject getAddressInfo(String address) throws JSONException {
        // 地址非法字符处理
        String regEx = "[\t\r\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
        //可以在中括号内加上任何想要替换的字符，实际上是一个正则表达式
        String aa = "";//这里是将特殊字符换为aa字符串,""代表直接去掉
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(address);//这里把想要替换的字符串传进来
        address = m.replaceAll(aa).trim();
        log.info("获取地址经纬度信息接收参数：address={}", address);
        // 基于高德地图 地理/逆地理编码接口
        String url = "https://restapi.amap.com/v3/geocode/geo";
        // key 来自个人注册的高德地图开放平台用户创建的，如果有问题，请直接自行去高德地图注册个人用户，或者企业用户
        // 基于企业用户认证信息太多，目前key取自个人用户
        String params = "?key=ed8652067f054abb156de40a110a08ea&address=" + address;

        String s = HttpUtil.get(url + params);
        log.info("调用高德地图获取地址经纬度信息返回结果：{}", s);
        s = s.replaceAll("\\[\\]", "\"\"");
        log.info("调用高德地图获取地址经纬度信息处理后结果：{}", s);
        // 返回结果
        // {"status":"1","info":"OK","infocode":"10000","count":"1","geocodes":[{"formatted_address":"浙江省杭州市拱墅区善贤人家","country":"中国","province":"浙江省","citycode":"0571","city":"杭州市","district":"拱墅区","township":[],"neighborhood":{"name":[],"type":[]},"building":{"name":[],"type":[]},"adcode":"330105","street":[],"number":[],"location":"120.161485,30.317090","level":"兴趣点"}]}
        JSONObject resultObject = JSONObject.parseObject(s);
        if (null == resultObject) {
            return null;
        }
        if ("0".equals(resultObject.getString("status"))) {
            log.info("获取经纬度信息失败，错误信息：" + resultObject.getString("info"));
            return null;
        }
        // 请求成功获取数据
        JSONArray geocodes = resultObject.getJSONArray("geocodes");
        if (null == geocodes || geocodes.isEmpty()) {
            log.info("获取经纬度信息失败");
            return null;
        }
        // 由于我们目前只需要单个地址的查询，暂不考虑多个地址的情况
        // 获取第一个结果
        JSONObject data = geocodes.getJSONObject(0);

        // 结构化地址信息 省份＋城市＋区县＋城镇＋乡村＋街道＋门牌号码
        String formatted_address = data.getString("formatted_address");
        // country 国家 国内地址默认返回中国
        String country = data.getString("country");
        // province 地址所在的省份名 例如：北京市。此处需要注意的是，中国的四大直辖市也算作省级单位
        String province = data.getString("province");
        // city 地址所在的城市名 例如：北京市
        String city = data.getString("city");
        // citycode 城市编码 例如杭州：0571
        String citycode = data.getString("citycode");
        // district 地址所在的区 例如：西湖区
        String district = data.getString("district");
        // adcode 区域编码
        String adcode = data.getString("adcode");
        // location 坐标点 经度，纬度,中间采用的是英文逗号隔开的
        String location = data.getString("location");
        // 获取经度，纬度
        String[] split = location.split(",");
        // 经度
        String longitude = split[0];
        // 纬度
        String latitude = split[1];

        return data;
    }
}
