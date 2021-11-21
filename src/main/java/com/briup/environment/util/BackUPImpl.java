package com.briup.environment.util;

import java.io.*;
import java.util.Properties;

public class BackUPImpl implements BackUP {
    private Log logger;
    private String properties_path;

    /*
    采集模块
        load：加载备份文件中保存的字节数
            以便跳过上次读取的数据内容
    入库模块
        load：加载备份文件中保存的环境对象集合
              以便将上次未入库存在问题的数据重新入库

   key:备份文件路径
   falg:是否删除备份文件
 */
    @Override
    public Object load(String key, boolean flag) {

        File file = new File(key);
        if (!file.exists()) return null;
        FileInputStream fis;
        ObjectInputStream ois;
        Object o = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            o = ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //是否删除备份文件
        if (flag) {
            file.delete();
        }
        return o;

    }


    /*
        采集模块：
            保存已经读取到的文件字节数
        入库模块：
            保存尚未保存的环境对象集合（存在问题）

        key:备份文件
        data:需要保存字节数（集合）
        flag:是否追加
     */
    @Override
    public void store(String key, Object data, boolean flag) {
        try {
            File file = new File(key);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file, flag);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            this.logger = configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Properties properties) throws Exception {
        properties_path = properties.getProperty("parent-path");
    }
}
