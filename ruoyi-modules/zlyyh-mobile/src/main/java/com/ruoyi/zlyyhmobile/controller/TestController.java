package com.ruoyi.zlyyhmobile.controller;

import com.ruoyi.zlyyhmobile.mq.producer.TestStreamProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试mq
 *
 * @author 25487
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/ignore")
public class TestController {

    private final TestStreamProducer testStreamProducer;
    private Integer total = 0;

    @PostMapping("/kafka/{msg}")
    public void sendMsg(@PathVariable("msg") String msg) {
        log.info("接收到请求：{}", msg);
        testStreamProducer.streamTestMsg(msg);
    }

    @PostMapping("/sub")
    public void sendMsg() {
        total++;
        log.info("接收到请求：{}", total);
    }
}
