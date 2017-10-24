package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 读取文件
 * 
 * @author Belie
 */
public class Fileop {
	public static String readTxtFile(String filePath) {
		try {
			StringBuffer sb = new StringBuffer();
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				read.close();
				return sb.toString();
			} else {
				System.out.println("找不到指定的文件:" + filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return "";
	}

	public static String readTxtFile(String filePath, int off) {
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStream in = new FileInputStream(file);
				InputStreamReader read = new InputStreamReader(in, encoding);// 考虑到编码格式
				int lenght = in.available();
				char[] ch = new char[lenght];
				read.read(ch, 0, lenght);
				/*
				 * for (char c : ch) { if(c=='\r'||c=='\n'){break;}
				 * if(c!='\0'){sb.append(c);} }
				 */
				read.close();
				if (off < lenght)
					return new String(ch).substring(off, lenght);
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 读取索引文件
	 * 
	 * @param filePath
	 * @param index
	 *            第几个索引 从0开始
	 * @return
	 */
	public static String firstFile(String filePath, int index) {
		String firstFile = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				File[] flist = file.listFiles();
				if (flist.length > index) {
					firstFile = flist[index].getName();
				}
			} else {
				System.out.println("找不到指定的文件夹");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return firstFile;
	}

	public static boolean moveFile(String oldPath, String newPath, String name) {
		try {
			File oldFile = new File(oldPath + name);
			File fnew = new File(newPath);
			oldFile.renameTo(fnew);
			boolean success = oldFile.renameTo(new File(fnew, System.currentTimeMillis() + oldFile.getName()));
			return success;
		} catch (Exception e) {
			System.out.println("移动文件出错");
			e.printStackTrace();
		}
		return false;
	}

	public static boolean writerTxtFile(String filePath, String content) {
		try {
			File file = new File(filePath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(content);
			writer.close();
			return true;
		} catch (Exception e) {

		}
		return false;
	}

}
