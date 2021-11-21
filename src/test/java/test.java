import com.briup.environment.bean.Environment;
import com.briup.environment.client.ClientImpl;
import com.briup.environment.client.Gather;
import com.briup.environment.client.GatherImpl;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.DBStoreImpl;
import com.briup.environment.server.ServerImpl;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class test {

    @Test
    public void testGather(){
        try {
            Gather gather= new GatherImpl();
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/source.properties"));
            Properties properties=new Properties();
            properties.load(bufferedReader);
            gather.init(properties);
            gather.gather();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClient(){
        try {
            Gather gather= new GatherImpl();
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/main/resources/source.properties"));
            Properties properties=new Properties();
            properties.load(bufferedReader);
            gather.init(properties);
            ClientImpl client = new ClientImpl();
            client.init(properties) ;
            client.send(gather.gather());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testServer(){
        try {
            ServerImpl server = new ServerImpl();
            server.init(new Properties());
            server.reciver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDB(){
        try {
            DBStore dbStore= new DBStoreImpl();

            BufferedReader bufferedReader1 = new BufferedReader(new FileReader("src/main/resources/source.properties"));
            Properties properties1=new Properties();
            properties1.load(bufferedReader1);

            dbStore.init(properties1);
            GatherImpl gather = new GatherImpl();
            gather.init(properties1);
            dbStore.saveDb(gather.gather());
            //2020-09-21 20:14:34.99
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSql(){
        Environment e = new Environment("温度","100","101","2","温度传感器",1,"3",1,66f,new Timestamp(new Date().getTime()));
        StringBuilder sql=new StringBuilder();
        String day="16";
        sql=sql.append("insert into ").append(day).append(" (`name`,srcId,dstId,sersorAddress,`count`,cmd,status,`data`,gather_date) values(").append("'").append(e.getName()).append("'")
                .append(",").append("'").append(e.getSrcId()).append("'").append(",").append("'").append(e.getDstId()).append("'").append(",").append("'").append(e.getSersorAddress()).append("'").append(",").append(e.getCount()).append(",")
                .append("'").append(e.getCmd()).append("'").append(",").append(e.getStatus()).append(",")
                .append(e.getData()).append(",").append("'").append(e.getGather_date()).append("'").append(")");
        System.out.println(sql);
    }

    @Test
    public void testDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String s="1970-1-1 0:0:0";
        try {
            Date d1=sdf.parse(s);
            System.out.println(d1);
            System.out.println(sdf.format(d1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp timestamp = new Timestamp(new Date().getTime());

        String date=sdf.format(timestamp);
        System.out.println("timestamp = " + timestamp);
        System.out.println("date = " + date);
    }

    @Test
    public void cast(){
        String day="01";
        int dayNum=Integer.parseInt(day);
        System.out.println(dayNum);
    }
}
