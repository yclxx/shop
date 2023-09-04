package com.ruoyi.zlyyhadmin.service;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.excel.convert.ExcelBigNumberConvert;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.Cust;
import com.ruoyi.zlyyh.domain.CustOrder;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.mapper.OrderMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyhadmin.config.FileConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AsyncService {
    private final IOrderService orderService;
    private final IOrderDownloadLogService orderDownloadLogService;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final ICustService custService;
    private final ICustOrderService custOrderService;

    /**
     * 同步订单数据
     */

    public void syncOrderData(String startTime, String endTime, Long platformKey) {
        int pageNum = 0;
        int pageSize = 500;
        int count = 0;
        while (true) {
            count = pageNum * pageSize;
            List<CustOrder> custOrderList = custOrderService.syncOrderData(startTime, endTime, count, pageSize);
            if (ObjectUtil.isNotEmpty(custOrderList)) {
                Order order = null;
                for (CustOrder custOrder : custOrderList) {
                    OrderVo orderVo = orderMapper.selectVoById(custOrder.getOrderId());
                    if (null != orderVo) {
                        continue;
                    }
                    //新增
                    order = new Order();
                    order.setNumber(custOrder.getOrderId());
                    order.setProductId(custOrder.getGoodsId());
                    order.setUserId(custOrder.getCustId());
                    order.setProductName(custOrder.getGoodsName());
                    order.setProductImg(custOrder.getMainImage());
                    if ("3".equals(custOrder.getType())) {
                        order.setPickupMethod("2");
                        order.setTotalAmount(new BigDecimal(custOrder.getTotalPoint()));
                        order.setOutAmount(new BigDecimal(custOrder.getRealPricePoint()));
                    } else {
                        order.setPickupMethod("1");
                        order.setTotalAmount(custOrder.getTotalPrice());
                        order.setOutAmount(custOrder.getDiscountPrice());
                    }
                    order.setReducedPrice(order.getReducedPrice());
                    order.setPayTime(custOrder.getPayTime());
                    order.setExpireDate(custOrder.getExpireDate());
                    order.setCount(Long.parseLong(custOrder.getTotalNum().toString()));
                    if (0 == custOrder.getStatus()) {
                        order.setStatus("0");
                    } else if (1 == custOrder.getStatus() || 2 == custOrder.getStatus()) {
                        order.setStatus("2");
                    } else if (3 == custOrder.getStatus() || 5 == custOrder.getStatus()) {
                        order.setStatus("3");
                    } else if (4 == custOrder.getStatus()) {
                        order.setStatus("1");
                    }
                    order.setAccount(custOrder.getMobile());
                    if (1 == custOrder.getExgStatus()) {
                        order.setSendStatus("2");
                    } else if (2 == custOrder.getExgStatus()) {
                        order.setSendStatus("3");
                    } else {
                        order.setSendStatus("0");
                    }
                    order.setPlatformKey(platformKey);
                    order.setCreateTime(custOrder.getCreateTime());
                    order.setUpdateTime(custOrder.getUpdateTime());
                    orderMapper.insert(order);
                }
            }
            if (ObjectUtil.isEmpty(custOrderList) || custOrderList.size() < pageSize) {
                break;
            } else {
                pageNum += 1;
            }
        }
    }

    /**
     * 同步用户数据
     */
    public void syncUserData(String beginStartDate, String endStartDate, Long platformKey) {
        int pageNum = 0;
        int pageSize = 500;
        int count = 0;
        while (true) {
            count = pageNum * pageSize;
            List<Cust> custList = custService.selectAll(beginStartDate, endStartDate, count, pageSize);
            User user = null;
            if (ObjectUtil.isNotEmpty(custList)) {
                for (Cust cust : custList) {
                    user = userMapper.selectById(cust.getCustId());
                    if (null != user) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(cust.getMobile())) {
                        user = userMapper.selectOneIncludeMobile(new LambdaQueryWrapper<User>()
                            .eq(User::getPlatformKey, platformKey), new User(cust.getMobile()));
                    }
                    if (null != user) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(cust.getUnionpayOpenid())) {
                        user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                            .eq(User::getPlatformKey, platformKey).eq(User::getOpenId, cust.getUnionpayOpenid()));
                    }
                    if (null != user) {
                        // 修改
                        continue;
                    }
                    user = new User();
                    user.setMobile(cust.getMobile());
                    user.setUserId(cust.getCustId());
                    user.setOpenId(cust.getUnionpayOpenid());
                    user.setUserName(cust.getRealName());
                    user.setUserImg(cust.getFaceUrl());
                    user.setCreateTime(cust.getCreateTime());
                    user.setUpdateTime(cust.getUpdateTime());
                    user.setPlatformKey(platformKey);
                    if (StringUtils.isNotBlank(user.getMobile())) {
                        user.setReloadUser("1");
                    }
                    userMapper.insert(user);
                }
            }
            if (ObjectUtil.isEmpty(custList) || custList.size() < pageSize) {
                break;
            } else {
                pageNum += 1;
            }
        }

    }

    /**
     * 导入商户门店
     *
     * @param file
     */
    public void importShopData(MultipartFile file, Long platformKey) throws IOException {

    }

    /**
     * 查询订单生成Excel
     *
     * @param bo
     * @return
     */
    public void orderImportExcel(OrderBo bo, OrderDownloadLogBo logBo) {
        logBo.setStatus("1");
        PageQuery pageQuery = new PageQuery();
        pageQuery.setOrderByColumn("number");
        pageQuery.setIsAsc("desc");
        try {
            // 创建临时文件
            String bucketName = FileConfig.getDomain() + FileConfig.getPrefix();
            String fileName = IdUtil.getSnowflake().nextIdStr() + ".xlsx";

            File file = new File(FileConfig.getPath() + "" + fileName);
            try {
                ExcelWriterBuilder builder = EasyExcel.write(file, OrderVo.class)
                    .autoCloseStream(false)
                    // 自动适配
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 大数值自动转换 防止失真
                    .registerConverter(new ExcelBigNumberConvert());
                WriteSheet sheet = builder.sheet("订单明细").build();
                ExcelWriter build = builder.build();
                int pageIndex = 1;
                int pageSize = 200;
                pageQuery.setPageSize(pageSize);
                try {
                    while (true) {
                        pageQuery.setPageNum(pageIndex);
                        TableDataInfo<OrderVo> tableDataInfo = orderService.queryPageList(bo, pageQuery);
                        for (OrderVo orderVo : tableDataInfo.getRows()) {
                            if (StringUtils.isNotBlank(orderVo.getAccount())) {
                                orderVo.setAccount(DesensitizedUtil.mobilePhone(orderVo.getAccount()));
                            }
                        }
                        build.write(tableDataInfo.getRows(), sheet);
                        int sum = pageIndex * pageSize;
                        if (sum >= tableDataInfo.getTotal()) {
                            break;
                        }
                        pageIndex++;
                    }
                } finally {
                    build.finish();
                }
                logBo.setFileUrl(bucketName + "/" + fileName);
                logBo.setStatus("2");
            } finally {
            }
        } catch (Exception e) {
            log.error("下载订单数据异常", e);
            logBo.setFailReason("下载异常，请联系管理员！");
            logBo.setStatus("3");
        }
        orderDownloadLogService.updateByBo(logBo);
    }
}
