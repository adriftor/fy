package com.xh.util.db;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.util.AppException;
import com.xh.util.CommonUtil;
import com.xh.util.Field;
import com.xh.util.FyCol;
import com.xh.util.Record;



/**
 * 
 * @author adriftor
 * @version 1.1
 */

public class SqlUtil {
	private static FyCol fcDatabaseKey = new FyCol();
	public static final Log logger = LogFactory.getLog(SqlUtil.class);
	public static final String PARAM_BY_INDEX_FIELD_NAME = "_param_by_index";// 设置参数时，是否按索引设置；字段名
	public static final String PARAM_BY_INDEX_NO = "0";// 设置参数时，是否按索引设置，否
	public static final String PARAM_BY_INDEX_YES = "1";// 设置参数时，是否按索引设置，是
	private static ArrayList vKey = new ArrayList();
	private static boolean debugFlag = true;
	
	//SQL日志相关
	private static int sqlLogSize = 100;//SQL执行日志条数
	private static int sqlLogCursor = 0;//SQL执行日志游标位置
	private static ArrayList<String> listLog = new ArrayList<String>(100);
	

	static {
		vKey.add("<>");
		vKey.add(">=");
		vKey.add("<=");
		vKey.add("=");
		vKey.add(">");
		vKey.add("<");
		vKey.add("!=");
		vKey.add(" like");
		vKey.add("]");

		fcDatabaseKey.setValue("key", "key");
		fcDatabaseKey.setValue("desc", "desc");
		fcDatabaseKey.setValue("name", "name");
		fcDatabaseKey.setValue("table", "table");
		fcDatabaseKey.setValue("type", "type");
		fcDatabaseKey.setValue("id", "id");
	}

	/**
	 * 根据JAVA数据类型匹配JDBC数据类型
	 * 只定义常见的数据类型。对于CLOB、ARRAY等非常见类型，返回Types.VARCHAR
	 * @param type
	 *            Class 待匹配的类型
	 * @return int java.sql.Types里定义的数据类型
	 */
	public static int getJdbcTypeByJavaType(Class type) {

		int iType = Types.VARCHAR;
		if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			iType = Types.INTEGER;

		} else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			iType = Types.BIGINT;

		} else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			iType = Types.DOUBLE;

		} else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			iType = Types.FLOAT;

		} else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
			iType = Types.SMALLINT;

		 }else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
			iType = Types.TINYINT;

		}  else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			iType = Types.BOOLEAN;

		} else if (type.equals(java.math.BigDecimal.class)) {
			iType = Types.DECIMAL;

		} else if (type.equals(java.math.BigInteger.class)) {
			iType = Types.BIGINT;

		} else if (type.equals(java.util.Date.class) || type.equals(java.sql.Date.class)) {
			iType = Types.DATE;

		} else if (type.equals(java.sql.Timestamp.class)) {
			iType = Types.TIMESTAMP;

		} else if (type.equals(java.sql.Time.class)) {
			iType = Types.TIME;
		}
		

		return iType;

	}

	public static Object[] getInsertParameter(String sql, Record crmFields) throws AppException {
		Vector vector = new Vector();
		String remainSql = removeAquot(sql);
		String fieldsNameStr = ""; // 包含insert SQL语句中的字段列表部分
		String valuesStr = ""; // 包含insert SQL语句中的“values”以后的参数部分
		String fieldName = "";
		String questionMark = ""; // 判断是否是需要设置的字段，“？”表示需设置
		String curHandleFieldName = null;
		int index = 1;
		int dataType;
		Object fieldValue = null;
		remainSql = remainSql.substring(remainSql.indexOf("("));
		fieldsNameStr = remainSql.substring(remainSql.indexOf("(") + 1, remainSql.indexOf(")") + 1).trim();
		/*
		 * valuesStr = remainSql.substring(remainSql.lastIndexOf("(") + 1,
		 * remainSql.lastIndexOf(")") + 1).trim();
		 */
		valuesStr = remainSql.substring(remainSql.lastIndexOf("values") + 6, remainSql.lastIndexOf(")") + 1).trim();
		// logger.po(valuesStr);
		valuesStr = initInsertSql(valuesStr.substring(1, valuesStr.length() - 1)) + ")";
		// logger.po("******"+valuesStr);
		while (valuesStr.indexOf(",") != -1 || valuesStr.indexOf(")") != -1) {
			if (valuesStr.indexOf(",") != -1) {
				fieldName = fieldsNameStr.substring(0, fieldsNameStr.indexOf(",")).trim();
				questionMark = valuesStr.substring(0, valuesStr.indexOf(",")).trim();
			} else {
				fieldName = fieldsNameStr.substring(0, fieldsNameStr.indexOf(")")).trim();
				questionMark = valuesStr.substring(0, valuesStr.indexOf(")")).trim();
			}
			// 以下处理SQL SERVER数据库中以关键字作为字段名的情况，如email,user等等
			if (fieldName.startsWith("["))
				fieldName = fieldName.substring(1);
			if (fieldName.endsWith("]"))
				fieldName = fieldName.substring(0, fieldName.length() - 1);

			if (fieldName.lastIndexOf(".") != -1) {
				fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
			}

			if (questionMark.equals("?")) {
				// dataType = getDataType(fieldName, crmFields);
				curHandleFieldName = fieldName;

				try {
					fieldValue = crmFields.getValue(fieldName);
				} catch (Exception e) {
					logger.info("字段不存在：" + fieldName);
					throw new AppException("字段不存在：" + fieldName);
				}

				// setParameter(fieldValue, dataType, index, vector);
				vector.add(fieldValue);
				index++;
			}
			if (valuesStr.indexOf(",") != -1) {
				fieldsNameStr = fieldsNameStr.substring(fieldsNameStr.indexOf(",") + 1).trim();
				valuesStr = valuesStr.substring(valuesStr.indexOf(",") + 1).trim();
			} else {
				fieldsNameStr = valuesStr = "";
			}
		}
		return vector.toArray();
	}

	/**
	 * 获得insert操作的SQL语句
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rd
	 *            Record 参数
	 * @param dao
	 *            Dao
	 * @return String insert语句
	 */
	public static String getInsertSql(String tableName, Record rd, Dao dao) {
		return getInsertSql(tableName, rd, "", dao);
	}

	/**
	 * 获得insert操作的SQL语句
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rd
	 *            Record 参数
	 * @param noUpdateField
	 *            String 不进行写入的字段列表；用逗号分隔
	 * @param dao
	 *            Dao
	 * @return String insert语句
	 */
	public static String getInsertSql(String tableName, Record rd, String noUpdateField, Dao dao) {
		Record rdNu = new Record();
		StringBuffer sbSql = new StringBuffer();
		StringBuffer sbValue = new StringBuffer();
		sbSql.append("insert into ");
		sbSql.append(tableName.toUpperCase());
		sbSql.append(" (");
		// String strSql = "insert into " + tableName.toLowerCase() + "(";
		// String strValue = " values(";
		sbValue.append(" values(");
		FyCol fcNoUpdate = CommonUtil.toFc(noUpdateField, ",", true);
		Record rdTable = dao.getResultSetMetaData(tableName.toUpperCase());
		
		for (int i = 0; i < rdTable.size(); i++) {
			String fieldName = rdTable.gName(i);
			if ( rd.containsKey(fieldName) && !fcNoUpdate.containsKey(fieldName) && rd.getValue(fieldName) != null) {
				boolean isAuto = false;// 是否指定了自动系列
				Field fieldDb = rdTable.gField(fieldName);

				if (fieldDb.isAutoIncrement() || rd.gField(fieldName).isAutoIncrement()) { // 序列
					String strV = rd.getString(fieldName, "").toLowerCase();
					if (strV.indexOf(".") >= 0 && strV.indexOf("nextval") >= 0 && dao.getDatabaseType() == DaoConfig.DATABASE_TYPE_ORACLE) {
						isAuto = true;
					} else {
						continue;
					}
				}

				rd.gField(fieldName).setType(fieldDb.getType());
				if (dao.isBracketFieldName()) {
					sbSql.append("[" + fieldName + "]");
				} else {
					sbSql.append(fieldName);
				}

				sbSql.append(",");

				if (isAuto) {// 序列
					sbValue.append(rd.getString(fieldName) + ",");
				} else {
					sbValue.append("?,");
				}

			} else {
				rdNu.pField(rdTable.gField(fieldName));
			}
		}
		String strSql = sbSql.toString();
		if (strSql.endsWith(",")) {
			strSql = strSql.substring(0, strSql.length() - 1);
		}
		String strValue = sbValue.toString();
		if (strValue.endsWith(",")) {
			strValue = strValue.substring(0, strValue.length() - 1);
		}

		return strSql + ")" + strValue + ")";
	}

	
	/**
	 * 替换sqlFy里的引号里原先的值。如"select * from t1 where f1='abcd'"解析后变成"select * from t1 where f1=##$1#"，此函数将##$1#恢复长原先的值
	 * @param sqlFy
	 * @param rdSql
	 * @return
	 */
	private static String replaceFySqlValue(String sqlFy,Record rdSql) {
		
		for (int i = 0; i < rdSql.size(); i++) {
			String fieldName = rdSql.gName(i);
			
			String getValue = CommonUtil.replace(rdSql.getString(fieldName),"?","zzz1");
			getValue = CommonUtil.replace(getValue,":","zzz2");
			getValue = CommonUtil.replace(getValue,"'","zzz3");
			getValue = CommonUtil.replace(getValue,";","zzz4");
			getValue = CommonUtil.replace(getValue,",","zzz5");
			getValue = CommonUtil.replace(getValue,"$","zzz6");
			if (getValue.length() > 100) {
				getValue = getValue.substring(0,100);
			}
			
			sqlFy = CommonUtil.replaceIgnoreCase(sqlFy, fieldName, "'" + getValue + "'");

			
		}
		return sqlFy;
	}
	
	/**
	 * 处理用":"符号标明的字段
	 * 在字段前可以用":"明确说明需要设置参数，如:substr(field1,1,length(field2))=:field111
	 * ，此时将以field111作为需要设置的参数 以":"表示的字段名只和rdFields里的字段匹配，不和数据表的字段匹配
	 * 以":"表示的字段的字段名称必须是"a-z A-Z 0-9"以及"_"、"$"中的字符
	 * 
	 * @param sql
	 *            String sql语句
	 * @return Record sqlJdbc:符合JDBC标准的SQL语句；sqlFy:用于继续处理?表示的SQL语句
	 */
	public static Record getSpecSql(String sql) {
		Record rdR = new Record();
		rdR.put("sqlJdbc", sql);
		rdR.put("sqlFy", sql);

		Record rdSql = initSqlRd(sql);
		sql = rdSql.getString("sql");
		rdR.put("sqlFy", sql);
		rdSql.remove("sql");
		// 查找用":"指定的字段名称
		
		//mybatis扩展,处理#{usename}格式指定的参数。将#{username}转换成:username
		if (sql.indexOf("#{")> 0) {
			Pattern p = Pattern.compile("\\#\\{(\\getString*[\\w\\d]+\\getString*)\\}");
			Matcher m = p.matcher(sql);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, ":"+m.group(1).trim());
			}
			m.appendTail(sb);
			sql = sb.toString();
		}
