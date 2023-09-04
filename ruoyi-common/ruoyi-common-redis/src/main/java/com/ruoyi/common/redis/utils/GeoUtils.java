package com.ruoyi.common.redis.utils;

import org.redisson.Redisson;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

public class GeoUtils {

    public static void main(String[] args) {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://47.114.148.191:6380");
        singleServerConfig.setPassword("Yzg!@#13.8Mdc");

        RedissonClient redissonClient = Redisson.create(config);

        RAtomicDouble testAtomicDouble = redissonClient.getAtomicDouble("testAtomicDouble");
        testAtomicDouble.set(0);
        double v = testAtomicDouble.get();
        System.out.println(v);
        System.out.println(testAtomicDouble.addAndGet(100));
//        Set<Double> list = new HashSet<>();
//        for (int i = 0; i < 1000; i++) {
//            new Thread(() -> {
//                list.add(testAtomicDouble.addAndGet(100));
//            }).start();
//        }
//        ThreadUtil.sleep(15000);
//        v = testAtomicDouble.get();
//        System.out.println("结束：" + v);
//        System.out.println(list.size());
//        v = testAtomicDouble.getAndAdd(102);
//        System.out.println(v);

//        RGeo<Object> testGeo0423 = redissonClient.getGeo("testGeo0423");
//
//        double lng = 120.5554;
//        double lat = 27.9665;
//
//        testGeo0423.add(lng,lat,"1644593584501489666_1644595894099181534");
//
//        Map<Object, Double> objectDoubleMap = testGeo0423.searchWithDistance(GeoSearchArgs.from(120.58, 27.97).radius(100, GeoUnit.KILOMETERS).order(GeoOrder.ASC));
//        System.out.println(objectDoubleMap);
//        testGeo0423.storeSortedSearchTo("testGeoSorted0423",GeoSearchArgs.from(120.58, 27.97).radius(100, GeoUnit.KILOMETERS).order(GeoOrder.ASC));
//        RScoredSortedSet<String> scoredSortedSet = redissonClient.getScoredSortedSet("testGeoSorted0423");
//        Set<String> collect = scoredSortedSet.stream("*_15555_*").collect(Collectors.toSet());
//        System.out.println(collect);
//        Iterator<String> iterator = scoredSortedSet.iterator("*15555*");
//
//        while (iterator.hasNext()){
//            System.out.println(iterator.next());
//        }
//        long count = scoredSortedSet.stream("1644593584501489667").count();
//        System.out.println(count);
//        System.out.println(scoredSortedSet.stream().count());

//        int size = scoredSortedSet.size();
//        System.out.println(size);
//        Collection<String> strings = scoredSortedSet.readDiff("1644593584501489667*");
////        long count = scoredSortedSet.stream("1644593584501489666*").count();
////        System.out.println(count);
////        Set<String> strings =
////            scoredSortedSet.readSort("1644593584501489666", SortOrder.ASC);
//        System.out.println(JSONObject.toJSONString(strings));
//        System.out.println(JSONObject.toJSONString(collect));

    }
}
