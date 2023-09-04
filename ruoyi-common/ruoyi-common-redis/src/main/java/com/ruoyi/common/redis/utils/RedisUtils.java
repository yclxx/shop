package com.ruoyi.common.redis.utils;

import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.core.utils.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;
import org.redisson.api.geo.GeoSearchArgs;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * redis 工具类
 *
 * @author Lion Li
 * @version 3.1.0 新增
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    /**
     * 限流
     *
     * @param key          限流key
     * @param rateType     限流类型
     * @param rate         速率
     * @param rateInterval 速率间隔
     * @return -1 表示失败
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, rateInterval, RateIntervalUnit.SECONDS);
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        } else {
            return -1L;
        }
    }

    /**
     * 获取客户端实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    /**
     * 发布通道消息
     *
     * @param channelKey 通道key
     * @param msg        发送数据
     * @param consumer   自定义处理
     */
    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
        consumer.accept(msg);
    }

    public static <T> void publish(String channelKey, T msg) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
    }

    /**
     * 订阅通道接收消息
     *
     * @param channelKey 通道key
     * @param clazz      消息类型
     * @param consumer   自定义处理
     */
    public static <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static <T> void setCacheObject(final String key, final T value) {
        setCacheObject(key, value, false);
    }

    /**
     * 缓存基本的对象，保留当前对象 TTL 有效期
     *
     * @param key       缓存的键值
     * @param value     缓存的值
     * @param isSaveTtl 是否保留TTL有效期(例如: set之前ttl剩余90 set之后还是为90)
     * @since Redis 6.X 以上使用 setAndKeepTTL 兼容 5.X 方案
     */
    public static <T> void setCacheObject(final String key, final T value, final boolean isSaveTtl) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        if (isSaveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception e) {
                long timeToLive = bucket.remainTimeToLive();
                setCacheObject(key, value, Duration.ofMillis(timeToLive));
            }
        } else {
            bucket.set(value);
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param duration 时间
     */
    public static <T> void setCacheObject(final String key, final T value, final Duration duration) {
        RBatch batch = CLIENT.createBatch();
        RBucketAsync<T> bucket = batch.getBucket(key);
        bucket.setAsync(value);
        bucket.expireAsync(duration);
        batch.execute();
    }

    /**
     * 注册对象监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addObjectListener(final String key, final ObjectListener listener) {
        RBucket<T> result = CLIENT.getBucket(key);
        result.addListener(listener);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final long timeout) {
        return expire(key, Duration.ofSeconds(timeout));
    }

    /**
     * 设置有效时间
     *
     * @param key      Redis键
     * @param duration 超时时间
     * @return true=设置成功；false=设置失败
     */
    public static boolean expire(final String key, final Duration duration) {
        RBucket rBucket = CLIENT.getBucket(key);
        return rBucket.expire(duration);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getCacheObject(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.get();
    }

    /**
     * 获得key剩余存活时间
     *
     * @param key 缓存键值
     * @return 剩余存活时间
     */
    public static <T> long getTimeToLive(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.remainTimeToLive();
    }

    /**
     * 删除单个对象
     *
     * @param key 缓存的键值
     */
    public static boolean deleteObject(final String key) {
        return CLIENT.getBucket(key).delete();
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     */
    public static void deleteObject(final Collection collection) {
        RBatch batch = CLIENT.createBatch();
        collection.forEach(t -> {
            batch.getBucket(t.toString()).deleteAsync();
        });
        batch.execute();
    }

    /**
     * 检查缓存对象是否存在
     *
     * @param key 缓存的键值
     */
    public static boolean isExistsObject(final String key) {
        return CLIENT.getBucket(key).isExists();
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public static <T> boolean setCacheList(final String key, final List<T> dataList) {
        RList<T> rList = CLIENT.getList(key);
        return rList.addAll(dataList);
    }

    /**
     * 缓存单个数据至List
     *
     * @param key  缓存的键值
     * @param data 待缓存的数据
     * @return 缓存的对象
     */
    public static <T> boolean setCacheList(final String key, final T data) {
        RList<T> rList = CLIENT.getList(key);
        return rList.add(data);
    }

    /**
     * 删除List中指定数据
     *
     * @param key  缓存的键值
     * @param data 待删除的数据
     * @return 缓存的对象
     */
    public static <T> boolean delCacheList(final String key, final T data) {
        RList<T> rList = CLIENT.getList(key);
        return rList.remove(data);
    }

    /**
     * 获取List中总数量
     *
     * @param key  缓存的键值
     * @return 缓存的对象
     */
    public static int getCacheListSize(final String key) {
        RList rList = CLIENT.getList(key);
        return rList.size();
    }

    /**
     * 注册List监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addListListener(final String key, final ObjectListener listener) {
        RList<T> rList = CLIENT.getList(key);
        rList.addListener(listener);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> getCacheList(final String key) {
        RList<T> rList = CLIENT.getList(key);
        return rList.readAll();
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public static <T> List<T> getCacheList(final String key, int start, int end) {
        RList<T> rList = CLIENT.getList(key);
        return rList.range(start, end);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> boolean setCacheSet(final String key, final Set<T> dataSet) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.addAll(dataSet);
    }

    /**
     * 单个值缓存到Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public static <T> boolean setCacheSet(final String key, final T dataSet) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.add(dataSet);
    }

    /**
     * 注册Set监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addSetListener(final String key, final ObjectListener listener) {
        RSet<T> rSet = CLIENT.getSet(key);
        rSet.addListener(listener);
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存的key
     * @return set对象
     */
    public static <T> Set<T> getCacheSet(final String key) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.readAll();
    }

    /**
     * 缓存Map
     *
     * @param key     缓存的键值
     * @param dataMap 缓存的数据
     */
    public static <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> rMap = CLIENT.getMap(key);
            rMap.putAll(dataMap);
        }
    }

    /**
     * 注册Map监听器
     * <p>
     * key 监听器需开启 `notify-keyspace-events` 等 redis 相关配置
     *
     * @param key      缓存的键值
     * @param listener 监听器配置
     */
    public static <T> void addMapListener(final String key, final ObjectListener listener) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.addListener(listener);
    }

    /**
     * 获得缓存的Map
     *
     * @param key 缓存的键值
     * @return map对象
     */
    public static <T> Map<String, T> getCacheMap(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.getAll(rMap.keySet());
    }

    /**
     * 获得缓存Map的key列表
     *
     * @param key 缓存的键值
     * @return key列表
     */
    public static <T> Set<String> getCacheMapKeySet(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.keySet();
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public static <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.put(hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T getCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.get(hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public static <T> T delCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.remove(hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public static <K, V> Map<K, V> getMultiCacheMapValue(final String key, final Set<K> hKeys) {
        RMap<K, V> rMap = CLIENT.getMap(key);
        return rMap.getAll(hKeys);
    }

    /**
     * 设置原子值
     *
     * @param key   Redis键
     * @param value 值
     */
    public static void setAtomicValue(String key, long value) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        atomic.set(value);
    }

    /**
     * 获取原子值
     *
     * @param key Redis键
     * @return 当前值
     */
    public static long getAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.get();
    }

    /**
     * 递增原子值
     *
     * @param key Redis键
     * @return 当前值
     */
    public static long incrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.incrementAndGet();
    }

    /**
     * 递减原子值
     *
     * @param key Redis键
     * @return 当前值
     */
    public static long decrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.decrementAndGet();
    }

    /**
     * 设置原子值 小数
     *
     * @param key   Redis键
     * @param value 值
     */
    public static void setAtomicDouble(String key, double value) {
        RAtomicDouble atomic = CLIENT.getAtomicDouble(key);
        atomic.set(value);
    }

    /**
     * 获取原子值 小数
     *
     * @param key Redis键
     * @return 当前值
     */
    public static double getAtomicDouble(String key) {
        RAtomicDouble atomic = CLIENT.getAtomicDouble(key);
        return atomic.get();
    }

    /**
     * 增加原子值 小数
     *
     * @param key   Redis键
     * @param value 添加数值
     * @return 添加后的值
     */
    public static double addAtomicDouble(String key, double value) {
        RAtomicDouble atomic = CLIENT.getAtomicDouble(key);
        return atomic.addAndGet(value);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public static Collection<String> keys(final String pattern) {
        Stream<String> stream = CLIENT.getKeys().getKeysStreamByPattern(pattern);
        return stream.collect(Collectors.toList());
    }

    /**
     * 删除缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     */
    public static void deleteKeys(final String pattern) {
        CLIENT.getKeys().deleteByPattern(pattern);
    }

    /**
     * 检查redis中是否存在key
     *
     * @param key 键
     */
    public static Boolean hasKey(String key) {
        RKeys rKeys = CLIENT.getKeys();
        return rKeys.countExists(key) > 0;
    }

    /**
     * sortedSet删除元素 geo删除指定名字的缓存
     *
     * @param key  geo缓存key
     * @param name 需要删除的对象
     */
    public static void geoDel(final String key, final String name) {
        RScoredSortedSet<Object> scoredSortedSet = CLIENT.getScoredSortedSet(key);
        scoredSortedSet.remove(name);
    }

    /**
     * sortedSet删除元素 geo删除指定名字的缓存
     *
     * @param key   geo缓存key
     * @param names 需要删除的对象
     */
    public static void geoDel(final String key, final Collection names) {
        RScoredSortedSet<Object> scoredSortedSet = CLIENT.getScoredSortedSet(key);
        scoredSortedSet.removeAllAsync(names);
    }

    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中
     *
     * @param name   集合名称
     * @param lng    经度
     * @param lat    纬度
     * @param member 成员名称
     * @return 添加元素个数
     */
    public static long geoAdd(String name, double lng, double lat, String member) {
        RGeo<Object> geo = CLIENT.getGeo(name);
        return geo.add(lng, lat, member);
    }

    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中
     *
     * @param name    集合名称
     * @param members 多个成员信息
     * @return 添加元素个数
     */
    public static long geoAdd(String name, GeoEntry... members) {
        RGeo<Object> geo = CLIENT.getGeo(name);
        return geo.add(members);
    }

    /**
     * 将指定的地理空间位置（纬度、经度、名称）添加到指定的key中
     *
     * @param name    集合名称
     * @param members 多个成员信息
     * @return 添加元素个数
     */
    public static long geoAdd(String name, List<GeoEntry> members) {
        RGeo<Object> geo = CLIENT.getGeo(name);
        return geo.add(ArrayUtil.toArray(members, GeoEntry.class));
    }

    /**
     * 以给定的经纬度为中心， 找出某一半径内的元素, 并返回距离
     *
     * @param name     集合名称
     * @param lng      经度
     * @param lat      纬度
     * @param radius   半径
     * @param geoUnit  半径单位
     * @param geoOrder 排序
     * @param count    返回元素个数
     * @return 结果
     */
    public static Map<Object, Double> geoRadiusWithDistance(String name, double lng, double lat, double radius, GeoUnit geoUnit, GeoOrder geoOrder, int count) {
        RGeo<Object> geo = CLIENT.getGeo(name);
        return geo.searchWithDistance(GeoSearchArgs.from(lng, lat).radius(radius, geoUnit).order(geoOrder).count(count));
    }

    /**
     * 以给定的经纬度为中心， 找出某一半径内的元素, 并返回距离
     *
     * @param name     集合名称
     * @param lng      经度
     * @param lat      纬度
     * @param radius   半径
     * @param geoUnit  半径单位
     * @param geoOrder 排序
     * @return 结果
     */
    public static Map<Object, Double> geoRadiusWithDistance(String name, double lng, double lat, double radius, GeoUnit geoUnit, GeoOrder geoOrder) {
        RGeo<Object> geo = CLIENT.getGeo(name);
        return geo.searchWithDistance(GeoSearchArgs.from(lng, lat).radius(radius, geoUnit).order(geoOrder));
    }

//    /**
//     * 以给定的经纬度为中心， 找出某一半径内的元素, 并存储到另外的key中
//     *
//     * @param name     集合名称
//     * @param lng      经度
//     * @param lat      纬度
//     * @param radius   半径
//     * @param geoUnit  半径单位
//     * @param geoOrder 排序
//     * @param duration 超时时间
//     */
//    public static void geoStoreRadiusWithDistance(final String key, final String name, final double lng, final double lat, final double radius, final GeoUnit geoUnit, final GeoOrder geoOrder, final Duration duration) {
//        RGeo<Object> geo = CLIENT.getGeo(name);
//        // 此处需人工添加缓存前缀
//        geo.storeSortedSearchTo(addKeyPrefix(key), GeoSearchArgs.from(lng, lat).radius(radius, geoUnit).order(geoOrder));
//        expire(key, duration);
//    }
//
//    /**
//     * 以给定的经纬度为中心， 找出某一半径内的元素, 并存储到另外的key中
//     *
//     * @param name     集合名称
//     * @param lng      经度
//     * @param lat      纬度
//     * @param radius   半径
//     * @param geoUnit  半径单位
//     * @param geoOrder 排序
//     * @param duration 超时时间
//     * @return 结果集
//     */
//    public static Collection<ScoredEntry<String>> geoRadiusWithDistancePage(final String name, final double lng, final double lat, final double radius, final GeoUnit geoUnit, final GeoOrder geoOrder, final Duration duration, final int pageNum, final int pageSize) {
//        // 生成结果缓存key
//        final String key = getGeoZSetKey(name, lng, lat, radius, geoUnit, geoOrder);
//        // 校验key是否存在，不存在，则先保存
//        Boolean has = hasKey(key);
//        if (!has) {
//            geoStoreRadiusWithDistance(key, name, lng, lat, radius, geoUnit, geoOrder, duration);
//        }
//        int startPage = (pageNum - 1) * pageSize;
//        int endPage = pageNum * pageSize - 1;
//        RScoredSortedSet<String> scoredSortedSet = CLIENT.getScoredSortedSet(key);
//        return scoredSortedSet.entryRange(startPage, endPage);
//    }
//
//    /**
//     * 以给定的经纬度为中心， 找出某一半径内的元素, 并存储到另外的key中
//     *
//     * @param name     集合名称
//     * @param lng      经度
//     * @param lat      纬度
//     * @param radius   半径
//     * @param geoUnit  半径单位
//     * @param geoOrder 排序
//     * @return 结果集
//     */
//    public static long geoRadiusWithDistanceCount(final String name, final double lng, final double lat, final double radius, final GeoUnit geoUnit, final GeoOrder geoOrder) {
//        // 生成结果缓存key
//        final String key = getGeoZSetKey(name, lng, lat, radius, geoUnit, geoOrder);
//        // 校验key是否存在，不存在，则先保存
//        Boolean has = hasKey(key);
//        if (!has) {
//            return 0;
//        }
//        RScoredSortedSet<String> scoredSortedSet = CLIENT.getScoredSortedSet(key);
//        return scoredSortedSet.size();
//    }
//
//    public static String getGeoZSetKey(final String name, final double lng, final double lat, final double radius, final GeoUnit geoUnit, final GeoOrder geoOrder) {
//        return "geoZSet:" + name + ":" + lng + "_" + lat + ":" + radius + geoUnit + ":" + geoOrder;
//    }
//
//    /**
//     * 查询zset中数据
//     *
//     * @param key 集合名称
//     * @return 结果集
//     */
//    public static Collection<ScoredEntry<String>> zsetPage(final String key, final int pageNum, final int pageSize) {
//        int startPage = (pageNum - 1) * pageSize;
//        int endPage = pageNum * pageSize - 1;
//        RScoredSortedSet<String> scoredSortedSet = CLIENT.getScoredSortedSet(key);
//        return scoredSortedSet.entryRange(startPage, endPage);
//    }
//
//    /**
//     * 查询zset中长度
//     *
//     * @param key 集合名称
//     * @return 结果集
//     */
//    public static long zsetSize(final String key) {
//        // 校验key是否存在，不存在，则先保存
//        Boolean has = hasKey(key);
//        if (!has) {
//            return 0;
//        }
//        RScoredSortedSet<String> scoredSortedSet = CLIENT.getScoredSortedSet(key);
//        return scoredSortedSet.size();
//    }
//
//    /**
//     * 手动添加redis缓存key前缀
//     *
//     * @param key 缓存key
//     * @return 添加前缀之后的缓存key
//     */
//    public static String addKeyPrefix(String key) {
//        // 增加key前缀
//        RedissonProperties redissonProperties = SpringUtils.getBean(RedissonProperties.class);
//        return StringUtils.isBlank(redissonProperties.getKeyPrefix()) ? key : (redissonProperties.getKeyPrefix() + ":" + key);
//    }
}
