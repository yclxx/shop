package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.Market;
import com.ruoyi.zlyyh.domain.bo.MarketBo;
import com.ruoyi.zlyyh.domain.vo.ActionVo;
import com.ruoyi.zlyyh.domain.vo.MarketVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.ActionMapper;
import com.ruoyi.zlyyh.mapper.MarketMapper;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyhadmin.service.IMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新用户营销Service业务层处理
 *
 * @author yzg
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class MarketServiceImpl implements IMarketService {
    private final MarketMapper baseMapper;
    // 产品信息表
    private final ProductMapper productMapper;
    // 优惠券批次表
    private final ActionMapper actionMapper;

    /**
     * 查询新用户营销
     */
    @Override
    public MarketVo queryById(Long marketId) {
        return baseMapper.selectVoById(marketId);
    }

    /**
     * 查询新用户营销
     */
    @Override
    public Map<String, Object> queryByIds(Long marketId) {
        MarketVo marketVo = baseMapper.selectVoById(marketId);
        if (marketVo != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("market", marketVo);
            if (marketVo.getRewardType().equals("2")) {
                ActionVo actionVo = actionMapper.selectVoById(marketVo.getActionId());
                map.put("action", actionVo);
            } else {
                ProductVo productVo = productMapper.selectVoById(marketVo.getProductId());
                map.put("product", productVo);
            }
            return map;
        }
        return null;
    }

    @Override
    public TableDataInfo<MarketVo> queryPageList(MarketBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Market> lqw = buildQueryWrapper(bo);
        Page<MarketVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询新用户营销列表
     */
    @Override
    public List<MarketVo> queryList(MarketBo bo) {
        LambdaQueryWrapper<Market> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Market> buildQueryWrapper(MarketBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Market> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, Market::getPlatformKey, bo.getPlatformKey());
        lqw.like(StringUtils.isNotBlank(bo.getMarketName()), Market::getMarketName, bo.getMarketName());
        lqw.eq(bo.getBeginTime() != null, Market::getBeginTime, bo.getBeginTime());
        lqw.eq(bo.getEndTime() != null, Market::getEndTime, bo.getEndTime());
        lqw.eq(bo.getDateSpecific() != null, Market::getDateSpecific, bo.getDateSpecific());
        lqw.eq(StringUtils.isNotBlank(bo.getRewardType()), Market::getRewardType, bo.getRewardType());
        return lqw;
    }

    /**
     * 新增新用户营销
     */
    @CacheEvict(cacheNames = CacheNames.marketInfo, allEntries = true)
    @Override
    public Boolean insertByBo(MarketBo bo) {
        Market add = BeanUtil.toBean(bo, Market.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMarketId(add.getMarketId());
        }
        return flag;
    }

    /**
     * 修改新用户营销
     */
    @CacheEvict(cacheNames = CacheNames.marketInfo, allEntries = true)
    @Override
    public Boolean updateByBo(MarketBo bo) {
        Market update = BeanUtil.toBean(bo, Market.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除新用户营销
     */
    @CacheEvict(cacheNames = CacheNames.marketInfo, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
