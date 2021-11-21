package com.briup.environment.test;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class Test {
	public static void test1(){
		/*Timestamp t = new Timestamp(1516323596029L);
		System.out.println(t);
		String f = new SimpleDateFormat("yyyy-MM-dd").format(1516323596029L);
		System.out.println(f);*/
	}
	public static void main(String[] args) throws Exception{
		System.out.println(new FileInputStream("src\\main\\resources\\radwtmp"));
	}
}
