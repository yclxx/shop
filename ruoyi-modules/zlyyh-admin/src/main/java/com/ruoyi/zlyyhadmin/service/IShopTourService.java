package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ShopTour;
import com.ruoyi.zlyyh.domain.vo.ShopTourVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.vo.ShopVo;

import java.util.Collection;
import java.util.List;

/**
 * 巡检商户Service接口
 *
 * @author yzg
 * @date 2024-01-28
 */
public interface IShopTourService {

    /**
     * 查询巡检商户
     */
    ShopTourVo queryById(Long id);

    /**
     * 查询巡检商户列表
     */
    TableDataInfo<ShopTourVo> queryPageList(ShopTourBo bo, PageQuery pageQuery);

    /**
     * 查询巡检商户列表
     */
    List<ShopTourVo> queryList(ShopTourBo bo);

    /**
     * 修改巡检商户
     */
    Boolean insertByBo(ShopTourBo bo);

    /**
     * 修改巡检商户
     */
    Boolean updateByBo(ShopTourBo bo);

    /**
     * 校验并批量删除巡检商户信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 添加巡检商户
     */
    void changeTourShop(ShopTourBo bo);

    /**
     * 巡检审核通过
     */
    void tourCheckPass(ShopTourBo bo);

    ShopVo queryByShopId(Long shopId);
}
