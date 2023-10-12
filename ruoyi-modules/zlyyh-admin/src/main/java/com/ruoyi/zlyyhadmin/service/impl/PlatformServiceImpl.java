package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.CacheUtils;
import com.ruoyi.zlyyh.domain.Merchant;
import com.ruoyi.zlyyh.domain.Platform;
import com.ruoyi.zlyyh.domain.PlatformChannel;
import com.ruoyi.zlyyh.domain.bo.PlatformBo;
import com.ruoyi.zlyyh.domain.bo.PlatformChannelBo;
import com.ruoyi.zlyyh.domain.vo.PlatformChannelVo;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.mapper.MerchantMapper;
import com.ruoyi.zlyyh.mapper.PlatformChannelMapper;
import com.ruoyi.zlyyh.mapper.PlatformMapper;
import com.ruoyi.zlyyhadmin.service.IPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台信息Service业务层处理
 *
 * @author yzgnet
 * @date 2023-03-21
 */
@RequiredArgsConstructor
@Service
public class PlatformServiceImpl implements IPlatformService {

    private final PlatformMapper baseMapper;
    private final PlatformChannelMapper platformChannelMapper;
    private final MerchantMapper merchantMapper;

    /**
     * 查询平台信息
     */
    @Override
    public PlatformVo queryById(Long platformKey) {
        PlatformVo platformVo = baseMapper.selectVoById(platformKey);
        if (StringUtils.isNotEmpty(platformVo.getSupportChannel())) {
            LambdaQueryWrapper<PlatformChannel> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(PlatformChannel::getPlatformKey, platformKey);
            queryWrapper.orderByAsc(PlatformChannel::getChannel);
            List<PlatformChannelVo> platformChannelVos = platformChannelMapper.selectVoList(queryWrapper);
            platformVo.setPlatformChannel(platformChannelVos);
        }
        return platformVo;
    }

    /**
     * 查询平台信息列表
     */
    @Override
    public TableDataInfo<PlatformVo> queryPageList(PlatformBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Platform> lqw = buildQueryWrapper(bo);
        Page<PlatformVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        TableDataInfo<PlatformVo> build = TableDataInfo.build(result);
        List<PlatformVo> rows = build.getRows();
        if (CollectionUtils.isNotEmpty(rows)) {
            for (PlatformVo platformVo : rows) {
                Merchant merchant = merchantMapper.selectById(platformVo.getMerchantId());
                if (null != merchant) {
                    platformVo.setMerchantNo(merchant.getMerchantName());
                }
            }
        }
        return TableDataInfo.build(result);
    }

