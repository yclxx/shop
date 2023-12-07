package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.CodeBo;
import com.ruoyi.zlyyh.domain.vo.CodeVo;

import java.util.List;
import java.util.Map;

/**
 * 商品券码Service接口
 *
 * @author yzg
 * @date 2023-09-20
 */
public interface ICodeService {
    /**
     * 核销人员查询券码
     */
    Map<String,Object> queryVerifierByCodeNo(CodeBo bo, Long userId);

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
     * @param codeNo 券码
     * @return true 成功，false 失败
     */
    Boolean usedCode(String codeNo);


    /**
     * 商户端核销
     */
    Boolean usedCodes(CodeBo bo);

    /**
     * 票券返还
     *
     * @param codeNo 核销码
     * @return true 成功,false 失败
     */
    Boolean rollbackCode(String codeNo);
    /**
     * 今日核销，今日预约统计
     */
    Map<String, Long> getCodeTimeCount();

    /**
     * 商品预约核销统计
     */
    List<CodeVo> statistics(CodeBo bo);
    /**
     * 核销码查询（核销人员）
     */
    TableDataInfo<CodeVo> getCodeListByVerifier(CodeBo bo, PageQuery pageQuery);
}
