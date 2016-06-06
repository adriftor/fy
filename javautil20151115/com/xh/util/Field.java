package com.xh.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 字段名称不区分大小写
 * <p>
 * Title: 字段包装类
 * Company:
 * </p>
 * 
 * @author adriftor
 * @version 1.1
 */
public class Field implements Serializable {
	public static final Log logger = LogFactory.getLog(Field.class);

	protected boolean autoIncrement = false; // 是否自动增长
	protected boolean caseSensitive = false; // 指示列的大小写是否有关系
	protected String columnClassName = null; // 检索指定列的数据库特定的类型名称。
	protected String columnTypeName = null; // 检索指定列的数据库特定的类型名称。
	protected boolean convertStringGroup = false;// 对于字符串数组类型的值，如果数组长度为1，是否直接把数组值作为元素值
	protected boolean currency = false; // 指示指定的列是否是一个哈希代码值
	protected String description = null; // 描述

	protected int length = 0; // 长度,如果字段类型为CLOB/BLOB,在提交数据时，必须指定
	protected String name = null; // 名称，不区分大小写
	protected String realName = null;//真实的名称，没有转换大小写前的名称
	protected boolean nullable = false; // 此字段在数据库中是否可为空值
	protected String opCon;//作为sql条件时的判断连接符号，如"=","like","<="。目前支持"=,<,>,<=,>=,<>"和"like"
	protected boolean outputParameter = false; // 是否存储过程的输出参数
	protected int precision = 0; // 小数位数
	protected boolean primaryKey = false; // 是否是主键
	protected int scale = 0; //小数点右边的位数
	protected String seqName = null; // 序列名称，用于字段为自动增长的情况（如ORACLE数据库里的序列名称）
	protected boolean signed = false; // 指示指定列中的值是否带正负号

	protected int type = 0;
	protected boolean unique = false; // 是否唯一

	protected Object value = null;

	/**
     *
     */
	public Field() {
	}

	/**
	 * 
	 * @param field
	 */
	public Field(Field field) {
		this.realName = field.getName();
		name = field.getName().toLowerCase();
		type = field.getType();
		value = field.getValue();
	}

