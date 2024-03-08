package com.ruoyi.zlyyhadmin.dubbo;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.system.api.RemoteMissionService;
import com.ruoyi.system.api.model.RemoteShopTourService;
import com.ruoyi.zlyyh.domain.ShopTour;
import com.ruoyi.zlyyh.domain.ShopTourLog;
import com.ruoyi.zlyyh.domain.bo.ShopTourBo;
import com.ruoyi.zlyyh.domain.vo.ShopTourVo;
import com.ruoyi.zlyyh.mapper.ShopTourLogMapper;
import com.ruoyi.zlyyh.mapper.ShopTourMapper;
import com.ruoyi.zlyyhadmin.service.IShopTourService;
import com.ruoyi.zlyyhadmin.service.IUnionpayMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteShopTourServiceImpl implements RemoteShopTourService {
    private final ShopTourMapper shopTourMapper;
    private final ShopTourLogMapper shopTourLogMapper;

    /**
     * 定时查巡检预约有效期
     */
    @Async
    @Override
    public void queryShopTourValidity(String jobParam) {
        List<ShopTourVo> shopTourVos = shopTourMapper.selectVoList(new LambdaQueryWrapper<ShopTour>().eq(ShopTour::getStatus, "1"));
        log.info("定时查询巡检预约有效期的预约数量：{}",shopTourVos.size());
        if (ObjectUtil.isNotEmpty(shopTourVos)) {
            Long count = 0L;
            for (ShopTourVo shopTourVo : shopTourVos) {
                if (DateUtils.compare(shopTourVo.getReserveValidity()) <= 0) {
                    ShopTourLog tourLog = new ShopTourLog();
                    tourLog.setTourId(shopTourVo.getId());
                    tourLog.setVerifierId(shopTourVo.getVerifierId());
                    tourLog.setOperType("7");
                    tourLog.setShopId(shopTourVo.getShopId());
                    shopTourLogMapper.insert(tourLog);

                    ShopTour shopTour = new ShopTour();
                    shopTour.setId(shopTourVo.getId());
                    shopTour.setVerifierId(null);
                    shopTour.setIsReserve("0");
                    shopTour.setStatus("0");
                    shopTour.setReserveDate(null);
                    shopTour.setReserveValidity(null);
                    shopTour.setCheckRemark(null);
                    shopTour.setShopStatus("0");
                    shopTour.setVerifierImage(null);
                    shopTour.setGoodsImage(null);
                    shopTour.setShopImage(null);
                    shopTour.setTourRemark(null);
                    shopTour.setMerchantNo(null);
                    shopTour.setOldMerchantNo(null);
                    shopTour.setMerchantType(null);
                    shopTour.setIsActivity("1");
                    shopTour.setNoActivityRemark(null);
                    shopTour.setIsClose("1");
                    shopTour.setCloseRemark(null);
                    LambdaUpdateWrapper<ShopTour> wrapper = Wrappers.lambdaUpdate();
                    wrapper.eq(ShopTour::getId,shopTour.getId());
                    wrapper.set(ShopTour::getVerifierId,null);
                    wrapper.set(ShopTour::getReserveDate,null);
                    wrapper.set(ShopTour::getReserveValidity,null);
                    wrapper.set(ShopTour::getCheckRemark,null);
                    wrapper.set(ShopTour::getVerifierImage,null);
                    wrapper.set(ShopTour::getGoodsImage,null);
                    wrapper.set(ShopTour::getShopImage,null);
                    wrapper.set(ShopTour::getTourRemark,null);
                    wrapper.set(ShopTour::getMerchantNo,null);
                    wrapper.set(ShopTour::getOldMerchantNo,null);
                    wrapper.set(ShopTour::getMerchantType,null);
                    wrapper.set(ShopTour::getNoActivityRemark,null);
                    wrapper.set(ShopTour::getCloseRemark,null);
                    shopTourMapper.update(shopTour, wrapper);

                    //清理暂存数据
                    RedisUtils.deleteObject("tourShop" + shopTourVo.getId() + "user" + shopTourVo.getVerifierId());

                    count++;
                }
            }
            log.info("巡检预约过期数量：{}",count);
        }
    }
}
