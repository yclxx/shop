package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.CommercialTenant;
import com.ruoyi.zlyyh.domain.CommercialTenantProduct;
import com.ruoyi.zlyyh.domain.bo.CategoryProductBo;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantBo;
import com.ruoyi.zlyyh.domain.bo.CommercialTenantProductBo;
import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantProductVo;
import com.ruoyi.zlyyh.domain.vo.CommercialTenantVo;
import com.ruoyi.zlyyh.mapper.CommercialTenantMapper;
import com.ruoyi.zlyyhadmin.service.ICategoryProductService;
import com.ruoyi.zlyyhadmin.service.ICommercialTenantProductService;
import com.ruoyi.zlyyhadmin.service.ICommercialTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商户Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class CommercialTenantServiceImpl implements ICommercialTenantService {

    private final CommercialTenantMapper baseMapper;
    private final ICommercialTenantProductService commercialTenantProductService;
    private final ICategoryProductService categoryProductService;

    /**
     * 查询商户
     */
    @Override
    public CommercialTenantVo queryById(Long commercialTenantId) {
        CommercialTenantVo tenantVo = baseMapper.selectVoById(commercialTenantId);
        CommercialTenantProductBo categoryProductBo = new CommercialTenantProductBo();
        categoryProductBo.setCommercialTenantId(commercialTenantId);
        List<CommercialTenantProductVo> commercialTenantProductVos = commercialTenantProductService.queryList(categoryProductBo);
        if (ObjectUtil.isNotEmpty(commercialTenantProductVos)) {
            tenantVo.setProductIds(commercialTenantProductVos.stream().map(CommercialTenantProductVo::getProductId).toArray(Long[]::new));
        }
        CategoryProductBo productBo = new CategoryProductBo();
        productBo.setProductId(commercialTenantId);
        List<CategoryProductVo> categoryProductVos = categoryProductService.queryList(productBo);
        if (ObjectUtil.isNotEmpty(categoryProductVos)) {
            tenantVo.setCategoryIds(categoryProductVos.stream().map(CategoryProductVo::getCategoryId).toArray(Long[]::new));
        }
        return tenantVo;
    }

    /**
     * 查询商户
     */
    @Override
    public CommercialTenantVo queryByBrandId(Long brandId) {
        LambdaQueryWrapper<CommercialTenant> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CommercialTenant::getBrandId, brandId);
        wrapper.last("Limit 1");
        return baseMapper.selectVoOne(wrapper);
    }
    @Override
    public CommercialTenantVo queryByYlBrandId(String ylBrandId) {
        LambdaQueryWrapper<CommercialTenant> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CommercialTenant::getYlBrandId, ylBrandId);
        wrapper.last("Limit 1");
        return baseMapper.selectVoOne(wrapper);
    }

    /**
     * 查询商户列表
     */
    @Override
    public TableDataInfo<CommercialTenantVo> queryPageList(CommercialTenantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CommercialTenant> lqw = buildQueryWrapper(bo);
        Page<CommercialTenantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商户列表
     */
    @Override
    public List<CommercialTenantVo> queryList(CommercialTenantBo bo) {
        LambdaQueryWrapper<CommercialTenant> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CommercialTenant> buildQueryWrapper(CommercialTenantBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<CommercialTenant> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getCommercialTenantName()), CommercialTenant::getCommercialTenantName, bo.getCommercialTenantName());
        lqw.eq(StringUtils.isNotBlank(bo.getCommercialTenantImg()), CommercialTenant::getCommercialTenantImg, bo.getCommercialTenantImg());
        lqw.eq(StringUtils.isNotBlank(bo.getTags()), CommercialTenant::getTags, bo.getTags());
        lqw.eq(bo.getStartTime() != null, CommercialTenant::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, CommercialTenant::getEndTime, bo.getEndTime());
        lqw.eq(StringUtils.isNotBlank(bo.getIndexShow()), CommercialTenant::getIndexShow, bo.getIndexShow());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), CommercialTenant::getStatus, bo.getStatus());
        lqw.eq(bo.getSort() != null, CommercialTenant::getSort, bo.getSort());
        lqw.eq(bo.getPlatformKey() != null, CommercialTenant::getPlatformKey, bo.getPlatformKey());
        lqw.between(params.get("beginStartTime") != null && params.get("endStartTime") != null,
            CommercialTenant::getStartTime, params.get("beginStartTime"), params.get("endStartTime"));
        lqw.between(params.get("beginEndTime") != null && params.get("endEndTime") != null,
            CommercialTenant::getEndTime, params.get("beginEndTime"), params.get("endEndTime"));
        return lqw;
    }

    /**
     * 新增商户
     */
    @Override
    public Boolean insertByBo(CommercialTenantBo bo) {
        CommercialTenant add = BeanUtil.toBean(bo, CommercialTenant.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCommercialTenantId(add.getCommercialTenantId());
            processCategory(bo, false);
        }
        return flag;
    }

    private void processCategory(CommercialTenantBo bo, boolean update) {
        if (null == bo.getCommercialTenantId()) {
            return;
        }
        if (update) {
            categoryProductService.remove(new LambdaQueryWrapper<CategoryProduct>().eq(CategoryProduct::getProductId, bo.getCommercialTenantId()));
            commercialTenantProductService.remove(new LambdaQueryWrapper<CommercialTenantProduct>().eq(CommercialTenantProduct::getCommercialTenantId, bo.getCommercialTenantId()));
        }
        if (ObjectUtil.isNotEmpty(bo.getCategoryIds())) {
            for (Long s : bo.getCategoryIds()) {
                CategoryProductBo categoryProductBo = new CategoryProductBo();
                categoryProductBo.setProductId(bo.getCommercialTenantId());
                categoryProductBo.setCategoryId(s);
                categoryProductService.insertByBo(categoryProductBo);
            }
        }
        if (ObjectUtil.isNotEmpty(bo.getProductIds())) {
            for (Long productId : bo.getProductIds()) {
                CommercialTenantProductBo tenantProductBo = new CommercialTenantProductBo();
                tenantProductBo.setCommercialTenantId(bo.getCommercialTenantId());
                tenantProductBo.setProductId(productId);
                commercialTenantProductService.insertByBo(tenantProductBo);
            }
        }
    }

    /**
     * 修改商户
     */
    @CacheEvict(cacheNames = CacheNames.COMMERCIAL, key = "#bo.getCommercialTenantId()")
    @Override
    public Boolean updateByBo(CommercialTenantBo bo) {
        CommercialTenant update = BeanUtil.toBean(bo, CommercialTenant.class);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            processCategory(bo, true);
        }
        return flag;
    }

    /**
     * 批量删除商户
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ObjectUtil.isEmpty(ids)) {
            return false;
        }
        // 删除商户缓存
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.COMMERCIAL, id);
        }
        categoryProductService.remove(new LambdaQueryWrapper<CategoryProduct>().in(CategoryProduct::getProductId, ids));
        commercialTenantProductService.remove(new LambdaQueryWrapper<CommercialTenantProduct>().in(CommercialTenantProduct::getCommercialTenantId, ids));
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
