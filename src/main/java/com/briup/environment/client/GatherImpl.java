package com.briup.environment.client;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.BackUP;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.Log;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

public class GatherImpl implements Gather {

    //数据文件路径
    private String path;
    private String record_file;

    private Log logger;
    private BackUP backUP;

    @Override
    public void init(Properties properties) throws Exception {
        path = properties.getProperty("src_file");
        record_file = properties.getProperty("record_file");
    }

    @Override
    public Collection<Environment> gather() throws Exception {
/*        File file = new File("src/main/resources/radwtmp");
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);*/

        logger.info("开始读取数据");
        FileReader fr = new FileReader(path);

        List<Environment> list = new ArrayList<>();
        Environment environment = null;


        //是否有备份文件存在，存在：读取备份数据（行数），跳过行数(文件字节数)，从新数据开始读
        //backup.load(本份文件)
        File file = new File(record_file);
        long load = 0;
        if (file.exists()) {
            load = (long) backUP.load(record_file, BackUP.LOAD_REMOVE);
        }
        //跳过上次读取文件字节数
        fr.skip(load);

        BufferedReader br = new BufferedReader(fr);

        while (br.ready()) {
            String s = br.readLine();
            String[] e = s.split("[|]");
            if (e.length == 9) {
                environment = new Environment();
                setValue(environment, e);
                switch (e[3]) {
                    case "16":
                        environment.setSersorAddress("湿度和温度");
                        environment.setData(getTemperature(e[6]));
                        environment.setName("温度");
                        list.add(environment);
                        //list add
                        Environment environment1 = copyObj(environment);
//                        Environment environment1 = (Environment) environment.clone();
                        environment1.setData(getHumidity(e[6]));
                        environment1.setName("湿度");
                        list.add(environment1);
                        //list add
                        break;
                    case "256":
                        environment.setSersorAddress("光照强度");
                        environment.setData(Float.parseFloat(e[6].substring(0, 2)));
                        environment.setName("光照强度");
                        list.add(environment);
                        //list add
                        break;
                    case "1280":
                        environment.setSersorAddress("二氧化碳");
                        environment.setData(Float.parseFloat(e[6].substring(0, 2)));
                        environment.setName("二氧化碳浓度");
                        list.add(environment);
                        //list add
                        break;
                }
            }
        }
        long w = 0;
        int s = 0;
        long co2 = 0;
        long sun = 0;
        for (Environment en :
                list) {
            switch (en.getName()) {
                case "温度":
                    w++;
                    break;
                case "湿度":
                    s++;
                    break;
                case "光照强度":
                    sun++;
                    break;
                case "二氧化碳浓度":
                    co2++;
                    break;
            }
        }
/*        System.out.println("w = " + w);
        System.out.println("s = " + s);
        System.out.println("sun = " + sun);
        System.out.println("co2 = " + co2);
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));
        System.out.println(list.get(4));
        System.out.println(list.get(5));
        System.out.println(list.get(6));
        System.out.println("上方为gather");*/

        logger.info("读取完毕");
        return list;
    }

    private void setValue(Environment environment, String[] e) {
        environment.setSrcId(e[0]);
        environment.setDstId(e[1]);
        environment.setDevId(e[2]);
        environment.setCount(Integer.parseInt(e[4]));
        environment.setCmd(e[5]);
        environment.setStatus(Integer.parseInt(e[7]));
        environment.setGather_date(new Timestamp(Long.parseLong(e[8])));
    }

    private float getTemperature(String str) {
        String t = str.substring(0, 4);
        long t1 = Long.parseLong(t, 16);//String->16
        return (float) (t1 * 0.00268127 - 46.85);
    }

    private static float getHumidity(String str) {
        String h = str.substring(4, 8);
        long h1 = Long.parseLong(h, 16);//String->16
        return (float) (h1 * 0.00190735 - 6);
    }

    public <T> T copyObj(T en) throws Exception {
        //获取模板对象en的未知属性-》赋值-》copy对象----反射
        //获取反射镜像
        Class<T> c = (Class<T>) en.getClass();
        T copy = c.newInstance();
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            //对各个属性赋值
            //私有属性设置可见性
            f.setAccessible(true);
            f.set(copy, f.get(en));
        }
        return copy;
    }


    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            this.logger = configuration.getLogger();
            this.backUP = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
