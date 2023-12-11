package com.ruoyi.zlyyhadmin.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.SendDyInfoBo;
import com.ruoyi.zlyyh.domain.vo.SendDyInfoVo;

import java.util.Collection;
import java.util.List;

/**
 * 用户订阅Service接口
 *
 * @author yzg
 * @date 2023-12-07
 */
public interface ISendDyInfoService {

    /**
     * 查询用户订阅
     */
    SendDyInfoVo queryById(Long id);

    /**
     * 查询用户订阅列表
     */
    TableDataInfo<SendDyInfoVo> queryPageList(SendDyInfoBo bo, PageQuery pageQuery);

    /**
     * 查询用户订阅列表
     */
    List<SendDyInfoVo> queryList(SendDyInfoBo bo);

    /**
     * 修改用户订阅
     */
    Boolean insertByBo(SendDyInfoBo bo);

    /**
     * 修改用户订阅
     */
    Boolean updateByBo(SendDyInfoBo bo);

    /**
     * 校验并批量删除用户订阅信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
