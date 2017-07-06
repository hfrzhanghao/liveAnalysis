package com;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.mongodb.ServerAddress;
/**
 * 配置文件读取
 * @author Belie
 */
public class CommStats {
	  public static final String callmongohost;//数据库地址
	  public static final String callmongodatabase;//数据库名称
	  
	  public static final String username;//数据库用户名
	  public static final String password;//数据库密码

	  static
	    { 
		   callmongohost = ResourceBundle.getBundle("c3p0").getString("callmongohost");
		   callmongodatabase = ResourceBundle.getBundle("c3p0").getString("callmongodatabase");
		   username = ResourceBundle.getBundle("c3p0").getString("username");
		   password = ResourceBundle.getBundle("c3p0").getString("password");
	    }
	   public static List<ServerAddress> serverListCall(String mongohost){
		   List<ServerAddress> serverList = new ArrayList<ServerAddress>();
		  String []mogonurl =  mongohost.split(",");
		  for (String host : mogonurl) {
			  String [] h = host.split(":");
			  ServerAddress serverAddress = null;
			try {
				serverAddress = new ServerAddress(h[0],Integer.parseInt(h[1]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			  serverList.add(serverAddress);
		}
		   return serverList;
	   }
	   
	   public static void main(String[] args) {
		   int aa =  (int) Math.ceil(0.2/0.2);
		System.out.println(aa);
	}
}
