package com.ruoyi.zlyyhadmin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.excel.utils.ExcelUtil;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.zlyyh.domain.MessageTemplate;
import com.ruoyi.zlyyh.domain.PlatformChannel;
import com.ruoyi.zlyyh.domain.UserChannel;
import com.ruoyi.zlyyh.domain.bo.MessageTemplateBo;
import com.ruoyi.zlyyh.domain.vo.MessageTemplateVo;
import com.ruoyi.zlyyh.domain.vo.PlatformChannelVo;
import com.ruoyi.zlyyh.domain.vo.UserChannelVo;
import com.ruoyi.zlyyh.mapper.MessageTemplateMapper;
import com.ruoyi.zlyyh.mapper.PlatformChannelMapper;
import com.ruoyi.zlyyh.mapper.UserChannelMapper;
import com.ruoyi.zlyyh.mapper.UserMapper;
import com.ruoyi.zlyyh.service.YsfConfigService;
import com.ruoyi.zlyyh.utils.YsfUtils;
import com.ruoyi.zlyyhadmin.domain.bo.UserInfoBo;
import com.ruoyi.zlyyhadmin.service.IMessageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息模板Service业务层处理
 *
 * @author yzg
 * @date 2023-11-23
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MessageTemplateServiceImpl implements IMessageTemplateService {

    private final MessageTemplateMapper baseMapper;
    private final UserMapper userMapper;
    private final UserChannelMapper userChannelMapper;
    private final PlatformChannelMapper platformChannelMapper;
    private final YsfConfigService ysfConfigService;

    /**
     * 查询消息模板
     */
    @Override
    public MessageTemplateVo queryById(Long templateId) {
        return baseMapper.selectVoById(templateId);
    }

    /**
     * 查询消息模板列表
     */
    @Override
    public TableDataInfo<MessageTemplateVo> queryPageList(MessageTemplateBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MessageTemplate> lqw = buildQueryWrapper(bo);
        Page<MessageTemplateVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询消息模板列表
     */
    @Override
    public List<MessageTemplateVo> queryList(MessageTemplateBo bo) {
        LambdaQueryWrapper<MessageTemplate> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MessageTemplate> buildQueryWrapper(MessageTemplateBo bo) {
        //Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MessageTemplate> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), MessageTemplate::getStatus, bo.getStatus());
        lqw.eq(bo.getPlatformKey() != null, MessageTemplate::getPlatformKey, bo.getPlatformKey());
        lqw.eq(StringUtils.isNotBlank(bo.getTemplateKey()), MessageTemplate::getTemplateKey, bo.getTemplateKey());
        lqw.like(StringUtils.isNotBlank(bo.getTemplateName()), MessageTemplate::getTemplateName, bo.getTemplateName());
        lqw.eq(StringUtils.isNotBlank(bo.getChannel()), MessageTemplate::getChannel, bo.getChannel());
        return lqw;
    }

    /**
     * 新增消息模板
     */
    @Override
    public Boolean insertByBo(MessageTemplateBo bo) {
        MessageTemplate add = BeanUtil.toBean(bo, MessageTemplate.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setTemplateId(add.getTemplateId());
        }
        return flag;
    }

    /**
     * 修改消息模板
     */
    @Override
    public Boolean updateByBo(MessageTemplateBo bo) {
        MessageTemplate update = BeanUtil.toBean(bo, MessageTemplate.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MessageTemplate entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除消息模板
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public void importData(MessageTemplateBo bo) throws IOException {
        MessageTemplateVo vo = baseMapper.selectVoById(bo.getTemplateId());
        LambdaQueryWrapper<PlatformChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(PlatformChannel::getChannel, vo.getChannel());
        lqw.eq(PlatformChannel::getPlatformKey, vo.getPlatformKey());
        PlatformChannelVo platformChannelVo = platformChannelMapper.selectVoOne(lqw);
        if (ObjectUtil.isEmpty(platformChannelVo)) throw new ServiceException("平台没有此支持端APP数据");
        List<UserInfoBo> list = ExcelUtil.importExcel(bo.getFile().getInputStream(), UserInfoBo.class);
        if (ObjectUtil.isNotEmpty(list)) {
            String url = ysfConfigService.queryValueByKey(platformChannelVo.getPlatformKey(), "sendMessageUrl");
            JSONArray jsonArray = JSONArray.parseArray(bo.getKeyword());
            String backendToken = YsfUtils.getBackendToken(platformChannelVo.getAppId(), platformChannelVo.getSecret(), false, platformChannelVo.getPlatformKey());
            for (UserInfoBo userInfo : list) {
                if (StringUtils.isNotEmpty(userInfo.getMobile())) {
                    UserChannelVo userChannelVo = getUser(vo.getPlatformKey(), userInfo.getMobile(), vo.getChannel());
                    // 用户渠道信息不为空，并且用户信息
                    if (ObjectUtil.isNotEmpty(userChannelVo) && StringUtils.isNotEmpty(userChannelVo.getOpenId())) {
                        if (vo.getChannel().equals("0")) {
                            ysfMessage(url, backendToken, userChannelVo.getOpenId(), vo, platformChannelVo, jsonArray);
                        }
                    }
                }
            }
        }
    }

    /**
     * 发送云闪付消息
     */
    public void ysfMessage(String url, String backendToken, String openId, MessageTemplateVo messageTemplateVo,
                           PlatformChannelVo platformChannelVo, JSONArray jsonArray) {
        try {
            Map<String, Object> contentData = new HashMap<>();
            // 我方唯一标识
            contentData.put("appId", platformChannelVo.getAppId());
            // 用户唯一标识
            contentData.put("openId", openId);
            // 临时token
            contentData.put("backendToken", backendToken);
            // 小程序id
            contentData.put("mpId", platformChannelVo.getEncryptAppId());
            // 通知模板id
            contentData.put("templateId", messageTemplateVo.getTemplateKey());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                contentData.put(jsonObject.getString("key"), jsonObject.getString("value"));
            }
            String jsonString = JSONObject.toJSONString(contentData);

            String s = HttpUtil.post(url, jsonString);
            log.info("云闪付消息推送，请求参数：{},返回结果：{}", jsonString, s);
        } catch (Exception e) {
            log.info("云闪付消息发送异常：", e);
        }
    }

    private UserChannelVo getUser(Long platformKey, String mobile, String channel) {
        User user = userMapper.selectOneIncludeMobile(new LambdaQueryWrapper<User>().eq(User::getPlatformKey, platformKey), new User(mobile));
        if (ObjectUtil.isEmpty(user)) return null;
        LambdaQueryWrapper<UserChannel> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserChannel::getUserId, user.getUserId());
        lqw.eq(UserChannel::getChannel, channel);
        return userChannelMapper.selectVoOne(lqw);
    }
}
