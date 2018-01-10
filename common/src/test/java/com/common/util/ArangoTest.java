package com.common.util;

import com.arangodb.ArangoCursor;
import com.arangodb.util.MapBuilder;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ArangoTest {

    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:common.xml");
        ArangoUtil arango = (ArangoUtil) context.getBean("arangoUtil");
        String queryFigure = "for doc in @@entityCollName filter doc.status == 'active' and doc.label == 'role' "
                + "return {'key':doc._key,'status':doc.status,'profession':doc.profession,'formatName':doc.formatName,'formatNames':doc.formatNames,'label':doc.label,'name':doc.name, 'dataSource':doc.dataSource}";
        ArangoCursor<String> iter = arango.getDB().db("knowledge-graph-test").query(queryFigure, new MapBuilder()
                .put("@entityCollName", "entity").get(), null, String.class);
        while (iter.hasNext()){
            System.out.println(iter.next());
        }

    }
}
