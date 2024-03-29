package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.annotation.DataColumn;
import com.ruoyi.common.mybatis.annotation.DataPermission;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@DataPermission({
    @DataColumn(key = "deptName", value = "sys_dept_id"),
    @DataColumn(key = "userName", value = "sys_user_id")
})
public interface ProductMapper extends BaseMapperPlus<ProductMapper, Product, ProductVo> {
    Page<ProductVo> selectPageByShopId(@Param("page") Page<Product> page, @Param("shopIds")List<Long> shopIds);
}
