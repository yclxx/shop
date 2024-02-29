package com.ruoyi.zlyyhmobile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.zlyyh.domain.*;
import com.ruoyi.zlyyh.domain.bo.*;
import com.ruoyi.zlyyh.domain.vo.*;
import com.ruoyi.zlyyh.enumd.PlatformEnumd;
import com.ruoyi.zlyyh.mapper.*;
import com.ruoyi.zlyyh.utils.PermissionUtils;
import com.ruoyi.zlyyh.utils.YsfSm4Utils;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyh.utils.ZlyyhUtils;
import com.ruoyi.zlyyh.utils.sdk.YinLianUtil;
import com.ruoyi.zlyyhmobile.domain.bo.CreateOrderBo;
import com.ruoyi.zlyyhmobile.domain.vo.CreateOrderResult;
import com.ruoyi.zlyyhmobile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 银联任务配置Service业务层处理
 *
 * @author yzg
 * @date 2024-02-21
 */
@RequiredArgsConstructor
@Service
public class UnionpayMissionServiceImpl implements IUnionpayMissionService {

    private final UnionpayMissionMapper baseMapper;
    private final IUserService userService;
    private final UnionpayMissionGroupMapper unionpayMissionGroupMapper;
    private final IPlatformService platformService;
    private final UnionpayMissionUserMapper unionpayMissionUserMapper;
    private final IUserChannelService userChannelService;
    private final UnionpayMissionMapper unionpayMissionMapper;
    private final UnionpayMissionProgressMapper unionpayMissionProgressMapper;
    private final UnionpayMissionUserLogMapper unionpayMissionUserLogMapper;
    private final IOrderService orderService;
    private final ProductMapper productMapper;

    /**
     * 查询银联任务配置
     */
    @Override
    public UnionpayMissionVo queryById(Long upMissionId){
        return baseMapper.selectVoById(upMissionId);
    }

    /**
     * 查询银联任务配置列表
     */
    @Override
    public TableDataInfo<UnionpayMissionVo> queryPageList(UnionpayMissionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<UnionpayMission> lqw = buildQueryWrapper(bo);
        Page<UnionpayMissionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询银联任务配置列表
     */
    @Override
    public List<UnionpayMissionVo> queryList(UnionpayMissionBo bo) {
        LambdaQueryWrapper<UnionpayMission> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<UnionpayMission> buildQueryWrapper(UnionpayMissionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<UnionpayMission> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUpMissionGroupId() != null, UnionpayMission::getUpMissionGroupId, bo.getUpMissionGroupId());
        lqw.like(StringUtils.isNotBlank(bo.getUpMissionName()), UnionpayMission::getUpMissionName, bo.getUpMissionName());
        lqw.eq(bo.getProductId() != null, UnionpayMission::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), UnionpayMission::getStatus, bo.getStatus());
        lqw.eq(bo.getStartDate() != null, UnionpayMission::getStartDate, bo.getStartDate());
        lqw.eq(bo.getEndDate() != null, UnionpayMission::getEndDate, bo.getEndDate());
        lqw.eq(bo.getPlatformKey() != null, UnionpayMission::getPlatformKey, bo.getPlatformKey());
        lqw.eq(bo.getUserCountDay() != null, UnionpayMission::getUserCountDay, bo.getUserCountDay());
        lqw.eq(bo.getUserCountWeek() != null, UnionpayMission::getUserCountWeek, bo.getUserCountWeek());
        lqw.eq(bo.getUserCountMonth() != null, UnionpayMission::getUserCountMonth, bo.getUserCountMonth());
        lqw.eq(bo.getUserCountActivity() != null, UnionpayMission::getUserCountActivity, bo.getUserCountActivity());
        return lqw;
    }

    /**
     * 新增银联任务配置
     */
    @Override
    public Boolean insertByBo(UnionpayMissionBo bo) {
        UnionpayMission add = BeanUtil.toBean(bo, UnionpayMission.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setUpMissionId(add.getUpMissionId());
        }
        return flag;
    }

