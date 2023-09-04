package com.ruoyi.zlyyhmobile.service;

import com.alibaba.fastjson.JSONArray;
import com.ruoyi.zlyyh.domain.vo.OrderCardVo;

import java.util.List;

/**
 * 订单卡密Service接口
 *
 * @author yzg
 * @date 2023-05-31
 */
public interface IOrderCardService {

    /**
     * 查询订单卡密列表
     */
    List<OrderCardVo> queryListByNumber(Long number);

    /**
     * 保存卡密信息
     *
     * @param cards    卡密信息
     * @param number   订单号
     * @param usedType 使用方式
     */
    void save(JSONArray cards, Long number, String usedType);
}
