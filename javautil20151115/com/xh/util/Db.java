package com.xh.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
/**
 * 
 * @author adriftor
 * 用于SQLSERVER数据表结构，转换为MYSQL表结构。
 * 增加了注释的处理
 * 增加了字段备注里数据字典的引用
 *
 */
public class Db {

	public static void main(String[] argv) {
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("d:/cloud20111103.dmp")));
			RecordSet<RightTable> rsTable = new RecordSet<RightTable>();
			line = br.readLine();
			String tableName = null;
			Record rdTableName = new Record();
			RightTable rd = new RightTable();
			boolean primaryKeyFlag = false;
			while (line != null) {
				String lineBak = line;
				line = line.trim();
				if (line.toLowerCase().startsWith("create table")) {
					tableName = line.substring("CREATE TABLE ".length()).trim();
					tableName = tableName.substring(1, tableName.lastIndexOf("`")).trim();
					rd = new RightTable();
					rsTable.addRecord(tableName,rd);
					line = "";
					primaryKeyFlag = false;
				}
				
				if (line.length() > 0 && tableName != null && line.startsWith("`") && line.indexOf(" COMMENT ")>0) {
					String columnName = line.substring(1, line.lastIndexOf("`"));
					String comment = line.substring(line.indexOf(" COMMENT "));
					rd.put(columnName,comment);
				}
				else if (line.length() > 0 && tableName != null && line.startsWith(")") && line.indexOf(" COMMENT=")>0 && line.indexOf("ENGINE")>0) {
					String comment = line.substring(line.indexOf(" COMMENT="));
					rdTableName.put(tableName,comment);
					System.out.println(lineBak);
				}
				line = br.readLine();
			}
			br.close();
			
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("d:/platform20111103_out.dmp")));
			br = new BufferedReader(new FileReader(new File("d:/platform20111103.dmp")));
			line = br.readLine();
			tableName = null;
			while (line != null) {
				String lineBak = line;
				line = line.trim();
				if (line.toLowerCase().startsWith("create table")) {
					tableName = line.substring("CREATE TABLE ".length()).trim();
					tableName = tableName.substring(1, tableName.lastIndexOf("`")).trim();
				}
				
				if (line.length() > 0 && tableName != null && line.startsWith("`") && line.indexOf(" COMMENT=")<0) {
					if (rsTable.containsKey(tableName)) {
						String columnName = line.substring(1, line.lastIndexOf("`"));
						rd = rsTable.getRecord(tableName);
						if (rd.containsKey(columnName)) {
							lineBak = lineBak.substring(0,lineBak.lastIndexOf(","))+rd.getString(columnName);
							
						}
					}
				}
				else if (line.length() > 0 && tableName != null && line.startsWith(")") && line.indexOf(" COMMENT ")<0 && line.indexOf("ENGINE")>0) {
					if (rdTableName.containsKey(tableName)) {

						lineBak = lineBak.substring(0,lineBak.lastIndexOf(";"))+rdTableName.getString(tableName);
						
					}
				}
				bw.write(lineBak);
				bw.newLine();
				line = br.readLine();
			}
			rdTableName.d();
			br.close();
			bw.close();
			
		}
		catch (Exception ex) {
			System.out.println(line);
			ex.printStackTrace();
		}
	}
	public static void main2(String[] argv) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("d:/数据库ddl.txt")));
			RecordSet<RightTable> rsTable = new RecordSet<RightTable>();
			String line = br.readLine();
			String tableName = null;
			RightTable rd = new RightTable();
			boolean primaryKeyFlag = false;
			while (line != null) {
				String lineBak = line;
				line = line.trim();
				if (line.toLowerCase().startsWith("create table")) {
					tableName = line.substring("CREATE TABLE ".length());
					tableName = tableName.substring(0, tableName.length() -1).trim();
					rd = new RightTable();
					rsTable.addRecord(tableName,rd);
					line = "";
					primaryKeyFlag = false;
				}
				if (primaryKeyFlag) {
					if (line.length() > 0 &&( ! line.toLowerCase().startsWith(")"))) {
						String primaryKey = rd.getPrimaryKey();
						if (primaryKey == null) {
							rd.setPrimaryKey(line);
						}
						else {
							rd.setPrimaryKey(primaryKey+line);
						}
					}
				}
				else {
					if (line.toLowerCase().startsWith("primary key")) {
						primaryKeyFlag = true;
						line = "";
					}
				}
				if (line.length() > 0 && tableName != null && (! primaryKeyFlag)) {
					String columnName = line.substring(0, line.indexOf(" "));
					rd.put(columnName,lineBak);
				}
				line = br.readLine();
			}
			br.close();
			
			
			int nameInc = 0;
			br = new BufferedReader(new FileReader(new File("d:/数据状态.txt")));
			line = br.readLine();
			String dicName = null;
			boolean dicStartFlag = false;
			RecordSet rsDic = new RecordSet();
			while (line != null) {
				line = line.trim();
				if (line.endsWith(")") && line.indexOf("(")>0) {
					dicName = line.substring(0,line.indexOf("("));
					
					line = "";
					dicStartFlag = false;
					Record rdDic = new Record();
					rdDic.pOutputRealKeyFlag(true);
					rsDic.addRecord(dicName,rdDic);
				}
				if (dicName != null && line.length() > 0 && ( ! dicStartFlag)) {
					if (line.startsWith("常量值")) {
						dicStartFlag = true;
						line = "";
					}
				}
				if (line.length() > 0 && dicName != null && line.indexOf("\t") > 0 && dicStartFlag) {
					String value = line.substring(0, line.indexOf("\t"));
					String name = line.substring(line.lastIndexOf("\t")).trim().replaceAll("'", "''"); 
					Record rdDic = rsDic.getRecord(dicName);
					
					rdDic.put(value,name);
				}
				line = br.readLine();
			}
			br.close();
			
