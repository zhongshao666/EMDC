package com.briup.environment.test;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestJDBC {
    @Test
    public void test()throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connect = DriverManager.getConnection("jdbc:mysql://192.168.244.100:3306/datax?serverTimezone=UTC&characterEncoding=utf-8","ecjtu","123456");
        Statement stmt = connect.createStatement();
        String sql="select * from test ";
        ResultSet rs = stmt.executeQuery(sql);//"select username from usertable"
        // user 为你表的名称
        while (rs.next()) {
            System.out.println(rs.getString("id"));
            System.out.println(rs.getString("name"));
            System.out.println("\n");
        }
        connect.close();
    }
}
