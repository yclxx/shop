package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.PageSettingBo;
import com.ruoyi.zlyyh.domain.vo.PageSettingVo;

import java.util.Collection;
import java.util.List;

/**
 * 页面配置Service接口
 *
 * @author yzgnet
 * @date 2023-03-21
 */
public interface IPageSettingService {

    /**
     * 查询页面配置
     */
    PageSettingVo queryById(Long id);

    /**
     * 查询页面配置列表
     */
    TableDataInfo<PageSettingVo> queryPageList(PageSettingBo bo, PageQuery pageQuery);

    /**
     * 查询页面配置列表
     */
    List<PageSettingVo> queryList(PageSettingBo bo);

    /**
     * 修改页面配置
     */
    Boolean insertByBo(PageSettingBo bo);

    /**
     * 修改页面配置
     */
    Boolean updateByBo(PageSettingBo bo);

    /**
     * 校验并批量删除页面配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
