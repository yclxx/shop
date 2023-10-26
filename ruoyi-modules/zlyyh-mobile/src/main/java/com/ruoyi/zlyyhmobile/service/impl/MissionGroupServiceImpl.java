package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.MissionGroup;
import com.ruoyi.zlyyh.domain.MissionGroupProduct;
import com.ruoyi.zlyyh.domain.vo.MissionGroupProductVo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupVo;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.MissionGroupMapper;
import com.ruoyi.zlyyh.mapper.MissionGroupProductMapper;
import com.ruoyi.zlyyhmobile.service.IMissionGroupService;
import com.ruoyi.zlyyhmobile.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 任务组Service业务层处理
 *
 * @author yzg
 * @date 2023-05-10
 */
@RequiredArgsConstructor
@Service
public class MissionGroupServiceImpl implements IMissionGroupService {

    private final MissionGroupMapper baseMapper;
    private final MissionGroupProductMapper missionGroupProductMapper;
    private final IProductService productService;

    /**
     * 查询任务组
     */
    @Cacheable(cacheNames = CacheNames.MISSION_GROUP, key = "#missionGroupId")
    @Override
    public MissionGroupVo queryById(Long missionGroupId) {
        return baseMapper.selectVoById(missionGroupId);
    }

    /**
     * 查询所有可用任务组
     *
     * @return 结果
     */
    @Override
    public List<MissionGroupVo> queryList() {
        LambdaQueryWrapper<MissionGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionGroup::getStatus, "0");
        lqw.and(lm ->
            lm.isNull(MissionGroup::getStartDate).or().lt(MissionGroup::getStartDate, new Date())
        );
        lqw.and(lm ->
            lm.isNull(MissionGroup::getEndDate).or().gt(MissionGroup::getEndDate, new Date())
        );
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询任务组可兑换商品
     *
     * @param missionGroupId 任务组ID
     * @return 商品集合
     */
    @Override
    public List<ProductVo> missionProduct(Long missionGroupId,Long platformKey) {
        List<MissionGroupProductVo> missionGroupProductVos = missionGroupProductMapper.selectVoList(new LambdaQueryWrapper<MissionGroupProduct>().eq(MissionGroupProduct::getMissionGroupId, missionGroupId).or().eq(MissionGroupProduct::getMissionId,missionGroupId));
        if (ObjectUtil.isEmpty(missionGroupProductVos)) {
            return new ArrayList<>();
        }
        Map<Long, Long> collect = missionGroupProductVos.stream().collect(HashMap::new, (m, v) -> m.put(v.getProductId(), Optional.ofNullable(v.getSort()).orElse(99L)), HashMap::putAll);
        List<ProductVo> productVos = productService.queryGrabPeriodProduct((Set) collect.keySet(), ServletUtils.getHeader(ZlyyhConstants.CITY_CODE), null, platformKey);
        if (ObjectUtil.isNotEmpty(productVos)) {
            for (ProductVo productVo : productVos) {
                productVo.setSort(collect.get(productVo.getProductId()));
            }
            productVos.sort(Comparator.comparing(ProductVo::getSort));
        }
        return productVos;
    }

}
