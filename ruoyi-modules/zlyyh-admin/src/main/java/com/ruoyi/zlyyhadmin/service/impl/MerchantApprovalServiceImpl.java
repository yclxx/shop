package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.MerchantApproval;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.domain.ShopMerchant;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.vo.MerchantApprovalVo;
import com.ruoyi.zlyyh.mapper.MerchantApprovalMapper;
import com.ruoyi.zlyyh.mapper.ShopMapper;
import com.ruoyi.zlyyh.mapper.ShopMerchantMapper;
import com.ruoyi.zlyyhadmin.service.IMerchantApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 商户申请审批Service业务层处理
 *
 * @author yzg
 * @date 2023-10-19
 */
@RequiredArgsConstructor
@Service
public class MerchantApprovalServiceImpl implements IMerchantApprovalService {

    private final MerchantApprovalMapper baseMapper;
    private final ShopMapper shopMapper;
    private final ShopMerchantMapper shopMerchantMapper;

    /**
     * 查询商户申请审批
     */
    @Override
    public MerchantApprovalVo queryById(Long approvalId) {
        return baseMapper.selectVoById(approvalId);
    }

    /**
     * 查询商户申请审批列表
     */
    @Override
    public TableDataInfo<MerchantApprovalVo> queryPageList(MerchantApprovalBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MerchantApproval> lqw = buildQueryWrapper(bo);
        Page<MerchantApprovalVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询商户申请审批列表
     */
    @Override
    public List<MerchantApprovalVo> queryList(MerchantApprovalBo bo) {
        LambdaQueryWrapper<MerchantApproval> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MerchantApproval> buildQueryWrapper(MerchantApprovalBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MerchantApproval> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPlatformKey() != null, MerchantApproval::getPlatformKey, bo.getPlatformKey());
        lqw.like(StringUtils.isNotBlank(bo.getBrandName()), MerchantApproval::getBrandName, bo.getBrandName());
        lqw.like(StringUtils.isNotBlank(bo.getShopName()), MerchantApproval::getShopName, bo.getShopName());
        lqw.eq(StringUtils.isNotBlank(bo.getShopMobile()), MerchantApproval::getShopMobile, bo.getShopMobile());
        lqw.eq(StringUtils.isNotBlank(bo.getNature()), MerchantApproval::getNature, bo.getNature());
        lqw.eq(StringUtils.isNotBlank(bo.getAccount()), MerchantApproval::getAccount, bo.getAccount());
        lqw.orderByDesc(MerchantApproval::getCreateTime);
        return lqw;
    }

    /**
     * 新增商户申请审批
     */
    @Override
    public Boolean insertByBo(MerchantApprovalBo bo) {
        MerchantApproval add = BeanUtil.toBean(bo, MerchantApproval.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setApprovalId(add.getApprovalId());
        }
        return flag;
    }

    /**
     * 修改商户申请审批
     */
    @Override
    public Boolean updateByBo(MerchantApprovalBo bo) {
        if (bo.getApprovalStatus().equals("1")) {
            MerchantApprovalVo vo = baseMapper.selectVoById(bo.getApprovalId());
            Shop shop = new Shop();
            shop.setShopId(IdUtil.getSnowflakeNextId());
            shop.setPlatformKey(vo.getPlatformKey());
            shop.setShopName(vo.getShopName());
            shop.setShopTel(vo.getShopMobile());
            shop.setShopImgs(vo.getShopImage());
            shop.setShopLogo(vo.getBrandLogo());
            shop.setAddress(vo.getShopAddress());
            shop.setStatus("0");
            if (ObjectUtil.isEmpty(vo.getShopAddressInfo())) {
                String key = "importShop:" + vo.getShopAddressInfo();
                JSONObject addressInfo = AddressUtils.getAddressInfo(vo.getShopAddressInfo());
                shop.setFormattedAddress(addressInfo.getString("formatted_address"));
                shop.setProvince(addressInfo.getString("province"));
                shop.setCity(addressInfo.getString("city"));
                shop.setDistrict(addressInfo.getString("district"));
                String adcode = addressInfo.getString("adcode");
                String procode = adcode.substring(0, 2) + "0000";
                String citycode = adcode.substring(0, 4) + "00";
                shop.setProcode(procode);
                shop.setCitycode(citycode);
                shop.setAdcode(adcode);
                if (ObjectUtil.isEmpty(shop.getLatitude()) || ObjectUtil.isEmpty(shop.getLongitude())) {
                    String location = addressInfo.getString("location");
                    String[] split = location.split(",");
                    String longitude = split[0];
                    String latitude = split[1];
                    shop.setLongitude(new BigDecimal(longitude));
                    shop.setLatitude(new BigDecimal(latitude));
                }
                if (StringUtils.isNotBlank(key)) {
                    RedisUtils.setCacheObject(key, addressInfo, Duration.ofDays(5));
                }
            }
            shop.setAccount(vo.getAccount());
            shop.setNature(vo.getNature());
            shop.setInvoice(vo.getInvoiceType());
            shop.setActivity(vo.getActivity());
            if (StringUtils.isNotEmpty(vo.getBusinessWeek())) {
                shop.setWeekDate(vo.getBusinessWeek());
                shop.setBusinessHours(vo.getBusinessBegin() + "-" + vo.getBusinessEnd());
            }
            List<ShopMerchant> shopMerchants = new ArrayList<>();
            // 云闪付商户号
            if (StringUtils.isNotEmpty(vo.getYsfMerchant())) {
                ShopMerchant shopMerchant = new ShopMerchant();
                shopMerchant.setShopId(shop.getShopId());
                shopMerchant.setMerchantNo(vo.getYsfMerchant());
                shopMerchant.setMerchantType("1");
                shopMerchant.setStatus("0");
                shopMerchants.add(shopMerchant);
            }
            // 微信商户号
            if (StringUtils.isNotEmpty(vo.getWxMerchant())) {
                ShopMerchant shopMerchant = new ShopMerchant();
                shopMerchant.setShopId(shop.getShopId());
                shopMerchant.setMerchantNo(vo.getWxMerchant());
                shopMerchant.setMerchantType("0");
                shopMerchant.setStatus("0");
                shopMerchants.add(shopMerchant);
            }
            // 支付宝商户号
            if (StringUtils.isNotEmpty(vo.getPayMerchant())) {
                ShopMerchant shopMerchant = new ShopMerchant();
                shopMerchant.setShopId(shop.getShopId());
                shopMerchant.setMerchantNo(vo.getPayMerchant());
                shopMerchant.setMerchantType("2");
                shopMerchant.setStatus("0");
                shopMerchants.add(shopMerchant);
            }
            int insert = shopMapper.insert(shop);
            if (ObjectUtil.isNotEmpty(shopMerchants) && insert > 0) {
                shopMerchantMapper.insertBatch(shopMerchants);
            }
        }
        MerchantApproval update = BeanUtil.toBean(bo, MerchantApproval.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 批量删除商户申请审批
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
