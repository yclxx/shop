package com.ruoyi.zlyyhmobile;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 系统模块
 *
 * @author ruoyi
 */
@EnableDubbo
@SpringBootApplication
public class ZlyyhMobileApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ZlyyhMobileApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  浙里有优惠手机端模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