//			rsDic.d();
			Record rdTableDesc = new Record();
			br = new BufferedReader(new FileReader(new File("d:/中文字段信息.txt")));
			line = br.readLine();
			rd = new RightTable();
			tableName = null;
			while (line != null) {
				line = line.trim();
				if (line.endsWith(")") && line.indexOf("(")>0) {
					tableName = line.substring(line.indexOf("(")+1,line.length() - 1);
					
					rdTableDesc.put(tableName,line.substring(0,line.indexOf("(")));
					
					line = "";
				}
				
				if (line.length() > 0 && tableName != null && line.indexOf("\t") > 0) {
					String columnName = line.substring(0, line.indexOf("\t"));
					String comment = line.substring(line.lastIndexOf("\t")).trim(); 
					
					if (rsTable.containsKey(tableName)) {
						rd = rsTable.getRecord(tableName);
						if (rd.containsKey(columnName)) {
							String columnInfo = rd.getString(columnName);
							columnInfo = columnInfo.substring(0, columnInfo.lastIndexOf(","));
							dicName = null;
							if (comment.indexOf("，参考") > 0) {
								dicName = comment.substring(0, comment.indexOf("，"));
								
							}
							if (dicName != null && rsDic.containsKey(dicName)) {
								comment+= " (" + rsDic.getRecord(dicName).toStringCh(",")+")";
										
							}
							columnInfo = columnInfo + " COMMENT '"+comment+"',";
							rd.put(columnName,columnInfo);
						}
					}
				}
				line = br.readLine();
			}
			br.close();
			
			
			//rsTable.d();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("d:/最终DDL.txt")));
			int size = rsTable.size();
			for (int i = 0;i<size;i++) {
				rd = rsTable.getRecord(i);
				tableName = rsTable.getRecordName(i);
				bw.write("CREATE TABLE "+tableName+"(");
				bw.newLine();
				for (int j = 0;j<rd.size();j++) {
					String columnInfo = rd.getString(j);
					bw.write(columnInfo);
					bw.newLine();
				}
				bw.write("PRIMARY KEY (");
				String primaryKey = rd.getPrimaryKey();
				if (primaryKey == null) {
					rd.d();
					System.out.println(tableName);
				}
				bw.write(primaryKey+"");
				bw.write(")) comment '"+rdTableDesc.getString(tableName,"")+"';");
				bw.newLine();
				bw.newLine();
				bw.flush();
			}
			
			bw.close();
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
