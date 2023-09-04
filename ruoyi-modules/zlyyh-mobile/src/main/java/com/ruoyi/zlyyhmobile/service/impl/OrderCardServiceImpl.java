package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.OrderCard;
import com.ruoyi.zlyyh.domain.vo.OrderCardVo;
import com.ruoyi.zlyyh.mapper.OrderCardMapper;
import com.ruoyi.zlyyhmobile.service.IOrderCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单卡密Service业务层处理
 *
 * @author yzg
 * @date 2023-05-31
 */
@RequiredArgsConstructor
@Service
public class OrderCardServiceImpl implements IOrderCardService {

    private final OrderCardMapper baseMapper;

    /**
     * 查询订单卡密列表
     */
    @Override
    public List<OrderCardVo> queryListByNumber(Long number) {
        LambdaQueryWrapper<OrderCard> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderCard::getNumber, number);
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public void save(JSONArray cards, Long number, String usedType) {
        if (ObjectUtil.isEmpty(cards)) {
            return;
        }
        for (int i = 0; i < cards.size(); i++) {
            OrderCard orderCard = cards.getObject(i, OrderCard.class);
            orderCard.setNumber(number);
            orderCard.setUsedType(usedType);
            // 查询是否存在，存在则修改，不存在新增
            LambdaQueryWrapper<OrderCard> lqw = Wrappers.lambdaQuery();
            lqw.eq(OrderCard::getNumber, number);
            lqw.eq(OrderCard::getCardPwd, orderCard.getCardPwd());
            lqw.last("limit 1");
            OrderCard card = baseMapper.selectOne(lqw);
            if (null == card) {
                baseMapper.insert(orderCard);
            } else {
                orderCard.setOrderCardId(card.getOrderCardId());
                baseMapper.updateById(orderCard);
            }
        }
    }
}
