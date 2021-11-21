package com.briup.environment.util;

import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class ConfigurationImpl implements Configuration {
    private Map<String, WossModuleInit> map;

    private final static ConfigurationImpl conf = new ConfigurationImpl();

    private ConfigurationImpl() {
        try {
            map = new HashMap<>();
            File file = new File("src/main/resources/config.xml");
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);
            Element root = doc.getRootElement();
            List<Element> elements = root.elements();
            for (Element e : elements) {
                Attribute aClass = e.attribute("class");
                String className = aClass.getValue();
                Object object = Class.forName(className).newInstance();
                List<Element> e1s = e.elements();
                Properties properties = new Properties();//容器  存储键值对
                for (Element e2 : e1s) {
                    String key = e2.getName();
                    String value = e2.getStringValue();
                    properties.setProperty(key, value);
                }
                WossModuleInit module = (WossModuleInit) object;//把反射产生的所有对象统一强转为WossModuleInit,方便调用init初始化对象(初始化成员变量)
                module.init(properties);//初始化成员变量
                //存储对象到map集合
                map.put(e.getName(), module);
            }
            //上方init需要调用得到properties，下方setConfiguration依然需要初始化，让各个模块得到其他模块的对象(成员变量)
            Collection<WossModuleInit> values = map.values();
            for (WossModuleInit wossModuleInit : values) {
                ((ConfigurationAWare) wossModuleInit).setConfiguration(this);//this为configuration对象,该方法为构造方法,前面括号为强转对象类型

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationImpl getInstance(){
        return conf;
    }

    @Override
    public Log getLogger() throws Exception {
        return (Log) map.get("logger");
    }

    @Override
    public Server getServer() throws Exception {
        return (Server) map.get("server");
    }

    @Override
    public Client getClient() throws Exception {
        return (Client) map.get("client");
    }

    @Override
    public DBStore getDbStore() throws Exception {
        return (DBStore) map.get("dbstore");
    }

    @Override
    public Gather getGather() throws Exception {
        return (Gather) map.get("gather");
    }

    @Override
    public BackUP getBackup() throws Exception {
        return (BackUP) map.get("backup");
    }

/*    public static void main(String[] args) {
        try {
            new ConfigurationImpl().getGather().gather();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
