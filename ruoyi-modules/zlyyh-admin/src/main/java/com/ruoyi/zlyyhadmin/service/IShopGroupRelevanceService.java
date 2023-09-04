package com.ruoyi.zlyyhadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.zlyyh.domain.ShopGroupRelevance;
import com.ruoyi.zlyyh.domain.vo.ShopGroupRelevanceVo;
import com.ruoyi.zlyyh.domain.bo.ShopGroupRelevanceBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 门店组门店关联Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IShopGroupRelevanceService {

    /**
     * 查询门店组门店关联
     */
    ShopGroupRelevanceVo queryById(Long id);

    /**
     * 查询门店组门店关联列表
     */
    TableDataInfo<ShopGroupRelevanceVo> queryPageList(ShopGroupRelevanceBo bo, PageQuery pageQuery);

    /**
     * 查询门店组门店关联列表
     */
    List<ShopGroupRelevanceVo> queryList(ShopGroupRelevanceBo bo);

    /**
     * 修改门店组门店关联
     */
    Boolean insertByBo(ShopGroupRelevanceBo bo);

    /**
     * 修改门店组门店关联
     */
    Boolean updateByBo(ShopGroupRelevanceBo bo);

    /**
     * 校验并批量删除门店组门店关联信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean remove(LambdaQueryWrapper<ShopGroupRelevance> queryWrapper);
}
