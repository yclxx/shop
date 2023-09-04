package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.vo.ProductVo;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyhmobile.service.ISearchGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.SearchGroupBo;
import com.ruoyi.zlyyh.domain.vo.SearchGroupVo;
import com.ruoyi.zlyyh.domain.SearchGroup;
import com.ruoyi.zlyyh.mapper.SearchGroupMapper;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 搜索彩蛋配置Service业务层处理
 *
 * @author yzg
 * @date 2023-07-24
 */
@RequiredArgsConstructor
@Service
public class SearchGroupServiceImpl implements ISearchGroupService {

    private final SearchGroupMapper baseMapper;
    private final ProductMapper productMapper;

    /**
     * 查询搜索彩蛋配置
     */
    @Override
    public SearchGroupVo queryById(Long searchId) {
        return baseMapper.selectVoById(searchId);
    }

    /**
     * 查询搜索彩蛋配置列表
     */
    @Override
    public TableDataInfo<SearchGroupVo> queryPageList(SearchGroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SearchGroup> lqw = buildQueryWrapper(bo);
        Page<SearchGroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public SearchGroupVo queryByContent(SearchGroupBo searchGroupBo) {
        LambdaQueryWrapper<SearchGroup> lqw = buildQueryWrapper(searchGroupBo);
        SearchGroupVo searchGroupVo = baseMapper.selectVoOne(lqw);
        if (ObjectUtil.isNotEmpty(searchGroupVo)) {
            String toType = searchGroupVo.getToType();
            Long productId = searchGroupVo.getProductId();
            if (toType.equals("0") && ObjectUtil.isNotEmpty(productId)) {
                //无需跳转加上商品信息
                LambdaQueryWrapper<Product> lqwProduct = Wrappers.lambdaQuery();
                lqwProduct.eq(ObjectUtil.isNotEmpty(productId), Product::getProductId, productId);
                List<ProductVo> productVos = productMapper.selectVoList(lqwProduct);
                if (ObjectUtil.isNotEmpty(productVos)) {
                    searchGroupVo.setProductVoList(productVos);
                }
            }
            return searchGroupVo;
        }
        return null;
    }

    private LambdaQueryWrapper<SearchGroup> buildQueryWrapper(SearchGroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SearchGroup> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getSearchContent()), SearchGroup::getSearchContent, bo.getSearchContent());
        lqw.eq(bo.getPlatformKey() != null, SearchGroup::getPlatformKey, bo.getPlatformKey());
        lqw.eq(SearchGroup::getStatus, "0");
        lqw.and(lm -> {
            lm.isNull(SearchGroup::getStartTime).or().lt(SearchGroup::getStartTime, new Date());
        });
        lqw.and(lm -> {
            lm.isNull(SearchGroup::getEndTime).or().gt(SearchGroup::getEndTime, new Date());
        });
        lqw.and(lm -> {
            lm.eq(SearchGroup::getShowCity, "ALL").or().like(SearchGroup::getShowCity, bo.getShowCity());
        });

        lqw.and(lm -> {
            lm.eq(SearchGroup::getAssignDate, "0").or().like(SearchGroup::getWeekDate, bo.getWeekDate());
        });


        return lqw;
    }


}
