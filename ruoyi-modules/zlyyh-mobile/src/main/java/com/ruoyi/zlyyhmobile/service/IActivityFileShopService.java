package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.bo.ShopProductBo;
import com.ruoyi.zlyyh.domain.vo.*;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author yzg
 * @date 2023-05-16
 */
public interface IActivityFileShopService {

    /**
     * 获取商户列表
     */
    TableDataInfo<ActivityFileShopVo> queryPageList(ActivityFileShopBo bo, PageQuery pageQuery);

    /**
     * 获取当前查询批次城市列表
     */
    List<AreaVo> getCityList(String fileId);

    /**
     * 获取商户类别列表
     */
    List<MerchantTypeVo> getMerTypeList(String fileId);

    /**
     * 获取导入文件信息
     */
    FileImportLogVo getFileInfo(String fileId);
}
