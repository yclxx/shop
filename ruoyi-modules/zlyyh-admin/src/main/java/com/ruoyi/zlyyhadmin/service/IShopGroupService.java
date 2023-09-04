package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ShopGroup;
import com.ruoyi.zlyyh.domain.vo.ShopGroupVo;
import com.ruoyi.zlyyh.domain.bo.ShopGroupBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 门店组配置Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IShopGroupService {

    /**
     * 查询门店组配置
     */
    ShopGroupVo queryById(Long shopGroupId);

    /**
     * 查询门店组配置列表
     */
    TableDataInfo<ShopGroupVo> queryPageList(ShopGroupBo bo, PageQuery pageQuery);

    /**
     * 查询门店组配置列表
     */
    List<ShopGroupVo> queryList(ShopGroupBo bo);

    /**
     * 修改门店组配置
     */
    Boolean insertByBo(ShopGroupBo bo);

    /**
     * 修改门店组配置
     */
    Boolean updateByBo(ShopGroupBo bo);

    /**
     * 校验并批量删除门店组配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