//		sql = sql.replaceAll("\\#\\{\\getString*", ":").replaceAll("\\}","");
		
		if (sql.indexOf(":") == -1) {
			if (debugFlag) {
				String sqlFy = replaceFySqlValue(sql, rdSql);
				rdR.put("sqlFy", sqlFy);
			}
			
			return rdR;
		}

		String sqlFy = sql;

		FyCol fc = CommonUtil.toFc(sqlFy, ":", false);// ############
		FyCol fcJdbc = (FyCol) fc.clone(); // 生成用户JDBC格式的SQL语句
		int size = fc.size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				String fieldStr = fc.getString(i);
				int count = fieldStr.length();
				boolean isReplace = false;
				for (int j = 0; j < count; j++) {
					char ch = fieldStr.charAt(j);
					if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '$')) {
						String fieldName = fieldStr.substring(0, j);
						fcJdbc.setValue(i, "?" + fieldStr.substring(j));
						fieldStr = " " + fieldName + "=:? " + fieldStr.substring(fieldName.length());

						fc.setValue(i, fieldStr);
						isReplace = true;
						break;
					}
				}
				if (!isReplace) {
					fcJdbc.setValue(i, "?");
					fieldStr = " " + fieldStr + "=:? ";

					fc.setValue(i, fieldStr);
					isReplace = true;
				}
			}
		}
		// rdSql.d();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < fc.size(); i++) {
			sb.append(fc.getString(i));
		}
		sqlFy = sb.toString();

		sb.setLength(0);
		for (int i = 0; i < fcJdbc.size(); i++) {
			sb.append(fcJdbc.getString(i));
		}
		String sqlJdbc = sb.toString();
		sqlJdbc = CommonUtil.replaceIgnoreCase(sqlJdbc, "&lt;", "<");
		sqlJdbc = CommonUtil.replaceIgnoreCase(sqlJdbc, "&gt;", ">");
		sqlJdbc = CommonUtil.replaceIgnoreCase(sqlJdbc, "&amp;", "&");
		for (int i = 0; i < rdSql.size(); i++) {
			String fieldName = rdSql.gName(i);
			sqlJdbc = CommonUtil.replaceIgnoreCase(sqlJdbc, fieldName, "'" + rdSql.getString(fieldName) + "'");
			
		}
		
		
		if (debugFlag) {
			sqlFy = CommonUtil.replaceIgnoreCase(sqlFy, "&lt;", "<");
			sqlFy = CommonUtil.replaceIgnoreCase(sqlFy, "&gt;", ">");
			sqlFy = CommonUtil.replaceIgnoreCase(sqlFy, "&amp;", "&");
			sqlFy = replaceFySqlValue(sqlFy, rdSql);
		}
		
		rdR.put("sqlJdbc", sqlJdbc);
		rdR.put("sqlFy", sqlFy);
		return rdR;
	}

	
	public static Record getSpecSqlExp(String sql) {
		Record rdR = new Record();
		rdR.put("sqlJdbc", sql);
		rdR.put("sqlFy", sql);
		
		Pattern p = null;
		Matcher m = null;
		
		//去掉单引号
		Record rdReplace = new Record();
		p = Pattern.compile("\'([^\']*)\'");
		m = p.matcher(sql);
		StringBuffer sbJdbc = new StringBuffer();
		int index = 0;
		while (m.find()) {
			String strG = m.group(1);
			String key = "'__"+(index++)+"'";
			rdReplace.put(key,strG);
			m.appendReplacement(sbJdbc,key);
		}
		m.appendTail(sbJdbc);
		
		sql = sbJdbc.toString();
		logger.info(sql);
		
		
		
		////mybatis扩展,处理#{usename}格式指定的参数。将#{username}转换成:username
		//"#\\{([\\w\\d\\$]+)\\}"
		p = Pattern.compile("#\\{\\getString*([\\w\\d_]+)\\getString*\\}");
		m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, ":"+m.group(1).trim());
		}
		m.appendTail(sb);
		sql = sb.toString();
		logger.info(sql);
		
		//sqlFy=将:username格式转换为username=?
		p = Pattern.compile(":\\getString*([\\w\\d_]+)");
		
		m = p.matcher(sql);
		sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, m.group(1)+"=?");
		}
		m.appendTail(sb);
		String sqlFy = sb.toString();
		logger.info(sb.toString());
		
		//sqlJdbc=将:username格式转换为?
		m = p.matcher(sql);
		sbJdbc = new StringBuffer();
		while (m.find()) {
//			sb.append(m.replaceAll(m.group(1)));
			m.appendReplacement(sbJdbc, "?");
		}
		m.appendTail(sbJdbc);
		String sqlJdbc = sbJdbc.toString();
		logger.info(sqlJdbc);
		
		//还原单引号
		for (int i = 0;i<rdReplace.size();i++) {
			sqlJdbc = CommonUtil.replace(sqlJdbc, rdReplace.gName(i), "'"+rdReplace.getString(i)+"'");
		}
		
		logger.info(sqlJdbc);
		
		
		rdR.put("sqlJdbc", sqlJdbc);
		rdR.put("sqlFy", sqlFy);
		// rdR.d();
		return rdR;
	}
	public static Object[] getSqlParameter(String sql, Record crmFields) throws AppException {
		Vector vector = new Vector();
		String curHandleFieldName = null;
		String sqlBak = sql;
		sql = removeAquot(sql).trim(); // 代表要进行分析的SQL语句
		if (sql.indexOf("?") == -1) {
			return vector.toArray();
		}

		if (sql.startsWith("insert")) {
			return getInsertParameter(sql, crmFields);

		}
		int paramCount = 1;
		String fieldStr = "";
		while (sql.length() > 0) {
			if (sql.indexOf("?") != -1) {
				fieldStr = sql.substring(0, sql.indexOf("?")).trim();
				sql = sql.substring(sql.indexOf("?") + 1);
			} else {
				break;
			}

			for (int i = 0; i < vKey.size(); i++) {
				String key = ((String) vKey.get(i)).toLowerCase();
				if (fieldStr.endsWith(key)) {
					fieldStr = fieldStr.substring(0, fieldStr.length() - key.length()).trim();
				}
			}
			if (fieldStr.indexOf("[") != -1) {
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf("[") + 1).trim();
			}
			if (fieldStr.indexOf(".") != -1) {
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(".") + 1).trim();
			}
			if (fieldStr.indexOf(" ") != -1)
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(" ") + 1).trim();
			if (fieldStr.indexOf(",") != -1)
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(",") + 1).trim();
			if (fieldStr.startsWith("(")) {
				fieldStr = fieldStr.substring(1);
			}

			curHandleFieldName = fieldStr;
			String fieldValue = "";
			try {
				fieldValue = crmFields.getString(fieldStr);
			} catch (Exception e) {
				logger.info("字段不存在：" + fieldStr);
				throw new AppException("字段不存在：" + fieldStr);
			}

			// 设置预编译参数
			vector.add(fieldValue);
			paramCount++;
		}
		return vector.toArray();
	}

	/**
	 * 获得update操作的SQL语句
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rd
	 *            Record 参数
	 * @param sqlWhere
	 *            String SQL语句的条件表达式，不包含"where"关键字；或作为条件的字段列表，字段间用逗号分割
	 * @param dao
	 *            Dao
	 * @return String update语句
	 */
	public static String getUpdateSql(String tableName, Record rd, String sqlWhere, Dao dao) {
		return getUpdateSql(tableName, rd, sqlWhere, "", dao);
	}

	/**
	 * 获得update操作的SQL语句
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rd
	 *            Record 参数
	 * @param sqlWhere
	 *            String SQL语句的条件表达式，不包含"where"关键字；或作为条件的字段列表，字段间用逗号分割
	 * @param noUpdateField
	 *            String 不进行更新的字段列表；用逗号分隔
	 * @param dao
	 *            Dao
	 * @return String update语句
	 */
	public static String getUpdateSql(String tableName, Record rd, String sqlWhere, String noUpdateField, Dao dao) {
		Record rdNu = new Record();
		FyCol fcWhere = new FyCol();
		StringBuffer sbSql = new StringBuffer();

		if (!sqlWhere.toLowerCase().trim().startsWith("where ")) { // 字段列表
			String strWhere = "where ";
			fcWhere = CommonUtil.toFc(sqlWhere, ",", true);
			for (int i = 0; i < fcWhere.size(); i++) {
				String fieldName = fcWhere.getKey(i);
				if (fieldName.length() > 0) {
					strWhere += fieldName + "=? and ";
				}
			}
			strWhere = strWhere.trim();
			if (strWhere.endsWith("and")) {
				strWhere = strWhere.substring(0, strWhere.length() - 3).trim();
			}
			if (strWhere.equals("where")) {
				strWhere = "";
			}
			sqlWhere = strWhere;
		}

		sbSql.append("update ");
		sbSql.append(tableName.toUpperCase());
		sbSql.append(" set ");
		FyCol fcNoUpdate = CommonUtil.toFc(noUpdateField, ",", true);
		Record rdTable = dao.getResultSetMetaData(tableName);
		for (int i = 0; i < rdTable.size(); i++) {
			String fieldName = rdTable.gName(i);
			if ( rd.containsKey(fieldName) && !fcNoUpdate.containsKey(fieldName) && rd.getValue(fieldName) != null) {
				Field fieldDb = rdTable.gField(fieldName);
				if (fieldDb.isAutoIncrement()) {
					continue;
				}
				if (fcWhere.containsKey(fieldName)) { // SQL条件中的字段，没有更新必要
					continue;
				}

				rd.gField(fieldName).setType(fieldDb.getType());
				// strSql += fieldName + "=?,";
				if (dao.isBracketFieldName()) {
					sbSql.append("[" + fieldName + "]");
				} else {
					sbSql.append(fieldName);
				}

				sbSql.append("=?,");
			} else {
				rdNu.pField(rdTable.gField(fieldName));
			}

		}
		String strSql = sbSql.toString();
		if (strSql.endsWith(",")) {
			strSql = strSql.substring(0, strSql.length() - 1);
		}

		return strSql + " " + sqlWhere + " ";
	}

	/**
	 * 处理insert sql语句中包含函数时的问题 如insert into test1 (f1,f2,f3)
	 * values(?,upper(lower(f1)),?);
	 * 
	 * @param sqlValues
	 *            String insert sql语句里值部分，既values后括号里（不包括括号）的那部分
	 * @return String
	 */
	public static String initInsertSql(String sqlValues) {
		sqlValues = sqlValues.toLowerCase();
		while (sqlValues.indexOf("(") != -1) {
			String sqlHead = sqlValues.substring(0, sqlValues.indexOf("("));
			String sqlTail = sqlValues.substring(sqlValues.indexOf("(") + 1);
			boolean isMatch = false;
			int count = 1;
			for (int i = 0; i < sqlTail.length(); i++) {
				char ch = sqlTail.charAt(i);
				if ('(' == ch) {
					count++;
				} else if (')' == ch) {
					count--;
				}
				if (count == 0) {
					sqlTail = sqlTail.substring(i + 1);
					isMatch = true;
					break;
				}
			} // for
			if (sqlHead.indexOf(",") != -1) {
				sqlHead = sqlHead.substring(0, sqlHead.lastIndexOf(",")) + ",2";
			} else {
				sqlHead = "2";
			}
			if (isMatch) {
				sqlValues = sqlHead + sqlTail;
			} else {
				throw new AppException("值语句无法匹配：" + sqlValues);
			}
			// logger.debug(sqlValues);
		} // while
		return sqlValues;
	}

	/*
	 * 处理SQL语句；剔除SQL语句里单引号里包含的内容,以便为下一步解析SQL准备
	 * 
	 * @param sql sql语句 @return 整理后的sql语句
	 */
	public static String removeAquot(String sql) {
//		sql = sql.toLowerCase();
		String rSql = "";
		String sqlHead = "";
		int count = 1;
		int countQuot = 0; // 单引号个数
		while (sql.length() > 0) {
			if (sql.indexOf("'") != -1) {
				sqlHead = sql.substring(0, sql.indexOf("'"));
				sql = sql.substring(sql.indexOf("'") + 1);
				countQuot++;
			} else {
				sqlHead = sql;
				sql = "";
			}
			if (count % 2 == 0) {
				sqlHead = "1";

			}
			rSql += sqlHead;
			count++;
		}
		if (countQuot % 2 == 1) {
			throw new AppException("SQL语句无法进行初始化处理，请检查单引号使用情况！");
		}
		return rSql;
	}

	/**
	 * 替换掉"''"单引号里的内容，避免参数混淆 单引号必须成双出现，否则处理错误;
	 * 
	 * @param sql
	 *            String
	 * @return Record 元素为''号里的内容，最后一个元素是SQL语句
	 */
	public static Record initSqlRd(String sql) {
		if ( ! debugFlag) {
			sql = sql.toLowerCase();
		}
		String rSql = "";
		String sqlHead = "";
		int count = 1;
		int countQuot = 0; // 单引号个数
		Record rdR = new Record();
		while (sql.length() > 0) {
			if (sql.indexOf("'") != -1) {
				sqlHead = sql.substring(0, sql.indexOf("'"));
				sql = sql.substring(sql.indexOf("'") + 1);
				countQuot++;
			} else {
				sqlHead = sql;
				sql = "";
			}
			if (count % 2 == 0) {
				String key = "##" + countQuot / 2 + "$";
				rdR.put(key, sqlHead);
				sqlHead = key;

			}
			rSql += sqlHead;
			count++;
		}
		if (countQuot % 2 == 1) {
			throw new AppException("SQL语句无法进行初始化处理，请检查单引号使用情况！");
		}
		rdR.put("sql", rSql);
		// rdR.d();
		return rdR;
	}

	public static void main(String[] argv) {
		LongDao dao = new LongDao();
		try {
			
			Record rd = new Record();
			rd.put("uid_cinema","1");
			rd.put("display_name","12");
			rd.put("f3","12");
			rd.put("f0","13");
			
			String sql = "select * from SCH_PLAN where uid='1234' and plan_id in ('1','2') and display_name=? and uid_cinema=? and movie_code in('1','12')";
			for (int i = 0;i<1;i++) {
				dao.query("select * from sch_plan where plan_id="+SqlUtil.sqlLogCursor +" and uid='aa:cc?dd''ee;ff,'");
			}
			sql = "select * from sch_plan";
//			sql = "select a.* from V_SCH_MOVIE_LANGUAGE a,V_SCH_PLAN_TIME b where a.uid_define=b.uid_movie and b.uid=:uid ";
			SqlUtil.getSpecSql(sql);
//			dao.query(sql,rd);
			logger.info(getSqlLogString());
//			String sql = "select '\"fdsfdsf sdfd 13334 \n fds',a.* from ta where   a.text_date <= dateadd(minute,10,"+"  #{ \n cc }, 'ccc ca ',   :aa"+",:text_date,#{i$d}) c=#{  4b_b5bf } ,ff=:cccc";
//			Record rdSql = SqlUtil.getSpecSqlExp(sql);
//			rdSql.d();
//			
////			logger.info("select a.* from ta where   a.text_date <= dateadd(minute,10,#{  text_date})".replaceAll("\\#\\{\\getString*", ":").replaceAll("\\}",""));
////			logger.info("d5dddd".replaceAll("([d]).*/1","cc"));
//			Pattern p = Pattern.compile("\\#\\{(\\getString*[\\w_\\d]+\\getString*)\\}");
//			Matcher m = p.matcher(sql);
//			StringBuffer sb = new StringBuffer();
//			while (m.find()) {
////				sb.append(m.replaceAll(m.group(1)));
//				m.appendReplacement(sb, ":"+m.group(1).trim());
//				logger.info(m.group(1).trim());
//			}
//			m.appendTail(sb);
//			logger.info(sb.toString());
//			
//			p = Pattern.compile(":(\\getString*[\\w\\d]+)([\\getString\\,\\)])");
//			sql = sb.toString();
//			m = p.matcher(sql);
//			sb = new StringBuffer();
//			while (m.find()) {
////				sb.append(m.replaceAll(m.group(1)));
//				m.appendReplacement(sb, m.group(1)+"=?"+m.group(2));
//				logger.info("%"+m.group(2)+"%");
//			}
//			m.appendTail(sb);
//			logger.info(sb.toString());
//			
//			m = p.matcher(sql);
//			StringBuffer sbJdbc = new StringBuffer();
//			while (m.find()) {
////				sb.append(m.replaceAll(m.group(1)));
//				m.appendReplacement(sbJdbc, "?"+m.group(2));
//			}
//			m.appendTail(sbJdbc);
//			logger.info(sbJdbc.toString());
//			
//			Record rdReplace = new Record();
//			p = Pattern.compile("\'([^\']*)\'");
//			m = p.matcher("select '    \n    1 ',a.* FROM t1 where f1='ccc' and f2=? and f3=:fff3 and f4=#{f4}");
//			sbJdbc = new StringBuffer();
//			int index = 0;
//			while (m.find()) {
//				String strG = m.group(1);
//				String key = "'__"+(index++)+"'";
//				rdReplace.put(key,strG);
//				m.appendReplacement(sbJdbc,key);
//			}
//			m.appendTail(sbJdbc);
//			logger.info(sbJdbc.toString());
//			sql = sbJdbc.toString();
//			for (int i = 0;i<rdReplace.size();i++) {
//				sql = CommonUtil.replace(sql, rdReplace.gName(i), "'"+rdReplace.getString(i)+"'");
//			}
//			logger.info(sql);
//			
//			
//			p = Pattern.compile("#\\{([\\w\\d\\$]+)\\}");
//			m = p.matcher("select aa=#{a$a}");
//			sbJdbc = new StringBuffer();
//			while (m.find()) {
//				String strG = m.group(1);
//				String key = "'__"+(index++)+"'";
//				rdReplace.put(key,strG);
//				m.appendReplacement(sbJdbc,key);
//			}
//			m.appendTail(sbJdbc);
//			logger.info(sbJdbc.toString());
//			
//			// SqlUtil.setSqlParameterTest("select a.* from ta where (a=? and (b=? and ((c=?))))",new
//			// Record());
		} catch (Exception e) {

			logger.debug(CommonUtil.toGBK(e.getMessage()));
			e.printStackTrace();
		} finally {
			dao.releaseCon();
		}
	}

	/**
	 * 生成统计数据库记录的SQL语句 会删除所有order by语句。
	 * 
	 * @param sql
	 *            String 查询SQL语句
	 * @return String 统计数据库记录的SQL语句
	 */
	public static String makeCountSql(String sql, boolean hasLimit) {
		String rSql = null;
		sql = sql.trim();
		String strSql = sql.toLowerCase().trim();
		rSql = sql.substring(0, sql.indexOf(" ")) + " count(*) as cnt " + sql.substring(strSql.indexOf(" from "));

		rSql = rSql.trim();
		strSql = rSql.toLowerCase().trim();

		if (hasLimit && strSql.indexOf(" limit ") >= 0) {
			rSql = rSql.substring(0, strSql.lastIndexOf(" limit ")).trim();
		}

		strSql = rSql.toLowerCase().trim();
		Pattern p = Pattern.compile("[\\getString]+order[\\getString]+by[\\getString]+[^\\)]+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(rSql);
		rSql = m.replaceAll("");
		// if (strSql.indexOf(" order by ")>=0) {
		// rSql = rSql.substring(0,strSql.lastIndexOf(" order by "));
		// }
		return rSql;
	}

	/**
	 * 处理insert into ..galues（）格式的SQL语句 支持":fieldName"格式
	 * values（）语句不支持子查询，如不支持：values(?,select field1 from t2 where field2=?,?)
	 * 
	 * @param sql
	 * @param rdParam
	 * @param pst
	 * @param dao
	 * @return
	 */
	public static void setInsertParameter(String sql, Record rdParam, PreparedStatement pst, Dao dao) {
		String sqlBak = sql;
		String remainSql = removeAquot(sql);
		String fieldsNameStr = ""; // 包含insert SQL语句中的字段列表部分
		String valuesStr = ""; // 包含insert SQL语句中的“values”以后的参数部分
		String fieldName = "";
		String questionMark = ""; // 判断是否是需要设置的字段，“？”表示需设置
		int index = 1;
		remainSql = remainSql.substring(remainSql.indexOf("("));
		fieldsNameStr = remainSql.substring(remainSql.indexOf("(") + 1, remainSql.indexOf(")") + 1).trim();

		valuesStr = remainSql.substring(remainSql.lastIndexOf("values") + 6, remainSql.lastIndexOf(")") + 1).trim();

		valuesStr = initInsertSql(valuesStr.substring(1, valuesStr.length() - 1)) + ")";

		while (valuesStr.indexOf(",") != -1 || valuesStr.indexOf(")") != -1) {
			if (valuesStr.indexOf(",") != -1) {
				fieldName = fieldsNameStr.substring(0, fieldsNameStr.indexOf(",")).trim();
				questionMark = valuesStr.substring(0, valuesStr.indexOf(",")).trim();
			} else {
				fieldName = fieldsNameStr.substring(0, fieldsNameStr.indexOf(")")).trim();
				questionMark = valuesStr.substring(0, valuesStr.indexOf(")")).trim();
			}
			// 以下处理SQL SERVER数据库中以关键字作为字段名的情况，如email,user等等
			while (fieldName.startsWith("[")) {
				fieldName = fieldName.substring(1);
			}
			while (fieldName.endsWith("]")) {
				fieldName = fieldName.substring(0, fieldName.length() - 1);
			}
			if (fieldName.lastIndexOf(".") != -1) {
				fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
			}

			if (valuesStr.indexOf(",") != -1) {
				fieldsNameStr = fieldsNameStr.substring(fieldsNameStr.indexOf(",") + 1).trim();
				valuesStr = valuesStr.substring(valuesStr.indexOf(",") + 1).trim();
			} else {
				fieldsNameStr = valuesStr = "";
			}
			if (questionMark.indexOf("?") >= 0) {
				if (questionMark.equals("?")) {

					setParameter(rdParam, fieldName, index, pst, dao);
					sqlBak = CommonUtil.replace(sqlBak,fieldName, fieldName+"="+getDisplaySqlValue(rdParam, fieldName),1);
					index++;
				} else { // ":fieldName"格式的参数
					fieldName = questionMark.substring(0, questionMark.indexOf("=")).trim();
					setParameter(rdParam, fieldName, index, pst, dao);
					sqlBak = CommonUtil.replace(sqlBak,fieldName, fieldName+"="+getDisplaySqlValue(rdParam, fieldName),1);
					index++;
				}
			}
		}
		if (debugFlag) {
			putSqlLog(sqlBak);
			System.err.println(sqlBak+";");
		}
		
	}
	
	/**
	 * 生成用于SQL开发时可看性的字段值
	 * @param rd
	 * @param fieldName
	 * @return
	 */
	public static String getDisplaySqlValue(Record rd,String fieldName) {
		if (rd.containsKey(fieldName)) {
			String getValue = rd.getString(fieldName);
			int type = rd.gType(fieldName);
			switch(type) {
				case Types.VARCHAR:
				case Types.CHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.LONGVARCHAR:
				case Types.LONGNVARCHAR:
				case Types.CLOB:
				case Types.DATE:
				case Types.TIMESTAMP:
					return "'" + getValue + "'";

			}
			return getValue;
		}
		return null;
		
	}

	/**
	 * 给PreparedStatement对象的指定位置参数设置值
	 * 
	 * @param rdParam
	 *            Record 参数集合
	 * @param fieldName
	 *            String 字段名
	 * @param index
	 *            int PreparedStatement需要设置值的参数的位置索引
	 * @param pst
	 *            PreparedStatement PreparedStatement对象
	 * @param dao
	 *            Dao
	 */
	public static void setParameter(Record rdParam, String fieldName, int index, PreparedStatement pst, Dao dao) {
		try {
			if (rdParam.getString(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, "").equals(SqlUtil.PARAM_BY_INDEX_YES)) {
				fieldName = rdParam.gName(index-1);
			}


			Object value = rdParam.getValue(fieldName);

			// if (value != null && value instanceof Object[]) {
			// Object[] aO = (Object[]) value;
			// if (aO.length == 1) {
			// value= aO[0];
			// }
			// }
			int valueType = rdParam.gField(fieldName).getType();
			if (value == null) {
				pst.setNull(index, valueType);
				return;
			}

			switch (valueType) {
			case Types.TIMESTAMP:
			case Types.DATE:
				if (value instanceof String) {
					String strDate = "" + value;
					if (strDate.length() > 0) {
						java.sql.Timestamp d = new java.sql.Timestamp(CommonUtil.strToDate(strDate).getTime());
						// logger.debug(strDate+"%%%%"+d.toString());
						pst.setTimestamp(index, d);
					} else {
						pst.setNull(index, valueType);
					}
				} else if (value instanceof java.util.Date) {
					// pst.setDate(index, new java.sql.Date(((java.util.Date)
					// value).getTime()));
					java.sql.Timestamp d = new java.sql.Timestamp(((java.util.Date) value).getTime());
					pst.setTimestamp(index, d);

				} else if (value instanceof java.sql.Date) {
					pst.setDate(index, (java.sql.Date) value);
				} else if (value instanceof java.sql.Timestamp) {
					pst.setTimestamp(index, (java.sql.Timestamp) value);
				} else {
					throw new AppException(fieldName + ":当前日期型字段值无效！");
				}
				break;

			case Types.TIME:
				if (value instanceof String) {
					String strDate = value.toString();
					if (strDate.length() > 0) {
						// 必须是hh:mm:ss格式
						pst.setTime(index, java.sql.Time.valueOf(strDate));
					} else {
						pst.setNull(index, valueType);
					}
				} else if (value instanceof java.sql.Time) {
					pst.setTime(index, (java.sql.Time) value);

				} else {
					throw new AppException(fieldName + ":当前时间型字段值无效！");
				}
				break;
			case Types.NUMERIC:
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.FLOAT:
			case Types.REAL:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.BIGINT:
			case Types.TINYINT:
				String getValue = value.toString();
				if (getValue.length() == 0) {
					if (dao.isFillZero()) {
						getValue = "0";
					}
				}
				// pst.setString(index, getValue);
				pst.setDouble(index, Double.parseDouble(getValue));
				// pst.setBigDecimal(index, new BigDecimal(getValue));
				break;
			case Types.CLOB:
			case Types.LONGVARCHAR:
			case Types.LONGVARBINARY:
				if (value instanceof java.sql.Clob) {
					pst.setClob(index, (java.sql.Clob) value);
				} else if (value instanceof java.sql.Blob) {
					pst.setBlob(index, (java.sql.Blob) value);
				} else if (value instanceof java.io.Reader) {
					pst.setCharacterStream(index, (java.io.Reader) value, rdParam.gField(fieldName).getLength());
				} else if (value instanceof java.io.InputStream) {
					pst.setCharacterStream(index, (java.io.InputStreamReader) value, rdParam.gField(fieldName).getLength());
				} else {
					getValue = value.toString();
					pst.setCharacterStream(index, new java.io.InputStreamReader(new java.io.ByteArrayInputStream(getValue.getBytes())), getValue.length());
				}
				break;

			case Types.BLOB:

				if (value instanceof java.sql.Clob) {
					pst.setClob(index, (java.sql.Clob) value);
				} else if (value instanceof java.sql.Blob) {
					pst.setBlob(index, (java.sql.Blob) value);
				} else if (value instanceof java.io.Reader) {
					pst.setCharacterStream(index, (java.io.Reader) value, rdParam.gField(fieldName).getLength());
				} else if (value instanceof java.io.InputStream) {
					pst.setCharacterStream(index, new java.io.InputStreamReader((java.io.InputStream) value), rdParam.gField(fieldName).getLength());
				} else {
					getValue = value.toString();
					byte[] aB = getValue.getBytes();
					// logger.debug(aB.length);
					pst.setBinaryStream(index, new java.io.ByteArrayInputStream(aB), aB.length);
				}
				break;
			case Types.DATALINK:
			case Types.JAVA_OBJECT:
			case Types.OTHER:
			case Types.STRUCT:
			case Types.REF:
				pst.setObject(index, value, valueType);
				break;
			case Types.BOOLEAN:
			case Types.BIT:
				if (value instanceof Boolean) {

					pst.setBoolean(index, (Boolean) value);
				} else {
					String strV = value.toString();
					if (strV.equalsIgnoreCase("true")) {
						strV = "true";
					} else if (strV.equalsIgnoreCase("false")) {
						strV = "false";
					} else {
						if (strV.equals("1")) {
							strV = "true";
						} else {
							strV = "false";
						}
					}

					Boolean bv = new Boolean(strV);
					pst.setBoolean(index, bv);

				}
				break;
			default:
				getValue = value.toString();
				String charsetDb = dao.getCharsetDb();
				String charsetClient = dao.getCharsetClient();
				if (charsetDb != null && charsetClient != null) {
					if (!charsetDb.equalsIgnoreCase(charsetClient)) {
						getValue = CommonUtil.ch2(getValue, charsetClient, charsetDb);
					}
				}
				// 一条sql语句中只能包含一个字节长度大于1000的字段，如果有多个，则各字段值会互相错乱
				if (getValue.getBytes().length > 1000) {
					pst.setCharacterStream(index, new java.io.InputStreamReader(new java.io.ByteArrayInputStream(getValue.getBytes())), getValue.length());
				} else {
					pst.setString(index, getValue);
				}
				break;
			}
		} catch (Exception e) {
			throw new AppException("设置参数错误,当前字段为：" + fieldName, e);
		}
	}

	/**
	 * 设置非insert的SQL语句参数。
	 * 此函数根据key=value的格式查找字段，其中“=”表达式可以为">,<>,<,>=,<=,like",具体参考vKey属性
	 * 不支持用作为函数参数形式的字段识别
	 * ，如"trim(field2)=?"将无法识别字段field2,此中情况请使用：trim(field2)=:field2格式
	 * 
	 * 在字段前可以用":"明确说明需要设置参数，如:substr(field1,1,length(field2))=:field111，
	 * 此时将以field111作为需要设置的参数 以":"表示的字段名只和rdFields里的字段匹配，不和数据表的字段匹配
	 * 以":"表示的字段的字段名称必须是"a-z A-Z 0-9"以及"_"、"$"中的字符
	 * 此函数不能适应所有SQL语句的情况，所以对于比较罕见的SQL语句，请慎用
	 * 
	 * @param sql
	 *            String sql语句
	 * @param rdParam
	 *            Record 参数集合
	 * @param pst
	 *            PreparedStatement PreparedStatement对象
	 * @return Record 对于PreparedStatement,返回所有匹配中SQL语句中的字段,字段值为rdFields里的对应字段的值；
	 *         对于CallableStatement,返回所有输出参数字段，字段值是CallableStatement对象参数的索引号
	 */
	public static void setSqlParameter(String sql, Record rdParam, PreparedStatement pst, Dao dao) {
		String sqlFy = sql;
		String sqlBak = "";
		if ( ! debugFlag) {
			sql = removeAquot(sql).trim(); // 代表要进行分析的SQL语句
		}
		
		if (sql.indexOf("?") == -1) {
			putSqlLog(sql);
			logger.info(sql+";");
			return ;
		}
		if (sql.startsWith("insert") && sql.indexOf("values") >= 0) {
			 setInsertParameter(sql, rdParam, pst, dao);
			 return;
		}

		boolean isCall = false; // 是否存储过程调用
		java.sql.ParameterMetaData pmd = null;
//		if (pst instanceof java.sql.CallableStatement) {
//			isCall = true;
//			try {
//
//				pmd = pst.getParameterMetaData();
//			} catch (Exception ex2) {
//				throw new AppException("获取参数元数据错误：" + ex2.getMessage(), ex2);
//			}
//		}

		int paramCount = 1;
		String fieldStr = "";
		int replacePosition = 0;
		while (sql.length() > 0) {
			int position = sql.indexOf("?");

			if (position != -1) {
				fieldStr = sql.substring(0, position).trim();
				sql = sql.substring(position + 1);
			} else {
				break;
				
			}
			String strOne = fieldStr+"?";
			
			int count = fieldStr.length();
			boolean bFindStart = false;//是否找到字段名开始位置(倒序)
			int iEnd = 0;//字段名结束位置
			int iStart = 0;//字段名开始位置
			boolean isMarkField = false;
			for (int j = count-1; j >= 0; j--) {
				char ch = fieldStr.charAt(j);
				
				if (bFindStart) {
					if ( ! ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '$')) {
						iStart = j+1;
						replacePosition = j;
						break;
					}
				}
				else {//确定字段名位置
					if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '$')) {
						bFindStart = true;
						iEnd = j+1;
						
					}
					else {
						if (j == count - 1 && ch ==':') {
							isMarkField = true;
						}
					}
				}
				
			}
			fieldStr = fieldStr.substring(iStart,iEnd);
