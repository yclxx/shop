package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.bo.ShopBo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;
import org.apache.ibatis.annotations.Param;

/**
 * 门店Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface ShopMapper extends BaseMapperPlus<ShopMapper, Shop, ShopVo> {

    /**
     * 查询列表（按照地址排序）
     */
    Page<ShopVo> selectShopList(Page page, @Param("bo") ShopBo bo);
    /**
     * 查询列表（按照地址排序）
     */
    Page<ShopVo> selectShopListByCommercialTenantId(Page page, @Param("bo") ShopBo bo);
    /**
     * 查询列表（按照地址排序）
     */
    Page<ShopVo> selectShopListByProductId(Page page, @Param("bo") ShopBo bo);
}
