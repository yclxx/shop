package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyh.domain.vo.*;

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
     * @param shopIds
     */
    void changeTourShop(List<Long> shopIds);

    /**
     * 获取城市列表
     * @param cityCode
     * @return
     */
    List<CityVo> getCityList(String cityCode);

    /**
     * 获取商圈列表
     * @param adcode
     * @return
     */
    List<BusinessDistrictVo> getBusinessDistrictList(String adcode);

    /**
     * 查询巡检商户列表
     */
    TableDataInfo<ShopTourVo> queryPageTourList(ShopTourBo bo, PageQuery pageQuery);

    /**
     * 查询附近巡检商户列表
     */
    TableDataInfo<ShopTourVo> queryPageNearTourList(ShopTourBo bo, PageQuery pageQuery);

    /**
     * 预约巡检商户门店
     */
    String reserveTourShop(Long tourShopId,Long userId);

    /**
     * 取消预约
     * @param tourShopId
     */
    void cancelReserveTourShop(Long tourShopId);

    /**
     * 获取预约商户
     * @param bo
     * @param pageQuery
     * @return
     */
    TableDataInfo<ShopTourVo> getReserveShopList(ShopTourBo bo, PageQuery pageQuery);

    /**
     * 获取巡检商户门店信息
     * @param tourId
     * @return
     */
    ShopTourVo getTourShopInfo(Long tourId);

    /**
     * 暂存
     * @param bo
     */
    void tourZanCun(ShopTourBo bo);

    /**
     * 提交
     * @param bo
     */
    void tourSubmit(ShopTourBo bo);

    /**
     * 获取巡检奖励
     */
    ShopTourRewardVo getTourReward(Long userId);

    /**
     * 核验有效期
     * @param tourShopId
     * @return
     */
    String verifyDate(Long tourShopId);

    /**
     * 获取商户号
     */
    List<ShopMerchantVo> getShopMerchantNoList(Long shopId);
}
