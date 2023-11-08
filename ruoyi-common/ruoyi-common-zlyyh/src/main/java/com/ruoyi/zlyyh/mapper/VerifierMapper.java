package com.ruoyi.zlyyh.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.zlyyh.domain.Verifier;
import com.ruoyi.zlyyh.domain.vo.VerifierVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 核销人员Mapper接口
 *
 * @author yzg
 * @date 2023-10-31
 */
public interface VerifierMapper extends BaseMapperPlus<VerifierMapper, Verifier, VerifierVo> {
    /**
     * 查询单个对象
     *
     * @param wrapper 查询条件
     * @param user    手机号
     * @return 对象信息
     */
    Verifier selectOneIncludeMobile(@Param(Constants.WRAPPER) Wrapper<Verifier> wrapper, @Param("user") Verifier user);

    /**
     * 分页查询
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @param user    带手机号查询
     * @return 结果
     */
    Page<Verifier> selectPageIncludeMobile(@Param("page") IPage<Verifier> page, @Param(Constants.WRAPPER) Wrapper<Verifier> wrapper, @Param("user") Verifier user);

    /**
     * 查询集合
     *
     * @param wrapper 查询条件
     * @param user    带手机号查询
     * @return 结果
     */
    List<Verifier> selectListIncludeMobile(@Param(Constants.WRAPPER) Wrapper<Verifier> wrapper, @Param("user") Verifier user);
    /**
     * 根据上级查询
     */
    List<Long> selectIdBySuperior(Long superiorId);
}
