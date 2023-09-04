package com.ruoyi.zlyyhadmin.runner;

import com.ruoyi.zlyyhadmin.service.IShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化 system 模块对应业务数据
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminApplicationRunner implements ApplicationRunner {

    private final IShopService shopService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        shopService.loadingShopCache();
    }

}
