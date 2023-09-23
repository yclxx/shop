package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.RSAUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.zlyyh.domain.Code;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.vo.CodeVo;
import com.ruoyi.zlyyh.domain.vo.DistributorVo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.domain.vo.UnionPayContentOrderVo;
import com.ruoyi.zlyyh.mapper.CodeMapper;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.mapper.ProductMapper;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyhmobile.enums.UnionPayCallbackBizMethodType;
import com.ruoyi.zlyyhmobile.event.UnionPayContentCallbackEvent;
import com.ruoyi.zlyyhmobile.service.ICodeService;
import com.ruoyi.zlyyhmobile.service.IDistributorService;
import com.ruoyi.zlyyhmobile.service.IUnionPayContentOrderService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
    private final IUnionPayContentOrderService unionPayContentOrderService;
    private final IDistributorService distributorService;

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
     *
     * @param codeNo 券码
     * @return true 成功，false 失败
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
        UnionPayContentOrderVo unionPayContentOrderVo = unionPayContentOrderService.queryByNumber(orderVo.getNumber());
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

}
