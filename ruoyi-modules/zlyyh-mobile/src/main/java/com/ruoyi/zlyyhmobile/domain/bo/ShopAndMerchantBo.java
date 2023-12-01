package com.ruoyi.zlyyhmobile.domain.bo;

import com.ruoyi.zlyyh.domain.ShopMerchant;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopAndMerchantBo implements Serializable {
    private CommercialTenantBo commercialTenant;
    private ShopBo shop;
    private List<ShopMerchant> ysfMerchant;
    private List<ShopMerchant> wxMerchant;
    private List<ShopMerchant> payMerchant;
}
