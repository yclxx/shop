package com.ruoyi.zlyyhadmin;

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
public class ZlyyhAdminApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ZlyyhAdminApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  浙里有优惠管理端模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