	/**
	 * 
	 * @param name
	 * @param type
	 * @param value
	 */
	public Field(String name, int type, Object value) {
		this.realName = name;
		this.name = name.toLowerCase();
		this.type = type;
		this.value = value;
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public Field(String name, Object value) {
		this(name, Types.VARCHAR, value);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return null;

	}

	public String getColumnClassName() {
		return columnClassName;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public java.util.Date getDate() {
		if (value == null) {
			return null;
		}
		if (value instanceof java.util.Date) {
			return (java.util.Date) value;
		}
		if (value.toString().trim().length() == 0) {
			return null;
		}
		return CommonUtil.strToDate(value.toString());
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 取得该对象的double值。
	 * 
	 * @return double值。如果为null或为空串，返回0,如果对象类型为非数值型，跳错
	 * 
	 */
	public double getDoubleValue() {

		String str = "";
		if (this.value != null) {
			if (this.value instanceof Double) {
				return (Double) value;
			}
			str = this.value.toString();
			if (str.trim().equals("")) {
				str = "0";
				// throw new AppException(name + ":从Field获取double值错误，field无值");
			} else if (!CommonUtil.isNumberString(str)) {
				throw new AppException(name + ":从Field获取double值错误，field为非数值:" + str);
			}
		} else { // Field值为null
			str = "0";
			// throw new AppException(name + ":从Field获取double值错误，field值为null");
		}
		return Double.parseDouble(str);
	}
	public BigDecimal getBigDecimalValue() {

		String str = "";
		if (this.value != null) {
			if (this.value instanceof BigDecimal) {
				return (BigDecimal) value;
			}
			str = this.value.toString();
			if (str.trim().equals("")) {
				str = "0";
				// throw new AppException(name + ":从Field获取double值错误，field无值");
			} else if (!CommonUtil.isNumberString(str)) {
				throw new AppException(name + ":从Field获取BigDecimal值错误，field为非数值:" + str);
			}
		} else { // Field值为null
			str = "0";
			// throw new AppException(name + ":从Field获取double值错误，field值为null");
		}
		
		return new BigDecimal(str);
	}
	public BigInteger getBigIntegerValue() {

		String str = "";
		if (this.value != null) {
			if (this.value instanceof BigInteger) {
				return (BigInteger) value;
			}
			str = this.value.toString();
			if (str.trim().equals("")) {
				str = "0";
				// throw new AppException(name + ":从Field获取double值错误，field无值");
			} else if (!CommonUtil.isNumberString(str)) {
				throw new AppException(name + ":从Field获取BigInteger值错误，field为非数值:" + str);
			}
		} else { // Field值为null
			str = "0";
			// throw new AppException(name + ":从Field获取double值错误，field值为null");
		}
		
		return new BigInteger(str);
	}
	
	/**
	 * 
	 * @return null、空串、"f"、"false"返回false,其他返回true
	 */
	public Boolean getBooleanValue() {

		String str = "";
		if (this.value != null) {
			if (this.value instanceof Boolean) {
				return (Boolean) value;
			}
			str = this.value.toString().trim();
			if (str.equals("") || str.equalsIgnoreCase("false") || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("0")) {
				return false;
			} else {
				return true;
			}
		} else { // Field值为null
			return false;
		}
	}
	/**
	 * 取得该对象的int值(四舍五入)
	 * 
	 * @return int值。如果对象类型为非数值型，返回0
	 * 
	 */
	public int getIntValue() {

		return (int) java.lang.Math.round(this.getDoubleValue());
	}

	/**
	 * 
	 * @return
	 */
	public int getLength() {
		return this.length;
	}

	public long getLongValue() {

		return java.lang.Math.round(this.getDoubleValue());
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the opCon
	 */
	public String getOpCon() {
		return this.opCon;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}

	public String getSeqName() {
		return seqName;
	}

	/**
	 * 返回字符串值，会对Clob\Object[]\Date对象进行转换
	 * 
	 * @return 如果是null，返回空串
	 */
	public String getString() {
		if (value == null)
			return "";
		if (value instanceof java.sql.Clob) {
			try {
				java.sql.Clob cb = (java.sql.Clob) value;
				return cb.getSubString(1, (int) cb.length());
			} catch (Exception ex) {
				throw new AppException(this.name + ":获取CLOB数据错误！");
			}
		} else if (value instanceof Object[]) {
			Object[] aO = (Object[]) value;
			if (aO.length == 1 && aO[0] != null) {
				return aO[0].toString();
			} else {
				return java.util.Arrays.toString(aO);
			}
		}
		else if (value instanceof String) {
			return (String) value;
		}
		else if (value instanceof java.util.Date) {
			String strDate = CommonUtil.dateToStr((java.util.Date)value,19);
			if (strDate.endsWith("00:00:00")) {
				return strDate.substring(0,10);
			}
			return strDate;
		}

		return value.toString();
	}
	
	/**
	 * 处理用于SQL 语句里LIKE条件的字符串值
	 * 获得字符串值，并且把字符串值里的每个单引号，替换成两个单引号
	 * 
	 * @return String
	 */
	public String getStringForSqlLike() {
		String v = this.getString();
		if (v != null) {
			return CommonUtil.replace(v, "'", "''");
		}
		return v;
	}
	/**
	 * 值为空或为空串(包括只包含空格的字符串)时，返回指定的替代值
	 * 
	 * @param instead
	 * @return
	 */
	public String getString(String instead) {
		String strV = this.getString();
		if (strV == null || strV.length() == 0) {
			return instead;
		}
		return strV;
		
	}
	public String getStringForInput() {
		String rValue = this.getString();
		if (rValue == null)
			return "";
		rValue = CommonUtil.toHTMLInput(rValue);

		return rValue;
	}


	/**
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Get data type name
	 */
	public String getTypeName() {
		String typeName = "Unknown Type " + type;
		switch (type) {
		case Types.ARRAY:
			typeName = "ARRAY";
			break;
		case Types.BIGINT:
			typeName = "BIGINT";
			break;
		case Types.BINARY:
			typeName = "BINARY";
			break;
		case Types.BIT:
			typeName = "BIT";
			break;
		case Types.BOOLEAN:
			typeName = "BOOLEAN";
			break;
		case Types.BLOB:
			typeName = "BLOB";
			break;
		case Types.CHAR:
			typeName = "CHAR";
			break;
		case Types.CLOB:
			typeName = "CLOB";
			break;
		case Types.DATE:
			typeName = "DATE";
			break;
		case Types.DECIMAL:
			typeName = "DECIMAL";
			break;
		case Types.DISTINCT:
			typeName = "DISTINCT";
			break;
		case Types.DOUBLE:
			typeName = "DOUBLE";
			break;
		case Types.FLOAT:
			typeName = "FLOAT";
			break;
		case Types.INTEGER:
			typeName = "INTEGER";
			break;
		case Types.JAVA_OBJECT:
			typeName = "JAVA_OBJECT";
			break;
		case Types.LONGVARBINARY:
			typeName = "LONGVARBINARY";
			break;
		case Types.LONGVARCHAR:
			typeName = "LONGVARCHAR";
			break;
		case Types.NULL:
			typeName = "NULL";
			break;
		case Types.NUMERIC:
			typeName = "NUMERIC";
			break;
		case Types.OTHER:
			typeName = "OTHER";
			break;
		case Types.REAL:
			typeName = "REAL";
			break;
		case Types.REF:
			typeName = "REF";
			break;
		case Types.SMALLINT:
			typeName = "SMALLINT";
			break;
		case Types.STRUCT:
			typeName = "STRUCT";
			break;
		case Types.TIME:
			typeName = "TIME";
			break;
		case Types.TIMESTAMP:
			typeName = "TIMESTAMP";
			break;
		case Types.TINYINT:
			typeName = "TINYINT";
			break;
		case Types.VARBINARY:
			typeName = "VARBINARY";
			break;
		case Types.VARCHAR:
			typeName = "VARCHAR";
			break;
		case Types.NVARCHAR:
			typeName = "NVARCHAR";
			break;
		}
		return typeName;
	} // end of getTypeName()

	/**
	 * 
	 * @return
	 */
	public Object getValue() {
		// if (value != null && value instanceof Object[]) {
		// Object[] aO = (Object[]) value;
		// if (aO.length == 1) {
		// return aO[0];
		// }
		// else {
		// return java.util.Arrays.toString(aO);
		// }
		// }
		return value;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isConvertStringGroup() {
		return convertStringGroup;
	}

	public boolean isCurrency() {
		return currency;
	}


	public boolean isNullable() {
		return nullable;
	}

	public boolean isOutputParameter() {
		return outputParameter;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public boolean isSigned() {
		return signed;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUnique() {
		return unique | autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public void setColumnClassName(String columnClassName) {
		this.columnClassName = columnClassName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public void setConvertStringGroup(boolean convertStringGroup) {
		this.convertStringGroup = convertStringGroup;
	}

	public void setCurrency(boolean currency) {
		this.currency = currency;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/**
	 * 
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * 设置作为sql条件时的判断连接符号，如"=","like","<="
	 * @param opCon 作为sql条件时的判断连接符号，如"=","like","<="，可以为null值或空串
	 */
	public void setOpCon(String opCon) {
		this.opCon = opCon;
	}

	public void setOutputParameter(boolean outputParameter) {
		this.outputParameter = outputParameter;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * 
	 * @param primaryKey
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	/**
	 * 
	 * @param type
	 */
	public Field setType(int type) {
		this.type = type;
		return this;
	}

	/**
	 * 
	 * @param unique
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * 
	 * @param value
	 */
	protected Field setValue(Object value) {
		if (this.convertStringGroup) {
			if (value != null && value instanceof String[]) {
				if (((String[]) value).length == 1) {
					value = ((String[]) value)[0];
				}
			}
		}
		this.value = value;
		return this;
	}

	public String getRealName() {
		return realName;
	}



	/**
	 * 
	 * @return
	 */
	public String toString() {
		String str = "";
		str += "name=" + getName();
		str += "\ttype=" + getTypeName();
		str += "\tvalue=" + getValue();
		return str;
	}

} // end of class
