package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.ChineseCharacterUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.utils.TreeBuildUtils;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyhadmin.service.IAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.AreaBo;
import com.ruoyi.zlyyh.domain.vo.AreaVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 行政区Service业务层处理
 *
 * @author ruoyi
 * @date 2023-03-27
 */
@RequiredArgsConstructor
@Service
public class AreaServiceImpl implements IAreaService {

    private final AreaMapper baseMapper;
    private final PlatformMapper platformMapper;
    private final BannerMapper bannerMapper;
    private final ProductMapper productMapper;
    private final HotNewsMapper hotNewsMapper;
    private final SearchGroupMapper searchGroupMapper;

    /**
     * 查询行政区
     */
    @Override
    public AreaVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询行政区列表
     */
    @Override
    public List<AreaVo> queryList(AreaBo bo) {
        LambdaQueryWrapper<Area> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Area> buildQueryWrapper(AreaBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Area> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getAreaName()), Area::getAreaName, bo.getAreaName());
        lqw.eq(null != bo.getAdcode(), Area::getAdcode, bo.getAdcode());
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), Area::getLevel, bo.getLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getFirstLetter()), Area::getFirstLetter, bo.getFirstLetter());
        lqw.eq(null != bo.getParentCode(), Area::getParentCode, bo.getParentCode());
        return lqw;
    }

    /**
     * 新增行政区
     */
    @Override
    public Boolean insertByBo(AreaBo bo) {
        Area add = BeanUtil.toBean(bo, Area.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改行政区
     */
    @Override
    public Boolean updateByBo(AreaBo bo) {
        Area update = BeanUtil.toBean(bo, Area.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Area entity) {
        //TODO 做一些数据校验,如唯一约束
        if (StringUtils.isNotBlank(entity.getAreaName())) {
            String firstLetter = ChineseCharacterUtils.getSpells(String.valueOf(entity.getAreaName().charAt(0)).toUpperCase());
            entity.setFirstLetter(firstLetter);
        }
    }

    /**
     * 批量删除行政区
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
            //查询是否存在子集
            if (CollectionUtils.isNotEmpty(ids)) {
                for (Long id : ids) {
                    AreaVo areaVo = baseMapper.selectVoById(id);
                    if (null != areaVo) {
                        LambdaQueryWrapper<Area> wrapper = Wrappers.lambdaQuery();
                        wrapper.eq(Area::getParentCode, areaVo.getAdcode());
                        List<AreaVo> areaVoList = baseMapper.selectVoList(wrapper);
                        if (CollectionUtils.isNotEmpty(areaVoList)) {
                            throw new ServiceException("删除失败，存在子行政区");
                        }
                    }

                }
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public List<Area> selectCityList(Area area) {
        return baseMapper.selectList(new LambdaQueryWrapper<Area>().orderByAsc(Area::getParentCode).orderByAsc(Area::getId));
    }

    /**
     * 下拉树
     */
    @Override
    public List<Tree<Long>> buildCityTreeSelect(List<Area> areaList) {
        if (CollUtil.isEmpty(areaList)) {
            return CollUtil.newArrayList();
        }
        return TreeBuildUtils.builds(areaList, (city, tree) ->
            tree.setId(city.getAdcode())
                .setParentId(city.getParentCode())
                .setName(city.getAreaName()));

    }

    /**
     * 查询
     */
    @Override
    public List<Long> selectPlatformCityByPlatform(Long platformId) {
        Platform platform = platformMapper.selectById(platformId);
        if (null != platform && StringUtils.isNotBlank(platform.getPlatformCity())) {
            List<Long> list = new ArrayList<>();
            if (platform.getPlatformCity().equals("ALL")) {
                list.add(99L);
            } else {
                String[] split = platform.getPlatformCity().split(",");
                for (String s : split) {
                    if (StringUtils.isNotBlank(s)) {
                        list.add(Long.parseLong(s));
                    }
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<Long> selectPlatformCityByBannerId(Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (null != banner && null != banner.getShowCity()) {
            List<Long> list = new ArrayList<>();
            if (banner.getShowCity().equals("ALL")) {
                list.add(99L);
            } else {
                String[] split = banner.getShowCity().split(",");
                for (String s : split) {
                    list.add(Long.parseLong(s));
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<Long> selectPlatformCityByNewsId(Long newsId) {
        HotNews hotNews = hotNewsMapper.selectById(newsId);
        if (null != hotNews && null != hotNews.getShowCity()) {
            List<Long> list = new ArrayList<>();
            if (hotNews.getShowCity().equals("ALL")) {
                list.add(99L);
            } else {
                String[] split = hotNews.getShowCity().split(",");
                for (String s : split) {
                    list.add(Long.parseLong(s));
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<Long> selectPlatformCityByProductId(Long productId) {
        Product product = productMapper.selectById(productId);
        if (null != product && null != product.getShowCity()) {
            List<Long> list = new ArrayList<>();
            if (StringUtils.isBlank(product.getShowCity()) && product.getShowCity().equals("ALL")) {
                list.add(99L);
            } else {
                String[] split = product.getShowCity().split(",");
                for (String s : split) {
                    list.add(Long.parseLong(s));
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<Long> selectPlatformCityBySearchId(Long searchId) {
        SearchGroup searchGroup = searchGroupMapper.selectById(searchId);
        if (null != searchGroup && null != searchGroup.getShowCity()) {
            List<Long> list = new ArrayList<>();
            if (searchGroup.getShowCity().equals("ALL")) {
                list.add(99L);
            } else {
                String[] split = searchGroup.getShowCity().split(",");
                for (String s : split) {
                    list.add(Long.parseLong(s));
                }
            }
            return list;
        }
        return null;
    }
}
