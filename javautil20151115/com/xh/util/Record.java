package com.xh.util;

import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thinkgem.jeesite.common.persistence.Page;


/**
 * Map接口类<br>
 * 记录包装类
 * <p>
 * Title: 记录包装类
 * </p>
 * <p>
 * Description: 用于包装一条数据库记录，也用于前后台数据传递。元素名称忽略大小写,忽略下划线（以下划线开头的元素不忽略）
 * get方法不会有异常，但getString等方法会扔出异常
 * 对于常规类型的属性，除了日期类型,不管属性key是否存在，get()和getXXX()方法都不会返回null值
 * @author adriftor
 * @version 1.1
 */
public class Record implements Map,Serializable {
	public static final Log logger = LogFactory.getLog(Record.class);
	
	
	static RecordSet rsProperty = new RecordSet();

	/**
	 * 缓存bean的getXXX方法，格式为:key=className,value=HashMap<propertyName,Method>
	 */
	private static Map<String,Map<String,Method>> mapClassGetMethod = new HashMap<String,Map<String,Method>>();
	
	/**
	 * 将Object对象根据属性转换为Record对象
	 * 
	 * @param bean 要转换的对象,可以是POJO/Map/Record对象。不可以是基本类型对象、String、BigDecimal、BigInteger
	 * @return Record对象
	 */
	public static Record beanToRd(Object bean) {
		return beanToRd(bean,  null, null,false);


	}

