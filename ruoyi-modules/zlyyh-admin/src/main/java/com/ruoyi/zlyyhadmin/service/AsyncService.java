package com.ruoyi.zlyyhadmin.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.excel.convert.ExcelBigNumberConvert;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.domain.CategoryPlatformProduct;
import com.ruoyi.zlyyh.domain.CategoryProduct;
import com.ruoyi.zlyyh.domain.Order;
import com.ruoyi.zlyyh.domain.bo.OrderBo;
import com.ruoyi.zlyyh.domain.bo.OrderDownloadLogBo;
import com.ruoyi.zlyyh.domain.vo.CategoryProductVo;
import com.ruoyi.zlyyh.domain.vo.OrderVo;
import com.ruoyi.zlyyh.mapper.CategoryPlatformProductMapper;
import com.ruoyi.zlyyh.mapper.CategoryProductMapper;
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
    private final CategoryPlatformProductMapper categoryPlatformProductMapper;
    private final CategoryProductMapper categoryProductMapper;

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

    /**
     * 多平台类别新增修改时 将多平台类别关联的商品 跟平台做关联
     */
    public void categoryPlatformToCategory(Long categoryPlatformId,String categoryIds){
        //先查多平台类别下的商品(还是分页查好了)
        long totalCount = categoryPlatformProductMapper.selectCount(new LambdaQueryWrapper<CategoryPlatformProduct>().eq(CategoryPlatformProduct::getCategoryPlatformId, categoryPlatformId));
        Integer pageNum = 1;
        Integer pageSize = 100;
        log.info("添加大订单数据开始：{}", DateUtil.now());
        while (true) {
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(pageNum);
            pageQuery.setPageSize(pageSize);
            Page<CategoryPlatformProduct> result = categoryPlatformProductMapper.selectPage(pageQuery.build(), new LambdaQueryWrapper<CategoryPlatformProduct>().eq(CategoryPlatformProduct::getCategoryPlatformId, categoryPlatformId));
            TableDataInfo<CategoryPlatformProduct> tableDataInfo = TableDataInfo.build(result);

            for (CategoryPlatformProduct row : tableDataInfo.getRows()) {
                //事务 一起成功一起失败
                try {
                    setCategoryProduct(row,categoryIds);
                } catch (Exception e) {
                    log.error("类别关联商品失败：", e);
                }
            }

            if (Integer.valueOf(pageNum * pageSize).longValue() >= totalCount) {
                break;
            }
            pageNum++;
        }

    }

    private void setCategoryProduct(CategoryPlatformProduct categoryPlatformProduct,String categoryIds){
        String[] categorySplit = categoryIds.split(",");
        for (String s : categorySplit) {
            Long categoryId = Long.valueOf(s);
            CategoryProductVo categoryProductVo = categoryProductMapper.selectVoOne(new LambdaQueryWrapper<CategoryProduct>().eq(CategoryProduct::getCategoryId, categoryId).eq(CategoryProduct::getProductId, categoryPlatformProduct.getProductId()));
            if (ObjectUtil.isEmpty(categoryProductVo)){
                //未关联分类商品
                CategoryProduct categoryProduct = new CategoryProduct();
                categoryProduct.setProductId(categoryPlatformProduct.getProductId());
                categoryProduct.setCategoryId(categoryId);
                categoryProductMapper.insert(categoryProduct);
            }
        }
    }

}
