package com.ruoyi.zlyyh.utils;

import com.ruoyi.common.core.enums.UserType;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.reflect.ReflectUtils;
import com.ruoyi.common.satoken.utils.LoginHelper;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.zlyyh.domain.Platform;
import com.ruoyi.zlyyh.domain.Product;
import com.ruoyi.zlyyh.domain.Shop;
import com.ruoyi.zlyyh.mapper.PlatformMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 25487
 */
@Slf4j
public class PermissionUtils {
    private static final PlatformMapper PLATFORM_MAPPER = SpringUtils.getBean(PlatformMapper.class);

    /**
     * 获取平台部门和用户并设置到对象中
     *
     * @param entity      需要设置的对象
     * @param platformKey 平台key
     */
    public static void setPlatformDeptIdAndUserId(Object entity, Long platformKey, boolean isAdd, boolean base) {
        try {
            if (null == entity || null == platformKey) {
                return;
            }
            Platform platform = PLATFORM_MAPPER.selectById(platformKey);
            if (null != platform) {
                if (base) {
                    ReflectUtils.invokeSetter(entity, "sysDeptId", platform.getSysDeptId());
                } else {
                    ReflectUtils.invokeSetter(entity, "sysDeptId", platform.getManangerDeptId());
                }
                if (isAdd) {
                    Long userId = null;
                    try {
                        LoginUser loginUser = LoginHelper.getLoginUser();
                        if (null != loginUser && !UserType.APP_USER.getUserType().equals(loginUser.getUserType())) {
                            userId = loginUser.getUserId();
                        }
                    } catch (Exception e) {
//                        log.error("设置用户异常：", e);
                    }
                    if (null != userId) {
                        ReflectUtils.invokeSetter(entity, "sysUserId", userId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取平台部门异常：", e);
        }
    }

    /**
     * 获取部门和用户并设置到对象中
     *
     * @param entity 需要设置的对象
     */
    public static void setDeptIdAndUserId(Object entity, Long sysDeptId, Long sysUserId) {
        try {
            if (null == entity) {
                return;
            }
            ReflectUtils.invokeSetter(entity, "sysDeptId", sysDeptId);
            ReflectUtils.invokeSetter(entity, "sysUserId", sysUserId);
        } catch (Exception ignored) {
        }
    }

    /**
     * 设置当前登录用户的部门以及用户
     *
     * @param entity 需要设置的对象
     */
    public static void setLoginDeptIdAndUserId(Object entity) {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (null != loginUser && !UserType.APP_USER.getUserType().equals(loginUser.getUserType())) {
                ReflectUtils.invokeSetter(entity, "sysDeptId", loginUser.getDeptId());
                ReflectUtils.invokeSetter(entity, "sysUserId", loginUser.getUserId());
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 设置门店的部门以及用户
     *
     * @param shop 需要设置的对象
     */
    public static void setShopDeptIdAndUserId(Shop shop) {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (null != loginUser && !UserType.APP_USER.getUserType().equals(loginUser.getUserType())) {
                shop.setSysDeptId(loginUser.getDeptId());
                shop.setSysUserId(loginUser.getUserId());
            } else {
                if (null != shop.getPlatformKey()) {
                    Platform platform = PLATFORM_MAPPER.selectById(shop.getPlatformKey());
                    if (null != platform) {
                        shop.setSysDeptId(platform.getSysDeptId());
                        shop.setSysUserId(platform.getSysUserId());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 设置产品的部门以及用户
     *
     * @param product 需要设置的对象
     */
    public static void setProductDeptIdAndUserId(Product product) {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (null != loginUser && !UserType.APP_USER.getUserType().equals(loginUser.getUserType())) {
                product.setSysDeptId(loginUser.getDeptId());
                product.setSysUserId(loginUser.getUserId());
            } else {
                if (null != product.getPlatformKey()) {
                    Platform platform = PLATFORM_MAPPER.selectById(product.getPlatformKey());
                    if (null != platform) {
                        product.setSysDeptId(platform.getSysDeptId());
                        product.setSysUserId(platform.getSysUserId());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
