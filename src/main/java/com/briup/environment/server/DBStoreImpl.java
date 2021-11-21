package com.briup.environment.server;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.BackUP;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.Log;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Properties;

public class DBStoreImpl implements DBStore {
    private Log logger;
    private String driver;
    private String url;
    private String username;
    private String password;
    private Connection conn;
    private PreparedStatement ps;

    private String record_file;
    private BackUP backUP;

    @Override
    public void saveDb(Collection<Environment> coll) throws Exception {
        logger.info("开始存储");
        int count = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        StringBuilder sql=new StringBuilder();
        String day;
        String date;
        int dayNum;
        int lastDay = -1;
        String sql;


        //读取备份文件，加载上次未入库的异常数据，将这些数据添加到coll
        //backup.load
        File file=new File(record_file);
        if(file.exists()){
            //读取备份文件中上次未入库的环境对象集合
            Collection<Environment> load =
                    (Collection<Environment>) backUP.load(record_file, BackUP.LOAD_REMOVE);
            coll.addAll(load);
        }

        for (Environment en : coll) {

            date = sdf.format(en.getGather_date());
            day = date.substring(8, 10);
            dayNum = Integer.parseInt(day);

            if (dayNum != lastDay) {
                if (lastDay != -1) {
                    ps.executeBatch();
                    ps.close();
                }
                sql = "insert into e_detail_" + day + "(`name`,srcId,dstId,sersorAddress,`count`,cmd,status,`data`,gather_date) values (?,?,?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
            }
/*            else if (lastDay == -1) {
                sql = "insert into e_detail_" + day + " (`name`,srcId,dstId,sersorAddress,`count`,cmd,status,`data`,gather_date) values(?,?,?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
            }*/
            ps.setString(1, en.getName());
            ps.setString(2, en.getSrcId());
            ps.setString(3, en.getDstId());
            ps.setString(4, en.getSersorAddress());
            ps.setInt(5, en.getCount());
            ps.setString(6, en.getCmd());
            ps.setInt(7, en.getStatus());
            ps.setFloat(8, en.getData());
            ps.setTimestamp(9, en.getGather_date());

            ps.addBatch();
            if (count%88 == 0) {
                ps.executeBatch();
            }
            lastDay = dayNum;
            count++;
        }
        ps.executeBatch();
        ps.close();
        conn.commit();
        conn.close();
    }

    @Override
    public void init(Properties properties) throws Exception {
        record_file = properties.getProperty("record_file");
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        Class.forName(driver);
        conn = DriverManager.getConnection(url, username, password);
        conn.setAutoCommit(false);
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            this.logger=configuration.getLogger();
            this.backUP = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*            //数据采集日期,时间戳
            Timestamp gather_date = e.getGather_date();
//            int day = gather_date.getDate();
            //Calendar:日历类,帮助获取一个月的第几天
            Calendar calendar=Calendar.getInstance();
            //设置时间
            calendar.setTime(gather_date);
            //获取对应的’日‘
            int day = calendar.get(Calendar.DAY_OF_MONTH);*/