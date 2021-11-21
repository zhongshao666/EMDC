package com.briup.environment.server;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;
import com.briup.environment.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ServerImpl implements Server {
    private int port;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private boolean flag=true;
    private DBStore dbStore;
    private Log logger;

    @Override
    public void reciver() throws Exception {


        logger.info("启动服务器");
        serverSocket = new ServerSocket(this.port);
        while (flag) {
            socket = serverSocket.accept();

            new Thread(()->{
                try {
                    logger.info("建立连接");
                    InputStream is = socket.getInputStream();

                    ObjectInputStream ois = new ObjectInputStream(is);
                    Object o = ois.readObject();
                    if (o instanceof Collection) {
                        Collection<Environment> coll = (Collection<Environment>) o;
                        // 获取当前系统时间
                        long startTime = System.currentTimeMillis();
                        dbStore.saveDb(coll);
                        // 获取当前的系统时间，与初始时间相减就是程序运行的毫秒数，除以1000就是秒数*/
                        long endTime = System.currentTimeMillis();
                        long usedTime = (endTime - startTime) / 1000;
                        logger.info("本次存储完毕，插入数据所用时间 = " + usedTime + "S");
                    }
                    is.close();
                    ois.close();
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                Thread.interrupted();
            }).start();
        }
    }

    @Override
    public void shutdown() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                flag=false;
                logger.info("服务器关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.port=Integer.parseInt(properties.getProperty("port"));


    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            this.dbStore=configuration.getDbStore();
            this.logger=configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 运行服务器端
        try {
            Server server = ConfigurationImpl.getInstance().getServer();
            server.reciver();

            server.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


/*            logger.info("建立连接");
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            Object o = ois.readObject();
            if (o instanceof Collection) {
                Collection<Environment> coll = (Collection<Environment>) o;
                // 获取当前系统时间
                long startTime = System.currentTimeMillis();
                dbStore.saveDb(coll);
                // 获取当前的系统时间，与初始时间相减就是程序运行的毫秒数，除以1000就是秒数
                long endTime = System.currentTimeMillis();
                long usedTime = (endTime - startTime) / 1000;
                logger.info("插入数据所用时间 = " + usedTime + "S");
            }
            is.close();
            ois.close();
            socket.close();*/