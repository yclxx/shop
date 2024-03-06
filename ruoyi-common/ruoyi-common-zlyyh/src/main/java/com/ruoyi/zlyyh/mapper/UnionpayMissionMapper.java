package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.UnionpayMission;
import com.ruoyi.zlyyh.domain.bo.UnionpayMissionUserLogBo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionUserLogVo;
import com.ruoyi.zlyyh.domain.vo.UnionpayMissionVo;
import org.apache.ibatis.annotations.Param;

/**
 * 银联任务配置Mapper接口
 *
 * @author yzg
 * @date 2024-02-21
 */
public interface UnionpayMissionMapper extends BaseMapperPlus<UnionpayMissionMapper, UnionpayMission, UnionpayMissionVo> {

    Page<UnionpayMissionUserLogVo> queryPageRewardList(@Param("bo") UnionpayMissionUserLogBo bo, Page page);
}
