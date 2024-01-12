package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.zlyyh.domain.Mission;
import com.ruoyi.zlyyhmobile.service.IMissionGroupBgImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.zlyyh.domain.bo.MissionGroupBgImgBo;
import com.ruoyi.zlyyh.domain.vo.MissionGroupBgImgVo;
import com.ruoyi.zlyyh.domain.MissionGroupBgImg;
import com.ruoyi.zlyyh.mapper.MissionGroupBgImgMapper;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 任务组背景图片配置Service业务层处理
 *
 * @author yzg
 * @date 2024-01-04
 */
@RequiredArgsConstructor
@Service
public class MissionGroupBgImgServiceImpl implements IMissionGroupBgImgService {

    private final MissionGroupBgImgMapper baseMapper;

    /**
     * 查询任务组背景图片配置
     */
    @Override
    public MissionGroupBgImgVo queryById(Long missionBgImgId){
        return baseMapper.selectVoById(missionBgImgId);
    }

    /**
     * 查询任务组背景图片配置列表
     */
    @Override
    @Cacheable(cacheNames = CacheNames.MISSION_BJ_IMG, key = "#bo.getPlatformKey()+'-'+#bo.getMissionGroupId()")
    public MissionGroupBgImgVo queryListOne(MissionGroupBgImgBo bo) {
        LambdaQueryWrapper<MissionGroupBgImg> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoOne(lqw);
    }

    private LambdaQueryWrapper<MissionGroupBgImg> buildQueryWrapper(MissionGroupBgImgBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MissionGroupBgImg> lqw = Wrappers.lambdaQuery();
        lqw.eq(MissionGroupBgImg::getStatus, "0");
        lqw.eq(bo.getMissionGroupId() != null, MissionGroupBgImg::getMissionGroupId, bo.getMissionGroupId());
        lqw.and(lm ->
            lm.isNull(MissionGroupBgImg::getStartDate).or().lt(MissionGroupBgImg::getStartDate, new Date())
        );
        lqw.and(lm ->
            lm.isNull(MissionGroupBgImg::getEndDate).or().gt(MissionGroupBgImg::getEndDate, new Date())
        );
        lqw.eq(bo.getPlatformKey() != null, MissionGroupBgImg::getPlatformKey, bo.getPlatformKey());
        return lqw;
    }


}
