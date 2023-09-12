package com.ruoyi.zlyyh.utils;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.reflect.ReflectUtils;
import com.ruoyi.zlyyh.domain.vo.PlatformVo;
import com.ruoyi.zlyyh.mapper.PlatformMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 25487
 */
@Slf4j
public class PermissionUtils {
    private static final PlatformMapper platformMapper = SpringUtils.getBean(PlatformMapper.class);

    /**
     * 获取平台部门和用户并设置到对象中
     *
     * @param entity      需要设置的对象
     * @param platformKey 平台key
     */
    public static void setPlatformDeptIdAndUserId(Object entity, Long platformKey) {
        try {
            if (null == entity || null == platformKey) {
                return;
            }
            PlatformVo platformVo = platformMapper.selectVoById(platformKey);
            if (null != platformVo) {
                ReflectUtils.invokeSetter(entity, "sysDeptId", platformVo.getSysDeptId());
                ReflectUtils.invokeSetter(entity, "sysUserId", platformVo.getSysUserId());
            }
        } catch (Exception e) {
            log.error("获取平台部门和用户异常：", e);
        }
    }
}
