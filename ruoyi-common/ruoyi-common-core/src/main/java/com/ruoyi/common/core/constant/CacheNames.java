package com.ruoyi.common.core.constant;

/**
 * 缓存组名称常量
 * <p>
 * key 格式为 cacheNames#ttl#maxIdleTime#maxSize
 * <p>
 * ttl 过期时间 如果设置为0则不过期 默认为0
 * maxIdleTime 最大空闲时间 根据LRU算法清理空闲数据 如果设置为0则不检测 默认为0
 * maxSize 组最大长度 根据LRU算法清理溢出数据 如果设置为0则无限长 默认为0
 * <p>
 * 例子: test#60s、test#0#60s、test#0#1m#1000、test#1h#0#500
 *
 * @author Lion Li
 */
public interface CacheNames {

    /**
     * 演示案例
     */
    String DEMO_CACHE = "demo:cache#60s#10m#20";

    /**
     * 系统配置
     */
    String SYS_CONFIG = "sys_config";

    /**
     * 数据字典
     */
    String SYS_DICT = "sys_dict";

    /**
     * 用户账户
     */
    String SYS_USER_NAME = "sys_user_name#30d";

    /**
     * 部门
     */
    String SYS_DEPT = "sys_dept#30d";

    /**
     * OSS内容
     */
    String SYS_OSS = "sys_oss#30d";

    /**
     * OSS配置
     */
    String SYS_OSS_CONFIG = "sys_oss_config";

    /**
     * 在线用户
     */
    String ONLINE_TOKEN = "online_tokens";

    /**
     * 平台信息
     */
    String PLATFORM = "platforms#5d";

    /**
     * 平台顶部导航栏配置
     */
    String PAGE = "pages#5d";
    /**
     * 平台顶部导航栏配置
     */
    String PAGE_SETTING = "page_settings#5d";
    /**
     * 广告管理
     */
    String BANNER = "banners#50m";
    /**
     * 栏目缓存
     */
    String CATEGORY = "category#40m";
    /**
     * 栏目缓存
     */
    String CATEGORY_LIST = "categoryList#40m";
    /**
     * 栏目缓存
     */
    String GRAB_PERIOD = "grabPeriod#220m";
    /**
     * 栏目缓存
     */
    String GRAB_PERIOD_PRODUCT = "grabPeriodProduct#220m";
    /**
     * 商品缓存
     */
    String PRODUCT = "product#240m";
    /**
     * 商品缓存
     */
    String FOOD_PRODUCT = "foodProduct#240";
    /**
     * 商品缓存
     */
    String CATEGORY_PRODUCT = "categoryProduct#240m";
    /**
     * 今日特惠商户ID缓存 根据商品查询出来的
     */
    String COMMERCIAL_PRODUCT = "commercialProduct#10m";
    /**
     * 商户ID 查询对应商品Id
     */
    String COMMERCIAL_PRODUCT_IDS = "commercialProductIds#10m";
    /**
     * 商户缓存
     */
    String COMMERCIAL = "commercial#60m";
    /**
     * 门店缓存
     */
    String SHOP = "shop#60m";
    /**
     * 任务列表缓存
     */
    String MISSION_LIST = "missionList#5m";
    /**
     * 任务用户缓存
     */
    String MISSION_USER = "missionUser#5d";
    /**
     * 任务组缓存
     */
    String MISSION_GROUP = "missionGroup#2d";
    /**
     * 任务信息缓存
     */
    String MISSION = "mission#2d";
    /**
     * 任务奖品缓存
     */
    String MISSION_GROUP_DRAW = "missionGroupDraw#250m";
    /**
     * 任务背景图片缓存
     */
    String MISSION_BJ_IMG = "missionBjImg#5m";
    /**
     * 抽奖奖池缓存
     */
    String DRAW_LIST = "drawList#300m";
    /**
     * 权益包产品缓存
     */
    String EQUITY_PRODUCT_LIST = "equityProduct#2d";
    /**
     * 落地页缓存
     */
    String TEMPLATE_PAGE = "templatePage#2d";
    /**
     * 落地页配置缓存
     */
    String TEMPLATE_SETTING_LIST = "templateSettingList#2d";
    /**
     * 订单补发缓存
     */
    String reloadOrderNumbers = "reloadOrderNumbers#300m";
    /**
     * 订单补发缓存
     */
    String ProductPackage = "ProductPackage#1d";
    /**
     * 热门搜索
     */
    String hotNews = "HotNews#1d";
    /**
     * token缓存，1分钟以内的token不重复生成
     */
    String loginUserTokens = "loginUserTokens#60s";

    /**
     * ysf缓存数据
     */
    String ysfConfig = "ysfConfig#5d";

    /**
     * 城市自定义首页
     */
    String cityIndexList = "cityIndexList#3d";

    /**
     * 商品列表
     */
    String productList = "productList#5m";

    /**
     * 分销商缓存
     */
    String DISTRIBUTOR = "distributor#2d";
    /**
     * 微信用户令牌缓存
     */
    String WX_ENTITY = "wxEntity#1d";
    /**
     * 新用户营销
     */
    String marketInfo = "MarketInfo#1d";
    /**
     * 新用户营销奖励信息
     */
    String marketLog = "MarketLog#1d";
    String userMarketLog = "userMarketLog#1d";
    /**
     * 新用户营销奖励信息
     */
    String recordList = "recordList#20m";
    String recordStringList = "recordStringList#40m";

    /* *************************** 以下缓存key皆为商户端使用 ************************************/
    /**
     * 微信用户令牌缓存
     */
    String M_WX_ENTITY = "mwxEntity#1d";
    /**
     * 核销人员信息缓存
     */
    String M_VERIFIER = "m_verifier#1d";
    /**
     * 银联产品查询缓存
     */
    String UNIONPAY_PRODUCT = "unionpayProduct#60m";
    /**
     * 浏览任务
     */
    String BROWSELIST = "browseList#40m";
    /**
     * 浏览任务
     */
    String BROWSEVO = "browseVo#120m";

    /**
     * 活动商户缓存
     */
    String ACTIVITY_MERCHANT = "activityMerchant#15d";

    /**
     * 商户类型缓存
     */
    String MERCHANT_TYPE = "merchantType#15d";

    /**
     * 省市区列表缓存
     */
    String CITY_AREA_LIST = "cityAreaList#30d";
}
