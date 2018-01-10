package com.common.util;

import com.mongodb.client.MongoCursor;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MongoDBTest {

    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:common.xml");
        MongoDBUtil mongoDBUtil = (MongoDBUtil) context.getBean("mongoDBUtil");
        MongoCursor c = mongoDBUtil.getDBCollection("MovieKnowledgeMap","life_Knowledge").find().iterator();
        while (c.hasNext()){
            System.out.println(c.next());
        }
    }
}
