package com.berry.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author Gunnar Hillert
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
public class RedisApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RedisApplication.class, args);
    }

}
