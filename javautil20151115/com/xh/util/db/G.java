package com.xh.util.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.util.AppException;
import com.xh.util.CommonUtil;
import com.xh.util.Record;
import com.xh.util.RecordSet;
import com.xh.util.RecordTable.TableInfo;




/**
 * 根据数据库表，生成表的字段名列表JAVA类<br>
 * 要定制生成字段的数据库表，请修改本方法<br>
 * 总共生成3个JAVA类文件，字段检索方式各不相同，文件名/类名名为：d.java,d2.java,d3.java<br>
 * d:java:字段按字段名检索，对于多个表的中字段名相同的字段，会在备注里列出包含该字段的表列表、字段类型列表等信息,内类JdbcType包含字段的jdbc数据类型<br>
 * d2.java:字段按dd表名.字段名、表名_字段名两种格式检索<br>
 * d3.java:字段按dd表名.字段名、字段名_表名两种格式检索<br>
 * <br>
 * 表名可能和数据库不相同，一般会去掉下划线"_",去掉通用的表名前缀，如去掉前缀"t_"<br>
 * 生成过程中，会检查相同字段的数据类型、数据长度、null、唯一性是否相同，不同会在控制台输出警告信息<br>
 * 
 * @author adriftor
 *
 */
public class G {
	public static final Log logger = LogFactory.getLog(G.class);
	/**
	 * 表名验证和裁剪
	 * 对于不需要生成JAVA的表，返回null
	 * @param tableName 数据库表名
	 * @return 作为java成员的表的类名，一般操作是去掉统一的开头，去掉下划线，如 数据库表名为t_user_dept,返回的就是userdept。如果返回为null，则表示此表不用生成java
	 * @throws Exception
	 */
	public String filterTableName(String tableName)  {
		/**
		 * 表名过滤和输出到JAVA里的表名裁剪处理
		 */
		tableName = tableName.toLowerCase();
		if ( ! tableName.startsWith("sch_") ) {//不是以XXX开头的表
			if ( ! tableName.equals("a_sd_city") && ! tableName.equals("c_cmms_grid_type")) {//不是以XXX开头的表,但仍需要生成JAVA信息的表
				return null;
			}
		}
		
		//表名的最后一个字符为数字，一般都不是正式表
		char charEnd = tableName.charAt(tableName.length()-1);//表名的最后一个字符
		if (charEnd>='0' && charEnd <'9') {
			return null;
		}
		
		//以_bak/_tmp/_temp结尾的表，一般都不是正式表
		if (tableName.endsWith("_bak") || tableName.endsWith("_tmp") || tableName.endsWith("_temp")) {
			return null;
		}
		
		
		//去掉下划线，去掉通用的前缀pfc
		String tName = CommonUtil.replace(tableName, "_", "");
		if (tName.startsWith("pfc")) {
			tName = tName.substring(3);
		}
		return tName;
		
	}
	/**
	 * 根据数据库表，生成表的字段名列表JAVA类<br>
	 * 要定制生成字段的数据库表，请修改本方法<br>
	 * 总共生成3个JAVA类文件，字段检索方式各不相同，文件名/类名名为：d.java,d2.java,d3.java<br>
	 * d:java:字段按字段名检索，对于多个表的中字段名相同的字段，会在备注里列出包含该字段的表列表、字段类型列表等信息,内类JdbcType包含字段的jdbc数据类型<br>
	 * d2.java:字段按dd表名.字段名、表名_字段名两种格式检索<br>
	 * d3.java:字段按dd表名.字段名、字段名_表名两种格式检索<br>
	 * <br>
	 * 表名可能和数据库不相同，一般会去掉下划线"_",去掉通用的表名前缀，如去掉前缀"t_"<br>
	 * 生成过程中，会检查相同字段的数据类型、数据长度、null、唯一性是否相同，不同会在控制台输出警告信息<br>
	 *
	 */
	public void genFieldFileByDb() {
		Connection innerCon = this.getConnection();
		
		String className = "d";//生成的类名
		String filePath = "d:/"+className;//生成的java文件存放位置及默认的java名
		String packageName = "package com.fy.util";//包名
		
		
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		java.io.BufferedWriter bw = null;
		java.io.BufferedWriter bw2 = null;
		java.io.BufferedWriter bw3 = null;
		try {
			bw = new java.io.BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(filePath+".java"),"UTF-8"));
			bw2 = new java.io.BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(filePath+"2.java"),"UTF-8"));
			bw3 = new java.io.BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(filePath+"3.java"),"UTF-8"));
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			
			StringBuffer sb3 = new StringBuffer();
			StringBuffer sbStaticTable = new StringBuffer();
			sbStaticTable.append("\nstatic{\n");
			
			StringBuffer sbStaticColumn = new StringBuffer();//d.java 
			
			sbStaticColumn.append("/**");
			sbStaticColumn.append("\n*数据库字段名和数据类型映射关系，key为数据库字段名，value为java.sql.Types值，生成时间："+CommonUtil.dateToStr()+"<br>");
			sbStaticColumn.append("\n*/\n");
			sbStaticColumn.append("\npublic static Record rdJdbcType = new Record();\n");
			sbStaticColumn.append("\nstatic{\n");
			
			java.sql.DatabaseMetaData dm = innerCon.getMetaData();
			ResultSet resultSet = dm.getTables(null, null, null, new String[]{"TABLE"});
			RecordSet rsSet = new RecordSet(resultSet);

			RecordSet rsY = new RecordSet();
			
			sb2.append(packageName+";\n/**\n字段按dd表名.字段名、表名_字段名两种格式检索<br>生成时间："+CommonUtil.dateToStr()+"\n*/\npublic class "+className+"2 {\n");
			sb3.append(packageName+";\n/**\n字段按dd表名.字段名、字段名_表名两种格式检索<br>生成时间："+CommonUtil.dateToStr()+"\n*/public class "+className+"3 {\n");
			sb.append(packageName+";\n/**\n字段按字段名检索，对于多个表的中字段名相同的字段，会在备注里列出包含该字段的表列表、字段类型列表等信息<br>生成时间："+CommonUtil.dateToStr()+"\n*/\npublic class "+className+" {\n");

			String validTables = "";
			int validTableCount = 0;
			//rsSet.d();
			for (int k = 0;k<rsSet.size();k++) {
				Record rdSet = rsSet.r(k);
				String tableName = rdSet.getString("table_name");
				
				/**
				 * 表名过滤和输出到JAVA里的表名裁剪处理
				 */
				String tName = this.filterTableName(tableName);
				if (tName == null) {
					continue;
				}

				
				validTables += "," + tableName;
				validTableCount++;
				if (validTableCount%4 == 0) {
					validTables += "<br>";
				}
				
				
				//主键
				ResultSet rsetPrimary = dm.getPrimaryKeys(null, null, tableName);
				RecordSet rsPrimaryKey = new RecordSet(rsetPrimary);
				Record rdPrimaryKey = new Record();
				for (int i = 0; i < rsPrimaryKey.size(); i++) {
					String primaryKeyColumnName = rsPrimaryKey.r(i).getString("column_name");
					rdPrimaryKey.put(primaryKeyColumnName,primaryKeyColumnName);

				}
				
				//唯一索引
				Record rdIndex = new Record();
				ResultSet resultSetIndex = null;
				try {
					resultSetIndex = dm.getIndexInfo(null, null, tableName, true,true);
				}
				catch (Exception ex) {
					logger.error(tableName+"不存在！");
					continue;
					
				}
				RecordSet rsIndex = new RecordSet(resultSetIndex);
				for (int i = 0; i < rsIndex.size(); i++) {
					String indexColumnName = rsIndex.r(i).getString("column_name");
					rdIndex.put(indexColumnName,indexColumnName);

				}
				
				//字段
				ResultSet resultSetColumn = dm.getColumns(null, null, tableName, null);
				RecordSet rsResultSetColumn = new RecordSet(resultSetColumn);
				
				sbStaticTable.append("\nrdDbTables.put(\""+tableName+"\",\""+tName+"\");\n");	
				
				
				sb2.append("/**");
				sb2.append("\n*表名："+tableName+"<br>");
				sb2.append("\n*表说明："+rdSet.getString("remarks")+"<br>");
				sb2.append("\n*主键："+rdPrimaryKey.toStringOfValue(",")+"<br>");
				sb2.append("\n*其他：<br>"+rdSet.toStringCh("<br>")+"<br>");
				sb2.append("\n*/\n");
				sb2.append("public static final class dd"+tName.toUpperCase()+"{\n");
				
				sb2.append("/**");
				sb2.append("\n*表名："+tableName+"<br>");
				sb2.append("\n*表说明："+rdSet.getString("remarks")+"<br>");
				sb2.append("\n*格式：数据库字段名=JAVA字段变量名<br>");
				sb2.append("\n*主键："+rdPrimaryKey.toStringOfValue(",")+"<br>");
				sb2.append("\n*字段："+rsResultSetColumn.getString("column_name").toStringOfValue(",")+"<br>");
				sb2.append("\n*其他：<br>"+rdSet.toStringCh("<br>")+"<br>");
				sb2.append("\n*/\n");
				sb2.append("\npublic static Record rdDb = new Record();\n");
				sbStaticTable.append("\nrdDbTableColumn.put(\""+tableName+"\",dd"+tName.toUpperCase()+".rdDb);\n");	

				
				sb3.append("/**");
				sb3.append("\n*表名："+tableName+"<br>");
				sb3.append("\n*表说明："+rdSet.getString("remarks")+"<br>");
				sb3.append("\n*主键："+rdPrimaryKey.toStringOfValue(",")+"<br>");
				sb3.append("\n*其他：<br>"+rdSet.toStringCh("<br>")+"<br>");
				sb3.append("\n*/\n");
				
				//d.java和d2.java数据库表对应的类即类里的字段
				StringBuffer sbStaticInnerTable = new StringBuffer();
				sbStaticInnerTable.append("\nstatic {\n");
				sb3.append("public static final class dd"+tName.toUpperCase()+"{\n");
				for (int i = 0;i<rsResultSetColumn.size();i++) {
					
					Record rdColumn = rsResultSetColumn.r(i);
					String columnName = rdColumn.getString("column_name");				
					
					String cName = CommonUtil.replace(columnName, "_", "");
					
					rdColumn.put("is_primary_key",rdPrimaryKey.containsKey(columnName));
					rdColumn.put("is_unique",rdIndex.containsKey(columnName));
					
					sb2.append("/**");
					sb2.append("\n*列名："+columnName+"<br>");
					sb2.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
					sb2.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
					sb2.append("\n*是否主键："+rdColumn.getString("is_primary_key")+"<br>");
					sb2.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
					sb2.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
					sb2.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
					sb2.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
					sb2.append("\n*/\n");
					sb2.append("public static final String "+cName.toLowerCase()+" = "+"\""+columnName.toLowerCase()+"\";\n");	
					
					sbStaticInnerTable.append("\nrdDb.put(\""+columnName+"\",\""+cName+"\");\n");	
					
					sb3.append("/**");
					sb3.append("\n*列名："+columnName+"<br>");
					sb3.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
					sb3.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
					sb3.append("\n*是否主键："+rdColumn.getString("is_primary_key")+"<br>");
					sb3.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
					sb3.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
					sb3.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
					sb3.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
					sb3.append("\n*/\n");
					sb3.append("public static final String "+cName.toLowerCase()+" = "+"\""+columnName.toLowerCase()+"\";\n");	
				
					if (rsY.containsKey(columnName)) {
						Record rdD = rsY.r(columnName);
						int count = CommonUtil.getCount(rdD.getString("table_name"), ",");
						count += CommonUtil.getCount(rdD.getString("table_name"), "<br>");
						String strBr = ",";
						if ((count+1)%3 == 0) {
							strBr = "<br>";
							//logger.debug(count + ","+(count+1)%3);
						}
						rdD.put("table_name",rdD.getString("table_name")+strBr+rdColumn.getString("table_name"));
						rdD.put("type_name",rdD.getString("type_name")+","+rdColumn.getString("type_name"));
						rdD.put("column_size",rdD.getString("column_size")+","+rdColumn.getString("column_size"));
						rdD.put("char_octet_length",rdD.getString("char_octet_length")+","+rdColumn.getString("char_octet_length"));
						rdD.put("is_primary_key",rdD.getString("is_primary_key")+","+rdColumn.getString("is_primary_key"));
						rdD.put("is_nullable",rdD.getString("is_nullable")+","+rdColumn.getString("is_nullable"));
						rdD.put("is_unique",rdD.getString("is_unique")+","+rdColumn.getString("is_unique"));
						
						
						/**
						 * 判断字段是否统一
						 * 判断的有：数据类型是否一直
						 * 			是否允许null
						 * 			唯一值
						 * 			长度是否一致
						 * 			
						 */
						 if (!rdD.getString("data_type").equals(rdColumn.getString("data_type"))) {
							 logger.warn("字段"+columnName+"的数据类型不一致，数据类型：表"+rdD.getString("table_name")+"字段"+columnName+"数据类型为："+rdD.getString("type_name")+" 长度"+rdD.getString("char_octet_length")+"，表"+rdColumn.getString("table_name")+"字段"+columnName+"数据类型为："+rdColumn.getString("type_name")+" 长度"+rdColumn.getString("char_octet_length"));
						 }
						 
						 if (!rdD.getString("nullable").equals(rdColumn.getString("nullable"))) {
							 logger.warn("字段"+columnName+"的null值允许类型不一致，数据类型：表"+rdD.getString("table_name")+"字段"+columnName+"："+rdD.getString("is_nullable")+"，表"+rdColumn.getString("table_name")+"字段"+columnName+"："+rdColumn.getString("is_nullable"));
						 }
						 
						 if (!rdD.getString("char_octet_length2").equals(rdColumn.getString("char_octet_length"))) {
							 logger.warn("字段"+columnName+"的长度不一致，数据类型：表"+rdD.getString("table_name")+"字段"+columnName+"："+rdD.getString("char_octet_length")+"，表"+rdColumn.getString("table_name")+"字段"+columnName+"："+rdColumn.getString("char_octet_length"));
						 }
						 
//						 if (!rdD.getString("is_unique2").equals(rdColumn.getString("is_unique"))) {
//							 logger.warn("字段"+columnName+"的唯一性不一致，数据类型：表"+rdD.getString("table_name")+"字段"+columnName+"："+rdD.getString("is_unique")+"，表"+rdColumn.getString("table_name")+"字段"+columnName+"："+rdColumn.getString("is_unique"));
//						 }
						
					}
					else {
						rsY.addRecord(columnName,rdColumn);
						rdColumn.put("char_octet_length2",rdColumn.getString("char_octet_length"));
						rdColumn.put("is_unique2",rdColumn.getString("is_unique"));
						//rdColumn.put("type_name2",rdColumn.getString("type_name"));
						//rdColumn.put("is_nullable2",rdColumn.getString("is_nullable"));
						
					}
					
				}
				sbStaticInnerTable.append("\n}\n");
				sb2.append(sbStaticInnerTable.toString());
				
				sb2.append("\n}\n");
				sb3.append("\n}\n");
				
				
				
				//d3.java和d2.java字段
				for (int i = 0;i<rsResultSetColumn.size();i++) {
					Record rdColumn = rsResultSetColumn.r(i);
					String columnName = rdColumn.getString("column_name");
					String cName = CommonUtil.replace(columnName, "_", "");
					
					sb2.append("/**");
					sb2.append("\n*列名："+columnName+"<br>");
					sb2.append("\n*表名："+tableName+"<br>");
					sb2.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
					sb2.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
					sb2.append("\n*是否主键："+rdColumn.getString("is_primary_key")+"<br>");
					sb2.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
					sb2.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
					sb2.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
					sb2.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
					sb2.append("\n*/\n");
					sb2.append("public static final String "+tName.toLowerCase()+"_"+cName.toLowerCase()+" = "+"\""+columnName.toLowerCase()+"\";\n");	
					
					sb3.append("/**");
					sb3.append("\n*列名："+columnName+"<br>");
					sb3.append("\n*表名："+tableName+"<br>");
					sb3.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
					sb3.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
					sb3.append("\n*是否主键："+rdColumn.getString("is_primary_key")+"<br>");
					sb3.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
					sb3.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
					sb3.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
					sb3.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
					sb3.append("\n*/\n");
					sb3.append("public static final String "+cName.toLowerCase()+"_"+tName.toLowerCase()+" = "+"\""+columnName.toLowerCase()+"\";\n");
				}
				
				
			}
			
			sbStaticTable.append("\n}\n");
			if (validTables.length()>0) {
				validTables = validTables.substring(1);
			}
			sb2.append("/**");
			sb2.append("\n*包含本类所包含的数据库表，格式：数据库表名=JAVA变量表名。生成时间："+CommonUtil.dateToStr()+"<br>");
			sb2.append("\n*表名："+validTables+"<br>");
			sb2.append("\n*/");
			sb2.append("\npublic static Record rdDbTables = new Record();\n");	
			
			sb2.append("/**");
			sb2.append("\n*包含所有表的所有字段。格式：数据库表名=Record对象，Record对象格式：数据库字段名=字段名的java变量名<br>");
			sb2.append("\n*表名："+validTables+"<br>");
			sb2.append("\n*/");
			sb2.append("\npublic static Record rdDbTableColumn = new Record();\n");	
			sb2.append(sbStaticTable.toString());
			
			
			
			//d.java字段
			for (int i = 0;i<rsY.size();i++) {
				Record rdColumn = rsY.r(i);
				String columnName = rdColumn.getString("column_name");
				String cName = CommonUtil.replace(columnName, "_", "");
				
				boolean isSecond = false;
				//检查字段简写和真实字段是否存在冲突
				if ( ! cName.equalsIgnoreCase(columnName) && rsY.containsKey(cName)) {
					Record rdExistColumn = rsY.r(cName);
					//throw new AppException("字段简写冲突："+rdColumn.getString("table_name")+"表列:"+columnName+"的简写列名"+cName+"，存在真实字段名："+rdExistColumn.getString("table_name")+"表");
					isSecond = true;
				}
				
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*表名："+rdColumn.getString("table_name")+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				sb.append("\n*是否主键："+rdColumn.getString("is_primary_key")+"<br>");
				sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
				sb.append("public static final String "+cName.toLowerCase() +" = "+"\""+columnName.toLowerCase()+"\";\n");	
				
				
			
			}
			
			//d.java里的JdbcType字段的jdbc数据库类型。字段的jdbc数据类型，对应于java.sql.Types值
			sb.append("\npublic static final class JdbcType {\n");	
			for (int i = 0;i<rsY.size();i++) {
				Record rdColumn = rsY.r(i);
				String columnName = rdColumn.getString("column_name");
				String cName = CommonUtil.replace(columnName, "_", "");
				
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*表名："+rdColumn.getString("table_name")+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				sb.append("\n*是否主键："+rdColumn.getString("is_primary_key")+"<br>");
				sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
				sb.append("public static final int "+cName.toLowerCase()+"_jdbcType = "+rdColumn.getString("data_type")+";\n");	
				
//				sbStaticColumn.append("\nrdJdbcType.put(\""+columnName+"\",\""+rdColumn.getString("data_type")+"\");\n");
				sbStaticColumn.append("\nrdJdbcType.put(\""+columnName+"\",\""+rdColumn.getString("remarks")+"\");\n");
			
			}
			sb.append("\n}\n");
			
			sbStaticColumn.append("\n}\n");
			sb.append(sbStaticColumn.toString());	
			
			
			sb2.append("}\n");
			sb3.append("}\n");
			sb.append("}\n");
			
			
			bw.write(sb.toString());
			bw2.write(sb2.toString());
			bw3.write(sb3.toString());
			
			
			
			

		} catch (Exception ex) {
			throw new AppException("获取元数据错误", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (innerCon != null) {
					innerCon.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
					
					bw2.flush();
					bw2.close();
					
					bw3.flush();
					bw3.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			
			
			
		}
	}
	
	private static boolean isNumberByJdbcType(int type) {
		switch (type) {
		
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.DOUBLE:	
		case Types.FLOAT:	
		case Types.REAL:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
			return true;

		
		}
		return false;
	}
	private static boolean isStringByJdbcType(int type) {
		switch(type) {
		
        case Types.CLOB:

        case Types.LONGNVARCHAR:

       
        case Types.LONGVARCHAR:

        
        case Types.CHAR:

        case Types.VARCHAR:

        case Types.NCHAR:

        case Types.NVARCHAR:
        	return true;


		}
		return false;
	}
	public void alter(String tableName,String className,Record rdDataField,java.io.Writer wr) {
		if (rdDataField == null) {
			rdDataField = new Record();
		}
		tableName = tableName.toUpperCase();
		Connection innerCon = this.getConnection();

		String filePath = "d:/"+className+"";//生成的java文件存放位置及默认的java名
		String packageName = "package com.oristartech.cms.v1.moviePlan.basic.showPlan.domain;";//"package com.fy.util";//包名
		
		
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		java.io.BufferedWriter bw = null;
		try {
			java.sql.DatabaseMetaData dm = innerCon.getMetaData();
			//主键
			
			
			bw = new java.io.BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(filePath+".java"),"UTF-8"));
			StringBuffer sb = new StringBuffer();
			sb.append(packageName+"\n");
			sb.append("import com.oristartech.core.businessmodule.JsonObject;\n");
			sb.append("import com.oristartech.core.businessmodule.JsonProperty;\n");
			sb.append("import com.oristartech.cms.v1.moviePlan.basic.showPlan.util.Record;\n");
			sb.append("import java.util.Date;\n");
			sb.append("import java.sql.Types;\n");
			sb.append("import java.math.BigDecimal;\n");
			
			
			
			sb.append("/**");
			sb.append("\n*表名："+tableName+"<br>");
			//sb.append("\n*表说明："+rdSet.getString("remarks")+"<br>");
			//sb.append("\n*主键："+rdPrimaryKey.toStringOfValue(",")+"<br>");
			//sb.append("\n*其他：<br>"+rdSet.toStringCh("<br>")+"<br>");
			sb.append("\n*/\n");
			sb.append("@JsonObject\n");
			sb.append("public class "+className+" extends Record {\n");

			
			sb.append("public "+className+"() {\n");
			sb.append("super();\n");
			sb.append("}\n");
			sb.append("\n");
			sb.append("public "+className+"(Record rd) {\n");
			sb.append("this.merge(rd);\n");
			sb.append("}\n");
			
			
			Record rdDbField = new Record();//数据库表字段列表
			//字段
			ResultSet resultSetColumn = dm.getColumns(null, null, tableName, null);
			RecordSet rsResultSetColumn = new RecordSet(resultSetColumn).setRecordName("column_name");
//			
//			if ( ! rsResultSetColumn.containsKey("UID")  && ! rsResultSetColumn.containsKey("UID_SYNC")) {
//				String alter ="alter table  "+tableName.toUpperCase()+" ADD COLUMN UID_SYNC CHAR(36) NOT NULL  DEFAULT '' COMMENT '同步UUID';";
//				System.out.println(alter);
//			}
//			if (rsResultSetColumn.containsKey("UID_SYNC")) {
//				String alter ="update   "+tableName.toUpperCase()+" set UID_SYNC=uuid();";
//				System.out.println(alter);
//			}
			if (rsResultSetColumn.notContainsKey("UID_SYNC") && rsResultSetColumn.notContainsKey("UID")) {
				String alter ="select uid_sync from  "+tableName.toUpperCase()+";";
				System.out.println(alter);
			}
//			
//			if ( ! rsResultSetColumn.containsKey("UPDATE_TIME")) {
//				if (tableName.toLowerCase().indexOf("history") >=0) {
//					String alter ="alter table  "+tableName.toUpperCase()+" ADD COLUMN UPDATE_TIME timestamp  DEFAULT CURRENT_TIMESTAMP  COMMENT '修改时间';";
//					System.out.println(alter);
//				}
//				else {
//					String alter ="alter table  "+tableName.toUpperCase()+" ADD COLUMN UPDATE_TIME timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间';";
//					System.out.println(alter);
//				}
//				
//			}
////			
//			if ( ! rsResultSetColumn.containsKey("SYNC_TIME")) {
//				String alter ="alter table  "+tableName.toUpperCase()+" ADD COLUMN SYNC_TIME datetime  COMMENT '同步时间';";
//				System.out.println(alter);
//			}

		} catch (Exception ex) {
			throw new AppException("获取元数据错误", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (innerCon != null) {
					innerCon.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();

				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			
			
		}
		
	}
	
	/**
	 * 根据数据库列名，生成对应的java属性名，如user_name-->userName
	 * @param columnName 数据库列名
	 * @return
	 */
	public static String genProperyByColumn2(String columnName) {
		String propertyName = "";
		while (columnName.indexOf("_") > 0) {
			String strTmp =  columnName.substring(0,columnName.indexOf("_"));
			propertyName = propertyName + strTmp;
			columnName = columnName.substring(columnName.indexOf("_")+1);
			if (columnName.length() > 0) {
				columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
			}
			else {
				columnName = columnName + "_";
			}
			
			
			
		}
		propertyName = propertyName + columnName;
		return propertyName;
	}
	 public static String toCamelCase(String getString) {
	        if (getString == null) {
	            return null;
	        }
	        if (getString.indexOf("_") < 0) {
	        	return getString.substring(0,1).toLowerCase()+getString.substring(1);
	        }
	        

	        getString = getString.toLowerCase();

	        StringBuilder sb = new StringBuilder(getString.length());
	        boolean upperCase = false;
	        for (int i = 0; i < getString.length(); i++) {
	            char c = getString.charAt(i);

	            if (c == '_') {
	                upperCase = true;
	            } else if (upperCase) {
	                sb.append(Character.toUpperCase(c));
	                upperCase = false;
	            } else {
	                sb.append(c);
	            }
	        }

	        return sb.toString();
	    }
	static Record rdMybatisMethod = new Record();
	public void genPojoMap(String tableName,String className,Record rdDataField,java.io.Writer wr) {
		if (rdDataField == null) {
			rdDataField = new Record();
		}
		tableName = tableName.toUpperCase();
		Connection innerCon = this.getConnection();

		String filePath = "d:/javafy/"+className+"";//生成的java文件存放位置及默认的java名
		String packageName = "package com.xh.system.model;";//"package com.fy.util";//包名
		
		
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		java.io.BufferedWriter bw = null;
		
		try {
			java.sql.DatabaseMetaData dm = innerCon.getMetaData();
			//主键
			ResultSet rsetPrimary = dm.getPrimaryKeys(null, null, tableName);
			RecordSet rsPrimaryKey = new RecordSet(rsetPrimary);
			Record rdPrimaryKey = new Record();
			for (int i = 0; i < rsPrimaryKey.size(); i++) {
				String primaryKeyColumnName = rsPrimaryKey.r(i).getString("column_name");
				rdPrimaryKey.put(primaryKeyColumnName,primaryKeyColumnName);

			}
			
//			//唯一索引
			Record rdIndex = new Record();
			ResultSet resultSetIndex = null;
			resultSetIndex = dm.getIndexInfo(null, null, tableName, true,true);
			RecordSet rsIndex = new RecordSet(resultSetIndex);
			for (int i = 0; i < rsIndex.size(); i++) {
				String indexColumnName = rsIndex.r(i).getString("column_name");
				rdIndex.put(indexColumnName,indexColumnName);

			}


			
			bw = new java.io.BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(filePath+".java"),"UTF-8"));
			StringBuffer sb = new StringBuffer();
			sb.append(packageName+"\n");
			sb.append("import com.xh.util.*;\n");
			sb.append("import java.util.Date;\n");
			sb.append("import java.util.Map;\n");
			sb.append("import java.sql.Types;\n");
			sb.append("import java.math.BigDecimal;\n");
			
			
			
			sb.append("/**");
			sb.append("\n*表名："+tableName+"<br>");
			//sb.append("\n*表说明："+rdSet.getString("remarks")+"<br>");
			//sb.append("\n*主键："+rdPrimaryKey.toStringOfValue(",")+"<br>");
			//sb.append("\n*其他：<br>"+rdSet.toStringCh("<br>")+"<br>");
			sb.append("\n*/\n");
			sb.append("public class "+className+" extends RecordTable {\n");

			sb.append("public static final String TABLE_NAME=\""+tableName+"\";\n");
			sb.append("public static final String PRIMARY_KEY=\""+rdPrimaryKey.getString(0)+"\";\n");
			
			sb.append("public "+className+"() {\n");
			sb.append("}\n");
			sb.append("\n");
			
//			sb.append("public "+className+"(int size) {\n");
//			sb.append("super(size);\n");
//			sb.append("}\n");
//			sb.append("\n");
			
//			sb.append("public "+className+"(boolean realKeyFlag,int size) {\n");
//			sb.append("super(realKeyFlag,size);\n");
//			sb.append("}\n");
//			sb.append("\n");
//			
			sb.append("public "+className+"(Map map) {\n");
			sb.append("super(map);\n");
			sb.append("}\n");
			
//			sb.append("public "+className+"(Map map,boolean realKeyFlag) {\n");
//			sb.append("super(map,realKeyFlag);\n");
//			sb.append("}\n");
			
			sb.append("static {\n");
			sb.append("RecordTable.registerTable("+className+".class,new RecordTable.TableInfo(\""+tableName+"\",\""+rdPrimaryKey.getString(0)+"\"));\n");
			sb.append("}\n");
//			sb.append("public String getTableName() {\n");
//			sb.append("return \""+tableName+"\";\n");
//			sb.append("}\n");
//			sb.append("public String getPrimaryKey() {\n");
//			sb.append("return \""+rdPrimaryKey.getString(0)+"\";\n");
//			sb.append("}\n");
			Record rdDbField = new Record();//数据库表字段列表
			//字段
			ResultSet resultSetColumn = dm.getColumns(null, null, tableName, null);
			RecordSet rsResultSetColumn = new RecordSet(resultSetColumn);
//			rsResultSetColumn.d();
			StringBuilder sbInsertSql = new StringBuilder("\n<insert parameterType=\"Record\" id=\"insert"+className+"\">\ninsert into V_"+tableName+" (\n"+"<trim prefixOverrides=\",\">\n");
			StringBuilder sbInsertValuesSql = new StringBuilder("values (\n"+"<trim prefixOverrides=\",\">\n");
			StringBuilder sbUpdateSql = new StringBuilder("\n<update parameterType=\"Record\" id=\"update"+className+"\">\nupdate V_"+tableName +" set "+"\n<trim prefixOverrides=\",\">\n");
			StringBuilder sbMethod = new StringBuilder("\n");
			rdMybatisMethod.set("insert"+className,"public void insert"+className+"(Record rd);\n");
			rdMybatisMethod.set("update"+className,"public void update"+className+"(Record rd);\n");
			


			for (int i = 0;i<rsResultSetColumn.size();i++) {
				
				Record rdColumn = rsResultSetColumn.r(i);
				String columnName = rdColumn.getString("column_name").toLowerCase();
				if (columnName.equalsIgnoreCase("sync_time")) {
					continue;
				}
//				columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
				String propertyName = "";
				while (columnName.indexOf("_") > 0) {
					String strTmp =  columnName.substring(0,columnName.indexOf("_"));
					propertyName = propertyName + strTmp;
					columnName = columnName.substring(columnName.indexOf("_")+1);
					if (columnName.length() > 0) {
						columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
					}
					else {//以下划线结尾的字段名称
						columnName = columnName+"_";
					}

					
				}
				propertyName = propertyName + columnName;
				propertyName = toCamelCase(rdColumn.getString("column_name"));
				columnName = rdColumn.getString("column_name").toUpperCase();
				String javaTypeName = this.getJavaTypeByJdbcType(rdColumn.gIntValue("data_type"));
				if (javaTypeName.equalsIgnoreCase("bigdecimal") && rdColumn.getString("column_name").toUpperCase().endsWith("COUNT")) {
					javaTypeName = "Double";
				}
				String recordMethodType = getRecordTypeByJdbcType(rdColumn.gIntValue("data_type"));
				
				if (recordMethodType.equals("String")) {
					recordMethodType = "getString";
				}
				else {
					recordMethodType = "g"+recordMethodType;
				}
				if (recordMethodType.equalsIgnoreCase("bigdecimalvalue") && rdColumn.getString("column_name").toUpperCase().endsWith("COUNT")) {
					recordMethodType = "DoubleValue";
				}
				
				String setMethodName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				String getMethodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				
				String cName = CommonUtil.replace(columnName, "_", "");
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				//sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
				
				
				sb.append("public void "+setMethodName+"("+javaTypeName+" "+propertyName+") {\n");
//				sb.append("this."+propertyName+"="+propertyName+";\n");	
				sb.append("this.put(\""+columnName.toLowerCase()+"\","+this.getJdbcNameByJdbcTypes(rdColumn.gIntValue("data_type"))+","+propertyName+");\n");	
				
				sb.append("}\n");	
				
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				//sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
				sb.append("public "+javaTypeName+" "+getMethodName+"() {\n");
//				sb.append("return this."+propertyName+";\n");	
				sb.append("return this."+recordMethodType+"(\""+columnName.toLowerCase()+"\");\n");	
				sb.append("}\n");	
				
					
//				sb.append("public void "+setMethodName+"(String "+propertyName+") {\n");
////				sb.append("this."+propertyName+"Start="+propertyName+";\n");	
//				sb.append("this.put(\""+propertyName+"\","+propertyName+");\n");	
//				sb.append("}\n");	

				
				
			}
			
			
			int fieldIndex = 0;
			int fieldIndexUpdate = 0;
			
			for (int i = 0;i<rsResultSetColumn.size();i++) {
				
				Record rdColumn = rsResultSetColumn.r(i);
				int jdbcType = rdColumn.gIntValue("data_type");
				
				String columnName = rdColumn.getString("column_name").toLowerCase();
				boolean bXmlProperty = false;
				String xmlPropertyName = bXmlProperty ? columnName : G.toCamelCase(columnName);
				if (columnName.equalsIgnoreCase("sync_time")) {
					continue;
				}
				if (columnName.equalsIgnoreCase("id") 
						|| columnName.equalsIgnoreCase("plan_id")
						|| columnName.equalsIgnoreCase("TIME_id") 
						) {
					
				}
				else if (columnName.equalsIgnoreCase("uid_sync")) {
					sbInsertSql.append(","+columnName+"\n");
					sbInsertValuesSql.append(",UUID()\n");
				}
				else {
					if (isStringByJdbcType(jdbcType)) {
						sbInsertSql.append("<if test=\""+xmlPropertyName+" != null and "+xmlPropertyName+" != ''\">"+(fieldIndex == 0?"" :",")+columnName+"</if>\n");
						sbInsertValuesSql.append("<if test=\""+xmlPropertyName+" != null and "+xmlPropertyName+" != ''\">"+(fieldIndex == 0?"" :",")+"#{"+xmlPropertyName+"}</if>\n");
						
						if ( ! columnName.equalsIgnoreCase("uid")) {
							sbUpdateSql.append("<if test=\""+xmlPropertyName+" != null and "+xmlPropertyName+" != ''\">"+(fieldIndexUpdate == 0?"" :",")+columnName+"=#{"+xmlPropertyName+"}</if>\n");
							fieldIndexUpdate++;
						}
					}
					else {
						sbInsertSql.append("<if test=\""+xmlPropertyName+" != null \">"+(fieldIndex == 0?"" :",")+columnName+"</if>\n");
						sbInsertValuesSql.append("<if test=\""+xmlPropertyName+" != null\">"+(fieldIndex == 0?"" :",")+"#{"+xmlPropertyName+"}</if>\n");
						
						if ( ! columnName.equalsIgnoreCase("uid")) {
							sbUpdateSql.append("<if test=\""+xmlPropertyName+" != null\">"+(fieldIndexUpdate == 0?"" :",")+columnName+"=#{"+xmlPropertyName+"}</if>\n");
							fieldIndexUpdate++;
						}
					}
					
	
					fieldIndex++;
				}
				
				rdDbField.put(columnName,"");
				if (rdDataField.containsKey(columnName)) {
					rdDataField.remove(columnName);
				}
//				columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
				String propertyName = G.toCamelCase(rdColumn.getString("column_name"));
				columnName = rdColumn.getString("column_name").toUpperCase();
				String javaTypeName = this.getJavaTypeByJdbcType(rdColumn.gIntValue("data_type"));
				logger.error(columnName + " " + javaTypeName+" "+rdColumn.gIntValue("data_type"));
				if (javaTypeName.equalsIgnoreCase("bigdecimal") && columnName.endsWith("COUNT")) {
					javaTypeName = "Double";
				}
				String cName = CommonUtil.replace(columnName, "_", "");
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				//sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
					
				sb.append("//private  "+javaTypeName+" "+propertyName+";\n");	

				
			}
			
			sbInsertSql.append("</trim>\n)\n");
			sbInsertValuesSql.append("</trim>\n)\n</insert>\n");
			sbUpdateSql.append("\n</trim>\n where uid=#{uid}\n</update>\n");
			
			//根据数据整理出的多出字段
			if (rdDataField.size() > 0) {
				sb.append(genJava(rdDataField));
			}
			
//			System.err.println(sbInsertSql.toString()+sbInsertValuesSql.toString());
//			System.err.println(sbUpdateSql.toString());
//			rdDataField.put(tableName,sbInsertSql.toString()+sbInsertValuesSql.toString())
			wr.write(sbInsertSql.toString()+sbInsertValuesSql.toString());
			wr.write(sbUpdateSql.toString());
			sb.append("}\n");
			bw.write(sb.toString());
			bw.flush();
		

		} catch (Exception ex) {
			throw new AppException("获取元数据错误", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (innerCon != null) {
					innerCon.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();

				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			
			
		}
		
	}
	private static String getJdbcNameByJdbcTypes(int type) {
		String typeName = "VARCHAR";
		switch (type) {
		case Types.DATE:
			typeName = "DATE";//getDate(i);
			break;
		case Types.TIME:
			typeName = "TIME";//getTime(i);
			break;
		case Types.TIMESTAMP:
			typeName = "TIMESTAMP";//getTimestamp(i);
			break;
		case Types.NUMERIC:
			typeName = "NUMERIC";//getBigDecimal(i);
			break;
		case Types.DECIMAL:
			typeName = "DECIMAL";//getBigDecimal(i);
			break;
		case Types.DOUBLE:
			typeName = "DOUBLE";//getDouble(i);
			break;
		case Types.FLOAT:
			typeName = "FLOAT";//getDouble(i);
			break;
		case Types.REAL:
			typeName = "REAL";//getFloat(i);
			break;

		case Types.CLOB:
			typeName = "CLOB";//getClob(i);
			break;
		case Types.BLOB:
			typeName = "BLOB";//getBlob(i);
			break;
		case Types.ARRAY:
			typeName = "ARRAY";//getArray(i);
			break;
		case Types.DATALINK:
			typeName = "DATALINK";//getObject(i);
			break;
		case Types.JAVA_OBJECT:
			typeName = "JAVA_OBJECT";//getObject(i);
			break;
		case Types.OTHER:
			typeName = "OTHER";//getObject(i);
			break;
		case Types.STRUCT:
			typeName = "STRUCT";//getObject(i);
			break;
		case Types.REF:
			typeName = "REF";//getRef(i);
			break;

		case Types.BINARY:
			typeName = "BINARY";//getRef(i);
			break;
		case Types.VARBINARY:
			typeName = "VARBINARY";//getRef(i);
			break;
		case Types.LONGVARBINARY:
			typeName = "LONGVARBINARY";//getRef(i);
			break;

		case Types.NULL:
			typeName = "NULL";//getInt(i);
			break;
		case Types.DISTINCT:
			typeName = "DISTINCT";//getInt(i);
			break;
		case Types.ROWID:
			typeName = "ROWID";//getInt(i);
			break;
		case Types.SQLXML:
			typeName = "SQLXML";//getInt(i);
			break;
		case Types.NCLOB:
			typeName = "NCLOB";//getNClob(i);
			break;
		case Types.NCHAR:
			typeName = "NCHAR";//getInt(i);
			break;
		case Types.NVARCHAR:
			typeName = "NVARCHAR";//getInt(i);
			break;
		case Types.LONGNVARCHAR:
			typeName = "LONGNVARCHAR";//getInt(i);
			break;

		case Types.INTEGER:
			typeName = "INTEGER";//getInt(i);
			break;
		case Types.BIGINT:
			typeName = "BIGINT";//getLong(i);
			break;
		case Types.SMALLINT:
			typeName = "SMALLINT";//getInt(i);
			break;
		case Types.TINYINT:
			typeName = "TINYINT";//getInt(i);
			break;

		case Types.BIT:
			typeName = "BIT";//getBoolean(i);
			break;
		case Types.BOOLEAN:
			typeName = "BOOLEAN";//getBoolean(i);
			break;
		case Types.LONGVARCHAR:
			typeName = "LONGVARCHAR";//getString(i);
			break;
		case Types.VARCHAR:
			typeName = "VARCHAR";//getString(i);
			break;
		case Types.CHAR:
			typeName = "CHAR";//getString(i);
			break;

		default:
			typeName = "VARCHAR";//getObject(i);
			break;
		
		}
		return "Types."+typeName;
	}

	public void genPojoBean(String tableName,String className) {
		tableName = tableName.toUpperCase();
		Connection innerCon = this.getConnection();

		String filePath = "d:/javafy/"+className+"";//生成的java文件存放位置及默认的java名
		String packageName = "package com.oristartech.cms.v1.goods.basis.goodsMeal.po;";//"package com.fy.util";//包名
		
		
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		java.io.BufferedWriter bw = null;
		try {
			java.sql.DatabaseMetaData dm = innerCon.getMetaData();
			//主键
			ResultSet rsetPrimary = dm.getPrimaryKeys(null, null, tableName);
			RecordSet rsPrimaryKey = new RecordSet(rsetPrimary);
			Record rdPrimaryKey = new Record();
			for (int i = 0; i < rsPrimaryKey.size(); i++) {
				String primaryKeyColumnName = rsPrimaryKey.r(i).getString("column_name");
				rdPrimaryKey.put(primaryKeyColumnName,primaryKeyColumnName);

			}
			
//			//唯一索引
//			Record rdIndex = new Record();
//			ResultSet resultSetIndex = null;
//			resultSetIndex = dm.getIndexInfo(null, null, tableName, true,true);
//			RecordSet rsIndex = new RecordSet(resultSetIndex);
//			for (int i = 0; i < rsIndex.size(); i++) {
//				String indexColumnName = rsIndex.r(i).getString("column_name");
//				rdIndex.put(indexColumnName,indexColumnName);
//
//			}
//			
//			
//
			
			bw = new java.io.BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(filePath+".java"),"UTF-8"));
			StringBuffer sb = new StringBuffer();
			sb.append(packageName+"\n");
			sb.append("import com.oristartech.core.businessmodule.JsonObject;\n");
			sb.append("import com.oristartech.core.businessmodule.JsonProperty;\n");
			sb.append("import java.util.Date;\n");
			sb.append("import java.math.BigDecimal;\n");
			sb.append("import com.oristartech.cms.v1.basis.common.domain.BaseData;\n");
			
			
			
			
			sb.append("/**");
			sb.append("\n*表名："+tableName+"<br>");
			//sb.append("\n*表说明："+rdSet.getString("remarks")+"<br>");
			//sb.append("\n*主键："+rdPrimaryKey.toStringOfValue(",")+"<br>");
			//sb.append("\n*其他：<br>"+rdSet.toStringCh("<br>")+"<br>");
			sb.append("\n*/\n");
			sb.append("@JsonObject\n");
			sb.append("public class "+className+" extends BaseData {\n");

			
			
			
			
			
			//字段
			ResultSet resultSetColumn = dm.getColumns(null, null, tableName, null);
			RecordSet rsResultSetColumn = new RecordSet(resultSetColumn);
//			rsResultSetColumn.d();
			for (int i = 0;i<rsResultSetColumn.size();i++) {
				
				Record rdColumn = rsResultSetColumn.r(i);
				String columnName = rdColumn.getString("column_name").toLowerCase();
//				columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
				String propertyName = "";
				while (columnName.indexOf("_") > 0) {
					String strTmp =  columnName.substring(0,columnName.indexOf("_"));
					propertyName = propertyName + strTmp;
					columnName = columnName.substring(columnName.indexOf("_")+1);
					
					columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
					
				}
				propertyName = propertyName + columnName;
				columnName = rdColumn.getString("column_name").toUpperCase();
				String javaTypeName = this.getJavaTypeByJdbcType(rdColumn.gIntValue("data_type"));
	
				String cName = CommonUtil.replace(columnName, "_", "");
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				//sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
					
				sb.append("private  "+javaTypeName+" "+propertyName+";\n");	
//				if (javaTypeName.equalsIgnoreCase("date")&& ! rdColumn.getString("column_name").equalsIgnoreCase("update_time") && ! rdColumn.getString("column_name").equalsIgnoreCase("create_time") && ! rdColumn.getString("column_name").equalsIgnoreCase("sync_time")) {
//					sb.append("private  "+javaTypeName+" "+propertyName+"Start;\n");
//					sb.append("private  "+javaTypeName+" "+propertyName+"End;\n");
//				}
				
			}
			
			for (int i = 0;i<rsResultSetColumn.size();i++) {
				
				Record rdColumn = rsResultSetColumn.r(i);
				String columnName = rdColumn.getString("column_name").toLowerCase();
				String propertyName = "";
				while (columnName.indexOf("_") > 0) {
					String strTmp =  columnName.substring(0,columnName.indexOf("_"));
					propertyName = propertyName + strTmp;
					columnName = columnName.substring(columnName.indexOf("_")+1);
					
					columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
					
				}
				propertyName = propertyName + columnName;
				columnName = rdColumn.getString("column_name").toUpperCase();
				String javaTypeName = this.getJavaTypeByJdbcType(rdColumn.gIntValue("data_type"));
				String recordMethodType = getRecordTypeByJdbcType(rdColumn.gIntValue("data_type"));
				String setMethodName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				String getMethodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				
				String cName = CommonUtil.replace(columnName, "_", "");
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				//sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
				
				
				sb.append("public void "+setMethodName+"("+javaTypeName+" "+propertyName+") {\n");
				sb.append("this."+propertyName+"="+propertyName+";\n");	
				sb.append("}\n");	
				
				sb.append("/**");
				sb.append("\n*列名："+columnName+"<br>");
				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
				//sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
				sb.append("\n*/\n");
				sb.append("@JsonProperty(name=\""+columnName.toLowerCase()+"\")\n");
				sb.append("public "+javaTypeName+" "+getMethodName+"() {\n");
				sb.append("return this."+propertyName+";\n");	
				sb.append("}\n");	
				
				System.out.println("<result property=\""+propertyName+"\" column=\""+columnName+"\"/>");
//				if (javaTypeName.equalsIgnoreCase("date")&& ! rdColumn.getString("column_name").equalsIgnoreCase("update_time") && ! rdColumn.getString("column_name").equalsIgnoreCase("create_time") && ! rdColumn.getString("column_name").equalsIgnoreCase("sync_time")) {
//					System.out.println("<result property=\""+propertyName+"Start\" column=\""+columnName+"_START\"/>");
//					System.out.println("<result property=\""+propertyName+"End\" column=\""+columnName+"_END\"/>");
//					
//					
//					sb.append("public void "+setMethodName+"Start("+javaTypeName+" "+propertyName+") {\n");
//					sb.append("this."+propertyName+"Start="+propertyName+";\n");	
//					sb.append("}\n");	
//					
////					sb.append("@JsonProperty(name=\""+columnName.toLowerCase()+"_start\")\n");
//					sb.append("public "+javaTypeName+" "+getMethodName+"Start() {\n");
//					sb.append("return this."+propertyName+"Start;\n");	
//					sb.append("}\n");	
//					
//					sb.append("public void "+setMethodName+"End("+javaTypeName+" "+propertyName+") {\n");
//					sb.append("this."+propertyName+"End="+propertyName+";\n");	
//					sb.append("}\n");	
//					
////					sb.append("@JsonProperty(name=\""+columnName.toLowerCase()+"_end\")\n");
//					sb.append("public "+javaTypeName+" "+getMethodName+"End() {\n");
//					sb.append("return this."+propertyName+"End;\n");	
//					sb.append("}\n");	
//				}
				
				
			}
			
//			for (int i = 0;i<rsResultSetColumn.size();i++) {
//				
//				Record rdColumn = rsResultSetColumn.r(i);
//				String columnName = rdColumn.getString("column_name").toUpperCase();				
//				
//				String cName = Util.replace(columnName, "_", "");
//				sb.append("/**");
//				sb.append("\n*列名："+columnName+"<br>");
//				sb.append("\n*类型："+rdColumn.getString("type_name")+"<br>");
//				sb.append("\n*允许null："+rdColumn.getString("is_nullable")+"<br>");
//				//sb.append("\n*是否主键："+rdColumn.getString("is_primary_key","")+"<br>");
//				//sb.append("\n*是否唯一："+rdColumn.getString("is_unique")+"<br>");
//				sb.append("\n*长度："+rdColumn.getString("char_octet_length")+"<br>");
//				sb.append("\n*备注："+rdColumn.getString("remarks")+"<br>");
//				sb.append("\n*其他：<br>"+rdColumn.toStringCh("<br>")+"<br>");
//				sb.append("\n*/\n");
//				sb.append("public  void set"+columnName+"();\n");	
//				
//			}
			sb.append("}\n");
			bw.write(sb.toString());
			bw.flush();
		

		} catch (Exception ex) {
			throw new AppException("获取元数据错误", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (st != null) {
					st.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (innerCon != null) {
					innerCon.close();
				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			try {
				if (bw != null) {
					bw.flush();
					bw.close();

				}
			} catch (Exception ex2) {
				ex2.printStackTrace();
			}
			
			
		}
		
	}
	private static String getJavaTypeByJdbcType(int sqlType) {

		String type = "String";
		switch(sqlType) {
		case Types.DATE:
			type = "Date";
			break;
        case Types.TIME:
        	type = "Date";
        	
			break;
        case Types.TIMESTAMP:
        	type = "Date";
        	
			break;
        case Types.NUMERIC:
        	type = "Double";
			break;
        case Types.DECIMAL:
        	type = "BigDecimal";

			break;
        case Types.DOUBLE:
        	type = "Double";
			break;
        case Types.FLOAT:
        	type = "Double";
			break;
        case Types.REAL:
        	type = "Float";
			break;
        case Types.CLOB:
        	type = "String";
			break;
        case Types.BLOB:
        	type = "Object";
			break;
        case Types.ARRAY:
        	type = "RecordSet";
			break;
        case Types.LONGVARCHAR:
        	type = "String";
			break;
        case Types.DATALINK:
        	type = "Object";
			break;
        case Types.JAVA_OBJECT:
        	type = "Object";
			break;
        case Types.OTHER:
        	type = "Object";
			break;
        case Types.STRUCT:
        	type = "Object";
			break;
        case Types.REF:
        	type = "Object";
			break;
        case Types.LONGVARBINARY:
        	type = "String";
            break;
            
        case Types.INTEGER:
        	type = "Integer";
            break;
        case Types.BIGINT:
        	type = "Long";
            break;
        case Types.SMALLINT:
        	type = "Integer";
            break;
        case Types.TINYINT:
        	type = "Integer";
            break;
        case Types.BIT:
        	type = "Boolean";
            break;
        case Types.CHAR:
        	type = "String";
            break;
        case Types.VARCHAR:
        	type = "String";
            break;
        case Types.BOOLEAN:
        	type = "Boolean";
            break;
        default:
        	type = "String";
            break;
		}
		
		return type;
	}
	public Connection getConnection() {
		Connection innerCon = null;
			try {

				// GetConnection getconn = GetConnection.getInstance();
				// con = getconn.getConn1();
				/*
				 * InitialContext ic = new InitialContext(); DataSource ds =
				 * (DataSource) ic.lookup("sqlserverds");//门户、论坛
				 * 
				 * con = ds.getConnection();
				 */
				/*
				 * //String url = "jdbc:mysql://61.145.116.208:3306/mms"; String
				 * url =
				 * "jdbc:mysql://fy:3306/ss?characterEncoding=gbk&useUnicode=true"
				 * ;
				 * //"jdbc:mysql://fy:3306/ss?characterEncoding=gbk&useUnicode=true"
				 * ;
				 */
//				String strDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				String url="jdbc:sqlserver://192.168.1.180:1433;DatabaseName=ArchiveSystem";
//				Class.forName(strDriver);
//				innerCon = DriverManager.getConnection(url, "sa", "1");
				  
				 
//				 String url = "jdbc:sqlserver://fy2:1433;databaseName=test;user=fy2;password=sa";
//				 String strDriver ="com.microsoft.sqlserver.jdbc.SQLServerDriver";
//				 Class.forName(strDriver);
//				 innerCon =DriverManager.getConnection(url);
				 
				/*
				 * String url = "jdbc:oracle:thin:@fy2:1521:jnjd"; String
				 * strDriver = "oracle.jdbc.driver.OracleDriver";
				 * Class.forName(strDriver); innerCon =
				 * DriverManager.getConnection(url,"hsm","gzcss");
				 * 
				 * 
				 * 
				 * this.tlCon.set(innerCon);
				 * 
				 * String url =
				 * "jdbc:sybase:Tds:172.30.8.133:2640/shenzhen_write?useUnicode=true&amp;characterEncoding=UTF-8"
				 * ; String strDriver = "com.sybase.jdbc3.jdbc.SybDriver";
				 * Class.forName(strDriver); innerCon =
				 * DriverManager.getConnection(url, "DBA", "SZ10sj!@3");
				 * 
				 * this.tlCon.set(innerCon);
				 */
//					java.util.Properties prop = new Properties();
//					prop.put("user", "apps");
//					prop.put("password", "apps");
//					prop.put("remarks", "true");
//
//
//					 String url = "jdbc:oracle:thin:@10.18.221.9:1521:GDYDDEPV"; 
//					 String strDriver = "oracle.jdbc.driver.OracleDriver";
//					Class.forName(strDriver); 
//					innerCon = DriverManager.getConnection(url,prop);
				String url = "jdbc:mysql://192.168.1.142:3306/platform?characterEncoding=utf-8&useUnicode=true";
				String strDriver = "com.mysql.jdbc.Driver";
				Class.forName(strDriver);
				innerCon = DriverManager.getConnection(url, "root", "xinhe123");
//
//					 String url = "jdbc:postgresql://10.17.36.18/gdctc_new";
//					 String strDriver = "org.postgresql.Driver";
//					 Class.forName(strDriver);
//					 innerCon =
//					 DriverManager.getConnection(url,"gpadmin","gpadmin");

	

			} catch (Exception se) {
				throw new AppException("数据库连接错误", se);
			}


		return innerCon;

	}
	
	/**
	 * 返回Record.getXXX名字
	 * @param sqlType
	 * @return
	 */
	private static String getRecordTypeByJdbcType(int sqlType) {

		String type = "String";
		switch(sqlType) {
		case Types.DATE:
			type = "Date";
			break;
        case Types.TIME:
        	type = "Date";
        	
			break;
        case Types.TIMESTAMP:
        	type = "Date";
        	
			break;
        case Types.NUMERIC:
        	type = "DoubleValue";
			break;
        case Types.DECIMAL:
        	type = "BigDecimalValue";

			break;
        case Types.DOUBLE:
        	type = "DoubleValue";
			break;
        case Types.FLOAT:
        	type = "DoubleValue";
			break;
        case Types.REAL:
        	type = "DoubleValue";
			break;
        case Types.CLOB:
        	type = "String";
			break;
        case Types.BLOB:
        	type = "";
			break;
        case Types.ARRAY:
        	type = "RecordSet";
			break;
        case Types.LONGVARCHAR:
        	type = "String";
			break;
        case Types.DATALINK:
        	type = "";
			break;
        case Types.JAVA_OBJECT:
        	type = "";
			break;
        case Types.OTHER:
        	type = "";
			break;
        case Types.STRUCT:
        	type = "";
			break;
        case Types.REF:
        	type = "";
			break;
        case Types.LONGVARBINARY:
        	type = "String";
            break;
            
        case Types.INTEGER:
        	type = "IntValue";
            break;
        case Types.BIGINT:
        	type = "LongValue";
            break;
        case Types.SMALLINT:
        	type = "IntValue";
            break;
        case Types.TINYINT:
        	type = "IntValue";
            break;
        case Types.BIT:
        	type = "BooleanValue";
            break;
        case Types.CHAR:
        	type = "String";
            break;
        case Types.VARCHAR:
        	type = "String";
            break;
        case Types.BOOLEAN:
        	type = "BooleanValue";
            break;
        default:
        	type = "String";
            break;
		}
		return type;
	}
	public void getTables() {
		Connection innerCon = this.getConnection();
		
		String className = "d";//生成的类名
		String filePath = "d:/javafy/"+className;//生成的java文件存放位置及默认的java名
		String packageName = "package com.fy.util";//包名
		
		
		Statement st = null;
		ResultSet rs = null;
		String sql = null;
		try {
			
			
			java.io.BufferedWriter bw = new BufferedWriter(new FileWriter("d:/mybatis.java"));
			
			java.sql.DatabaseMetaData dm = innerCon.getMetaData();
			ResultSet resultSet = dm.getTables(null, null, "sys_user%", new String[]{"TABLE"});
			RecordSet rsSet = new RecordSet(resultSet);
			
			for (int i = 0;i<rsSet.size();i++) {
				
				Record rd = rsSet.r(i);
//				rd.d();
				String tableName = rd.getString("table_name").toLowerCase();
				if (tableName.toLowerCase().indexOf("copy")>=0) {
					continue;
				}
				
				String javaClassName = getClassNameByTableName(tableName);
//				logger.error("<typeAlias type=\"com.oristartech.cms.v1.moviePlan.basic.showPlan.domain."+javaClassName+"\" alias=\""+javaClassName+"\"/>");
				
//				this.alter(rd.getString("table_name").toLowerCase(), javaClassName,null,bw);
				this.genPojoMap(rd.getString("table_name").toLowerCase(), javaClassName,null,bw);
//				this.genPojoBean(rd.getString("table_name").toLowerCase(), javaClassName);
				
//				this.genPojoBean(rd.getString("table_name").toLowerCase(), javaClassName);
			}
			bw.write(rdMybatisMethod.toStringOfValue("\n"));
			bw.flush();
			bw.close();
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			
		}
	}
	
	public static String getClassNameByTableName(String tableName) {
		tableName = tableName.toLowerCase();
		String javaClassName = "";
		while (tableName.indexOf("_") > 0) {
			String strTmp =  tableName.substring(0,tableName.indexOf("_"));
			javaClassName = javaClassName + strTmp;
			tableName = tableName.substring(tableName.indexOf("_")+1);
			
			tableName = tableName.substring(0,1).toUpperCase()+tableName.substring(1);
			
		}
		javaClassName = javaClassName + tableName;
		javaClassName = javaClassName.substring(0,1).toUpperCase()+javaClassName.substring(1);
		return javaClassName;
	}
	
	
	/**
	 * 根据字段名生成属性和get/set方法，集合格式
	 * @param rd 字段名
	 */
	public String genJava(Record rd) {
		
		StringBuffer sb = new StringBuffer();
		try {

	
			for (int i = 0;i<rd.size();i++) {
				
	
				String columnName = rd.gName(i);
				if (rd.getValue(i) instanceof RecordSet) {
					rd.pType(i, Types.ARRAY);
				}
				else if (columnName.equalsIgnoreCase("sch_Sale_Channel")) {
					
				}
				else  {
					continue;
				}
//				columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
				String propertyName = "";
				while (columnName.indexOf("_") > 0) {
					String strTmp =  columnName.substring(0,columnName.indexOf("_"));
					propertyName = propertyName + strTmp;
					columnName = columnName.substring(columnName.indexOf("_")+1);
					
					columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
					
				}
				propertyName = propertyName + columnName;
				String javaTypeName = G.getJavaTypeByJdbcType(rd.gType(i));
	
				String cName = CommonUtil.replace(columnName, "_", "");
				
					
				sb.append("private  "+javaTypeName+" "+propertyName+";\n");	
				if (javaTypeName.equalsIgnoreCase("date")) {
					sb.append("private  "+javaTypeName+" "+propertyName+"Start;\n");
					sb.append("private  "+javaTypeName+" "+propertyName+"End;\n");
				}
				
			}
			
			

			for (int i = 0;i<rd.size();i++) {
				
				String columnName = rd.gName(i);
				if (rd.getValue(i) instanceof RecordSet) {
					rd.pType(i, Types.ARRAY);
				}
				else if (columnName.equalsIgnoreCase("sch_Sale_Channel")) {
					
				}
				else  {
					continue;
				}
//				columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
				String propertyName = "";
				while (columnName.indexOf("_") > 0) {
					String strTmp =  columnName.substring(0,columnName.indexOf("_"));
					propertyName = propertyName + strTmp;
					columnName = columnName.substring(columnName.indexOf("_")+1);
					
					columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
					
				}
				propertyName = propertyName + columnName;

				String javaTypeName = G.getJavaTypeByJdbcType(rd.gType(i));
				String recordMethodType = G.getRecordTypeByJdbcType(rd.gType(i));
				if (recordMethodType.equals("String")) {
					recordMethodType = "getString";
				}
				else {
					recordMethodType = "g"+recordMethodType;
				}

				String setMethodName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				String getMethodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
				
				String cName = CommonUtil.replace(columnName, "_", "");
				
				
				
				sb.append("public void "+setMethodName+"("+javaTypeName+" "+propertyName+") {\n");
//				sb.append("this."+propertyName+"="+propertyName+";\n");	
				sb.append("this.put(\""+rd.gName(i).toLowerCase()+"\","+getJdbcNameByJdbcTypes(rd.gType(i))+","+propertyName+");\n");	
				
				sb.append("}\n");	
				
				
				sb.append("@JsonProperty(name=\""+rd.gName(i).toLowerCase()+"\")\n");
				sb.append("public "+javaTypeName+" "+getMethodName+"() {\n");
//				sb.append("return this."+propertyName+";\n");	
				sb.append("return this."+recordMethodType+"(\""+rd.gName(i)+"\");\n");	
				sb.append("}\n");	
				
				
			}
			
			return sb.toString();
		

		} catch (Exception ex) {
			throw new AppException("获取元数据错误", ex);
		} finally {
			
		}
		
	}
	/**
	 * 根据java代码查找字段，并生成GET/SET属性
	 * @param c
	 * @param noValidFiedls 无效的字段名，用逗号分隔
	 */
	public Record makeFieldByJavaFile(Class<?> c,String tableName,String fullFileName,String noValidFields) {
		
		String className = "d";//生成的类名
		Method[] arrMethod = c.getMethods();
		int size = arrMethod.length;
		Record rdMethod = new Record();
		for (int i = 0;i<size;i++) {
			rdMethod.put(arrMethod[i].getName(),"");
		}
		
		Record rdNoValidField = new Record();
		if (CommonUtil.isNotEmpty(noValidFields)) {
			rdNoValidField = CommonUtil.toRd2(noValidFields, ",");
		}
//		rdMethod.d();
		
		StringBuilder sbFile = new StringBuilder();
		Record rd = new Record();
		String sql = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fullFileName));
			Pattern p =  Pattern.compile("(plan|rd)\\.(p(ut)?\\(\\getString*\"([\\w\\d_]+)\"\\getString*\\,)");
			Pattern p2 =  Pattern.compile("(plan|rd)\\.(g([^e])\\w+\\(\\getString*\"([\\w\\d_]+)\")");//\\getString*\\,?\\getString*\"?\\getString*[\\w\\d_]*\\getString*\"?\\getString* [^\\)]*
			Matcher m= null;
			String line = br.readLine();
//			line = "rd.put(\"sch_sale_channel\",rsMovieChannel.getString(\"uid_channel\").toStringOfValue(\",\"));";
			
			while (line != null) {
				m = p.matcher(line);
				StringBuffer sb = new StringBuffer();
				boolean findFlag = false;
				if (m.find()) {
					findFlag = true;
					String columnName = m.group(4).toLowerCase();
					if ( ! rdNoValidField.containsKey(columnName)) {
						String putStr = m.group(2);
						
						String propertyName = "";
						
						
						while (columnName.indexOf("_") > 0) {
							
							String strTmp =  columnName.substring(0,columnName.indexOf("_"));
							propertyName = propertyName + strTmp;
							columnName = columnName.substring(columnName.indexOf("_")+1);
							
							columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
							
						}
						propertyName = propertyName + columnName;
						
						String setMethodName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
						String getMethodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
						m.appendReplacement(sb,m.group(1)+ "."+setMethodName+"(");
						m.appendTail(sb);
						if ( ! rdMethod.containsKey(setMethodName)) {
							rd.put(m.group(4),sb.toString()+"---->>"+line);
//							sbFile.append(sb.toString()+'\n');
							sbFile.append(line+'\n');
						}
						else {
//							rd.put("!!"+m.group(4),sb.toString()+"---->>"+line);
							sbFile.append(sb.toString()+'\n');
						}
						
					}
					else {
						sbFile.append(line+'\n');
					}
					
					
					
				}
				else {
					sbFile.append(line+'\n');
				}

				m = p2.matcher(line);
				sb = new StringBuffer();
				findFlag = false;
				if (m.find()) {
					findFlag = true;
					String columnName = m.group(4).toLowerCase();
					if ( ! rdNoValidField.containsKey(columnName)) {
						String putStr = m.group(2);
						
						String propertyName = "";
						
						
						while (columnName.indexOf("_") > 0) {
							
							String strTmp =  columnName.substring(0,columnName.indexOf("_"));
							propertyName = propertyName + strTmp;
							columnName = columnName.substring(columnName.indexOf("_")+1);
							
							columnName = columnName.substring(0,1).toUpperCase()+columnName.substring(1);
							
						}
						propertyName = propertyName + columnName;
						
//						String setMethodName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
						String getMethodName = "get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
						m.appendReplacement(sb,m.group(1)+ "."+getMethodName+"(");
						m.appendTail(sb);
						if ( ! rdMethod.containsKey(getMethodName)) {
							rd.put(m.group(4),sb.toString()+"---->>"+line);
						}
						else {
//							rd.put("!!"+m.group(4),sb.toString()+"---->>"+line);
						}
					}
				}
				
				line = br.readLine();
			}
			
			size = rd.size();
			for (int i = 0;i<size;i++) {
				String fieldName = rd.gName(i);
				
			}
				
			rd.d();
//			genPojoMap(tableName, getClassNameByTableName(tableName), rd);
			br.close();
			
			//写新文件
			String newJavaFileName = fullFileName.substring(0,fullFileName.lastIndexOf("."))+"New"+".java";
			java.io.BufferedWriter bw = new  BufferedWriter(new FileWriter(new java.io.File(newJavaFileName)));
			bw.write(sbFile.toString());
			bw.flush();
			bw.close();
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			
		}
		return rd;
	}
	private static Record rdSuperClass = new Record();
	static {
		rdSuperClass.put("sch_plan","SchPlanTime");
	}
	public static void main(String[] argv) {
		G dao = new G();

		try {
			//dao.begin();
//			String tableNames = "p_fc_factor";
			//dao.genFieldFileByDb();
			dao.getTables();
//			dao.genPojoMap("SCH_PLAN", "SchPlan", new Record());
//			makeFieldByJavaFile(SchPlan.class,"");
//			dao.makeFieldByJavaFile(SchPlan.class, "SCH_PLAN","D:/cx/svn/CMS3.0/WorkingArea/Coding/CMS3.0/Server/ICEService/src/main/java/com/oristartech/cms/v1/moviePlan/basic/showPlan/service/impl/SchPlanServiceImpl.java","");
//			dao.genFieldFileByDb();
			
			
			//dao.commit();
			// rs.d();

		} catch (Exception ex) {
			ex.printStackTrace();
			//dao.rollback();
			// dao.rollback();

		} 
		finally {
			
		}
		
	}
}
