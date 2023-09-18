package com.ruoyi.zlyyhadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.BusinessDistrictShop;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.vo.BusinessDistrictShopVo;
import com.ruoyi.zlyyh.domain.bo.BusinessDistrictShopBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 商圈门店关联Service接口
 *
 * @author yzg
 * @date 2023-09-15
 */
public interface IBusinessDistrictShopService {

    /**
     * 查询商圈门店关联
     */
    BusinessDistrictShopVo queryById(Long id);

    /**
     * 查询商圈门店关联列表
     */
    TableDataInfo<BusinessDistrictShopVo> queryPageList(BusinessDistrictShopBo bo, PageQuery pageQuery);

    /**
     * 查询商圈门店关联列表
     */
    List<BusinessDistrictShopVo> queryList(BusinessDistrictShopBo bo);

    /**
     * 修改商圈门店关联
     */
    Boolean insertByBo(BusinessDistrictShopBo bo);

    /**
     * 修改商圈门店关联
     */
    Boolean updateByBo(BusinessDistrictShopBo bo);

    /**
     * 校验并批量删除商圈门店关联信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Integer deleteWithValidByShopId(Long shopId);

    Boolean remove(LambdaQueryWrapper<BusinessDistrictShop> queryWrapper);
}
