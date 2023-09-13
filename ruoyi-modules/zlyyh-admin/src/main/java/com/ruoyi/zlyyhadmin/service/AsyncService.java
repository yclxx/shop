package com.ruoyi.zlyyhadmin.service;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.excel.convert.ExcelBigNumberConvert;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyhadmin.config.FileConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class AsyncService {
    private final IOrderService orderService;
    private final IOrderDownloadLogService orderDownloadLogService;

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
