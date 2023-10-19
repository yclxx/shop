package com.ruoyi.auth.form;

import lombok.Data;

@Data
public class WxMobileLoginBody extends AppLoginBody {
    /**
     * 包括敏感数据在内的完整用户信息的加密数据，详细见加密数据解密算法
     */
    private String encryptedData;
    /**
     * 加密算法的初始向量，详细见加密数据解密算法
     */
    private String iv;
    /**
     * 临时token
     */
    private String token;
    /**
     * 用户openId
     */
    private String openId;
}
