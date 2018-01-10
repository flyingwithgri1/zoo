package com.common.util;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JedisUtilTest {

    @Test
    public void test2(){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:common.xml");
        JedisUtil jedis = (JedisUtil) context.getBean("jedisUtil");
        jedis.hset("d","d","d");
    }
}
