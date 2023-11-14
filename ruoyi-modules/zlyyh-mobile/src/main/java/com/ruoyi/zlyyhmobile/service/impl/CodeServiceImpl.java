package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.*;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.CodeBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhmobile.enums.UnionPayCallbackBizMethodType;
import com.ruoyi.zlyyhmobile.event.UnionPayContentCallbackEvent;
import com.ruoyi.zlyyhmobile.service.ICodeService;
import com.ruoyi.zlyyhmobile.service.IDistributorService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品券码Service业务层处理
 *
 * @author yzg
 * @date 2023-09-20
 */
@RequiredArgsConstructor
@Service
public class CodeServiceImpl implements ICodeService {

    private final CodeMapper baseMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ShopMapper shopMapper;
    private final ShopProductMapper shopProductMapper;
    private final VerifierMapper verifierMapper;
    private final VerifierShopMapper verifierShopMapper;
    private final UnionPayContentOrderMapper unionPayContentOrderMapper;
    private final IDistributorService distributorService;

    @Override
    public Map<String, Object> queryVerifierByCodeNo(CodeBo bo, Long userId) {
        // 查询核销码信息
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.eq(Code::getCodeNo, bo.getCodeNo());
        CodeVo codeVo = baseMapper.selectVoOne(lqw);
        if (ObjectUtil.isEmpty(codeVo)) return null;
        // 查询门店与商品是否绑定
        LambdaQueryWrapper<ShopProduct> lqw1 = Wrappers.lambdaQuery();
        lqw1.eq(ShopProduct::getProductId, codeVo.getProductId());
        List<ShopProductVo> shopProductVos = shopProductMapper.selectVoList(lqw1);
        if (ObjectUtil.isEmpty(shopProductVos)) return null;
        // 查询核销人员是否所属门店
        List<Long> ShopIds = shopProductVos.stream().map(ShopProductVo::getShopId).collect(Collectors.toList());
        LambdaQueryWrapper<VerifierShop> lqw2 = Wrappers.lambdaQuery();
        lqw2.eq(VerifierShop::getVerifierId, userId);
        lqw2.in(VerifierShop::getShopId, ShopIds);
        long verifierCount = verifierShopMapper.selectCount(lqw2);
        if (verifierCount <= 0) return null;
        Order order = orderMapper.selectById(codeVo.getNumber());
        Map<String, Object> map = new HashMap<>();
        map.put("code", codeVo);
        map.put("order", order);
        return map;
    }

