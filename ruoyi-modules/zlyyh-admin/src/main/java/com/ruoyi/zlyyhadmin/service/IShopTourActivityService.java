package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ShopTourActivity;
import com.ruoyi.zlyyh.domain.vo.ShopTourActivityVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourActivityBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 巡检活动Service接口
 *
 * @author yzg
 * @date 2024-03-01
 */
public interface IShopTourActivityService {

    /**
     * 查询巡检活动
     */
    ShopTourActivityVo queryById(Long tourActivityId);

    /**
     * 查询巡检活动列表
     */
    TableDataInfo<ShopTourActivityVo> queryPageList(ShopTourActivityBo bo, PageQuery pageQuery);

    /**
     * 查询巡检活动列表
     */
    List<ShopTourActivityVo> queryList(ShopTourActivityBo bo);

    /**
     * 修改巡检活动
     */
    Boolean insertByBo(ShopTourActivityBo bo);

    /**
     * 修改巡检活动
     */
    Boolean updateByBo(ShopTourActivityBo bo);

    /**
     * 校验并批量删除巡检活动信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
