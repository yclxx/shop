package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.BeanCopyUtils;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.constant.ZlyyhConstants;
import com.ruoyi.zlyyh.domain.InviteUserLog;
import com.ruoyi.zlyyh.domain.bo.InviteUserLogBo;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.mapper.InviteUserLogMapper;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 邀请记录Service业务层处理
 *
 * @author yzg
 * @date 2023-08-08
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class InviteUserLogServiceImpl implements IInviteUserLogService {

    private final InviteUserLogMapper baseMapper;
    private final IUserService userService;
    private final YsfConfigService ysfConfigService;
    private final IOrderService orderService;
    private final IPlatformService platformService;
    private final IMissionService missionService;
    private final IMissionGroupService missionGroupService;
    private final String inviteBind = "inviteBind:";

    /**
     * 绑定用户邀请关系
     *
     * @param bo
     */
    @Override
    public void inviteBind(InviteUserLogBo bo) {
        Long platformId = ZlyyhUtils.getPlatformId();
        // 被邀请用户ID
        Long userId;
        try {
            userId = LoginHelper.getUserId();
        } catch (Exception e) {
            log.info("绑定用户邀请关系，被邀请用户未登录，bo={},e={}", bo, e);
            return;
        }
        if (null == userId || null == bo.getUserId() || bo.getUserId() < 1 || null == bo.getMissionId() || bo.getMissionId() < 1) {
            log.info("绑定用户邀请关系，请求信息缺失，bo={}", bo);
            return;
        }
        if (userId.equals(bo.getUserId())) {
            log.info("绑定用户邀请关系，自己不能给自己助力，bo={},userId={}", bo, userId);
            return;
        }
        // 校验用户是否满足条件
        MissionVo missionVo = missionService.queryById(bo.getMissionId());
        try {
            check(missionVo);
        } catch (Exception e) {
            log.info("绑定用户邀请关系，绑定失败校验不通过，bo={}，e={}", bo, e);
            return;
        }
        UserVo userVo = userService.queryById(userId);
        if (null == userVo || !userVo.getPlatformKey().equals(platformId)) {
            log.info("绑定用户邀请关系，用户不存在或平台不匹配，bo={}，userId={},platformKey={}", bo, userId, platformId);
            return;
        }
        int day = 30;
        try {
            String s = ysfConfigService.queryValueByKey(platformId, ZlyyhConstants.inviteUserDay);
            if (StringUtils.isNotBlank(s)) {
                day = Integer.parseInt(s);
            }
        } catch (Exception e) {
            log.error("绑定用户邀请关系，获取ysfConfig配置异常：", e);
        }
        if (null == userVo.getLastLoginDate()) {
            // 查询用户订单
            Long orderCount = orderService.countByUserId(userId, day);
            if (null != orderCount && orderCount > 0) {
                log.info("绑定用户邀请关系，用户不是新用户，被邀请用户ID：{}，30天内有订单：{}，bo={}", userId, orderCount, bo);
                return;
            }
        } else {
            if (DateUtils.getDatePoorDay(new Date(), userVo.getLastLoginDate(), true) <= day) {
                log.info("绑定用户邀请关系，用户不是新用户，被邀请用户ID：{}，上次登录时间：{}，bo={}", userId, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, userVo.getLastLoginDate()), bo);
                return;
            }
        }
        String cacheKey = inviteBind + bo.getMissionId() + ":" + userId;
        RedisUtils.setCacheObject(cacheKey, bo.getUserId(), Duration.ofHours(12));
        log.info("绑定用户邀请关系成功，被邀请用户ID：{}，邀请用户ID：{}", userId, bo.getUserId());
    }

    /**
     * 查询邀请记录
     */
    @Override
    public InviteUserLogVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询邀请记录列表
     */
    @Override
    public TableDataInfo<InviteUserLogVo> queryPageList(InviteUserLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<InviteUserLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, InviteUserLog::getUserId, bo.getUserId());
        lqw.eq(bo.getInviteUserId() != null, InviteUserLog::getInviteUserId, bo.getInviteUserId());
        lqw.like(StringUtils.isNotBlank(bo.getInviteCityName()), InviteUserLog::getInviteCityName, bo.getInviteCityName());
        lqw.eq(bo.getNumber() != null, InviteUserLog::getNumber, bo.getNumber());
        lqw.eq(bo.getMissionId() != null, InviteUserLog::getMissionId, bo.getMissionId());
        lqw.eq(bo.getPlatformKey() != null, InviteUserLog::getPlatformKey, bo.getPlatformKey());
        Page<InviteUserLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 新增邀请记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertByBo(InviteUserLogBo bo) {
        Long platformId = ZlyyhUtils.getPlatformId();
        // 被邀请用户ID
        Long userId = LoginHelper.getUserId();
        if (null == userId || null == bo.getUserId() || bo.getUserId() < 1 || null == bo.getMissionId() || bo.getMissionId() < 1) {
            throw new ServiceException("请求错误，请关闭重试");
        }
        if (userId.equals(bo.getUserId())) {
            throw new ServiceException("不能给自己助力");
        }
        // 校验用户是否满足条件
        MissionVo missionVo = missionService.queryById(bo.getMissionId());
        check(missionVo);
        ZlyyhUtils.checkCity(missionVo.getShowCity(), platformService.queryById(platformId, ZlyyhUtils.getPlatformType()));
        // 查询用户信息
        UserVo userVo = userService.queryById(userId);
        if (null == userVo || !userVo.getPlatformKey().equals(platformId)) {
            throw new ServiceException("登录超时，请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        int day = 30;
        try {
            String s = ysfConfigService.queryValueByKey(platformId, ZlyyhConstants.inviteUserDay);
            if (StringUtils.isNotBlank(s)) {
                day = Integer.parseInt(s);
            }
        } catch (Exception e) {
            log.error("邀请助力，获取ysfConfig配置异常：", e);
        }
        String cacheKey = inviteBind + bo.getMissionId() + ":" + userId;
        Long cacheObject = RedisUtils.getCacheObject(cacheKey);
        if (null == cacheObject || !cacheObject.equals(bo.getUserId())) {
            if (null == userVo.getLastLoginDate()) {
                // 查询用户订单
                Long orderCount = orderService.countByUserId(userId, day);
                if (null != orderCount && orderCount > 0) {
                    log.info("绑定用户邀请关系，用户不是新用户，被邀请用户ID：{}，30天内有订单：{}，bo={}", userId, orderCount, bo);
                    throw new ServiceException("您不符合助力条件");
                }
            } else {
                if (DateUtils.getDatePoorDay(new Date(), userVo.getLastLoginDate(), true) <= day) {
                    log.info("绑定用户邀请关系，用户不是新用户，被邀请用户ID：{}，上次登录时间：{}，bo={}", userId, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, userVo.getLastLoginDate()), bo);
                    throw new ServiceException("您不符合助力条件");
                }
            }
        }
        // 查询用户是否被邀请过了
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<InviteUserLog>().eq(InviteUserLog::getInviteUserId, userId).eq(InviteUserLog::getMissionId, bo.getMissionId()).last("and create_time >= DATE_SUB(CURDATE(), INTERVAL " + day + " DAY)"));
        if (count > 0) {
            throw new ServiceException("只能助力一次");
        }
        // 查询用户今日是否已达标
        List<ProductVo> productVos = missionGroupService.missionProduct(missionVo.getMissionGroupId());
        if (ObjectUtil.isEmpty(productVos)) {
            throw new ServiceException("感谢您的助力，本次活动已结束");
        }
        // 查询用户今日已获奖励次数
        Long userInviteLogCount = getUserInviteLogCount(bo.getUserId(), bo.getMissionId());
        if (null == userInviteLogCount) {
            userInviteLogCount = 0L;
        }
        if (userInviteLogCount >= productVos.size()) {
            throw new ServiceException("感谢您的助力，今日已达标");
        }
        log.info("助力成功，被邀请用户ID：{}，邀请用户ID：{}", userId, bo.getUserId());
        InviteUserLog add = new InviteUserLog();
        add.setUserId(bo.getUserId());
        add.setInviteUserId(userId);
        add.setInviteCityName(ZlyyhUtils.getCityName());
        add.setInviteCityCode(ZlyyhUtils.getAdCode());
        add.setMissionId(bo.getMissionId());
        add.setPlatformKey(platformId);
        add.setSupportChannel(ZlyyhUtils.getPlatformChannel());
        // 生成订单
        CreateOrderBo createOrderBo = new CreateOrderBo();
        createOrderBo.setProductId(productVos.get(userInviteLogCount.intValue()).getProductId());
        createOrderBo.setUserId(add.getUserId());
        createOrderBo.setAdcode(ZlyyhUtils.getAdCode());
        createOrderBo.setCityName(ZlyyhUtils.getCityName());
        createOrderBo.setPlatformKey(ZlyyhUtils.getPlatformId());
        CreateOrderResult order = orderService.createOrder(createOrderBo, true);
        // 回填订单号
        add.setNumber(order.getNumber());
        PermissionUtils.setPlatformDeptIdAndUserId(add, add.getPlatformKey(), true, false);
        boolean flag = baseMapper.insert(add) > 0;
        if (!flag) {
            throw new ServiceException("操作失败，请稍后重试");
        }
    }

    private void check(MissionVo missionVo) {
        if (null == missionVo || "1".equals(missionVo.getStatus())) {
            throw new ServiceException("活动不存在或已结束");
        }
        if (null != missionVo.getStartDate() && DateUtils.compare(missionVo.getStartDate()) > 0) {
            throw new ServiceException("开始时间:" + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", missionVo.getStartDate()));
        }
        if (null != missionVo.getEndDate() && DateUtils.compare(missionVo.getEndDate()) < 0) {
            throw new ServiceException("任务已结束");
        }
    }

    /**
     * 查询用户今日已获奖励次数
     *
     * @param userId    用户ID
     * @param missionId 任务ID
     * @return 今日已获奖励次数
     */
    @Override
    public Long getUserInviteLogCount(Long userId, Long missionId) {
        LambdaQueryWrapper<InviteUserLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(InviteUserLog::getUserId, userId).eq(InviteUserLog::getMissionId, missionId)
            .ge(InviteUserLog::getCreateTime, DateUtil.beginOfDay(new Date()));
        return baseMapper.selectCount(lqw);
    }

    @Override
    public TableDataInfo<String> getUserInviteLogMobile(Long userId, Long missionId, PageQuery pageQuery) {
        LambdaQueryWrapper<InviteUserLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(InviteUserLog::getUserId, userId);
        lqw.eq(InviteUserLog::getMissionId, missionId);
        Page<InviteUserLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<String> resultList = new ArrayList<>(result.getRecords().size());
        for (InviteUserLogVo record : result.getRecords()) {
            UserVo userVo = userService.queryById(record.getInviteUserId());
            if (null != userVo) {
                resultList.add(DesensitizedUtil.mobilePhone(userVo.getMobile()));
            }
        }
        TableDataInfo<String> build = TableDataInfo.build(resultList);
        build.setTotal(result.getTotal());
        return build;
    }

    @Override
    public TableDataInfo<InviteUserLogMobileOrderVo> getInviteLong(Long userId, Long missionId, PageQuery pageQuery) {
        LambdaQueryWrapper<InviteUserLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(InviteUserLog::getUserId, userId);
        lqw.eq(InviteUserLog::getMissionId, missionId);
        Page<InviteUserLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<InviteUserLogMobileOrderVo> resultList = new ArrayList<>(result.getRecords().size());
        for (InviteUserLogVo record : result.getRecords()) {
            InviteUserLogMobileOrderVo copy = BeanCopyUtils.copy(record, InviteUserLogMobileOrderVo.class);
            if (null == copy) {
                continue;
            }
            UserVo userVo = userService.queryById(record.getInviteUserId());
            if (null != userVo) {
                copy.setInviteUserMobile(userVo.getMobile());
                copy.setOrderVo(orderService.queryById(record.getNumber()));
            }
            resultList.add(copy);
        }
        TableDataInfo<InviteUserLogMobileOrderVo> build = TableDataInfo.build(resultList);
        build.setTotal(result.getTotal());
        return build;
    }
}
