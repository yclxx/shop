package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.MarketLog;
import com.ruoyi.zlyyh.domain.bo.MarketLogBo;
import com.ruoyi.zlyyh.domain.vo.MarketLogVo;
import com.ruoyi.zlyyh.mapper.MarketLogMapper;
import com.ruoyi.zlyyhadmin.service.IMarketLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 奖励发放记录Service业务层处理
 *
 * @author yzg
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class MarketLogServiceImpl implements IMarketLogService {

    private final MarketLogMapper baseMapper;

    /**
     * 查询奖励发放记录
     */
    @Override
    public MarketLogVo queryById(Long logId) {
        return baseMapper.selectVoById(logId);
    }

    /**
     * 查询奖励发放记录列表
     */
    @Override
    public TableDataInfo<MarketLogVo> queryPageList(MarketLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MarketLog> lqw = buildQueryWrapper(bo);
        Page<MarketLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询奖励发放记录列表
     */
    @Override
    public List<MarketLogVo> queryList(MarketLogBo bo) {
        LambdaQueryWrapper<MarketLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MarketLog> buildQueryWrapper(MarketLogBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MarketLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, MarketLog::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getMarketId() != null, MarketLog::getMarketId, bo.getMarketId());
        lqw.eq(bo.getUserId() != null, MarketLog::getUserId, bo.getUserId());
        lqw.eq(bo.getReceiveDate() != null, MarketLog::getReceiveDate, bo.getReceiveDate());
        lqw.eq(StringUtils.isNotBlank(bo.getRewardType()), MarketLog::getRewardType, bo.getRewardType());
        return lqw;
    }

    /**
     * 新增奖励发放记录
     */
    @Override
    public Boolean insertByBo(MarketLogBo bo) {
        MarketLog add = BeanUtil.toBean(bo, MarketLog.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setLogId(add.getLogId());
        }
        return flag;
    }

    /**
     * 修改奖励发放记录
     */
    @Override
    public Boolean updateByBo(MarketLogBo bo) {
        MarketLog update = BeanUtil.toBean(bo, MarketLog.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除奖励发放记录
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {

        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
