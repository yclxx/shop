package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.zlyyh.domain.vo.CodeVo;

import java.util.List;

/**
 * 商品券码Service接口
 *
 * @author yzg
 * @date 2023-09-20
 */
public interface ICodeService {

    /**
     * 新增商品券码
     */
    Boolean insertByOrder(Long number);

    /**
     * 查询核销码
     *
     * @param number 订单号
     * @return 核销码集合
     */
    List<CodeVo> queryByNumber(Long number);

    /**
     * 查询券码
     *
     * @param codeNo 核销码
     * @return 券码信息
     */
    CodeVo queryByCodeNo(String codeNo);

    /**
     * 作废券码
     *
     * @param codeNo 券码
     * @return 结果
     */
    boolean cancellationCode(String codeNo);

    /**
     * 核销券码
     *
     * @param codeNo 券码
     * @return true 成功，false 失败
     */
    Boolean usedCode(String codeNo);

    /**
     * 票券返还
     *
     * @param codeNo 核销码
     * @return true 成功,false 失败
     */
    Boolean rollbackCode(String codeNo);
}
