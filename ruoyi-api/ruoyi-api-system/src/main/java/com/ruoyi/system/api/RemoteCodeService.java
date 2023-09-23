package com.ruoyi.system.api;

/**
 * 券码服务
 *
 * @author Lion Li
 */
public interface RemoteCodeService {

    /**
     * 作废券码
     *
     * @param codeNo 核销码
     * @return true 成功，false 失败
     */
    Boolean cancellationCode(String codeNo);

    /**
     * 核销券码
     *
     * @param codeNo 核销码
     * @return true 核销成功，false 核销失败
     */
    Boolean usedCode(String codeNo);

    /**
     * 票券返还
     *
     * @param codeNo 核销码
     * @return true 返还成功，false 返还失败
     */
    Boolean rollbackCode(String codeNo);
}
