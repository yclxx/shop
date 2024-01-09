package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.zlyyh.domain.ActivityFileShop;
import com.ruoyi.zlyyh.domain.bo.FileImportLogBo;
import com.ruoyi.zlyyh.domain.vo.ActivityFileShopVo;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * 活动商户Service接口
 *
 * @author yzg
 * @date 2024-01-03
 */
public interface IActivityFileShopService {

    /**
     * 查询活动商户
     */
    ActivityFileShopVo queryById(Long activityShopId);

    /**
     * 查询活动商户列表
     */
    TableDataInfo<ActivityFileShopVo> queryPageList(ActivityFileShopBo bo, PageQuery pageQuery);

    /**
     * 查询活动商户列表
     */
    List<ActivityFileShopVo> queryList(ActivityFileShopBo bo);

    /**
     * 修改活动商户
     */
    Boolean insertByBo(ActivityFileShopBo bo);

    /**
     * 修改活动商户
     */
    Boolean updateByBo(ActivityFileShopBo bo);

    /**
     * 校验并批量删除活动商户信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 导入商户
     * @param file
     */
    void importMerchant(MultipartFile file, FileImportLogBo logBo) throws IOException;
}
