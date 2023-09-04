package com.ruoyi.zlyyhmobile.service;

import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.EquityRecordBo;
import com.ruoyi.zlyyh.domain.vo.EquityRecordVo;

import java.util.Collection;
import java.util.List;

/**
 * 领取记录Service接口
 *
 * @author yzg
 * @date 2023-06-06
 */
public interface IEquityRecordService {

    /**
     * 查询领取记录
     */
    EquityRecordVo queryById(Long id);

    /**
     * 查询领取记录列表
     */
    TableDataInfo<EquityRecordVo> queryPageList(EquityRecordBo bo, PageQuery pageQuery);

    /**
     * 查询领取记录列表
     */
    List<EquityRecordVo> queryList(EquityRecordBo bo);

    /**
     * 修改领取记录
     */
    Boolean insertByBo(EquityRecordBo bo);

    /**
     * 修改领取记录
     */
    Boolean updateByBo(EquityRecordBo bo);

    /**
     * 校验并批量删除领取记录信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
