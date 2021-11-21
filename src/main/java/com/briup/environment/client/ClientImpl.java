package com.briup.environment.client;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;
import com.briup.environment.util.Log;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ClientImpl implements Client {
    private Log logger;
    private String ip;
    private int port;
    private Socket socket=null;
    @Override
    public void send(Collection<Environment> coll) throws Exception {
        logger.info("启动客户端");
        socket = new Socket(ip,port);
        OutputStream os= socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(coll);
        logger.info("客户端开始发送");
        oos.flush();
        logger.info("客户端发送完毕");
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.ip=properties.getProperty("ip");
        this.port=Integer.parseInt(properties.getProperty("port"));
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        try {
            this.logger=configuration.getLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //启动客户端
    public static void main(String[] args) {
        ClientImpl client = null;
        Gather gather;
        try {
            //网络模块调用采集模块
            ConfigurationImpl configuration = ConfigurationImpl.getInstance();
            gather = configuration.getGather();
            Collection<Environment> list = gather.gather();
            client =(ClientImpl) configuration.getClient();
            client.send(list);
        } catch (Exception e) {
            e.printStackTrace();
            assert client != null;
            client.logger.info(e.getMessage());
        }
    }

}