    /**
     * 新增商品券码
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean insertByOrder(Long number) {
        // 先查询是否有生成过了，有券码直接返回成功
        Long l = baseMapper.selectCount(Wrappers.lambdaQuery(Code.class).eq(Code::getNumber, number));
        if (l > 0) {
            return true;
        }
        OrderVo orderVo = orderMapper.selectVoById(number);
        if (null == orderVo) {
            return false;
        }
        Product product = productMapper.selectById(orderVo.getProductId());
        if (null == product) {
            return false;
        }
        for (int i = 0; i < orderVo.getCount(); i++) {
            Code add = getCode(orderVo);
            // 部门信息
            PermissionUtils.setDeptIdAndUserId(add, product.getSysDeptId(), product.getSysUserId());
            boolean flag = baseMapper.insert(add) > 0;
            if (!flag) {
                throw new ServiceException("系统繁忙，请稍后重试！");
            }
        }
        return true;
    }

    /**
     * 查询核销码
     *
     * @param number 订单号
     * @return 核销码集合
     */
    public List<CodeVo> queryByNumber(Long number) {
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.eq(Code::getNumber, number);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 查询券码
     *
     * @param codeNo 核销码
     * @return 券码信息
     */
    public CodeVo queryByCodeNo(String codeNo) {
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.eq(Code::getCodeNo, codeNo);
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 作废券码
     *
     * @param codeNo 券码
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancellationCode(String codeNo) {
        CodeVo codeVo = this.queryByCodeNo(codeNo);
        if (null == codeVo) {
            throw new ServiceException("核销码错误或不存在");
        }
        if ("1".equals(codeVo.getUsedStatus())) {
            throw new ServiceException("券码已核销，不可操作");
        }
        if (!"3".equals(codeVo.getUsedStatus())) {
            Code code = new Code();
            code.setId(codeVo.getId());
            code.setUsedStatus("3");
            code.setUsedTime(new Date());
            boolean b = baseMapper.updateById(code) > 0;
            if (!b) {
                throw new ServiceException("作废失败");
            }
        }
        // 查询订单
        OrderVo orderVo = orderMapper.selectVoById(codeVo.getNumber());
        if (null == orderVo) {
            return true;
        }
        callback(codeVo, orderVo, UnionPayCallbackBizMethodType.RETURN_BOND);
        return true;
    }

    /**
     * 核销券码
     */
    @Override
    public Boolean usedCodes(CodeBo bo) {
        CodeVo codeVo = this.queryByCodeNo(bo.getCodeNo());
        if (null == codeVo) {
            throw new ServiceException("核销码错误或不存在");
        }
        if (!"0".equals(codeVo.getUsedStatus()) && !"1".equals(codeVo.getUsedStatus())) {
            throw new ServiceException("券码已失效");
        }

        // 查询订单
        OrderVo orderVo = orderMapper.selectVoById(codeVo.getNumber());
        if (null == orderVo) {
            throw new ServiceException("订单不存在");
        }
        if (!"1".equals(codeVo.getUsedStatus())) {
            Code code = new Code();
            code.setId(codeVo.getId());
            code.setUsedStatus("1");
            code.setUsedTime(new Date());
            // 查询核销人员
            if (ObjectUtil.isNotEmpty(bo.getShopId())) {
                LoginHelper.getUserId();
                Verifier verifier = verifierMapper.selectById(LoginHelper.getUserId());
                if (ObjectUtil.isNotEmpty(verifier)) {
                    code.setVerifierId(verifier.getId());
                    code.setVerifierMobile(verifier.getMobile());
                }
            }
            // 查询门店
            if (ObjectUtil.isNotEmpty(bo.getShopId())) {
                Shop shop = shopMapper.selectById(bo.getShopId());
                if (ObjectUtil.isNotEmpty(shop)) {
                    code.setShopId(shop.getShopId());
                    code.setShopName(shop.getShopName());
                }
            }
            boolean b = baseMapper.updateById(code) > 0;
            if (!b) {
                throw new ServiceException("核销失败");
            }
        }
        // 向银联发送请求
        callback(codeVo, orderVo, UnionPayCallbackBizMethodType.VERIFY_BOND);
        return true;
    }

    /**
     * 核销券码
     */
    @Override
    public Boolean usedCode(String codeNo) {
        CodeVo codeVo = this.queryByCodeNo(codeNo);
        if (null == codeVo) {
            throw new ServiceException("核销码错误或不存在");
        }
        if (!"0".equals(codeVo.getUsedStatus()) && !"1".equals(codeVo.getUsedStatus())) {
            throw new ServiceException("券码已失效");
        }
        // 查询订单
        OrderVo orderVo = orderMapper.selectVoById(codeVo.getNumber());
        if (null == orderVo) {
            throw new ServiceException("订单不存在");
        }
        if (!"1".equals(codeVo.getUsedStatus())) {
            Code code = new Code();
            code.setId(codeVo.getId());
            code.setUsedStatus("1");
            code.setUsedTime(new Date());
            boolean b = baseMapper.updateById(code) > 0;
            if (!b) {
                throw new ServiceException("核销失败");
            }
        }
        callback(codeVo, orderVo, UnionPayCallbackBizMethodType.VERIFY_BOND);
        return true;
    }

    /**
     * 票券返还
     *
     * @param codeNo 核销码
     * @return true 成功,false 失败
     */
    @Override
    public Boolean rollbackCode(String codeNo) {
        CodeVo codeVo = this.queryByCodeNo(codeNo);
        if (null == codeVo) {
            throw new ServiceException("核销码错误或不存在");
        }
        if (!"0".equals(codeVo.getUsedStatus()) && !"1".equals(codeVo.getUsedStatus())) {
            throw new ServiceException("券码不可返还");
        }
        // 查询订单
        OrderVo orderVo = orderMapper.selectVoById(codeVo.getNumber());
        if (null == orderVo) {
            throw new ServiceException("订单不存在");
        }
        if (!"0".equals(codeVo.getUsedStatus())) {
            Code code = new Code();
            code.setId(codeVo.getId());
            code.setUsedStatus("0");
            boolean b = baseMapper.updateById(code) > 0;
            if (!b) {
                throw new ServiceException("票券返还失败");
            }
        }
        callback(codeVo, orderVo, UnionPayCallbackBizMethodType.ROLLBACK);
        return true;
    }

    public boolean checkCodeNo(String codeNo) {
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.eq(Code::getCodeNo, codeNo);
        return baseMapper.selectCount(lqw) > 0;
    }

    @NotNull
    private Code getCode(OrderVo orderVo) {
        Code add = new Code();
        add.setProductId(orderVo.getProductId());
        add.setProductName(orderVo.getProductName());
        add.setProductSessionId(orderVo.getProductSessionId());
        add.setProductSessionName(orderVo.getProductSessionName());
        add.setProductSkuId(orderVo.getProductSkuId());
        add.setProductSkuName(orderVo.getProductSkuName());
        // 生成券码
        add.setCodeNo(getCodeNo(5));
        add.setAllocationState("1");
        add.setNumber(orderVo.getNumber());
        return add;
    }

    /**
     * 生成核销码
     *
     * @param maxCount 失败重试次数
     * @return 核销码
     */
    private String getCodeNo(int maxCount) {
        maxCount--;
        if (maxCount < 0) {
            return IdUtil.getSnowflakeNextIdStr();
        }
        String codeNo = IdUtils.getSortNumbers();
        boolean b = checkCodeNo(codeNo);
        if (b) {
            return getCodeNo(maxCount);
        }
        return codeNo;
    }

    /**
     * 获取产品编号 如果有规格则返回规格ID
     *
     * @param orderVo 订单信息
     * @return 产品编号
     */
    private String getProductId(OrderVo orderVo) {
        String productId = orderVo.getProductId().toString();
        if (null != orderVo.getProductSkuId()) {
            productId = orderVo.getProductSkuId().toString();
        }
        return productId;
    }

    /**
     * 获取分销商
     *
     * @param codeVo  券码信息
     * @param orderVo 订单信息
     * @return 分销商信息
     */
    private void callback(CodeVo codeVo, OrderVo orderVo, UnionPayCallbackBizMethodType bizMethod) {
        LambdaQueryWrapper<UnionPayContentOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(UnionPayContentOrder::getNumber, orderVo.getNumber());
        lqw.last("order by id desc limit 1");
        UnionPayContentOrderVo unionPayContentOrderVo = unionPayContentOrderMapper.selectVoOne(lqw);
        if (null == unionPayContentOrderVo) {
            // 没有银联分销订单，直接返回
            return;
        }
        DistributorVo distributorVo = distributorService.queryById(unionPayContentOrderVo.getUnionPayAppId());
        if (null == distributorVo) {
            return;
        }
        // 异步通知退券结果
        Date time = codeVo.getUsedTime();
        if (null == time || bizMethod.equals(UnionPayCallbackBizMethodType.ROLLBACK)) {
            time = new Date();
        }
        String prodTime = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSS, time);
        UnionPayContentCallbackEvent event = new UnionPayContentCallbackEvent(getProductId(orderVo), distributorVo.getPrivateKey(), distributorVo.getDistributorId(), distributorVo.getBackUrl(), RSAUtils.encryptByPublicKey(codeVo.getCodeNo(), distributorVo.getPublicKey()), prodTime, bizMethod);
        SpringUtils.context().publishEvent(event);
    }

    /**
     * 今日预约，今日核销。统计
     */
    public Map<String, Long> getCodeTimeCount() {
        Long userId = LoginHelper.getUserId();
        Verifier verifier = verifierMapper.selectById(userId);
        getVerifierList(verifier);
        // 查询已核销数据量
        LambdaQueryWrapper<Code> lqw1 = Wrappers.lambdaQuery();
        List<Long> longs = getVerifierList(verifier);
        lqw1.in(Code::getVerifierId, longs);
        lqw1.ge(Code::getUsedTime, DateUtil.beginOfDay(DateUtil.date()));
        lqw1.ge(Code::getUsedStatus, "1");
        long usedCount = baseMapper.selectCount(lqw1);
        // 查询已预约数量
        LambdaQueryWrapper<Code> lqw2 = Wrappers.lambdaQuery();
        lqw2.in(Code::getVerifierId, longs);
        lqw2.ge(Code::getAppointmentDate, DateUtil.beginOfDay(DateUtil.date()));
        lqw2.ge(Code::getAllocationState, "1");
        long appointmentCount = baseMapper.selectCount(lqw2);

        Map<String, Long> map = new HashMap<>();
        map.put("usedCount", usedCount);
        map.put("appointmentCount", appointmentCount);
        return map;
    }

    @Override
    public TableDataInfo<CodeVo> getCodeListByVerifier(CodeBo bo, PageQuery pageQuery) {
        Verifier verifier = verifierMapper.selectById(bo.getVerifierId());
        if (ObjectUtil.isEmpty(verifier)) throw new ServiceException("无此核销人员");
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(Code::getCreateTime);
        lqw.likeRight(StringUtils.isNotEmpty(bo.getProductName()), Code::getProductName, bo.getProductName());
        // 核销日期处理
        if (StringUtils.isNotEmpty(bo.getUsedTimes())) {
            lqw.ge(Code::getUsedTime, DateUtils.parseDate(bo.getUsedTimes()));
            if (ObjectUtil.isNotEmpty(bo.getUsedEndTime())) {
                lqw.le(Code::getUsedTime, DateUtils.parseDate(bo.getUsedEndTime()));
            }
        } else {
            lqw.ge(Code::getUsedTime, DateUtil.beginOfDay(DateUtil.date()));
        }
        // 核销人员处理
        if (verifier.getVerifierType().equals("admin")) {
            LambdaQueryWrapper<VerifierShop> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(VerifierShop::getVerifierId, verifier.getId());
            List<VerifierShopVo> verifierShopVos = verifierShopMapper.selectVoList(queryWrapper);
            List<Long> collect = verifierShopVos.stream().map(VerifierShopVo::getShopId).collect(Collectors.toList());
            lqw.in(Code::getShopId, collect);
        } else {
            lqw.eq(Code::getVerifierId, verifier.getId());
        }
        Page<CodeVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    public List<CodeVo> statistics(CodeBo bo) {
        if (StringUtils.isEmpty(bo.getProductName())) return null;
        if (ObjectUtil.isEmpty(bo.getShopId())) return null;
        Verifier verifier = verifierMapper.selectById(bo.getVerifierId());
        List<Long> longs = getVerifierList(verifier);
        List<CodeVo> codeVos = baseMapper.selectProductList(bo.getProductName(), bo.getShopId(), longs);
        // 核销日期处理
        Date startTime;
        Date endTime;
        if (StringUtils.isNotEmpty(bo.getUsedTimes())) {
            startTime = DateUtils.parseDate(bo.getUsedTimes());
        } else {
            startTime = DateUtil.beginOfDay(DateUtils.getNowDate());
        }
        if (ObjectUtil.isNotEmpty(bo.getUsedEndTime())) {
            endTime = DateUtils.parseDate(bo.getUsedEndTime());
        } else {
            endTime = DateUtil.endOfDay(DateUtils.getNowDate());
        }

        codeVos.forEach(o -> {
            Long usedCount = buildQueryWrapper(bo.getShopId(), o.getProductId(), longs, startTime, endTime, "1");
            Long appointmentCount = buildQueryWrapper(bo.getShopId(), o.getProductId(), longs, startTime, endTime, "2");
            o.setUsedCount(usedCount);
            o.setAppointmentCount(appointmentCount);
        });
        return codeVos;
    }

    private Long buildQueryWrapper(Long shopId, Long productId, List<Long> verifierId, Date startTime, Date endTime, String type) {
        LambdaQueryWrapper<Code> lqw = Wrappers.lambdaQuery();
        lqw.in(Code::getVerifierId, verifierId);
        lqw.eq(Code::getShopId, shopId);
        lqw.eq(Code::getProductId, productId);
        if (type.equals("1")) {
            lqw.ge(Code::getUsedTime, startTime);
            lqw.le(Code::getUsedTime, endTime);
            lqw.eq(Code::getUsedStatus, "1");
        } else {
            lqw.ge(Code::getAppointmentDate, startTime);
            lqw.le(Code::getAppointmentDate, endTime);
            lqw.eq(Code::getAppointmentStatus, "1");
        }
        return baseMapper.selectCount(lqw);
    }

    private List<Long> getVerifierList(Verifier verifier) {
        List<Long> longs;
        if (verifier.getVerifierType().equals("admin")) {
            longs = new ArrayList<>();
            longs.add(verifier.getId());
            LambdaQueryWrapper<VerifierShop> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(VerifierShop::getVerifierId, verifier.getId());
            List<VerifierShopVo> verifierShopVos = verifierShopMapper.selectVoList(queryWrapper);
            List<Long> collect = verifierShopVos.stream().map(VerifierShopVo::getShopId).collect(Collectors.toList());
            LambdaQueryWrapper<VerifierShop> queryWrapper2 = Wrappers.lambdaQuery();
            queryWrapper2.in(VerifierShop::getShopId, collect);
            List<VerifierShop> verifierShops = verifierShopMapper.selectList(queryWrapper2);

            List<Long> collect1 = verifierShops.stream().map(VerifierShop::getVerifierId).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(collect1)) {
                longs.addAll(collect1);
            }
        } else {
            longs = new ArrayList<>();
            longs.add(verifier.getId());
        }
        return longs;
    }
}
