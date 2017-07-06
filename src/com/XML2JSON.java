/**
 * 
 */
package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;


public class XML2JSON {
	
	public static JSON json(String path) {
		 //File file = new File(path);
		StringBuffer sb = new StringBuffer();
		try{
		// 创建一个读取流对象和文件相关联
		//FileReader fr = new FileReader(path);

		// 为了提高效率，加入了缓冲技术，将字符读取流对象作为参数传递给缓冲对象的构造函数。
		//BufferedReader br = new BufferedReader(fr);
		InputStreamReader read = new InputStreamReader(
                new FileInputStream(path),"UTF-8");//考虑到编码格式
		BufferedReader br = new BufferedReader(read);
		
		
		String line = null;
		// readLine()读取一个文本行。包含该行内容的字符串，不包含任何行终止符，如果已到达流末尾，则返回 null
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		}catch(Exception x){
			
		}
		
		String out = "";
		if(sb.toString().getBytes()[0]==63){//防止修改文件造成的BOM
			try {
			byte[] b = sb.toString().getBytes("UTF-8");
			out = new String(b,3,b.length-3,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			out = sb.toString();
		}
		
		XMLSerializer xmlSerializer = new XMLSerializer();
		 JSON json = xmlSerializer.read(out);
		return json;
	}
	
	
}