    /**
     * 修改银联任务配置
     */
    @Override
    public Boolean updateByBo(UnionpayMissionBo bo) {
        UnionpayMission update = BeanUtil.toBean(bo, UnionpayMission.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(UnionpayMission entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除银联任务配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 用户报名
     */
    @Override
    public void userSingUp(UnionpayMissionUserBo bo) {
        UnionpayMissionUser add = BeanUtil.toBean(bo, UnionpayMissionUser.class);
        UserVo userVo = userService.queryById(bo.getUserId(), PlatformEnumd.MP_YSF.getChannel());
        if (null == userVo || StringUtils.isEmpty(userVo.getOpenId())) {
            throw new ServiceException("登录超时，请退出重试", HttpStatus.HTTP_UNAUTHORIZED);
        }
        UnionpayMissionGroupVo missionGroupVo = unionpayMissionGroupMapper.selectVoById(bo.getUpMissionGroupId());
        if (null == missionGroupVo || !"0".equals(missionGroupVo.getStatus())) {
            throw new ServiceException("本期活动已结束,敬请期待下期活动");
        }
        if (null != missionGroupVo.getEndDate() && DateUtils.compare(missionGroupVo.getEndDate()) < 0) {
            throw new ServiceException("本期活动已结束,敬请期待下期活动");
        }
        PlatformVo platformVo = platformService.queryById(missionGroupVo.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
        if (null == platformVo) {
            throw new ServiceException("平台信息错误");
        }
        UnionpayMissionUserVo unionpayMissionUserVo = unionpayMissionUserMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUserId, bo.getUserId()).eq(UnionpayMissionUser::getUpMissionGroupId, bo.getUpMissionGroupId()).eq(UnionpayMissionUser::getPlatformKey, bo.getPlatformKey()).last("limit 1"));
        if (ObjectUtil.isEmpty(unionpayMissionUserVo)) {
            boolean flag = unionpayMissionUserMapper.insert(add) > 0;
            if (flag) {
                bo.setUpMissionUserId(add.getUpMissionUserId());
            }
            JSONObject r = YsfUtils.userSingUp(platformVo.getAppId(), platformVo.getSecret(), userVo.getOpenId(), missionGroupVo.getUpMissionGroupUpid(), IdUtil.createSnowflake(2, 2).nextIdStr(), platformVo.getPlatformKey());
            if (ObjectUtil.isEmpty(r)) {
                throw new ServiceException("报名失败");
            } else {
                List<UnionpayMissionVo> missionVos = unionpayMissionMapper.selectVoList(new LambdaQueryWrapper<UnionpayMission>().eq(UnionpayMission::getUpMissionGroupId, bo.getUpMissionGroupId()).eq(UnionpayMission::getStatus,"0"));
                if (ObjectUtil.isNotEmpty(missionVos)) {
                    for (UnionpayMissionVo missionVo : missionVos) {
                        List<UnionpayMissionProgressVo> progressVoList = unionpayMissionProgressMapper.selectVoList(new LambdaQueryWrapper<UnionpayMissionProgress>().eq(UnionpayMissionProgress::getUpMissionId, missionVo.getUpMissionId()).eq(UnionpayMissionProgress::getUpMissionGroupId, bo.getUpMissionGroupId()).eq(UnionpayMissionProgress::getUpMissionUserId, bo.getUpMissionUserId()));
                        if (ObjectUtil.isEmpty(progressVoList)) {
                            UnionpayMissionProgress missionProgress = new UnionpayMissionProgress();
                            missionProgress.setUpMissionId(missionVo.getUpMissionId());
                            missionProgress.setUpMissionGroupId(bo.getUpMissionGroupId());
                            missionProgress.setUpMissionUserId(bo.getUpMissionUserId());
                            if (missionVo.getTranType().equals("1")) {
                                //笔数交易
                                missionProgress.setTranProgress("0/" + missionVo.getTranCount());
                            } else if (missionVo.getTranType().equals("2")) {
                                //金额交易
                                BigDecimal bigDecimal = new BigDecimal(missionVo.getTranCount()).divide(new BigDecimal(100L)).setScale(2, BigDecimal.ROUND_DOWN);
                                String s = bigDecimal.toString().split("\\.")[1];
                                if (s.equals("00")) {
                                    missionProgress.setTranProgress("0/" + bigDecimal.toString().split("\\.")[0]);
                                } else {
                                    missionProgress.setTranProgress("0/" + bigDecimal.toString());
                                }
                            }
                            unionpayMissionProgressMapper.insert(missionProgress);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal("2.00");
        String[] split = bigDecimal.toString().split("\\.");
        //for (String s : split) {
        //    System.out.println(s);
        //}
        String s1= bigDecimal.toString().split("\\.")[0];
        String s2 = bigDecimal.toString().split("\\.")[1];
        System.out.println(s1);
        System.out.println(s2);
    }

    /**
     * 用户报名校验  银联任务
     */
    @Override
    public R<Object> signUpVerify(UnionpayMissionUserBo bo) {
        UnionpayMissionUserVo unionpayMissionUserVo = unionpayMissionUserMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUserId, bo.getUserId()).eq(UnionpayMissionUser::getPlatformKey, bo.getPlatformKey()).eq(UnionpayMissionUser::getUpMissionGroupId, bo.getUpMissionGroupId()).last("limit 1"));
        if (ObjectUtil.isNotEmpty(unionpayMissionUserVo)) {
            return R.ok("1");
        } else {
            UserChannelVo userChannelVo = userChannelService.queryByUserId(PlatformEnumd.MP_YSF.getChannel(), bo.getUserId(), bo.getPlatformKey());
            if (ObjectUtil.isEmpty(userChannelVo)) {
                //查询平台参数
                PlatformVo platformVo = platformService.queryById(bo.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
                return R.ok("2",platformVo);
            } else {
                return R.ok("0");
            }
        }
    }

    /**
     * 查询任务进度
     * @param bo
     * @return
     */
    @Override
    public void getMissionProgress(UnionpayMissionUserBo bo) {
        if (null == bo.getUserId()) {
            return;
        }
        UnionpayMissionUserVo missionUserVo = unionpayMissionUserMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUserId, bo.getUserId()).eq(UnionpayMissionUser::getUpMissionGroupId, bo.getUpMissionGroupId()).eq(UnionpayMissionUser::getPlatformKey, bo.getPlatformKey()).last("limit 1"));
        if (ObjectUtil.isEmpty(missionUserVo)) {
            return;
        }
        UnionpayMissionGroupVo missionGroupVo = unionpayMissionGroupMapper.selectVoById(bo.getUpMissionGroupId());
        if (null == missionGroupVo || !"0".equals(missionGroupVo.getStatus()) || StringUtils.isBlank(missionGroupVo.getUpMissionGroupUpid())) {
            return;
        }
        if (null != missionGroupVo.getEndDate() && DateUtils.compare(missionGroupVo.getEndDate()) < 0) {
            return;
        }
        UserVo userVo = userService.queryById(bo.getUserId(), PlatformEnumd.MP_YSF.getChannel());
        if (null == userVo || StringUtils.isBlank(userVo.getOpenId())) {
            return;
        }
        PlatformVo platformVo = platformService.queryById(missionGroupVo.getPlatformKey(), PlatformEnumd.MP_YSF.getChannel());
        if (null == platformVo) {
            return;
        }
        List<UnionpayMissionVo> missionVos = unionpayMissionMapper.selectVoList(new LambdaQueryWrapper<UnionpayMission>().eq(UnionpayMission::getPlatformKey, bo.getPlatformKey()).eq(UnionpayMission::getUpMissionGroupId, bo.getUpMissionGroupId()));
        for (UnionpayMissionVo missionVo : missionVos) {
            if (StringUtils.isBlank(missionVo.getUpMissionUpid())) {
                continue;
            }
            //UnionpayMissionProgressVo missionProgressVo = unionpayMissionProgressMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionProgress>().eq(UnionpayMissionProgress::getUpMissionId, missionVo.getUpMissionId()).eq(UnionpayMissionProgress::getUpMissionUserId, missionUserVo.getUpMissionUserId()).last("limit 1"));
            //if (ObjectUtil.isNotEmpty(missionProgressVo)) {
            //    if (missionVo.getUserCountActivity() > 0 && missionProgressVo.getActivityProgress() >= missionVo.getUserCountActivity()) {
            //        continue;
            //    }
            //    if (missionVo.getUserCountWeek() > 0 && missionProgressVo.getWeekProgress() >= missionVo.getUserCountWeek()) {
            //        continue;
            //    }
            //    if (missionVo.getUserCountDay() > 0 && missionProgressVo.getDayProgress() >= missionVo.getUserCountDay()) {
            //        continue;
            //    }
            //}
            List<String> missionIdList = new ArrayList<>();
            missionIdList.add(missionVo.getUpMissionUpid());
            //missionIdList.add("JYRW2024022000948");
            // 请求接口查询用户完成任务进度
            JSONObject resultJson = YsfUtils.searchProgress(platformVo.getAppId(), platformVo.getSecret(), userVo.getOpenId(), missionGroupVo.getUpMissionGroupUpid(), missionIdList, IdUtil.createSnowflake(2, 2).nextIdStr(), platformVo.getPlatformKey());
            if (ObjectUtil.isEmpty(resultJson)) {
                continue;
            }
            String decryptedValue = null;
            try {
                // 解密用户任务进度数据
                decryptedValue = YinLianUtil.getDecryptedValue(resultJson.getString("missionProcessMap"), platformVo.getSymmetricKey());
                //decryptedValue = YinLianUtil.getDecryptedValue(resultJson.getString("missionProcessMap"), "985e230bc2f7dcb9da9b26859d25e040985e230bc2f7dcb9");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = JSONObject.parseObject(decryptedValue);
            JSONObject missionJson = jsonObject.getJSONObject(missionVo.getUpMissionUpid());
            if (ObjectUtil.isEmpty(missionJson)) {
                continue;
            }
            if (missionVo.getUserCountDay() > 0) {
                JSONObject resJson = missionJson.getJSONObject(DateUtils.getDate("yyyyMMdd"));
                if (ObjectUtil.isEmpty(resJson)) {
                    continue;
                }
                String userProcessStr = resJson.getString("userProcessStr");
                String[] userProcess = userProcessStr.split(",");
                String tranProgress = getTranProgress(missionVo, resJson, userProcess);
                UnionpayMissionProgressVo missionProgressVo = unionpayMissionProgressMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionProgress>().eq(UnionpayMissionProgress::getUpMissionId, missionVo.getUpMissionId()).eq(UnionpayMissionProgress::getUpMissionUserId, missionUserVo.getUpMissionUserId()).last("limit 1"));
                if (ObjectUtil.isNotEmpty(missionProgressVo)) {
                    missionProgressVo.setTranProgress(tranProgress);
                    missionProgressVo.setDayProgress(Long.valueOf(userProcess[0].split("/")[0]));
                    missionProgressVo.setActivityProgress(Long.valueOf(userProcess[userProcess.length - 1].split("/")[0]));
                    unionpayMissionProgressMapper.updateById(BeanUtil.toBean(missionProgressVo, UnionpayMissionProgress.class));
                } else {
                    UnionpayMissionProgress missionProgress = new UnionpayMissionProgress();
                    missionProgress.setUpMissionId(missionVo.getUpMissionId());
                    missionProgress.setUpMissionGroupId(missionGroupVo.getUpMissionGroupId());
                    missionProgress.setUpMissionUserId(missionUserVo.getUpMissionUserId());
                    missionProgress.setTranProgress(tranProgress);
                    missionProgress.setDayProgress(Long.valueOf(userProcess[0].split("/")[0]));
                    missionProgress.setActivityProgress(Long.valueOf(userProcess[userProcess.length - 1].split("/")[0]));
                    unionpayMissionProgressMapper.insert(missionProgress);
                }
                sendReward(missionVo,userProcess,missionUserVo);
            } else if (missionVo.getUserCountWeek() > 0) {
                String startWeek = DateFormatUtils.format(DateUtils.parseDate(DateUtils.getThisWeekDate(DateUtils.getDate())), "yyyyMMdd");
                String endWeek = DateFormatUtils.format(DateUtils.parseDate(DateUtils.getThisWeekEnd()), "yyyyMMdd");
                JSONObject resJson = missionJson.getJSONObject(startWeek + "_" + endWeek);
                if (ObjectUtil.isEmpty(resJson)) {
                    continue;
                }
                String userProcessStr = resJson.getString("userProcessStr");
                String[] userProcess = userProcessStr.split(",");
                String tranProgress = getTranProgress(missionVo, resJson, userProcess);
                UnionpayMissionProgressVo missionProgressVo = unionpayMissionProgressMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionProgress>().eq(UnionpayMissionProgress::getUpMissionId, missionVo.getUpMissionId()).eq(UnionpayMissionProgress::getUpMissionUserId, missionUserVo.getUpMissionUserId()).last("limit 1"));
                if (ObjectUtil.isNotEmpty(missionProgressVo)) {
                    missionProgressVo.setTranProgress(tranProgress);
                    missionProgressVo.setWeekProgress(Long.valueOf(userProcess[0].split("/")[0]));
                    missionProgressVo.setActivityProgress(Long.valueOf(userProcess[userProcess.length - 1].split("/")[0]));
                    unionpayMissionProgressMapper.updateById(BeanUtil.toBean(missionProgressVo,UnionpayMissionProgress.class));
                } else {
                    UnionpayMissionProgress missionProgress = new UnionpayMissionProgress();
                    missionProgress.setUpMissionId(missionVo.getUpMissionId());
                    missionProgress.setUpMissionGroupId(missionGroupVo.getUpMissionGroupId());
                    missionProgress.setUpMissionUserId(missionUserVo.getUpMissionUserId());
                    missionProgress.setTranProgress(tranProgress);
                    missionProgress.setWeekProgress(Long.valueOf(userProcess[0].split("/")[0]));
                    missionProgress.setActivityProgress(Long.valueOf(userProcess[userProcess.length - 1].split("/")[0]));
                    unionpayMissionProgressMapper.insert(missionProgress);
                }
                sendReward(missionVo,userProcess,missionUserVo);
            }
        }
    }

    /**
     * 获取交易进度
     */
    private String getTranProgress(UnionpayMissionVo missionVo, JSONObject resJson, String[] userProcess) {
        String tranProgress = "";
        if (missionVo.getTranType().equals("1")) {
            //笔数交易规则
            JSONArray missionProcessDetailVOList = resJson.getJSONArray("missionProcessDetailVOList");
            Long payCount = 0L;
            for (int i = 0; i < missionProcessDetailVOList.size(); i++) {
                JSONObject payDetail = missionProcessDetailVOList.getJSONObject(i);
                Long transAt = payDetail.getLong("transAt");
                if (transAt >= missionVo.getPayAmount()) {
                    payCount++;
                }
            }
            long l = payCount % missionVo.getTranCount();
            if (Long.valueOf(userProcess[0].split("/")[0]) >= missionVo.getUserCountDay()) {
                tranProgress = missionVo.getTranCount() + "/" + missionVo.getTranCount();
            } else {
                tranProgress = l + "/" + missionVo.getTranCount();
            }
        } else if (missionVo.getTranType().equals("2")) {
            //累计金额交易规则
            int length = userProcess[1].length();
            tranProgress = userProcess[1].substring(0, length - 1);
        }
        return tranProgress;
    }

    /**
     * 发放奖励
     */
    private void sendReward(UnionpayMissionVo missionVo,String[] userProcess,UnionpayMissionUserVo missionUserVo) {
        Long count = unionpayMissionUserLogMapper.selectCount(new LambdaQueryWrapper<UnionpayMissionUserLog>().eq(UnionpayMissionUserLog::getUpMissionUserId, missionUserVo.getUpMissionUserId()).eq(UnionpayMissionUserLog::getUpMissionId, missionVo.getUpMissionId()).eq(UnionpayMissionUserLog::getStatus,"2"));
        if (count >= missionVo.getUserCountActivity()) {
            return;
        }
        if (Long.valueOf(userProcess[0].substring(0,1)) <= 0) {
            return;
        }
        long syCount = missionVo.getUserCountActivity() - count;
        Long sendCount = Long.valueOf(userProcess[0].substring(0, 1)) - syCount;
        if (sendCount >= 0) {
            for (long l = 0; l < syCount; l++) {
                UnionpayMissionUserLog missionUserLog = new UnionpayMissionUserLog();
                missionUserLog.setUpMissionUserId(missionUserVo.getUpMissionUserId());
                missionUserLog.setUpMissionGroupId(missionVo.getUpMissionGroupId());
                missionUserLog.setUpMissionId(missionVo.getUpMissionId());
                // 生成订单发放奖励
                CreateOrderBo createOrderBo = new CreateOrderBo();
                createOrderBo.setProductId(missionVo.getProductId());
                createOrderBo.setUserId(missionUserVo.getUserId());
                createOrderBo.setPlatformKey(missionVo.getPlatformKey());
                createOrderBo.setChannel(PlatformEnumd.MP_YSF.getChannel());
                CreateOrderResult order = orderService.createOrder(createOrderBo, true);

                missionUserLog.setNumber(order.getNumber());
                unionpayMissionUserLogMapper.insert(missionUserLog);
            }
        } else {
            for (long l = 0; l < Long.valueOf(userProcess[0].substring(0, 1)); l++) {
                UnionpayMissionUserLog missionUserLog = new UnionpayMissionUserLog();
                missionUserLog.setUpMissionUserId(missionUserVo.getUpMissionUserId());
                missionUserLog.setUpMissionGroupId(missionVo.getUpMissionGroupId());
                missionUserLog.setUpMissionId(missionVo.getUpMissionId());
                // 生成订单发放奖励
                CreateOrderBo createOrderBo = new CreateOrderBo();
                createOrderBo.setProductId(missionVo.getProductId());
                //createOrderBo.setProductId(1760905911856824320L);
                createOrderBo.setUserId(missionUserVo.getUserId());
                createOrderBo.setPlatformKey(missionVo.getPlatformKey());
                createOrderBo.setChannel(PlatformEnumd.MP_YSF.getChannel());
                CreateOrderResult order = orderService.createOrder(createOrderBo, true);

                missionUserLog.setNumber(order.getNumber());
                unionpayMissionUserLogMapper.insert(missionUserLog);
            }
        }
    }

    /**
     * 查询任务奖励列表
     */
    @Override
    public List<UnionpayMissionUserLogVo> rewardList(UnionpayMissionUserLogBo bo) {
        Long userId = LoginHelper.getUserId();
        Long platformId = ZlyyhUtils.getPlatformId();
        UnionpayMissionUserVo missionUserVo = unionpayMissionUserMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUserId, userId).eq(UnionpayMissionUser::getPlatformKey, platformId).last("limit 1"));
        if (ObjectUtil.isEmpty(missionUserVo)) {
            return null;
        }
        List<UnionpayMissionUserLogVo> userLogVos = unionpayMissionUserLogMapper.selectVoList(new LambdaQueryWrapper<UnionpayMissionUserLog>().eq(UnionpayMissionUserLog::getUpMissionUserId, missionUserVo.getUpMissionUserId()));
        if (ObjectUtil.isNotEmpty(userLogVos)) {
            for (UnionpayMissionUserLogVo userLogVo : userLogVos) {
                if (ObjectUtil.isNotEmpty(userLogVo.getNumber())) {
                    userLogVo.setOrderVo(orderService.queryBaseOrderById(userLogVo.getNumber()));
                }
                UnionpayMissionVo missionVo = unionpayMissionMapper.selectVoById(userLogVo.getUpMissionId());
                if (ObjectUtil.isNotEmpty(missionVo)) {
                    ProductVo productVo = productMapper.selectVoById(missionVo.getProductId());
                    userLogVo.setProductVo(productVo);
                }
            }
        }
        return userLogVos;
    }

    /**
     * 任务通知处理
     * @param param
     */
    @Override
    public void missionCallback(JSONObject param) {
        if (ObjectUtil.isEmpty(param)) {
            return;
        }
        String openId = param.getString("openId");
        if (ObjectUtil.isEmpty(openId)) {
            return;
        }
        String activityId = param.getString("activityId");
        if (StringUtils.isEmpty(activityId)) {
            return;
        }
        UnionpayMissionGroupVo missionGroupVo = unionpayMissionGroupMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionGroup>().eq(UnionpayMissionGroup::getUpMissionGroupUpid, activityId).last("limit 1"));
        if (ObjectUtil.isEmpty(missionGroupVo)) {
            return;
        }
        UserChannelVo userChannelVo = userChannelService.queryByOpenId(PlatformEnumd.MP_YSF.getChannel(), openId, missionGroupVo.getPlatformKey());
        if (ObjectUtil.isEmpty(userChannelVo)) {
            return;
        }
        UnionpayMissionUserBo unionpayMissionUserBo = new UnionpayMissionUserBo();
        unionpayMissionUserBo.setUpMissionGroupId(missionGroupVo.getUpMissionGroupId());
        unionpayMissionUserBo.setUserId(userChannelVo.getUserId());
        unionpayMissionUserBo.setPlatformKey(userChannelVo.getPlatformKey());
        getMissionProgress(unionpayMissionUserBo);
    }

