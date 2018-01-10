package com.common.util;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;

public class ArangoUtil {
    private ArangoDB arangoDB;
    private String host;
    private int port;
    private String user;
    private String passwd;

    public void init(){
        if("".equals(user) || "".equals(passwd)){
            arangoDB = new ArangoDB.Builder().host(host, port).build();
        }else{
            arangoDB = new ArangoDB.Builder().host(host, port).user(user).password(passwd).build();
        }
    }

    public ArangoDB getDB(){
        return arangoDB;
    }
    public void setHost(String host){
        this.host = host;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void setPasswd(String passwd){
        this.passwd = passwd;
    }
}
