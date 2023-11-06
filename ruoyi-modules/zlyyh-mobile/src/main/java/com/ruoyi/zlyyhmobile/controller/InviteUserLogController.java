package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.idempotent.annotation.RepeatSubmit;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.bo.InviteUserLogBo;
import com.ruoyi.zlyyh.domain.vo.InviteUserLogMobileOrderVo;
import com.ruoyi.zlyyh.domain.vo.MissionVo;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyhmobile.service.IInviteUserLogService;
import com.ruoyi.zlyyhmobile.service.IMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 邀请记录控制器
 * 前端访问路由地址为:/zlyyh/inviteUserLog
 *
 * @author yzg
 * @date 2023-08-08
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/inviteUserLog")
public class InviteUserLogController extends BaseController {

    private final IInviteUserLogService iInviteUserLogService;
    private final IMissionService missionService;

    /**
     * 邀请关系绑定
     */
    @PostMapping("/inviteBind")
    public R<Void> inviteBind(@RequestBody InviteUserLogBo bo) {
        iInviteUserLogService.inviteBind(bo);
        return R.ok();
    }

    /**
     * 邀请助力
     */
    @RepeatSubmit(message = "操作频繁,请稍后重试")
    @PostMapping("/add")
    public R<Void> add(@RequestBody InviteUserLogBo bo) {
        Long platformId = ZlyyhUtils.getPlatformId();
        // 被邀请用户ID
        Long userId = LoginHelper.getUserId();
        String adCode = ZlyyhUtils.getAdCode();
        String cityName = ZlyyhUtils.getCityName();
        String platformChannel = ZlyyhUtils.getPlatformChannel();

        // 校验用户是否满足条件
        MissionVo missionVo = missionService.queryById(bo.getMissionId());
        iInviteUserLogService.check(missionVo);
        ZlyyhUtils.checkCity(missionVo.getShowCity());
        iInviteUserLogService.insertByBo(bo,platformId,userId,platformChannel,cityName,adCode);
        return R.ok();
    }

    /**
     * 查询用户今日已获奖励次数
     *
     * @param missionId 任务ID
     * @return 今日已获奖励次数
     */
    @GetMapping("/getUserInviteLogCount/{missionId}")
    public R<Long> getUserInviteLogCount(@PathVariable("missionId") Long missionId) {
        return R.ok(iInviteUserLogService.getUserInviteLogCount(LoginHelper.getUserId(), missionId));
    }

    /**
     * 查询用户邀请记录
     *
     * @param missionId 任务ID
     * @return 今日已获奖励次数
     */
    @GetMapping("/getUserInviteLog/{missionId}")
    public TableDataInfo<String> getUserInviteLogMobile(@PathVariable("missionId") Long missionId, PageQuery pageQuery) {
        return iInviteUserLogService.getUserInviteLogMobile(LoginHelper.getUserId(), missionId, pageQuery);
    }

    /**
     * 查询用户邀请记录
     *
     * @param missionId 任务ID
     * @return 今日已获奖励次数
     */
    @GetMapping("/getInviteLong/{missionId}")
    public TableDataInfo<InviteUserLogMobileOrderVo> getInviteLong(@PathVariable("missionId") Long missionId, PageQuery pageQuery) {

        return iInviteUserLogService.getInviteLong(LoginHelper.getUserId(), missionId, pageQuery);
    }

    @GetMapping("/ignore/redisTest")
    public R<Void> redisTest() {
        String key = "redisTest";
        System.out.println("获取" + RedisUtils.getAtomicValue(key));
        System.out.println(RedisUtils.incrAtomicValue(key));
        return R.ok();
    }

    @GetMapping("/ignore/redisTestDel")
    public R<Void> redisTestDel() {
        String key = "redisTest";
        System.out.println("获取" + RedisUtils.getAtomicValue(key));
        System.out.println(RedisUtils.decrAtomicValue(key));
        return R.ok();
    }

//    public static void main(String[] args) {
//        Date createTime = DateUtil.parse("2023-08-14 00:00:00");
//        // 检查是否当天
//        int i = DateUtil.weekOfYear(createTime);
//        int i1 = DateUtil.weekOfYear(new Date());
//        System.out.println(i);
//        System.out.println(i1);
//        if (i != i1) {
//            // 不是当天跳过
//            System.out.println("不是当天");
//        }else{
//            System.out.println("当天");
//        }
//        // 检查是否本周
////        if (value == DateType.WEEK) {
////            String formatCreateTime = DateUtil.format(createTime, DatePattern.NORM_DATE_PATTERN);
////            String formatNow = DateUtil.format(new Date(), DatePattern.NORM_DATE_PATTERN);
////            if (!formatCreateTime.equals(formatNow)) {
////                // 不是当天跳过
////                continue;
////            }
////        }
//        // 检查是否当月
////        if (value == DateType.MONTH) {
////            String formatCreateTime = DateUtil.format(createTime, DatePattern.NORM_MONTH_PATTERN);
////            String formatNow = DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN);
////            if (!formatCreateTime.equals(formatNow)) {
////                // 不是当月跳过
////                continue;
////            }
////        }
//    }

//    public static void main(String[] args) {
//        String key = "testAtomicLong20230813";
//        Config config = new Config();
//        SingleServerConfig singleServerConfig = config.useSingleServer();
//        singleServerConfig.setAddress("redis://127.0.0.1:6379");
//        singleServerConfig.setDatabase(0);
//        RedissonClient redissonClient = Redisson.create(config);
//        Set<Long> set = Collections.synchronizedSet(new HashSet<>());
//        for (int i = 0; i < 20000; i++) {
//            int finalI = i;
//            new Thread(() -> {
//                RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
//                long st = atomicLong.get();
//                log.info("循环：{}，get值：{}", finalI, st);
//                long l = atomicLong.incrementAndGet();
//                log.info("循环：{}，自增值：{}", finalI, l);
//                set.add(l);
//                if(finalI >= 500 && finalI < 1000){
//                    atomicLong.decrementAndGet();
//                }
//            }).start();
//        }
//        ThreadUtil.sleep(10000);
//        System.out.println(set.size());
//        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
//        long l = atomicLong.get();
//        System.out.println(l);
//    }
}