//			logger.info(replacePosition+"  " + fieldStr);
			
//			for (int i = 0; i < vKey.size(); i++) {
//				String key = (vKey.get(i).toString()).toLowerCase();
//				if (fieldStr.endsWith(key)) {
//					fieldStr = fieldStr.substring(0, fieldStr.length() - key.length()).trim();
//				}
//			}
//
//			if (fieldStr.indexOf("[") != -1) {
//				fieldStr = fieldStr.substring(fieldStr.lastIndexOf("[") + 1).trim();
//			}
//			if (fieldStr.indexOf(".") != -1) {
//				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(".") + 1).trim();
//			}
//			if (fieldStr.indexOf(" ") != -1)
//				replacePosition = fieldStr.lastIndexOf(" ");
//				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(" ") + 1).trim();
//				
//			if (fieldStr.indexOf(",") != -1)
//				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(",") + 1).trim();
//			while (fieldStr.startsWith("(")) {
//				fieldStr = fieldStr.substring(1);
//			}
			// throw new AppException("sql不正确：" + sqlBak);
			// logger.debug("****" + fieldStr);

			// 处理存储过程的输入型参数
			if (isCall) {
				try {
					// 设置输出参数
					if (pmd.getParameterMode(paramCount) == java.sql.ParameterMetaData.parameterModeOut || pmd.getParameterMode(paramCount) == java.sql.ParameterMetaData.parameterModeInOut) {
						((java.sql.CallableStatement) pst).registerOutParameter(paramCount, pmd.getParameterType(paramCount), pmd.getScale(paramCount));

					}
				} catch (Exception ex2) {
					throw new AppException("注册存储过程输出参数错误：" + ex2.getMessage(), ex2);
				}
				try {

					// 如果不是输出参数，设置参数值
					if (pmd.getParameterMode(paramCount) != java.sql.ParameterMetaData.parameterModeOut) {
						rdParam.gField(fieldStr).setType(pmd.getParameterType(paramCount));
						setParameter(rdParam, fieldStr, paramCount, pst, dao);

					}
				} catch (Exception ex2) {
					throw new AppException("设置存储过程参数错误：" + ex2.getMessage(), ex2);
				}

			} else {
				// 设置预编译参数
				setParameter(rdParam, fieldStr, paramCount, pst, dao);

				if (debugFlag) {
					String sqlOne0 = strOne.substring(0,replacePosition);
					String sqlOne1 = strOne.substring(replacePosition);
					String realFieldName = fieldStr;
					if (rdParam.getString(SqlUtil.PARAM_BY_INDEX_FIELD_NAME,"").equals(SqlUtil.PARAM_BY_INDEX_YES)) {
						realFieldName = rdParam.gName(paramCount - 1);
					}
					if (isMarkField) {
						sqlOne1 = CommonUtil.replace(sqlOne1,fieldStr+"=:", getDisplaySqlValue(rdParam, realFieldName),1);
						
					}
					else {
						sqlOne1 = CommonUtil.replace(sqlOne1,fieldStr, fieldStr+"="+getDisplaySqlValue(rdParam, realFieldName),1);
					}
					
					strOne = sqlOne0  + sqlOne1;
					sqlBak += strOne;
				}
				
				
			}
			paramCount++;
		}
		if (debugFlag) {
			sqlBak = sqlBak + sql;
			sqlBak = sqlBak.replaceAll("(>=)?(\\!=)?(<=)?(<>)?[=><]?\\?", " ");
			
			putSqlLog(sqlBak);
			logger.info(""+sqlBak+";");
		}
		
	}

	public static Record setSqlParameterTest(String sql, Record rdParam) {
		Record rdR = new Record();
		sql = removeAquot(sql).trim(); // 代表要进行分析的SQL语句
		if (sql.indexOf("?") == -1) {
			return rdR;
		}

		boolean isCall = false; // 是否存储过程调用
		java.sql.ParameterMetaData pmd = null;

		int paramCount = 1;
		String fieldStr = "";
		while (sql.length() > 0) {
			if (sql.indexOf("?") != -1) {
				fieldStr = sql.substring(0, sql.indexOf("?")).trim();
				sql = sql.substring(sql.indexOf("?") + 1);
			} else {
				break;
			}

			for (int i = 0; i < vKey.size(); i++) {
				String key = (vKey.get(i).toString()).toLowerCase();
				if (fieldStr.endsWith(key)) {
					fieldStr = fieldStr.substring(0, fieldStr.length() - key.length()).trim();
				}
			}

			if (fieldStr.indexOf("[") != -1) {
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf("[") + 1).trim();
			}
			if (fieldStr.indexOf(".") != -1) {
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(".") + 1).trim();
			}
			if (fieldStr.indexOf(" ") != -1)
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(" ") + 1).trim();
			if (fieldStr.indexOf(",") != -1)
				fieldStr = fieldStr.substring(fieldStr.lastIndexOf(",") + 1).trim();
			while (fieldStr.startsWith("(")) {
				fieldStr = fieldStr.substring(1);
			}

			// throw new AppException("sql不正确：" + sqlBak);
			// logger.debug("****" + fieldStr);

			// 处理存储过程的输入型参数
			if (isCall) {
			} else {
				// 设置预编译参数
				logger.debug(fieldStr);
			}
			paramCount++;
		}
		return rdR;
	}

	public static Object getDefaultValueByJdbcType(int jdbcType) {
		Object getValue = null;
		switch (jdbcType) {
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
		case Types.CLOB:
		case Types.BLOB:
		case Types.ARRAY:
		case Types.LONGVARCHAR:
		case Types.DATALINK:
		case Types.JAVA_OBJECT:
		case Types.OTHER:
		case Types.STRUCT:
		case Types.REF:
		case Types.LONGVARBINARY:
			break;
		case Types.NUMERIC:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
			getValue = 0;
			break;
		default:
			getValue = "";
			break;
		}
		
		return getValue;

	}
	
	/**
	 * 根据JDBC类型，转换对应的值.主要是将字符串转换为日期和数值
	 * 只对值为字符串的情况进行转换
	 * 
	 * @param getValue 要转换的值
	 * @param jdbcType
	 * @param fillZero 
	 * @return
	 */
	public static Object getValueByJdbcType(Object getValue,int jdbcType) {
		if (getValue == null) {
			return null;
		}
		//不是字符串，直接返回
		if ( ! (getValue instanceof String)) {
			return getValue;
		}
		switch (jdbcType) {
		case Types.TIMESTAMP:
		case Types.DATE:
		case Types.TIME:
			if (getValue instanceof String) {
				if (getValue.toString().length() == 0) {
					getValue = null;
				}
				else {
					getValue = CommonUtil.strToDate(getValue.toString());
				}
			} 
			break;
		case Types.NUMERIC:
		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.REAL:
			if (getValue instanceof String) {
				if (getValue.toString().length() == 0) {
					getValue = 0;
				}
				else {
					getValue = Double.parseDouble(getValue.toString());
				}
				
			}
			break;
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			if (getValue instanceof String) {
				if (getValue.toString().length() == 0) {
					getValue = 0;
				}
				else {
					getValue = Integer.parseInt(getValue.toString());
				}
				
			}
			
			break;
		case Types.BIGINT:
			if (getValue instanceof String) {
				if (getValue.toString().length() == 0) {
					getValue = 0;
				}
				else {
					getValue = Long.parseLong(getValue.toString());
				}
			}
			break;
		case Types.CLOB:
		case Types.LONGVARCHAR:
		case Types.LONGVARBINARY:		
			break;
		case Types.BLOB:
			break;
		case Types.DATALINK:
		case Types.JAVA_OBJECT:
		case Types.OTHER:
		case Types.STRUCT:
		case Types.REF:
			break;
		case Types.BOOLEAN:
		case Types.BIT:
			if (getValue instanceof String) {

				String strV = getValue.toString();
				if (strV.equalsIgnoreCase("true")) {
					strV = "true";
				} else if (strV.equalsIgnoreCase("false")) {
					strV = "false";
				} else {
					if (strV.equals("1")) {
						strV = "true";
					} else {
						strV = "false";
					}
				}

				getValue = new Boolean(strV);

			}
			break;
		default:
			
			break;
		}
		
		return getValue;

	}
	private static void putSqlLog(String sql) {
//		listLog.set(sqlLogCursor%sqlLogSize ,sql);
//		sqlLogCursor++;
		
	}
	static {
		initLogSize();
	}
	private static void initLogSize() {
		for (int i = 0;i<sqlLogSize;i++) {
			listLog.add("");
		}
	}
	/** 
	 * 
	 * 设置SQL日志条数
	 * @param size
	 */
	public static void setSqlLogSize(int size) {
		synchronized (PARAM_BY_INDEX_FIELD_NAME) {
			sqlLogCursor = 0;
			sqlLogSize = size;
			listLog.clear();
			initLogSize();
		}
	}
	/**
	 * 返回SQL日志条数
	 * @return
	 */
	public static int getSqlLogSize() {
		return sqlLogSize;
	}
	/**
	 * 返回最近执行的SQL语句
	 * @return
	 */
	public static String getSqlLogString(int sqlCount) {
		int count = 0;
		if (sqlLogCursor > 0) {
			StringBuilder sb = new StringBuilder("\n");
			int cursor = (sqlLogCursor -1)%sqlLogSize;
			for (int i = cursor;i>=0;i--) {
				sb.append(listLog.get(i));
				sb.append(";\n");
				count++;
				if (count >=sqlCount) {
					break;
				}
			}
			if (sqlLogCursor >= sqlLogSize) {
				for (int i = sqlLogSize - 1;i>cursor;i--) {
					if (count >=sqlCount) {
						break;
					}
					count++;
					sb.append(listLog.get(i));
					sb.append(";\n");
				}
			}
			return sb.toString();
		}
		else {
			return "";
		}
		
		
	}
	/**
	 * 返回最近执行的SQL语句
	 * @return
	 */
	public static String getSqlLogString() {
		return getSqlLogString(5);		
		
	}
}
