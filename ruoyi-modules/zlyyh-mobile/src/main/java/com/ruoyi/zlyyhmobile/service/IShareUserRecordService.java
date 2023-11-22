package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.ShareUserRecordBo;
import com.ruoyi.zlyyh.domain.vo.ShareUserRecordVo;

import java.util.Collection;
import java.util.List;

/**
 * 分销记录Service接口
 *
 * @author yzg
 * @date 2023-11-09
 */
public interface IShareUserRecordService {

    /**
     * 查询分销总次数
     *
     * @param userId 用户ID
     * @return 分销次数
     */
    Long shareCount(Long userId);

    /**
     * 查询分销记录
     */
    ShareUserRecordVo queryById(Long recordId);

    /**
     * 查询分销记录列表
     */
    TableDataInfo<ShareUserRecordVo> queryPageList(ShareUserRecordBo bo, PageQuery pageQuery);

    /**
     * 查询分销记录列表
     */
    List<ShareUserRecordVo> queryList(ShareUserRecordBo bo);

    /**
     * 修改分销记录
     */
    Boolean insertByBo(ShareUserRecordBo bo);

    /**
     * 修改分销记录
     */
    Boolean updateByBo(ShareUserRecordBo bo);

    /**
     * 校验并批量删除分销记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据分享用户ID和订单编号查询分享记录
     *
     * @param shareUserId 分享用户ID
     * @param number      订单编号
     * @return 分享记录
     */
    ShareUserRecordVo queryByShareUserIdAndNumber(Long shareUserId, Long number);
}
