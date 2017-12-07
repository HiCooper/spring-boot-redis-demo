package com.berry.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Berry_Cooper
 * @date 2017/12/7.
 */
@RestController
public class ControllerTest {
    @Autowired
    StringOperationServiceTest stringOperationServiceTest;

    @GetMapping("test")
    public void test() {
        stringOperationServiceTest.set("valueOperationTest", "12");
        String getValue = stringOperationServiceTest.get("valueOperationTest");
        System.out.println("valueOperationTest的值：" + getValue);
        Long ic = stringOperationServiceTest.increment("valueOperationTest", 1L);
        System.out.println("自增后:" + ic);
        Boolean result = stringOperationServiceTest.setIfAbsent("valueOperationTest", "14");
        System.out.println("setIfAbsent结果：" + result);
        stringOperationServiceTest.setWithExpire("keyExp", "999", 30L, TimeUnit.SECONDS);
        Long time = stringOperationServiceTest.getExpire("keyExp");
        System.out.println("过期时间：" + time);
    }
}