    /**
     * 查询银联任务进度列表
     */
    @Override
    public List<UnionpayMissionProgressVo> getProgressList(UnionpayMissionProgressBo bo) {
        Long userId = LoginHelper.getUserId();
        Long platformId = ZlyyhUtils.getPlatformId();
        UnionpayMissionUserVo missionUserVo = unionpayMissionUserMapper.selectVoOne(new LambdaQueryWrapper<UnionpayMissionUser>().eq(UnionpayMissionUser::getUserId, userId).eq(UnionpayMissionUser::getPlatformKey, platformId).last("limit 1"));
        if (ObjectUtil.isEmpty(missionUserVo)) {
            return null;
        }
        List<UnionpayMissionProgressVo> progressVoList = unionpayMissionProgressMapper.selectVoList(new LambdaQueryWrapper<UnionpayMissionProgress>().eq(UnionpayMissionProgress::getUpMissionUserId, missionUserVo.getUpMissionUserId()));
        if (ObjectUtil.isNotEmpty(progressVoList)) {
            for (UnionpayMissionProgressVo progressVo : progressVoList) {
                UnionpayMissionVo missionVo = unionpayMissionMapper.selectVoById(progressVo.getUpMissionId());
                if (ObjectUtil.isNotEmpty(missionVo)) {
                    progressVo.setUnionpayMissionVo(missionVo);
                }
            }
        }
        return progressVoList;
    }
}
