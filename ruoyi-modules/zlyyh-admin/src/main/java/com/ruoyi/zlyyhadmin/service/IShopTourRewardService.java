package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ShopTourReward;
import com.ruoyi.zlyyh.domain.vo.ShopTourRewardVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourRewardBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 巡检奖励Service接口
 *
 * @author yzg
 * @date 2024-01-28
 */
public interface IShopTourRewardService {

    /**
     * 查询巡检奖励
     */
    ShopTourRewardVo queryById(Long tourRewardId);

    /**
     * 查询巡检奖励列表
     */
    TableDataInfo<ShopTourRewardVo> queryPageList(ShopTourRewardBo bo, PageQuery pageQuery);

    /**
     * 查询巡检奖励列表
     */
    List<ShopTourRewardVo> queryList(ShopTourRewardBo bo);

    /**
     * 修改巡检奖励
     */
    Boolean insertByBo(ShopTourRewardBo bo);

    /**
     * 修改巡检奖励
     */
    Boolean updateByBo(ShopTourRewardBo bo);

    /**
     * 校验并批量删除巡检奖励信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
