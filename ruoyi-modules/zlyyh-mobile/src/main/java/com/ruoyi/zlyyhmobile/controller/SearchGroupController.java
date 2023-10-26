package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.bo.SearchGroupBo;
import com.ruoyi.zlyyh.domain.vo.SearchGroupVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.ISearchGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索彩蛋配置控制器
 * 前端访问路由地址为:/zlyyh/searchGroup
 * @author yzg
 * @date 2023-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/searchGroup/ignore")
public class SearchGroupController extends BaseController {

    private final ISearchGroupService searchGroupService;

    /**
     * 查询搜索彩蛋配置列表
     */
    @GetMapping("/list")
    public TableDataInfo<SearchGroupVo> list(SearchGroupBo bo, PageQuery pageQuery) {
        bo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        return searchGroupService.queryPageList(bo, pageQuery);
    }


    /**
     * 获取商品详情
     *
     * @return 商品列表
     */
    @GetMapping("/getSearchGroupInfo")
    public R<SearchGroupVo> getSearchGroupInfo(SearchGroupBo searchGroupBo) {
        searchGroupBo.setShowCity(ServletUtils.getHeader(ZlyyhConstants.CITY_CODE));
        searchGroupBo.setPlatformKey(ZlyyhUtils.getPlatformId());
        searchGroupBo.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        SearchGroupVo searchGroupVo = searchGroupService.queryByContent(searchGroupBo);
        return R.ok(searchGroupVo);
    }


}
