package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ShopTourLog;
import com.ruoyi.zlyyh.domain.vo.ShopTourLogVo;
import com.ruoyi.zlyyh.domain.bo.ShopTourLogBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 巡检记录Service接口
 *
 * @author yzg
 * @date 2024-03-06
 */
public interface IShopTourLogService {

    /**
     * 查询巡检记录
     */
    ShopTourLogVo queryById(Long tourLogId);

    /**
     * 查询巡检记录列表
     */
    TableDataInfo<ShopTourLogVo> queryPageList(ShopTourLogBo bo, PageQuery pageQuery);

    /**
     * 查询巡检记录列表
     */
    List<ShopTourLogVo> queryList(ShopTourLogBo bo);

    /**
     * 修改巡检记录
     */
    Boolean insertByBo(ShopTourLogBo bo);

    /**
     * 修改巡检记录
     */
    Boolean updateByBo(ShopTourLogBo bo);

    /**
     * 校验并批量删除巡检记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