	/**
	 * 将一个javaBean对象的转换为一个Record对象,缓存方式
	 * 只有声明为getXXX()或isXXX方法，二种方法必须没有任何参数，并且返回值不是void，才会被装入Record，
	 * 
	 * @param bean
	 *            bean 要转换的对象,可以是POJO/Map/Record对象。不可以是基本类型对象、String、BigDecimal、BigInteger
	 * @param overRide
	 *            是否覆盖目标Record对象已经有的同名属性。如果bean中的属性和目标Record对象里已经存在的属性同名，
	 *            根据此值来决定是否 要把bean里的属性值复制到Record同名属性里。true为覆盖，false为不覆盖
	 * @param validFieldName
	 *            定义要传递的属性列表，以逗号分隔。如果此值为null或空串，则表示所有属性都传递
	 * @param invalidFieldName
	 *            定义不要传递的属性列表，以逗号分隔。如果此值为null或空串，则表示不做限制。此值具有最高优先权，出现在此值中的
	 *            的属性名，不会进行属性传递，即使此属性名已经在validFieldName里指定
	 * 
	 * @param tranNull
	 *            是否对bean的属性值为null的属性，也进行传递。如果为true，在overRide允许时，
	 *            会删除Record对象里的同名属性
	 * @return Record
	 */
	public static Record beanToRd(Object bean,String validFieldName, String invalidFieldName,boolean tranNull) {

		Record rd = new Record();
		try {
			if (bean instanceof Record) {
				return (Record) bean;

			} else if (bean instanceof Map) {
				rd.putAll((Map) bean);
				return rd;
			}

			if (CommonUtil.isEmpty(validFieldName)) {
				validFieldName = "";
			}
			if (CommonUtil.isEmpty(invalidFieldName)) {
				invalidFieldName = "";
			}
			Record rdValid = CommonUtil.toRd2(validFieldName, ",");
			Record rdInvalid = CommonUtil.toRd2(invalidFieldName, ",");

			Map<String,java.lang.reflect.Method> map = getBeanGetMethods(bean.getClass());
			Iterator<Map.Entry<String,java.lang.reflect.Method>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, java.lang.reflect.Method> entry = it.next();
				Method m = entry.getValue();
				String fieldName = entry.getKey();
				
				if (rdInvalid.containsKey(fieldName)) {
					continue;
				}
				if (rdValid.size() > 0 && !rdValid.containsKey(fieldName)) {
					continue;
				}

				Object getValue = m.invoke(bean, new Object[] {});
				if (getValue != null || tranNull) {
					Field newField = new Field(fieldName,CommonUtil.getJdbcTypeByJavaType(m.getReturnType()), getValue);
					newField.setColumnTypeName(m.getReturnType().getName());
					rd.set(newField);

				} 
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new AppException("javaBean转换Record失败", ex);
		}
		return rd;
	}
	/**
	 * 将一个javaBean对象的转换为一个Record对象
	 * 只有声明为getXXX()或isXXX方法，二种方法必须没有任何参数，并且返回值不是void，才会被装入Record，
	 * 
	 * @param bean
	 *            bean 要转换的对象,可以是POJO/Map/Record对象。不可以是基本类型对象、String、BigDecimal、BigInteger
	 * @param overRide
	 *            是否覆盖目标Record对象已经有的同名属性。如果bean中的属性和目标Record对象里已经存在的属性同名，
	 *            根据此值来决定是否 要把bean里的属性值复制到Record同名属性里。true为覆盖，false为不覆盖
	 * @param validFieldName
	 *            定义要传递的属性列表，以逗号分隔。如果此值为null或空串，则表示所有属性都传递
	 * @param invalidFieldName
	 *            定义不要传递的属性列表，以逗号分隔。如果此值为null或空串，则表示不做限制。此值具有最高优先权，出现在此值中的
	 *            的属性名，不会进行属性传递，即使此属性名已经在validFieldName里指定
	 * 
	 * @param tranNull
	 *            是否对bean的属性值为null的属性，也进行传递。如果为true，在overRide允许时，
	 *            会删除Record对象里的同名属性
	 * @return Record
	 */
	public static Record beanToRdNoCache(Object bean,String validFieldName, String invalidFieldName,boolean tranNull) {

		Record rd = new Record();
		try {
			if (bean instanceof Record) {
				return (Record) bean;

			} else if (bean instanceof Map) {
				rd.putAll((Map) bean);
				return rd;
			} 

			if (CommonUtil.isEmpty(validFieldName)) {
				validFieldName = "";
			}
			if (CommonUtil.isEmpty(invalidFieldName)) {
				invalidFieldName = "";
			}
			Record rdValid = CommonUtil.toRd2(validFieldName, ",");
			Record rdInvalid = CommonUtil.toRd2(invalidFieldName, ",");

			Class c = bean.getClass();
			java.lang.reflect.Method[] aMethod = c.getMethods();
			int size = aMethod.length;
			for (int i = 0; i < size; i++) {
				java.lang.reflect.Method m = aMethod[i];
				String methodName = m.getName();
				if (methodName.startsWith("get") || methodName.startsWith("is")) {
					String fieldName = null;//格式为标准的java属性格式，如userName

					if (methodName.startsWith("get")) {
						fieldName = methodName.substring(3);
					} else {
						fieldName = methodName.substring(2);
					}
					fieldName = fieldName.substring(0,1).toLowerCase()+fieldName.substring(1);
					
					if (rdInvalid.containsKey(fieldName)) {
						continue;
					}
					if (rdValid.size() > 0 && !rdValid.containsKey(fieldName)) {
						continue;
					}
					if (fieldName.equals("class")) {
						continue;
					}
					if ( ! m.isAccessible()) {
						m.setAccessible(true);
					}
					Class clReturn = m.getReturnType();

					Class[] params = m.getParameterTypes();

					String typeName = clReturn.getName();
					if (clReturn != null && !typeName.equals("void")
							&& (params == null || params.length == 0)) { // 返回值不能是void，必须没有方法参数
						
						Object getValue = m.invoke(bean, new Object[] {});
						if (getValue != null || tranNull) {
							Field newField = new Field(fieldName,CommonUtil.getJdbcTypeByJavaType(clReturn), getValue);
							newField.setColumnTypeName(typeName);
							rd.set(newField);

						} 
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new AppException("javaBean转换Record失败", ex);
		}
		return rd;
	}

	/**
	 * 缓存bean的getXXX方法，格式为:key=className,value=HashMap<propertyName,Method>
	 */
	private static Map<String,Method> getBeanGetMethods(Class c) {
		if (mapClassGetMethod.containsKey(c.getName())) {
			return mapClassGetMethod.get(c.getName());
		}

		HashMap<String,Method> hmMethod = new HashMap<String,Method>();
		try {
			if (Map.class.isAssignableFrom(c)) {
				return hmMethod;
			} 
			java.lang.reflect.Method[] aMethod = c.getMethods();
			int size = aMethod.length;
			for (int i = 0; i < size; i++) {
				java.lang.reflect.Method m = aMethod[i];
				String methodName = m.getName();
				if (methodName.startsWith("get") || methodName.startsWith("is")) {
					String fieldName = null;//格式为标准的java属性格式，如userName

					if (methodName.startsWith("get")) {
						fieldName = methodName.substring(3);
					} else {
						fieldName = methodName.substring(2);
					}
					fieldName = fieldName.substring(0,1).toLowerCase()+fieldName.substring(1);
					if (fieldName.equals("class")) {
						continue;
					}
					if ( ! m.isAccessible()) {
						m.setAccessible(true);
					}
					Class clReturn = m.getReturnType();

					Class[] params = m.getParameterTypes();

					String typeName = clReturn.getName();
					if (clReturn != null && !typeName.equals("void")
							&& (params == null || params.length == 0)) { // 返回值不能是void，必须没有方法参数
						
						hmMethod.put(fieldName, m);
					}
				}
			}
		} catch (Exception ex) {
			throw new AppException("获取类的get方法错误："+c.getName(), ex);
		}
		mapClassGetMethod.put(c.getName(), hmMethod);
		return hmMethod;
	}
	
	/**
	 * 处理JSON字符串,转换特殊和中文字符
	 * @param s
	 * @return
	 */
	private static String handleJsonStringValue(String s) {
		if (s == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}
	
	
	private static Object handleJsonValue(Object getValue) {
		if (getValue == null) {
			return null;
		}
		else if (getValue instanceof String ) {
			return "\"" + handleJsonStringValue((String)getValue) + "\"";
		}
		Class<?> c = getValue.getClass();
		if (c.isPrimitive() 
				|| c.equals(java.math.BigDecimal.class) || c.equals(java.math.BigInteger.class)) {
			return getValue;
		}
		else {
			
			String str= "\"" + handleJsonStringValue(getValue.toString()) + "\"";
			System.out.println(str);
			return str;
		}
		
	}
	

	public static void main(String[] argv) {
		// logger.debug(rd.set("a"," a ").set("a").length());

		try {
			Record rd = new Record();
			rd.put("a","av");
			logger.info(rd.get("a2"));
			

//		    PRight right = new PRight(rd);
//		    right.setRightCode("倒萨大房间");
//		       ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("d:/t.txt")));  
//		       oos.writeObject(rd);  
//		       oos.close();  
//		       System.out.println("after:");  
//		       ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:/t.txt"));  
//		       Record after = (Record)ois.readObject();  
//		       ois.close();  
//		       System.out.println(after);  

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	
	private static Object transValueByJdbcType(Object value, int sqlType) {

		if (value == null) {
			return null;
		}
		Object valueR = value;
		switch (sqlType) {
		case Types.DATE:
			if (!(value instanceof java.sql.Date)) {
				if (value instanceof String) {
					String strDate = "" + value;
					if (strDate.length() > 0) {
						valueR = new java.sql.Date(CommonUtil
								.strToDate(strDate).getTime());
					} else {
						valueR = null;
					}
				} else if (value instanceof java.util.Date) {
					valueR = new java.sql.Date(
							((java.util.Date) value).getTime());

				} else if (value instanceof java.sql.Timestamp) {
					Timestamp ts = (Timestamp) value;
					valueR = new java.sql.Date(ts.getTime());
				} else {
					throw new AppException(value + ":当前日期型字段值无效！");
				}
			}

			break;
		case Types.TIME:
			if (!(value instanceof java.sql.Time)) {

				if (value instanceof String) {
					String strDate = "" + value;
					if (strDate.length() > 0) {

						valueR = new java.sql.Time(CommonUtil
								.strToDate(strDate).getTime());
					} else {
						valueR = null;
					}
				} else if (value instanceof java.util.Date) {
					valueR = new java.sql.Time(
							((java.util.Date) value).getTime());

				} else if (value instanceof java.sql.Timestamp) {
					Timestamp ts = (Timestamp) value;
					valueR = new java.sql.Time(ts.getTime());
				} else {
					throw new AppException(value + ":当前时间型字段值无效！");
				}
			}

			break;
		case Types.TIMESTAMP:
			if (!(value instanceof java.sql.Timestamp)) {
				if (value instanceof String) {
					String strDate = "" + value;
					if (strDate.length() > 0) {
						valueR = new java.sql.Timestamp(CommonUtil.strToDate(
								strDate).getTime());
					} else {
						valueR = null;
					}
				} else if (value instanceof java.util.Date) {
					valueR = new java.sql.Timestamp(
							((java.util.Date) value).getTime());

				} else if (value instanceof java.sql.Date) {
					java.sql.Date d = (java.sql.Date) value;
					valueR = new java.sql.Timestamp(d.getTime());
				} else {
					throw new AppException(value + ":当前时间戳型字段值无效！");
				}
			}

			break;
		case Types.NUMERIC:
			if (!(value instanceof BigDecimal)) {
				if (value.toString().trim().length() > 0) {
					valueR = new BigDecimal(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.DECIMAL:
			if (!(value instanceof BigDecimal)) {
				if (value.toString().trim().length() > 0) {
					valueR = new BigDecimal(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.DOUBLE:
			if (!(value instanceof Double)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Double(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.FLOAT:
			if (!(value instanceof Double)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Double(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.REAL:
			if (!(value instanceof Float)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Float(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.CLOB:

			break;
		case Types.BLOB:

			break;
		case Types.ARRAY:

			break;
		case Types.LONGVARCHAR:

			break;
		case Types.DATALINK:

			break;
		case Types.JAVA_OBJECT:

			break;
		case Types.OTHER:

			break;
		case Types.STRUCT:

			break;
		case Types.REF:

			break;
		case Types.LONGVARBINARY:

			break;

		case Types.INTEGER:
			if (!(value instanceof Integer)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Integer(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.BIGINT:
			if (!(value instanceof Long)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Long(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.SMALLINT:
			if (!(value instanceof Short)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Short(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.TINYINT:
			if (!(value instanceof Byte)) {
				if (value.toString().trim().length() > 0) {
					valueR = new Byte(value.toString());
				} else {
					valueR = null;
				}
			}

			break;
		case Types.BIT:
			if (!(value instanceof Boolean)) {
				if (value.toString().trim().length() > 0) {
					String strBit = value.toString().trim();
					if (strBit.equalsIgnoreCase("t")) {
						valueR = true;
					} else {
						valueR = false;
					}
				} else {
					valueR = null;
				}
			}

			break;
		case Types.CHAR:
			if (!(value instanceof Character)) {
				if (value.toString().length() > 0) {
					valueR = new Character(value.toString().charAt(0));
				} else {
					valueR = null;
				}
			}

			break;
		case Types.VARCHAR:
			if (!(value instanceof String)) {
				valueR = value.toString();
			}
			break;
		case Types.BOOLEAN:
			if (!(value instanceof Boolean)) {
				if (value.toString().trim().length() > 0) {
					String strBit = value.toString().trim();
					if (strBit.equalsIgnoreCase("t")) {
						valueR = true;
					} else {
						valueR = false;
					}
				} else {
					valueR = null;
				}
			}

			break;
		default:
			valueR = value;
			break;
		}
		return valueR;
	}


	
	/**
	 * 对于字符串数组类型的值，如果数组长度为1，是否直接把数组值作为元素值
	 */
	protected boolean convertStringGroup = true;

	
	/**
	 * 对KEY是否进行处理。如果为true，将忽略key里的非开头的下划线
	 */

	//返回的key是否是原始名称（字符串格式）,影响keySet() entrySet()
	protected boolean storeRealKeyFlag = true;
	/**
	 * 对于null、空串值，属性是否使用缺省值输出,默认为true。受影响的方法为get(key).如果为false，则按Map方式取值
	 *  如值为null，数值类型输出0，字符串类型输出空串
	 */
	protected boolean outputDefaultValueFlag = true;


	/**
	 * 是否输出真实的key,默认为false
	 * @return
	 */
	protected boolean outputRealKeyFlag = false;
	/**
	 * 列集合
	 */
	protected FyCol<Field> fields = null;
	
	/**
	 * 构造函数
	 */
	public Record() {
		this.fields = new FyCol<Field>(true);
	}
	/**
	 * 
	 * @param storeRealKeyFlag
	 *            如果为true,则会保存真实的key值,但花费存储空间,默认为false
	 */
	public Record(boolean storeRealKeyFlag) {
		this.fields = new FyCol<Field>(storeRealKeyFlag);
	}
	/**
	 * 
	 * @param dataFlag
	 *            如果为true,则取值速度可能提升，但花费存储空间和存储时间。默认为false
	 * @param storeRealKeyFlag
	 *            如果为true,则会保存真实的key值,但花费存储空间,默认为false
	 */
	public Record(boolean storeRealKeyFlag,int size) {
		this.fields = new FyCol<Field>(storeRealKeyFlag,size);
	}

	public Record(int size) {
		this.fields = new FyCol<Field>(true,size);
	}

	public Record(Map map) {
		this(map.size());
		if (map instanceof Record) {
			this.merge((Record)map);
		}
		else {
			putAll(map);
		}
		
	}
	public Record(Map map,boolean storeRealKeyFlag) {
		this(storeRealKeyFlag,map.size());
		if (map instanceof Record) {
			this.merge((Record)map);
		}
		else {
			putAll(map);
		}
		
	}
	/**
	 * 清除所有元素
	 */
	public void clear() {
		this.fields.clear();
	}
	public Object clone() {

		Record rdNew = new Record();
		rdNew.putAll(this, true);
		return rdNew;
	}
	public boolean containsKey(Object key) {
		return fields.containsKey(key);
	}
	
	public boolean containsValue(Object getValue) {
		for (int i = 0; i < this.size(); i++) {
			if (getValue.equals(this.getValue(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 复制常见数据类型的字段
	 * 不会复制值为null的字段
	 * @param rd
	 */
	public void copySingleTypeDataFrom(Record rd) {
		int size = rd.size();
		for (int i = 0;i<size;i++) {
			Object getValue = rd.getValue(i);
			if (getValue != null) {
				if (getValue.getClass().isPrimitive() || getValue.getClass().getName().startsWith("javax") || getValue.getClass().getName().startsWith("java")) {
					Field field = new Field(rd.gRealName(i).toString(),rd.gType(i),rd.getValue(i));
					this.pField(field);
				}
			}
		}
	}
	/**
	 * 在控制台显示所有字段的名、类型、值信息 @
	 */
	public String d() {
		return this.d("",true);
	}
	
	
	public String d(java.io.Writer wr) {
		return this.d(wr, true);
	}
	


	public String d(String preStr) {
		return this.d(preStr,true);
	}

	/**
	 * 在控制台显示所有字段的名、类型、值信息 @
	 */
	public String d(String preStr,boolean isPrint) {
		if (this.size() == 0 && ( ! isPrint)) {
			return preStr+"[]";
		}
		String recordName = "";
		if (preStr.indexOf(",") > 0) {
			recordName = preStr.substring(preStr.indexOf(",")+1);
			preStr = preStr.substring(0,preStr.indexOf(","));
			
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n"+preStr + "Record>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+recordName+">\n");

		String fieldPreStr = preStr + "\t";
		int i = 0, count = size();
		Field field = null;
		for (i = 0; i < count; i++) {

			field = gField(i);
			String fieldName = this.outputRealKeyFlag ? field.getRealName() : field.getName();
			sb.append(fieldPreStr + fieldName + '\t' + field.getTypeName() + '\t');
			Object obj = field.getValue();
			if (obj instanceof RecordSet) {
				sb.append(((RecordSet) obj).d(fieldPreStr+"\t",false));
			} else if (obj instanceof Record) {
				sb.append(((Record) obj).d(fieldPreStr+"\t",false));
			} else {
				Object getValue = field.getValue();
				if (obj != null) {

					sb.append(field.getString());

				} else {
					sb.append("null");
				}

			}
			sb.append(preStr + "\n");
		}
		sb.append(preStr + "Record<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+recordName+"<\n");
		if (isPrint) {
			logger.info(sb.toString());
		}

		return sb.toString();
	}

	public String d(Writer wr, boolean isPrint) {
		int i = 0, count = size();
		Field field = null;
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<tr>");
			for (i = 0; i < count; i++) {
				sb.append("<td>");
				field = gField(i);
				Object obj = field.getValue();
				if (obj instanceof RecordSet) {
					((RecordSet) obj).d(wr);
				} else if (obj instanceof Record) {
					sb.append(((Record) obj).d(wr, false));
				} else {
					String getValue = field.getValue().toString();
					if (getValue.length() == 0) {
						getValue = "&nbsp;";
					}
					sb.append(getValue);
				}
				sb.append("</td>");
			}
			sb.append("</tr>");
		} catch (Exception e) {

		}
		return sb.toString();

	}



	/**
	 * 销毁
	 */
	public void destroy() {
		fields = null;
	}

	/**
	 * 返回字段集合的entrySet对象
	 */
	public Set entrySet() {
		LinkedHashMap<String,Object> hmSet = new LinkedHashMap<String,Object>();
		int size = this.size();
		for (int i = 0;i<size;i++) {
			if (this.outputRealKeyFlag && storeRealKeyFlag) {
				hmSet.put(this.gRealName(i), this.getValue(i));
			}
			else {
				hmSet.put(this.gName(i), this.getValue(i));
			}
			
		}
		return hmSet.entrySet();
	}

	/**
	 * 判断是否相等。只有两者的fields字段和hmName名字集合是同一个时(merge()或者直接赋值)，才返回true
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof Record)) {
			return false;
		}
		Record target = (Record) obj;
		if (target.fields == this.fields) {
			return true;
		}
		return false;
	}

	/**
	 * 将参数rd对象中的字段的值赋给此对象中相同字段名称的字段
	 * 
	 * @param rd
	 *            来源值对象
	 */
	public void fillSameColumnFrom(Record rd) {
		String name = "";
		int fieldCount = rd.size();
		int i = 0;
		for (i = 0; i < fieldCount; i++) {
			name = rd.gField(i).getName();
			if (containsKey(name)) {
				this.set(name, rd.getValue(i));
			}
		}
	}

	/**
	 * 取值
	 * 
	 * @param fieldName
	 *            字段名
	 * @param objOfInstead 如果字段不存在，或为null或为空串，返回objOfInstead
	 * @return 对象
	 */
	public Object g(String fieldName, Object objOfInstead) {
		return this.getValue(fieldName,objOfInstead);
	}
	public List gArrayList() {
		List list = new ArrayList();
		int size = this.size();
		for (int i = 0;i<size;i++) {
			list.add(this.getValue(i));
		}
		return list;
	}
	/**
	 * 取BigDecimal数值
	 * 
	 * @param index
	 * @return @ 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public BigDecimal gBigDecimalValue(int index) {
		return this.gField(index).getBigDecimalValue();
	}

	/**
	 * 取BigDecimal数值
	 * 
	 * @param key
	 * @return 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public BigDecimal gBigDecimalValue(String fieldName) {
		return this.gField(fieldName).getBigDecimalValue();

	}

	public BigDecimal gBigDecimalValue(String fieldName, BigDecimal dInstead) {
		if (this.containsKey(fieldName)) {
			return this.gField(fieldName).getBigDecimalValue();
		} else {
			return dInstead;
		}
	}

	/**
	 * 取BigInteger数值
	 * 
	 * @param index
	 * @return @ 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public BigInteger gBigIntegerValue(int index) {
		return this.gField(index).getBigIntegerValue();
	}

	/**
	 * 取BigInteger数值
	 * 
	 * @param key
	 * @return 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public BigInteger gBigIntegerValue(String fieldName) {
		return this.gField(fieldName).getBigIntegerValue();

	}

	public BigInteger gBigIntegerValue(String fieldName, BigInteger dInstead) {
		if (this.containsKey(fieldName)) {
			return this.gField(fieldName).getBigIntegerValue();
		} else {
			return dInstead;
		}
	}

	/**
	 * 取Boolean数值	
	 * 
	 * @param index
	 * @return null、空串、"f"、"false"返回false,其他返回true
	 */
	public Boolean gBooleanValue(int index) {
		return this.gField(index).getBooleanValue();
	}
	/**
	 * 取Boolean数值
	 * 
	 * @param key
	 * @return null、空串、"f"、"false"返回false,其他返回true
	 */
	public Boolean gBooleanValue(String fieldName) {
		return this.gField(fieldName).getBooleanValue();

	}
	
	protected Object gByJdbcType(int index) {
		return gByJdbcType(this.gName(index));

	}
	protected Object gByJdbcType(int index, Object insteal) {
		if (index < this.size() && index >= 0) {
			return gByJdbcType(this.gName(index), insteal);
		} else {
			throw new AppException("位置" + index + "的字段不存在！");
		}

	}

	protected Object gByJdbcType(String fieldName) {
		if (this.containsKey(fieldName)) {
			Object getValue = this.getValue(fieldName);
			Object vR = transValueByJdbcType(getValue, this.gField(fieldName)
					.getType());
			return vR;
		} else {
			throw new AppException("字段" + fieldName + "不存在！");
		}
	}
	protected Object gByJdbcType(String fieldName, Object insteal) {
		if (this.containsKey(fieldName)) {
			Object getValue = this.getValue(fieldName);

			Object vR = transValueByJdbcType(getValue, this.gField(fieldName)
					.getType());
			if (vR == null || vR.toString().trim().length() == 0) {
				vR = insteal;
			}
			return vR;
		} else {
			return insteal;
		}
	}
	public Date gDate(int index) {
		return this.gField(index).getDate();

	}

	public Date gDate(String fieldName) {
		return this.gField(fieldName).getDate();

	}

	/**
	 * 取浮点数值
	 * 
	 * @param index
	 * @return @ 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public double gDoubleValue(int index) {
		return this.gField(index).getDoubleValue();
	}

	/**
	 * 取浮点数值
	 * 
	 * @param key
	 * @return 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public double gDoubleValue(String fieldName) {
		return this.gField(fieldName).getDoubleValue();

	}

	public double gDoubleValue(String fieldName, double dInstead) {
		if (this.containsKey(fieldName)) {
			return this.gField(fieldName).getDoubleValue();
		} else {
			return dInstead;
		}
	}

	public Object get(int index) {
		return getValue(index);
	}

	/**
	 * 取值
	 * 对于属性,不管属性key是否存在，如果值为null或空串，则会根据数据类型返回对应的缺省值
	 */
	public Object get(Object key) {
		String fieldName = key.toString();
		
		if ( ! this.outputDefaultValueFlag) {
			try {
				Field field = fields.get(fieldName);
				if (field != null) {
					return field.getValue();
				}
			}
			catch(Exception ex) {
			}
			return null;
		}
		Object returnV = null;
		try {
			Field field = fields.get(fieldName);
			if (field != null) {
				returnV = field.getValue();
				
				//值null或者是空串，如果是属性则进行默认值转换
				if ((returnV == null || (returnV instanceof String && returnV.toString().length() == 0)) && isProperty(fieldName)) {
					returnV = CommonUtil.getDefaultValueByJdbcType(this.gPropertyRecord().gType(fieldName));
				}
				return returnV;
			}
		}
		catch(Exception ex) {
		}
		
		//如果是属性值
		if (isProperty(fieldName)) {
			if (containsKey(fieldName)) {
				Field field = this.gField(fieldName);
				returnV =  field.getValue();
				
			}
			if (returnV == null || (returnV instanceof String && returnV.toString().length() == 0)) {
				returnV = CommonUtil.getDefaultValueByJdbcType(this.gPropertyRecord().gType(fieldName));
			}
			
		}
		
		//如果确定包含此元素，转换格式后返回
		else if (containsKey(fieldName)) {
			Field field = this.gField(fieldName);
			returnV = field.getValue();

			
		}
		else {
			returnV = null;
		}

		return returnV;
		

	}

	/**
	 * 确定是否存在fieldName元素。如果确定，返回实际存放的元素key。如果不存在，返回null
	 * 
	 * @param fieldName
	 *            元素key
	 * @return 返回实际存放的元素key。如果不存在，返回null
	 */
	protected String getExistFieldName(String fieldName) {
		if (this.fields.containsKey(fieldName)) {
			return fields.get(fieldName).getName();
		}
		
		return null;

	}

	private Object getOld(Object key) {

		Object returnV = null;
		String fieldName = key.toString();
		//如果是属性值，则调用相应的getXXX方法
		if (isProperty(fieldName)) {
			if (containsKey(fieldName)) {
				returnV =  this.gField(fieldName).getValue();
//				try {
//					String getMethodName = this.gPropertyGetMethod(fieldName, true);
//					if (getMethodName == null) {
//						returnV = this.gField(fieldName).getValue();
//					}
//					else {
//						returnV =  this.getClass().getMethod(getMethodName, null).invoke(this, null);
//					}
//					
//				} catch (Exception e) {
//					logger.warn(fieldName+"根据get方法获取数据异常");
//					returnV =  this.gField(fieldName).getValue();
//					
//				}
			}
			else {
				int jdbcType = this.gPropertyRecord().gType(fieldName);
				returnV =  CommonUtil.getDefaultValueByJdbcType(jdbcType);
			}
			
		}
		
		//如果确定包含此元素，转换格式后返回
		else if (containsKey(fieldName)) {
			try {
				Field field = this.gField(fieldName);
				returnV =  CommonUtil.getValueByJdbcType(field.getValue(), field.getType());
			} catch (Exception e) {
				logger.warn(fieldName+"获取转换数据异常");
				returnV =  this.gField(fieldName).getValue();
			}
			
		}
		else {
			returnV = null;
		}

		return returnV;
//		throw new AppException(key+"字段不存在");
		

	}

	public Page<?> getPage() {
		return (Page<?>)get("page");
	}
	
	public String getString(int index) {
		return this.gField(index).getString();
	}

	public String getString(int index,String instead) {
		if (this.size() > index && index >= 0) {
			return this.gField(index).getString(instead);
		}
		return instead;
	}

	public String getString(String fieldName) {
		return this.gField(fieldName).getString();

	}

	/**
	 * 返回字符串值。如果值为null或是长度为零的字符串，则返回instead
	 * 
	 * @param fieldName
	 *            字段名
	 * @param instead
	 *            缺省值
	 * @return 字符串
	 */
	public String getString(String fieldName,String instead) {
		if (containsKey(fieldName)) {
			return this.gField(fieldName).getString(instead);
		}

		return instead;

	}
	
	
	
	
	
	
	public String s(int index) {
		return this.getString(index);
	}

	public String s(int index,String instead) {
		return getString(index,instead);
	}

	public String s(String fieldName) {
		return this.getString(fieldName);

	}

	/**
	 * 返回字符串值。如果值为null或是长度为零的字符串，则返回instead
	 * 
	 * @param fieldName
	 *            字段名
	 * @param instead
	 *            缺省值
	 * @return 字符串
	 */
	public String s(String fieldName,String instead) {
		return getString(fieldName,instead);

	}
	
	
	
	

	/**
	 * 处理用于SQL 语句里LIKE条件的字符串值 获得字符串值，并且把字符串值里的每个单引号，替换成两个单引号
	 * 
	 * @return String
	 */
	public String getStringForSqlLike(String key) {
		return this.gField(key).getStringForSqlLike();
	}

	/**
	 * 处理用于SQL 语句里LIKE条件的字符串值 获得字符串值，并且把字符串值里的每个单引号，替换成两个单引号
	 * 
	 * @return String
	 */
	public String getStringForSqlLike(String key, String instead) {
		if (containsKey(key)) {
			return this.gField(key).getStringForSqlLike();
		}
		return CommonUtil.replace(instead, "'", "''");
	}

	/**
	 * 用于映射诸如：0——》男，1——》女等等关系
	 * 
	 * @param fieldName
	 *            如说明，其值如0，1等
	 * @param rdReal
	 *            真正存放返回值的对象
	 * @param userInstead
	 *            缺省值
	 * @return
	 */
	public String getStringMapping(String fieldName, Record rdReal) {
		return rdReal.getString(this.getString(fieldName));
	}

	/**
	 * 用于映射诸如：0——》男，1——》女等等关系
	 * 
	 * @param fieldName
	 *            如说明，其值如0，1等
	 * @param rdReal
	 *            真正存放返回值的对象
	 * @param userInstead
	 *            缺省值
	 * @return
	 */
	public String getStringMapping(String fieldName, Record rdReal,
			String instead) {
		return rdReal.getString(this.getString(fieldName), instead);
	}

	/**
	 * 取值,如果不存在key，跑出异常
	 * 此方法不会判断是否是属性，不进行值的转换
	 * @param index 
	 * @param objOfInstead 如果字段不存在，或为null或为空串，返回objOfInstead
	 * @return 对象
	 */
	public Object getValue(int index) {
		return this.gField(index).getValue();

	}

	/**
	 * 取值,如果不存在key，跑出异常
	 * 此方法不会判断是否是属性，不进行值的转换
	 * @param fieldName 字段名
	 * @return 对象
	 */
	public Object getValue(String fieldName) {
		if (containsKey(fieldName) || isProperty(fieldName)) {
			return get(fieldName);
		}
		throw new AppException(fieldName+"字段不存在");
		
		
	}

	/**
	 * 取值,如果不存在key，跑出异常
	 * 此方法不会判断是否是属性，不进行值的转换
	 * @param fieldName 字段名
	 * @param objOfInstead 如果字段不存在，或为null或为空串，返回objOfInstead
	 * @return 对象
	 */
	public Object getValue(String fieldName, Object objOfInstead) {
		if (containsKey(fieldName)) {
			Object obj = gField(fieldName).getValue();
			if (obj != null) {
				return obj;
			}
		}
		return objOfInstead;
	}

	/**
	 * 
	 * @param index
	 * @return 勿调用赋值操作
	 */
	public Field gField(int index) {
		String key = this.gName(index);
		return this.gField(key);
	}

	public Field gField(String fieldName) {
		try {
			Field field = fields.get(fieldName);
			if (field != null) {
				return field;
			}
		}
		catch (Exception ex) {
			
		}
		String foundName = this.getExistFieldName(fieldName);
		if (foundName != null) {
			return fields.get(foundName);
		}
		//如果是属性
		if (this.isProperty(fieldName)) {
			Field field = new Field(fieldName,this.gPropertyRecord().gType(fieldName),CommonUtil.getDefaultValueByJdbcType(this.gPropertyRecord().gType(fieldName)));
			this.pField(field);
			return field;
		}
		throw new AppException("字段：" + fieldName + "不存在");

	}

	/**
	 * 取整数值（四舍五入）
	 * 
	 * @param index
	 * @return @ 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public int gIntValue(int index) {
		return this.gField(index).getIntValue();
	}

	/**
	 * 取整数值（四舍五入）
	 * 
	 * @param key
	 * @return 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public int gIntValue(String fieldName) {
		return this.gField(fieldName).getIntValue();
	}

	public int gIntValue(String fieldName, int iInstead) {
		if (this.containsKey(fieldName)) {
			return this.gField(fieldName).getIntValue();
		} else {
			return iInstead;
		}
	}
	public long gLongValue(int index) {
		return this.gField(index).getLongValue();
	}


	/**
	 * 取整数值（四舍五入）
	 * 
	 * @param key
	 * @return @ 如果字段不存在或该字段的值为非数值，抛出异常，如果字段值是null值或空串，则返回0
	 */
	public long gLongValue(String fieldName) {
		return this.gField(fieldName).getLongValue();
	}



	public long gLongValue(String fieldName, long longInstead) {
		if (this.containsKey(fieldName)) {
			return this.gLongValue(fieldName);
		} else {
			return longInstead;
		}
	}
	/**
	 * 返回位置为index的元素的名称
	 * 
	 * @param index
	 *            int
	 * @return String
	 */
	public String gName(int index) {
		if (this.outputRealKeyFlag && this.storeRealKeyFlag) {
			return this.gRealName(index);
		}
		else {
			return fields.getKey(index);
		}
		
	}



	/**
	 * 获取本类定义的getXXX方法
	 * @return Record对象 key=属性名 VALUE=getXXX,setXxx
	 */
	public Record gPropertyRecord() {
		if (this.getClass() == Record.class) {
			return new Record();
		}
		if (rsProperty.containsKey(this.getClass().getName())) {
			return rsProperty.getRecord(this.getClass().getName());
		}
		Record rd = new Record();
		try {

			Class c = this.getClass();
			java.lang.reflect.Method[] aMethod = c.getMethods();//.getDeclaredMethods();
			int size = aMethod.length;
			for (int i = 0; i < size; i++) {
				java.lang.reflect.Method m = aMethod[i];
				
				String methodName = m.getName();
				Class classDeclaring = m.getDeclaringClass();
				if (methodName.startsWith("get") || methodName.startsWith("is")) {
					
					String fieldName = null;
					if (methodName.startsWith("get")) {
						 fieldName = methodName.substring(3);
					}
					else {
						 fieldName = methodName.substring(2);
					}
					
//					String newFieldName = "";
//					for (int j = 0;j<fieldName.length();j++) {
//						char ch = fieldName.charAt(j);
//						if (j == 0) {
//							newFieldName += (""+ch).toLowerCase();
//						}
//						else {
//							if (ch>='A' && ch<='Z') {
//								newFieldName += "_";
//								newFieldName += (""+ch).toLowerCase();
//							}
//							else {
//								newFieldName += ch;
//							}
//						}
//					}
					String newFieldName = fieldName;
					fieldName = newFieldName;
					
					Class clReturn = m.getReturnType();

					Class[] params = m.getParameterTypes();

					String typeName = clReturn.getName();
					
					if (clReturn != null && !typeName.equals("void")
							&& (params == null || params.length == 0)
							&& classDeclaring != Record.class
							&& classDeclaring != RecordTable.class
							&& classDeclaring != Object.class) { // 返回值不能是void，必须没有方法参数
						if (rd.isNotEmpty(fieldName)) {
							rd.set(fieldName,rd.gField(fieldName).getType(),"get,set");
						}
						else {
							rd.set(fieldName,CommonUtil.getJdbcTypeByJavaType(clReturn), "get");
						}
						
					}
					
				}
				else if (methodName.startsWith("set")) {
					String fieldName = methodName.substring(3);
					String newFieldName = "";
					for (int j = 0;j<fieldName.length();j++) {
						char ch = fieldName.charAt(j);
						if (j == 0) {
							newFieldName += (""+ch).toLowerCase();
						}
						else {
							if (ch>='A' && ch<='Z') {
								newFieldName += "_";
								newFieldName += (""+ch).toLowerCase();
							}
							else {
								newFieldName += ch;
							}
						}
					}
					fieldName = newFieldName;
					Class[] params = m.getParameterTypes();
					if (params != null && params.length == 1 
							&& classDeclaring != Record.class 
							&& classDeclaring != RecordTable.class
							&& classDeclaring != Object.class) {
						if (rd.isNotEmpty(fieldName)) {
							rd.set(fieldName,rd.gField(fieldName).getType(),"get,set");
						}
						else {
							rd.set(fieldName, CommonUtil.getJdbcTypeByJavaType(params[0]), "set");
						}
						
					} // aClass
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new AppException("javaBean转换Record失败", ex);
		}
		rsProperty.addRecord(this.getClass().getName(), rd);
		Long end = System.currentTimeMillis();
		return rd;
	}



	public String gRealName(int index) {
		return fields.getRealKey(index);

	}

	public Object gRealName(String key) {
		String foundName = this.getExistFieldName(key);
		if (key != null) {
			return this.fields.getRealKey(foundName);
		}
		throw new AppException("字段："+key+"不存在");
	}
	/**
	 * 返回真正的key集合,没有转换大小写
	 * 
	 * @return
	 */
	public List<String> gRealNames() {

		return this.fields.keyListReal();
	}

	/**
	 * 返回RecordSet集合
	 * @return 
	 */
	public RecordSet gRecordSet(int index) {
		return (RecordSet) this.getValue(index);
	}

	/**
	 * 返回RecordSet集合，如果不存在或null，返回空集合
	 * @param fieldName
	 * @return 不会返回null
	 */
	public RecordSet gRecordSet(String fieldName) {
		if (this.containsKey(fieldName)) {
			Object getValue = this.getValue(fieldName);
			if (getValue == null) {
				return new RecordSet();
			}
			if (getValue instanceof String) {
				if (getValue.toString().trim().length() == 0) {
					return new RecordSet();
				}
				throw new AppException(getValue+"无法转换成RecordSet对象");
			}
			return (RecordSet) this.getValue(fieldName);
		}
		return new RecordSet();
	}

	public int gType(int index) {
		return this.gField(index).getType();
	}

	public int gType(String key) {
		return this.gField(key).getType();
	}

	/**
	 * 返回指定名称的字段的索引。如果字段不存在，返回－1
	 * 
	 * @param fieldName
	 * @return 返回key的索引位置，没有则返回-1
	 */
	public int indexOfKey(String fieldName) {
		return this.fields.indexOfKey(fieldName);
	}

	/**
	 * 将属性名塞入集合
	 */
	private void initRecord() {
		Record rdProperty = this.gPropertyRecord();
		int size = rdProperty.size();
		for (int i = 0;i<size;i++) {
			if ( ! fields.containsKey(rdProperty.gName(i))) {
				fields.setValue(rdProperty.gName(i), new Field(rdProperty.gName(i),rdProperty.gField(i).getType(),""));
			}
			else {
				((Field)fields.get(rdProperty.gName(i))).setType(rdProperty.gField(i).getType());
			}
			
		}
	}

	/**
	 * 对于字符串数组类型的值，如果数组长度为1，是否直接把数组值作为元素值
	 * 
	 * @return boolean
	 */
	public boolean isConvertStringGroup() {
		return convertStringGroup;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return fields.isEmpty();
	}

	public boolean isEmpty(int index) {
		return this.getString(index, "").length() == 0;
	}

	/**
	 * 检查是否为空值或空串（长度为零的字符串） 如果名称为key的元素不存在，或值是null，或值是空的字符串，都将返回true.
	 * 此函数适用于值为字符串的情况
	 * 
	 * @param key
	 *            String
	 * @return boolean
	 */
	public boolean isEmpty(String key) {
		return this.getString(key, "").length() == 0;
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean isNotEmpty(int index) {
		return !isEmpty(index);
	}

	public boolean isNotEmpty(String key) {
		return !isEmpty(key);
	}


	/**
	 * 对于null、空串值，属性是否使用缺省值输出,默认为true。受影响的方法为get(key)
	 * 如值为null，数值类型输出0，字符串类型输出空串
	 * @param outputDefaultValueFlag
	 */
	public boolean isOutputDefaultValueFlag() {
		return outputDefaultValueFlag;
	}
	


	/**
	 * 是否输出真实的key,默认为false
	 * @return
	 */
	public boolean isOutputRealKeyFlag() {
		return outputRealKeyFlag;
	}

	/**
	 * 判断是否定义了getXXX或者setXXX方法
	 * @param fieldName
	 * @return
	 */
	public boolean isProperty(String fieldName) {
		if (this.getClass()==Record.class) {
			return false;
		}
		return this.gPropertyRecord().containsKey(fieldName);
	}

	
	


	/**
	 * 是否保存原始key（字符串格式）,影响keySet() entrySet()
	 * @return
	 */
	public boolean isStoreRealKeyFlag() {
		return storeRealKeyFlag;
	}

	public Set<String> keySet() {
		if (this.outputRealKeyFlag && this.storeRealKeyFlag) {
			return this.fields.keySetReal();
		}
		else {
			return this.fields.keySet();
		}
		
	}

	

	/**
	 * 将自身和目标Record对象完全按整合。调用此方法后，目标和对象完整一致，自身和目标的操作都将影响双方。
	 * @param target
	 */
	public void merge(Record target) {
		this.fields = target.fields;
		this.storeRealKeyFlag = target.storeRealKeyFlag;
		this.outputDefaultValueFlag = target.outputDefaultValueFlag;
		this.outputRealKeyFlag = target.outputDefaultValueFlag;
		this.convertStringGroup = target.convertStringGroup;
	}
	
	
	public boolean notContainsKey(Object key) {
		return !containsKey(key);
	}
	
	
	public boolean notContainsValue(Object getValue) {
		return !containsValue(getValue);
	}
	/**
	 * 对于字符串数组类型的值，如果数组长度为1，是否直接把数组值作为元素值
	 * 
	 * @param convertStringGroup
	 *            是否转换
	 */
	public void pConvertStringGroup(boolean convertStringGroup) {
		this.convertStringGroup = convertStringGroup;

	}
	
	/**
	 * 增加一个字段，如果该字段已经存在，则值和类型将覆盖原值,名称维持不变
	 * 
	 * @param field
	 */
	public void pField(Field field) {
		String fieldName = field.getRealName();
		if (this.isProperty(fieldName)) {
			field.setType(this.gPropertyRecord().gType(fieldName));
		}
		fields.setValue(fieldName, field);

	}
	


	/**
	 * 对于null、空串值，属性是否使用缺省值输出,默认为true。受影响的方法为get(key)
	 * 如值为null，数值类型输出0，字符串类型输出空串
	 * @param outputDefaultValueFlag
	 */
	public void pOutputDefaultValueFlag(boolean outputDefaultValueFlag) {
		this.outputDefaultValueFlag = outputDefaultValueFlag;
	}

	/**
	 * 是否输出真实的key,默认为false
	 * @return
	 */
	public void pOutputRealKeyFlag(boolean outputRealKeyFlag) {
		this.outputRealKeyFlag = outputRealKeyFlag;
		this.fields.setOutputRealKeyFlag(outputRealKeyFlag);
	}

	/**
	 * 将没有加入集合的属性,加入集合
	 */
	public void propertyToMap() {
		
		Record rdPropery = this.gPropertyRecord();
		if (rdPropery != null && rdPropery.size() > 0) {
			int size = rdPropery.size();
			
			for (int i = 0;i<size;i++) {
				String fieldName = rdPropery.gName(i);
				if ( ! this.containsKey(fieldName)) {
					Field field = new Field(fieldName,rdPropery.gType(i),CommonUtil.getDefaultValueByJdbcType(rdPropery.gType(i)));
					this.pField(field);
				}
			}
		}
	}



	public void pType(int index, int type) {
		this.gField(index).setType(type);
	}

	public void pType(String name, int type) {
		this.gField(name).setType(type);
	}

	public void put(Field field) { // #######
		this.set(field);
	}

	@Override
	public Object put(Object key, Object getValue) { // #######
		this.set(key.toString(), getValue);
		return getValue;
	}

	public void put(String fieldName, int sqlType, Object getValue) {
		this.set(fieldName,sqlType,getValue);
	}

	public void putAll(Map map) {
		putAll(map, true,"");
	}

	public void putAll(Map map, boolean isOverRide) {
		putAll(map,isOverRide,"");
	}

	/**
	 * 
	 * @param map
	 * @param isOverRide 是否覆盖已经有的元素
	 * @param notOverKeys 禁止加入的元素名，逗号分隔，如:"userName,userId"则userName和userId不会被加入
	 */
	public void putAll(Map map, boolean isOverRide,String notOverKeys) {
		if (CommonUtil.isEmpty(notOverKeys)) {
			notOverKeys = "";
		}
		Record rdNotOver = CommonUtil.toRd2(notOverKeys, ",",true);
		
		if (map == null) {
			return;
		}
		if (! (map instanceof Record) ) {

			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				if (rdNotOver.notContainsKey(key)) {
					Object getValue = map.get(key);
					int jdbcDataType = Types.VARCHAR;
					if (getValue != null) {
						if (this.convertStringGroup && getValue instanceof String[] && ((String[])getValue).length == 1) {
							getValue = ((String[])getValue)[0];
						}
						jdbcDataType = CommonUtil.getJdbcTypeByJavaType(getValue.getClass());
						
					}
					if (isOverRide) {
						this.set(key.toString(),jdbcDataType, getValue);
					} else {
						if (!this.containsKey(key)) {
							this.set(key.toString(),jdbcDataType, getValue);
						}
					}
				}
				
			}
		
		}
		else {

			Record rd = (Record) map;
			int size = rd.size();
			for (int i = 0;i<size;i++) {
				if (isOverRide) {
					Field newField = new Field(rd.gName(i),rd.gType(i),rd.getValue(i));
					this.pField(newField);
				}
				else {
					if (this.notContainsKey(rd.gName(i))) {
						Field newField = new Field(rd.gName(i),rd.gType(i),rd.getValue(i));
						this.pField(newField);
					}
					
				}
				
			}
				
		}

		

	}

	/**
	 * 将格式为："key1=v1,key2=v2,key3=v3...."的字符串，按指定分隔符转换成字段
	 * 
	 * @param str
	 *            格式为键值对，如"key1=v1,key2=v2,key3=v3"
	 * @param strCh
	 *            分隔符
	 * @return Record
	 */
	public void putAll(String strKeyChValue, String strCh) {
		if (CommonUtil.isEmpty(strKeyChValue)) {
			return;
		}
		if (strCh == null || strCh.length() == 0) {
			throw new AppException("分隔符不能是null或空串");
		}
		Record rdTmp = CommonUtil.toRd(strKeyChValue, strCh);
		this.putAll(rdTmp);
	}

	/**
	 * 加入MAP的所有元素
	 * @param rd
	 * @param isOverRide
	 */
	public void putAllMerge(Record rd, boolean isOverRide) {
		if (rd == null) {
			return;
		}
		int size = rd.size();
		for (int i = 0;i<size;i++) {
			if (isOverRide) {
				this.pField(rd.gField(i));
			}
			else {
				if (this.notContainsKey(rd.gName(i))) {
					this.pField(rd.gField(i));
				}
				
			}
		}
			
		
	}



	public Object remove(Object key) {
		return this.remove(key.toString());

	}

	/**
	 * 删除一个字段
	 * 
	 * @param key
	 *            字段名
	 */
	public Object remove(String key) {
		if (fields.containsKey(key)) {
			Field field = fields.get(key);
			return field.getValue();
		}
		return null;
	}

	/**
	 * 加入或覆盖一个Field
	 * @param field
	 */
	public void set(Field field) {
		this.pField(field);
	}

	public void set(int index, Object obj) {
		set(this.gName(index), obj);
	}

	/**
	 * put()
	 * @param fieldName
	 * @param sqlType
	 * @param getValue
	 */
	public void set(String fieldName, int sqlType, Object getValue) {
		Field field = new Field(fieldName, sqlType, getValue);
		this.pField(field);
	}

	/**
	 * 设置指定字段的值或新增字段 对于已经存在的字段，则执行更新字段值操作；
	 * 
	 * @param fieldName
	 *            字段名
	 * @param getValue
	 *            如果此对象是Field对象，那么此Field对象的字段名必须和参数fieldName相同，否则抛出异常
	 * @return
	 */
	public void set(String fieldName, Object getValue) {
		if (getValue instanceof Field) {
			Field field = (Field) getValue;
			if (!field.getName().equalsIgnoreCase(fieldName)) {
				throw new AppException(
						"Record.set(String fieldName,Field field)方法错误：fieldName必须和field字段名相同！");
			}
			this.pField((Field) getValue);
		} else {

			if (this.convertStringGroup) {
				if (getValue != null && getValue instanceof String[]) {
					if (((String[]) getValue).length == 1) {
						getValue = ((String[]) getValue)[0];
					}
				}
			}
			if (containsKey(fieldName)) {
				Field field = this.gField(fieldName);
				field.setValue(getValue);
				this.pField(field);
				// this.pField(new Field(fieldName,
				// this.gField(fieldName).getType(), getValue));
			} else {
				int type = Types.VARCHAR;
				if (getValue != null) {
					type = CommonUtil.getJdbcTypeByJavaType(getValue.getClass());
				}
				this.pField(new Field(fieldName, type, getValue));
			}
		}
	}
	
	public void setPage(Page<?> page) {
		set("page",page);
	}
	
	/**
	 * 元素个数
	 * 
	 * @return
	 */
	public int size() {
		return fields.size();
	}


	public <R extends Record> R  subRecord(Class<R> cr,String... keys) {
		R rd;
		try {
			rd =  cr.newInstance();
		} catch (Exception ex) {
			throw new AppException("生成子集对象错误",ex);
		} 
		for(String key : keys) {
			if (this.containsKey(key)) {
				rd.pField(this.gField(key));
			}
			
		}
		return rd;
	}

	

	public <R extends Record> R  subRecord(String... keys) {
		return (R) subRecord(this.getClass(),keys);
	}

	
	/**
	 * 子集
	 * @param keys 逗号分隔
	 * @return
	 */
	public <R extends Record> R  subRecord(String keys) {
		String[] arrKey = keys.split(",");
		return (R) subRecord(this.getClass(), arrKey);
	}
	
	public Object[] toAarry() {
		Object[] os = new Object[this.size()];
		for (int i = 0; i < this.size(); i++) {
			os[i] = this.getValue(i);
		}
		return os;
	}

	public <T> T toBean(Class<T> objClass) {
		return toBean(objClass, false, null, null, false);
	}
	

	
	public <T> T toBean(Class<T> objClass, boolean fillZero) {
		return toBean(objClass, false, null, null, fillZero);
	}
	/**
	 * 将Record对象转换为一个javaBean 只有声明了setXXX()方法的属性才会被赋值
	 * 如果Record的字段值和javaBean对应属性数据类型无法匹配
	 * ，或字段值无法转换（如空串转为Integer)，则不对javaBean对应属性赋值
	 * 
	 * @param objOrClass
	 *            对象或对象类。如果是对象类，必须至少有一个无参数的构造函数
	 * @param overRide
	 *            如果Record对象里没有包含bean的属性值，或Record属性值为null,是否覆盖bean里对应的属性值
	 * @param rdTarget
	 *            目标Record对象，bean属性值将存入此对象。如果此对象为null，则新建一个Record对象作为目标对象。
	 * @param validFieldName
	 *            定义要传递的属性列表，以逗号分隔。如果此值为null或空串，则表示所有属性都传递
	 * @param invalidFieldName
	 *            定义不要传递的属性列表，以逗号分隔。如果此值为null或空串，则表示不做限制。此值具有最高优先权，出现在此值中的
	 *            的属性名，不会进行属性传递，即使此属性名已经在validFieldName里指定
	 * 
	 * @param fillZero
	 *            对于数值型的字段，如果字段值为空串（长度为零的字符串）,在给bean的属性赋值时是否写入0值
	 * @return Object
	 */
	public <T> T toBean(Class<T> objClass, boolean overRide,
			String validFieldName, String invalidFieldName, boolean fillZero) {
//		Class c = objOrClass;
		T bean = null;
		try {
			bean = objClass.newInstance();

	
			if (CommonUtil.isEmpty(validFieldName)) {
				validFieldName = "";
			}
			if (CommonUtil.isEmpty(invalidFieldName)) {
				invalidFieldName = "";
			}
			Record rdValid = CommonUtil.toRd2(validFieldName, ",");
			Record rdInvalid = CommonUtil.toRd2(invalidFieldName, ",");

			java.lang.reflect.Method aMethod[] = objClass.getMethods();
			int size = aMethod.length;
			
			int sizeRdValid = rdValid.size();

			for (int i = 0; i < size; i++) {
				java.lang.reflect.Method m = aMethod[i];
				String methodName = m.getName();
				if (methodName.startsWith("set")) {
					String fieldName = methodName.substring(3);
					if (rdInvalid.containsKey(fieldName)) {
						continue;
					}
					if (sizeRdValid > 0 && rdValid.notContainsKey(fieldName)) {
						continue;
					}

					Class[] params = m.getParameterTypes();
					if (params != null && params.length == 1) {
						// String typeName = aClass[0].getName();
						Object getValue = this.getValue(fieldName, null);
						if (getValue != null) {

							if (getValue instanceof java.util.Date) {
								if (params[0].getName().equals("java.sql.Date")) {
									getValue = new java.sql.Date(
											((java.util.Date) getValue).getTime());
								} else if (params[0].getName().equals(
										"java.sql.Time")) {
									getValue = new java.sql.Time(
											((java.util.Date) getValue).getTime());
								} else if (params[0].getName().equals(
										"java.sql.Timestamp")) {
									getValue = new java.sql.Timestamp(
											((java.util.Date) getValue).getTime());
								}
							}

							getValue = this.tranValueByClass(getValue, params[0], fillZero);
							
							m.invoke(bean, new Object[] { getValue });

//							if (this.isValidValue(getValue, params[0])) {
//
//								m.invoke(form, new Object[] { getValue });
//							} else {
//								logger.debug(fieldName + "数据类型不匹配："
//										+ getValue.getClass().getName() + "!="
//										+ params[0].getName());
//							}
						} // null
						else {// getValue==null
							if (overRide) {
								try {
									m.invoke(bean, new Object[] { null });
								} catch (Exception ex2) {
									logger.debug(fieldName + "无法设置null值："
											+ ex2.getMessage());

								}

							}
						}
					} // aClass
				}
			}
			return bean;
		} catch (Exception ex) {
			ex.printStackTrace();
			this.d();
			throw new AppException("Record转换javaBean(" + objClass.getName() + ")失败",
					ex);
		}
	}
	/**
	 * 将一个字符串s转换为能在json中存储而不破坏其格式的字符串
	 * 
	 * @param s
	 * @return
	 */
	public String toJSON() {
		
		StringBuilder sb = new StringBuilder("{");
		int size = this.size();
		String key;
		for (int i = 0;i<size;i++) {
			if (this.outputRealKeyFlag && this.storeRealKeyFlag) {
				key = this.gRealName(i).toString();
			}
			else {
				key = this.gName(i);
			}
			if (i > 0) {
				sb.append(",");
			}
			sb.append(key).append(":");
			Object getValue = this.get(i);
			//logger.error("%%%%"+getValue);
			getValue = CommonUtil.getValueByJdbcType(getValue,this.gType(i));
			if (getValue == null) {
				sb.append("null");
			}
			else {
				
				if (getValue instanceof java.util.Date) {
					String strV = CommonUtil.dateToStr((Date)getValue);
					if (strV.endsWith(" 00:00:00")) {
						strV = strV.substring(0,10);
					}
					getValue = strV;
				}
				else if (getValue instanceof RecordSet) {
					sb.append("[");
					RecordSet rs = (RecordSet) getValue;
					int sizeRs = rs.size();
					for (int j = 0;j<sizeRs;j++) {
						Record rdNew = rs.getRecord(j);
						if (j>0) {
							sb.append(",");
							
						}
						sb.append(rdNew.toJSON());
					}
					sb.append("]");
				}
				else if (getValue instanceof Record) {
					Record rdNew = (Record) getValue;
					sb.append(rdNew.toJSON());
				}
				else if (getValue instanceof Map) {
					Record rdNew = new Record((Map) getValue);
					sb.append(rdNew.toJSON());
				}
				
				else {
					
					if (getValue instanceof String) {
						sb.append("\"").append(handleJsonStringValue((String)getValue)).append("\"");
					}
					else {
						sb.append(getValue);
					}
				}
				
			}
			
			
			
		}
		sb.append("}");
		

		return sb.toString();
	}
	/**
	 * 转换成Map对象，key值为原来赋值时的key
	 * 
	 * @return
	 */
	public Map<String,Object> toMap() {
		LinkedHashMap<String,Object> hm = new LinkedHashMap<String,Object> ();
		Map<String,Field> map =  fields.toMap();
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			Object key = it.next();
			Field field = map.get(key);
			hm.put(key.toString(), field.getValue());
		}
		return hm;
		
	}
	
	public RecordSet toRs(String fieldNameOfName, String fieldNameOfValue)
			throws Exception {
		RecordSet rs =  new RecordSet();
		rs.addField(this, fieldNameOfName, fieldNameOfValue);
		return rs;
	}


	/**
	 * 
	 * @return
	 */
	public String toString() {
		return this.d("",false);
	}

	/**
	 * 
	 * @param ch
	 * @return
	 */
	public String toStringCh(String ch) {
		StringBuffer sb = new StringBuffer();
		int size = size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append(ch);
			}
			sb.append(this.gField(i).getName());
			sb.append("=");
			sb.append(this.getString(i));
		}

		return sb.toString();
	}

	/**
	 * 将所有值输出为SQL语句中和in条件匹配的格式。如：key1=name1,key2=name2输出为："'name1','name2'"。
	 * @param stringFlag 是否是字符串.true是，false否
	 * @return 如果size()==0，stringFlag=true,返回"''"。如果size()=0,stringFlag=false,返回空串""
	 */
	public String toStringForSqlIn(boolean stringFlag) {
		return CommonUtil.getStringForSqlIn(this.toStringOfValue(","), stringFlag);
		
	}

	/**
	 * 以字符串的形式返回字段名集合，字段名之间以ch为分隔符
	 * 
	 * @param ch
	 *            字段名之间的分隔符
	 * @return 字符串格式的字段名集合
	 */
	public String toStringOfName(String strCh) {
		String strR = "";
		for (int i = 0; i < this.size(); i++) {
			strR += strCh + this.gName(i);
		}
		if (strR.length() > 0 && strCh.length() > 0) {
			strR = strR.substring(strCh.length());
		}

		return strR;
	}
	/**
	 * 以字符串的形式返回值集合，值之间以ch为分隔符
	 * 
	 * @param ch
	 *            值之间的分隔符
	 * @return 字符串格式的值集合
	 */
	public String toStringOfValue(String ch) {
		StringBuffer sb = new StringBuffer();
		int size = size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append(ch);
			}
			sb.append(this.getString(i));
		}
		return sb.toString();
	}
	
	/**
	 * 将对象v根据指定的类型，转换适当的对象
	 * 
	 * @param getValue
	 *            Object 要被转换的值
	 * @param propType
	 *            Class 要被转换成类型
	 * @param fillZero
	 *            boolean 对于数值型类型，如果没有指定值或是零长度的字符串，是否赋为0值
	 * @return Object 转换后的值
	 */
	private Object tranValueByClass(Object getValue, Class propType, boolean fillZero) {
		if (getValue == null) {
			return null;
		}
		if (propType.equals(String.class)) {//类型要求是字符串
			return getValue.toString();
		} else {
			
			if (getValue.getClass().equals(propType)) {//值和类型要求匹配
				return getValue;
			}
			if ( ! (getValue instanceof String)) {
				if (getValue.getClass().getName().startsWith("java.lang") || getValue.getClass().getName().startsWith("java.math.Big")) {
					getValue = getValue.toString();
				}
			}
			
			if (getValue instanceof String ) {
				
				String strV = getValue.toString();
				if (strV.length() > 0) {

					if (propType.equals(Integer.TYPE)
							|| propType.equals(Integer.class)) {
						return new Integer(strV);

					} else if (propType.equals(Boolean.TYPE)
							|| propType.equals(Boolean.class)) {
						return new Boolean(strV);

					} else if (propType.equals(Long.TYPE)
							|| propType.equals(Long.class)) {
						return new Long(strV);

					} else if (propType.equals(Double.TYPE)
							|| propType.equals(Double.class)) {
						return new Double(strV);

					} else if (propType.equals(Float.TYPE)
							|| propType.equals(Float.class)) {
						return new Float(strV);

					} else if (propType.equals(Short.TYPE)
							|| propType.equals(Short.class)) {
						return new Short(strV);

					} else if (propType.equals(Byte.TYPE)
							|| propType.equals(Byte.class)) {
						return new Byte(strV);

					} else if (propType.equals(java.math.BigDecimal.class)) {
						return new java.math.BigDecimal(strV);
					} else if (propType.equals(java.math.BigInteger.class)) {
						return new java.math.BigInteger(strV);
					} else if (propType.equals(java.util.Date.class)) {
						return CommonUtil.strToDate(strV);
					} else if (propType.equals(Timestamp.class)) {
						return new Timestamp(CommonUtil.strToDate(strV)
								.getTime());
					}
				} else {//
					if (fillZero) {
						if (propType.equals(Integer.TYPE)
								|| propType.equals(Integer.class)) {
							return new Integer("0");

						} else if (propType.equals(Long.TYPE)
								|| propType.equals(Long.class)) {
							return new Long("0");

						} else if (propType.equals(Double.TYPE)
								|| propType.equals(Double.class)) {
							return new Double("0");

						} else if (propType.equals(Float.TYPE)
								|| propType.equals(Float.class)) {
							return new Float("0");

						} else if (propType.equals(Short.TYPE)
								|| propType.equals(Short.class)) {
							return new Short("0");

						}
					}
				}
			}
			return getValue;
		}
	}

	/**
	 * 值集合
	 */
	@Override
	public Collection values() {
		List list = new ArrayList();
		int size = this.size();
		for (int i = 0;i<size;i++) {
			list.add(this.getValue(i));
		}
		return Collections.unmodifiableList(list);
	}
}
