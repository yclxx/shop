package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.ActivityFileShop;
import com.ruoyi.zlyyh.domain.bo.ActivityFileShopBo;
import com.ruoyi.zlyyh.domain.vo.ActivityFileShopVo;
import com.ruoyi.zlyyh.domain.vo.AreaVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动商户Mapper接口
 *
 * @author yzg
 * @date 2024-01-03
 */
public interface ActivityFileShopMapper extends BaseMapperPlus<ActivityFileShopMapper, ActivityFileShop, ActivityFileShopVo> {

    /**
     * 获取商户列表
     */
    Page<ActivityFileShopVo> selectFileShopList(Page page,@Param("bo") ActivityFileShopBo bo);

    /**
     * 获取当前查询批次城市列表
     */
    List<AreaVo> selectCityList(String fileId);

    /**
     * 查询商户类型
     */
    List<Long> selectTypeByFileId(String fileId);
}
