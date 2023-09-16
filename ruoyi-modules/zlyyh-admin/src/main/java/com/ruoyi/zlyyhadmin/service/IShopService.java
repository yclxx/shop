package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import com.ruoyi.zlyyhadmin.domain.bo.ShopImportDataBo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 门店Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IShopService {

    /**
     * 项目启动初始化门店缓存
     */
    void loadingShopCache();

    /**
     * 查询门店
     */
    ShopVo queryById(Long shopId);

    /**
     * 查询门店列表
     */
    TableDataInfo<ShopVo> queryPageList(ShopBo bo, PageQuery pageQuery);

    /**
     * 查询门店列表
     */
    List<ShopVo> queryList(ShopBo bo);

    /**
     * 修改门店
     */
    Boolean insertByBo(ShopBo bo);

    Boolean insertShop(ShopBo bo);

    /**
     * 修改门店
     */
    Boolean updateByBo(ShopBo bo);

    /**
     * 校验并批量删除门店信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    void importShopData(MultipartFile file, ShopImportDataBo shopImportDataBo) throws IOException;

    ShopVo queryByNameAndAddress(String shopName, String address, Long platformKey);

    List<ShopVo> queryByCommercialTenantId(Long commercialTenantId);

    ShopVo queryByNameAndCommercialTenantId(String name,Long commercialTenantId);

    ShopVo queryByNameAndSupplierId(String name,String supplierShopId);
}