    /**
     * 查询平台信息列表
     */
    @Override
    public List<PlatformVo> queryList(PlatformBo bo) {
        LambdaQueryWrapper<Platform> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Platform> buildQueryWrapper(PlatformBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Platform> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getPlatformName()), Platform::getPlatformName, bo.getPlatformName());
        lqw.eq(StringUtils.isNotBlank(bo.getPlatformTitle()), Platform::getPlatformTitle, bo.getPlatformTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Platform::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getAppId()), Platform::getAppId, bo.getAppId());
        lqw.eq(StringUtils.isNotBlank(bo.getEncryptAppId()), Platform::getEncryptAppId, bo.getEncryptAppId());
        lqw.eq(StringUtils.isNotBlank(bo.getSecret()), Platform::getSecret, bo.getSecret());
        lqw.eq(StringUtils.isNotBlank(bo.getSymmetricKey()), Platform::getSymmetricKey, bo.getSymmetricKey());
        lqw.eq(StringUtils.isNotBlank(bo.getRsaPrivateKey()), Platform::getRsaPrivateKey, bo.getRsaPrivateKey());
        lqw.eq(StringUtils.isNotBlank(bo.getRsaPublicKey()), Platform::getRsaPublicKey, bo.getRsaPublicKey());
        lqw.eq(StringUtils.isNotBlank(bo.getServiceTel()), Platform::getServiceTel, bo.getServiceTel());
        lqw.eq(StringUtils.isNotBlank(bo.getServiceTime()), Platform::getServiceTime, bo.getServiceTime());
        lqw.eq(StringUtils.isNotBlank(bo.getPlatformCity()), Platform::getPlatformCity, bo.getPlatformCity());
        lqw.eq(bo.getMerchantId() != null, Platform::getMerchantId, bo.getMerchantId());
        return lqw;
    }

    /**
     * 新增平台信息
     */
    @CacheEvict(cacheNames = CacheNames.PLATFORM, allEntries = true)
    @Override
    public Boolean insertByBo(PlatformBo bo) {
        Platform add = BeanUtil.toBean(bo, Platform.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPlatformKey(add.getPlatformKey());
            if (ObjectUtil.isNotEmpty(bo.getPlatformChannel())) {
                handlePlatformChannel(bo.getPlatformChannel(), add.getPlatformKey());
            }
        }
        return flag;
    }

    /**
     * 修改平台信息
     */
    @CacheEvict(cacheNames = CacheNames.PLATFORM, key = "#bo.platformKey")
    @Override
    public Boolean updateByBo(PlatformBo bo) {
        Platform update = BeanUtil.toBean(bo, Platform.class);
        boolean b = baseMapper.updateById(update) > 0;
        if (b) {
            if (ObjectUtil.isNotEmpty(bo.getPlatformChannel())) {
                handlePlatformChannel(bo.getPlatformChannel(), bo.getPlatformKey());
            }
        }
        return b;
    }

    /**
     * 批量删除平台信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        for (Long id : ids) {
            CacheUtils.evict(CacheNames.PLATFORM, id);
        }
        boolean b = baseMapper.deleteBatchIds(ids) > 0;
        if (b) {
            LambdaQueryWrapper<PlatformChannel> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.in(PlatformChannel::getId, ids);
            platformChannelMapper.delete(queryWrapper);
        }
        return b;
    }

    /**
     * 处理平台渠道配置
     */
    private void handlePlatformChannel(List<PlatformChannelBo> platformChannelBoList, Long platformKey) {
        // 查询平台信息
        LambdaQueryWrapper<PlatformChannel> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PlatformChannel::getPlatformKey, platformKey);
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(queryWrapper);
        if (ObjectUtil.isNotEmpty(platformChannels)) {
            Map<Long, PlatformChannel> map = platformChannels.stream().collect(Collectors.toMap(PlatformChannel::getId, o -> o));
            List<PlatformChannel> add = new ArrayList<>();
            List<PlatformChannel> update = new ArrayList<>();
            List<Long> notDelete = new ArrayList<>();
            for (PlatformChannelBo bo : platformChannelBoList) {
                if (ObjectUtil.isNotEmpty(bo.getId())) {
                    PlatformChannel platformChannel = map.get(bo.getId());
                    platformChannel.setPlatformKey(platformKey);
                    platformChannel.setPlatformTitle(bo.getPlatformTitle());
                    platformChannel.setAppId(bo.getAppId());
                    platformChannel.setEncryptAppId(bo.getEncryptAppId());
                    platformChannel.setSecret(bo.getSecret());
                    platformChannel.setRsaPrivateKey(bo.getRsaPrivateKey());
                    platformChannel.setRsaPublicKey(bo.getRsaPublicKey());
                    platformChannel.setChannel(bo.getChannel());
                    platformChannel.setMerchantId(bo.getMerchantId());
                    update.add(platformChannel);
                    notDelete.add(platformChannel.getId());
                } else {
                    PlatformChannel platformChannel = new PlatformChannel();
                    platformChannel.setPlatformKey(platformKey);
                    platformChannel.setPlatformTitle(bo.getPlatformTitle());
                    platformChannel.setAppId(bo.getAppId());
                    platformChannel.setEncryptAppId(bo.getEncryptAppId());
                    platformChannel.setSecret(bo.getSecret());
                    platformChannel.setRsaPrivateKey(bo.getRsaPrivateKey());
                    platformChannel.setRsaPublicKey(bo.getRsaPublicKey());
                    platformChannel.setChannel(bo.getChannel());
                    platformChannel.setMerchantId(bo.getMerchantId());
                    add.add(platformChannel);
                }
            }

            if (ObjectUtil.isNotEmpty(notDelete)) {
                queryWrapper.notIn(PlatformChannel::getId, notDelete);
                platformChannelMapper.delete(queryWrapper);
            }
            if (ObjectUtil.isNotEmpty(add)) platformChannelMapper.insertBatch(add);
            if (ObjectUtil.isNotEmpty(update)) platformChannelMapper.updateBatchById(update);
        } else {
            List<PlatformChannel> platformChannelList = BeanCopyUtils.copyList(platformChannelBoList, PlatformChannel.class);
            platformChannelList.forEach(o -> o.setPlatformKey(platformKey));
            platformChannelMapper.insertBatch(platformChannelList);
        }
    }
}
