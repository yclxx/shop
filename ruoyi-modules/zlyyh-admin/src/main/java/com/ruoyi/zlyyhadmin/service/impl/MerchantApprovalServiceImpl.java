package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.AddressUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.MerchantApprovalBo;
import com.ruoyi.zlyyh.domain.vo.MerchantApprovalVo;
import com.ruoyi.zlyyh.mapper.*;
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
    private final VerifierMapper verifierMapper;
    private final VerifierShopMapper verifierShopMapper;

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
            // 查询申请信息
            MerchantApprovalVo vo = baseMapper.selectVoById(bo.getApprovalId());
            // 处理核销人员信息
            Verifier verifier = insertOrUpdateVerifier(vo);
            // 处理门店，核销人员关联信息
            handleShop(vo, verifier);
        }
        MerchantApproval update = BeanUtil.toBean(bo, MerchantApproval.class);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 处理核销人员信息
     */
    private Verifier insertOrUpdateVerifier(MerchantApprovalVo vo) {
        Verifier verifier = verifierMapper.selectOne(new LambdaQueryWrapper<Verifier>().eq(Verifier::getMobile, vo.getShopMobile()).eq(Verifier::getPlatformKey, vo.getPlatformKey()));
        if (ObjectUtil.isNotEmpty(verifier)) {
            verifier.setPlatformKey(vo.getPlatformKey());
            verifier.setMobile(vo.getBrandMobile());
            verifier.setVerifierType("admin");
            verifierMapper.updateById(verifier);
        } else {
            verifier = new Verifier();
            verifier.setId(IdUtil.getSnowflakeNextId());
            verifier.setPlatformKey(vo.getPlatformKey());
            verifier.setMobile(vo.getBrandMobile());
            verifier.setStatus("0");
            verifier.setVerifierType("admin");
            verifierMapper.insert(verifier);
        }
        return verifier;
    }

    private void handleShop(MerchantApprovalVo vo, Verifier verifier) {
        Shop shop = new Shop();
        shop.setShopId(IdUtil.getSnowflakeNextId());
        shop.setPlatformKey(vo.getMerchantPlatformKey());
        shop.setShopName(vo.getShopName());
        shop.setShopTel(vo.getShopMobile());
        shop.setShopImgs(vo.getShopImage());
        shop.setShopLogo(vo.getBrandLogo());
        shop.setAddress(vo.getShopAddressInfo());
        shop.setStatus("0");
        if (StringUtils.isNotEmpty(vo.getExtend())) {
            shop.setExtensionServiceProviderId(Long.valueOf(vo.getExtend()));
        }
        if (ObjectUtil.isNotEmpty(vo.getShopAddressInfo())) {
            String key = "importShop:" + vo.getShopAddressInfo();
            // 查询地址信息
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
            shop.setAssignDate("1");
            shop.setWeekDate(vo.getBusinessWeek());
        }
        if (StringUtils.isNotEmpty(vo.getBusinessBegin()) && StringUtils.isNotEmpty(vo.getBusinessBegin())) {
            shop.setBusinessHours(vo.getBusinessBegin() + "-" + vo.getBusinessEnd());
        }
        List<ShopMerchant> shopMerchants = new ArrayList<>();

        // 商户号
        if (StringUtils.isNotEmpty(vo.getMerchant())) {
            JSONArray jsonArray = JSONObject.parseArray(vo.getMerchant());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShopMerchant shopMerchant = new ShopMerchant();
                shopMerchant.setShopId(shop.getShopId());
                shopMerchant.setMerchantNo(jsonObject.getString("merchantNo"));
                shopMerchant.setMerchantType(jsonObject.getString("merchantType"));
                shopMerchant.setAcquirer(jsonObject.getString("acquirer"));
                shopMerchant.setPaymentMethod(jsonObject.getString("paymentMethod"));
                shopMerchant.setStatus("0");
                shopMerchants.add(shopMerchant);
            }
        }
        int insert = shopMapper.insert(shop);
        // 核销人员，门店表关联
        if (insert > 0) {
            VerifierShop verifierShop = new VerifierShop();
            verifierShop.setId(IdUtil.getSnowflakeNextId());
            verifierShop.setShopId(shop.getShopId());
            verifierShop.setVerifierId(verifier.getId());
            verifierShop.setSort(99L);
            verifierShopMapper.insert(verifierShop);
        }
        if (ObjectUtil.isNotEmpty(shopMerchants) && insert > 0) {
            shopMerchantMapper.insertBatch(shopMerchants);
        }
    }

    /**
     * 批量删除商户申请审批
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
