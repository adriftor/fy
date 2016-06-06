package com.xh.util;

import java.io.Serializable;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thinkgem.jeesite.common.persistence.Page;

/**
 * List接口类<br>
 * 记录集包装类
 * Title: 记录集包装类
 * Description: 用于存储多行记录，其元素为Record对象，一个Record对象代表记录集的一行。 一般跟Record、Field类一起使用。元素名称忽略大小写
 * @author adriftor
 * @version 1.1
 */
public class RecordSet<T extends Record>  implements Serializable, List<T> {
	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(RecordSet.class);
	protected int pageNo = 1;
	protected int pageSize = 20;
	protected int totalCount = 0;
	
	//返回结果	
	private Class<? extends Record> recordClass = Record.class;

	public static RecordSet<Record> beanListToRs(List list) {
		return beanListToRs(list, null,Record.class);
	}
	/**
	 * 批量转换
	 * @param list Bean对象集合,Bean可以是POJO/Map/Record对象。不可以是基本类型对象、String、BigDecimal、BigInteger
	 * @param fieldNameOfRd 其值作为记录名的字段 
	 * @param c 类型
	 */
	public static <R extends Record> RecordSet<R> beanListToRs(List list, String fieldNameOfRd,Class<R> c) {
		RecordSet<R> rs = new RecordSet<R>();
		boolean bName = fieldNameOfRd != null && fieldNameOfRd.length() > 0;
		int size = list.size();
		for (int i = 0;i<size;i++) {
			Object obj = list.get(i);
			R rd = null;
			
			try {
				rd = c.newInstance();
			} catch (InstantiationException e) {
				throw new AppException(e);
			} catch (IllegalAccessException e) {
				throw new AppException(e);
			}
			
			Record rdBean = Record.beanToRd(obj);
			rd.merge(rdBean);
			
			if (bName) {
				rs.addRecord(rd.getString(fieldNameOfRd), rd);
			} else {
				rs.addRecord(rd);
			}
		}
		return rs;
	}
	/**
	 * 批量转换
	 * @param list Bean对象集合,Bean可以是POJO/Map/Record对象。不可以是基本类型对象、String、BigDecimal、BigInteger
	 */


	

	public static void main(String[] argv) {
		Record rd1 = new Record();
		rd1.set("f1",1);
		rd1.set("map",new RecordSet());
		Record rd2 = new Record();
		rd2.put("f1",2);
		RecordSet<Record> rs = new RecordSet<Record>();
//		List<Record> rs = new LinkedList<Record>();
		rs.add(rd1);
		rs.add(rd2);
		
//		order22(rs,"f1", true, true, false);
		rs.d();
		logger.error(JsonUtils.toJson(rs));
	
	}

	protected int pageCount = 0;
	protected FyCol<T> records = new FyCol<T>();

	/**
     *
     */
	public RecordSet() {

	}
	/**
	 * 根据List生成RecordSet对象
	 * <br>必须保证类的泛型和属性recordClass保持一致，否则可能发生错误
	 * @param list 包含Record元素的List对象
	 * @param recordClass 记录目标类型
	 * @param recordName 记录名字段
	 */
	public RecordSet(List<? extends Record> list,Class<? extends Record>recordClass, String recordName) {
		if (list != null && list.size() >0) {
			if (recordClass != null) {
				this.recordClass = recordClass;
			}
			
			int size = list.size();
			if (size == 0) {
				return;
			}
			
			//判断是否是相同的Record类，没指定要转的类或者指定要转的类和集合里的类的类型一致，就是相同的类
			boolean bSameClass = false;
			if (recordClass == null || list.get(0).getClass().equals(recordClass)) {
				bSameClass = true;
			}

			try {
				boolean isRecordSet = list instanceof RecordSet;
				RecordSet rsList = null;
				if (isRecordSet) {
					rsList = (RecordSet) list;
				}
				for (int i = 0;i<size;i++) {
					Record rd = list.get(i);
					if ( ! bSameClass) {
						T rdT =  this.genTByRecordClass(this.recordClass);
						rdT.merge(list.get(i));
						rd = rdT;
					}
					
					
					if (CommonUtil.isNotEmpty(recordName)) {
						this.addRecord(rd.getString(recordName), (T) rd);
					}
					else {
						if (isRecordSet) {
							this.addRecord(rsList.getRecordName(i),(T) rd);
						}
						else {
							this.addRecord( (T) rd);
						}
						
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
	}
	public RecordSet(List<? extends Record>  list,Class<? extends Record>recordClass) {
		this(list,recordClass,null);
	}
	public RecordSet(List<? extends Record>  list,String recordName) {
		this(list,null,recordName);
	}
//	public RecordSet(List<? extends Record>  list) {
//		this(list,null,null);
//	}
	public RecordSet(Class<T> c) {
		this.recordClass = c;
	}


	/**
	 * 构造器
	 * 
	 * @param rs
	 */
	public RecordSet(ResultSet rs) {
		this(rs, 1, 10000);
	}

	/**
	 * 构造器
	 * 
	 * @param rs
	 * @param pageFrom
	 *                开始页，从1开始
	 * @param pageSize
	 *                页大小
	 */
	public RecordSet(ResultSet rs, int pageFrom, int pageSize) {
		this(rs, pageFrom, pageSize, null, true);
	}

	public RecordSet(ResultSet rs, int pageFrom, int pageSize, String rdNameField, boolean rsClose) {
		this(rs, pageFrom, pageSize, rdNameField, rsClose, null, null);
	}

	public RecordSet(ResultSet rs, int pageFrom, int pageSize, String rdNameField, boolean rsClose, String charsetDb, String charsetClient) {
		this(rs, pageFrom, pageSize, rdNameField, rsClose, charsetDb, charsetClient, true);
	}

	public RecordSet(ResultSet rs, int pageFrom, int pageSize, String rdNameField, boolean rsClose, String charsetDb, String charsetClient, Class<? extends Record> recordClass) {
		this(rs, pageFrom, pageSize, rdNameField, rsClose, charsetDb, charsetClient, true, recordClass);
	}

	public RecordSet(ResultSet rs, int pageFrom, int pageSize, String rdNameField, boolean rsClose, String charsetDb, String charsetClient, boolean absolute) {
		this(rs, pageFrom, pageSize, rdNameField, rsClose, charsetDb, charsetClient, true, null);
	}

	/**
	 * 构造器
	 * 
	 * @param rs
	 *                ResultSet对象
	 * @param rsClose
	 *                ResultSet是否关闭
	 * @param keyField
	 *                作为一行名称的字段,必须是唯一（unique）列，否则相同名称的行的数据会被覆盖
	 * @param pageFrom
	 *                开始页，从1开始
	 * @param pageSize
	 *                页大小
	 * @param absolute
	 *                是否采用ResultSet.absolute定位分页
	 */
	public RecordSet(ResultSet rs, int pageFrom, int pageSize, String rdNameField, boolean rsClose, String charsetDb, String charsetClient, boolean absolute, Class<? extends Record> recordClass) {
		if (recordClass != null) {
			this.recordClass = recordClass;
		}

		pageSize = pageSize <= 0 ? 20 : pageSize;
		pageFrom = pageFrom < 1 ? 1 : pageFrom;
		this.setPageNo(pageFrom);
		this.setPageSize(pageSize);

		int count = 0;
		try {
			if (absolute) {
				if (pageFrom > 1) {
					rs.absolute(pageSize * (pageFrom - 1));
				}
				while (rs.next()) {
					count++;
					if (count <= pageSize) {
						parseResultSet(rs, rdNameField, charsetDb, charsetClient);
					}
				} // while
			} else {
				while (rs.next()) {
					count++;
					if (count > pageSize * (pageFrom - 1) && count <= pageSize * pageFrom) {
						parseResultSet(rs, rdNameField, charsetDb, charsetClient);
					} else if (count > pageSize * pageFrom) { // 已经取完了预定数据
						break;
					}
				} // while
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (rsClose)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("close rs error");
		}
	}

	public void add(int index, T obj) {
		this.records.add(index, obj);
	}

	public boolean add(T obj) {
		this.addRecord(obj);
		return true;
	}
	
	public boolean addAll(Collection<? extends T> c) {
		java.util.Iterator<? extends T> it = c.iterator();
		while (it.hasNext()) {
			T obj = it.next();
			this.add(obj);
		}
		return true;
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		java.util.Iterator<? extends T> it = c.iterator();
		while (it.hasNext()) {
			T obj = it.next();
			this.add(index, obj);
		}
		return true;
	}
	
//	/**
//	 * 加入一批记录
//	 * @param c Bean对象集合//
//	 */
//	public void addAllEx(Collection c) {
//		this.addAllEx(c,null);
//	}
//	
//	/**
//	 * 加入一批记录
//	 * @param c Bean对象集合
//	 * @param fieldNameOfRecord 其值作为记录名的字段 
//	 */
//	public void addAllEx(Collection c,String fieldNameOfRecord) {
//		java.util.Iterator it = c.iterator();
//		while (it.hasNext()) {
//			
//			Object obj = it.next();
//			Record rd = Record.beanToRd(obj);
//			
//			T t = (T) this.genRecordByRecordClass(this.recordClass);
//			t.append(rd);
//			if (CommonUtil.isNotEmpty(fieldNameOfRecord) && t.containsKey(fieldNameOfRecord)) {
//				this.addRecord(t.getString(fieldNameOfRecord),t);
//			}
//			else if (CommonUtil.isEmpty(fieldNameOfRecord)){
//				this.addRecord(t);
//			}
//			else {
//				throw new AppException("未找到"+fieldNameOfRecord+"字段！");
//			}
//		}
//	}
	/**
	 * 将Record对象转换成一个RecordSet对象上每条记录的新字段 转换后的RecordSet对象，每条记录都包含两个字段fieldNameOfName和fieldNameOfValue,值为Record 对象每个字段的名称和值 RecordSet对象必须是空的，或记录数和Record对象包含的字段个数相同，否则抛出异常
	 * <br>必须保证类的泛型和属性recordClass保持一致，否则可能发生错误
	 * @param fieldNameOfName
	 *                String 存储字段名称的字段名称
	 * @param fieldNameOfValue
	 *                String 存储字段值的字段名称
	 * @param rd
	 *                Record
	 * @return RecordSet
	 */
	public void addField(Record rd, String fieldNameOfName, String fieldNameOfValue) throws Exception {
		int sizeRs = this.size();
		if (sizeRs == 0) {
			int size = rd.size();
			for (int i = 0; i < size; i++) {
				T rdTmp =  this.genTByRecordClass(this.recordClass);
				rdTmp.set(fieldNameOfName, rd.gName(i));
				rdTmp.set(fieldNameOfValue, rd.getValue(i));
				addRecord(rdTmp);
			}
		} else {
			if (sizeRs == rd.size()) {
				for (int i = 0; i < sizeRs; i++) {
					Record rdTmp = this.r(i);
					rdTmp.set(fieldNameOfName, rd.gName(i));
					rdTmp.set(fieldNameOfValue, rd.getValue(i));
				}
			} else {
				throw new AppException("addField方法：RecordSet对象的记录数量和Record字段数量不相同！");
			}
		}
	}

	private T genTByRecordClass(Class<? extends Record> c) {
		return (T) this.genRecordByRecordClass(c,0);
	}
	private T genTByRecordClass(Class<? extends Record> c,int size) {
		return (T) this.genRecordByRecordClass(c,size);
	}
	private Record genRecordByRecordClass(Class<? extends Record> c,int size) {
		try {
			if (size > 0) {
				return c.getDeclaredConstructor(Integer.class).newInstance(size);
			}
			else {
				return c.newInstance();
			}
			
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	/**
	 * 将FyCol对象转换成一个RecordSet对象上每条记录的新字段 转换后的RecordSet对象，每条记录都包含一个字段fieldName,值为FyCol对应索引位置字的值 RecordSet对象必须是空的，或记录数和FyCol对象包含的元素个数相同，否则抛出异常
	 * <br>必须保证类的泛型和属性recordClass保持一致，否则可能发生错误
	 * @param fieldName
	 *                String 新字段的名字
	 * @param fc
	 *                FyCol
	 * @param ch
	 *                String
	 * @param valueAsRecordName
	 *                是否作为记录名。此值只有在RecordSet是空集合时才有效
	 * @return RecordSet
	 */
	public void addField(String fieldName, Record fc, boolean valueAsRecordName)  {
		int sizeRs = this.size();
		if (sizeRs == 0) {
			int size = fc.size();
			for (int i = 0; i < size; i++) {
				if (valueAsRecordName) {
					T rdTmp = this.genTByRecordClass(this.recordClass);
					rdTmp.set(fieldName, fc.getString(i));
					addRecord(fc.getString(i), rdTmp);
				} else {
					T rdTmp =  this.genTByRecordClass(this.recordClass);
					rdTmp.set(fieldName, fc.getString(i));
					addRecord(rdTmp);
				}

			}
		} else {
			if (sizeRs == fc.size()) {
				for (int i = 0; i < sizeRs; i++) {
					this.r(i).set(fieldName, fc.getString(i));

				}
			} else {
				throw new AppException("addField方法：RecordSet对象的记录数量和FyCol元素数量不相同！");
			}
		}
	}

	/**
	 * 将Record对象转换成一个RecordSet对象上每条记录的新字段 转换后的RecordSet对象，每条记录都包含一个字段fieldName,值为Record对应索引位置字的值 RecordSet对象必须是空的，或记录数和Record对象包含的字段个数相同，否则抛出异常 fc的值不作为RecordSet的记录名
	 * 
	 * @param fieldName
	 *                String 新字段的名字
	 * @param fc
	 *                FyCol
	 * @return RecordSet
	 */
	public void addField(String fieldName, Record fc) throws Exception {
		addField(fieldName, fc, false);
	}



	/**
	 * 将格式：a,b,c,d的字符串转换成一个RecordSet对象上每条记录的新字段 转换后的RecordSet对象，每条记录都包含一个字段fieldName,值为字符串对应索引位置字符（串） RecordSet对象必须是空的或和字符串（strFields）包含相同的记录数，否则抛出异常 值不作为RecordSet的记录名
	 * 
	 * @param fieldName
	 *                String 新字段的名字
	 * @param strFields
	 *                String
	 * @param ch
	 *                String
	 * @return RecordSet
	 */
	public void addField(String fieldName, String strValues, String ch) {
		addField(fieldName, strValues, ch, false);
	}

	/**
	 * 将格式：a,b,c,d的字符串转换成一个RecordSet对象上每条记录的新字段 转换后的RecordSet对象，每条记录都包含一个字段fieldName,值为字符串对应索引位置字符（串） RecordSet对象必须是空的或和字符串（strFields）包含相同的记录数，否则抛出异常
	 * 
	 * @param fieldName
	 *                String 新字段的名字
	 * @param strFields
	 *                String
	 * @param ch
	 *                String
	 * @param valueAsRecordName
	 *                值是否作为记录名。如果为true,实际生成的RecordSet对象将会覆盖相同名称的元素。如“000,001,000”，转换后的只有2条记录000和001
	 * 
	 * @return RecordSet
	 */
	public void addField(String fieldName, String strValues, String ch, boolean valueAsRecordName)  {
		Record fc = CommonUtil.toRd2(strValues, ch, valueAsRecordName);
		addField(fieldName, fc, valueAsRecordName);
	}

	public RecordSet<T> addRecord(int index, T rd) {
		this.records.add(index, rd);
		return this;
	}





	/**
	 * 增加一行
	 * 
	 * @param Record
	 * @return
	 */
	public RecordSet addRecord(T rd) {
		this.records.add(rd);
		return this;
	}

	/**
	 * 增加一行。如果已经存在相同名称的记录，原记录会北覆盖
	 * 
	 * @param rdName
	 *                此行的名称
	 * @param Record
	 * @return
	 */
	public RecordSet addRecord(String rdName, T Record) {
		this.records.setValue(rdName, Record);
		return this;
	}

	/**
	 * 加入SBRecrodSet中的所有记录
	 * 
	 * @param sb
	 *                SBRecrodSet对象
	 * @return 加入的记录数
	 */
	public int addRecords(RecordSet<T> rs) {
		int addCount = 0;
		if (rs != null) {
			int count = rs.size();
			for (int i = 0; i < count; i++) {
				addRecord(rs.r(i));
				addCount++;
			}
		}
		return addCount;
	}

	public int addRecords(RecordSet<T> rs, boolean overrideByRecordName) {
		int addCount = 0;
		if (rs != null) {
			int count = rs.size();
			for (int i = 0; i < count; i++) {
				if (overrideByRecordName) {
					this.addRecord(rs.getRecordName(i), rs.r(i));

				} else {
					addRecord(rs.r(i));
				}

				addCount++;
			}
		}
		return addCount;
	}

	public void clear() {
		this.records.clear();

	}

	public boolean contains(Object obj) {
		return this.records.containsValue(obj);
	}

	public boolean containsAll(Collection<?> c) {
		java.util.Iterator<?> it = c.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!this.contains(obj)) {
				return false;
			}
		}
		return true;
	}

	public boolean containsKey(String rdName) {
		return this.records.containsKey(rdName);
	}

	public boolean notContainsKey(String rdName) {
		return !this.containsKey(rdName);
	}

	public String d() {
		return this.d("",true);
	}
	public String d(String preStr) {
		return this.d(preStr,true);
	}
	/**
	 * 在控制台显示数据信息 @
	 */
	public String d(String preStr,boolean isPrint) {
		if (this.size() == 0 && ( ! isPrint)) {
			return preStr+"[]";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n"+preStr+"rs-start%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		Record rec = null;
		for (int i = 0; i < size(); i++) {
			rec = r(i);
			String name = this.getRecordName(i);
			if (name.indexOf("__") >= 0) {
				name = "";
			}
			sb.append(rec.d(preStr+"\t"+","+name,false));
		}
		sb.append(preStr+"rs-end%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		if (isPrint) {
			logger.info(sb.toString());
		}
		return sb.toString();
	}

	public void d(Writer wr) {
		int i = 0;
		Record rec = null;
		try {
			if (this.size() == 0) {
				return;
			}
			wr.write("<table border=1 >");
			wr.write("<tr>");
			Record rd = this.r(0);
			int count = rd.size();

			for (i = 0; i < count; i++) {
				Field field = rd.gField(i);
				wr.write("<th>" + field.getName() + "</th>");
			}
			wr.write("</tr>");
			for (i = 0; i < size(); i++) {
				rec = r(i);
				rec.d(wr);
			}
			wr.write("</table>");
		} catch (Exception e) {
		}
	}

	/**
     *
     */
	public void displayFieldContent() {
		int i = 0, j = 0;
		Record rec = null;
		for (i = 0; i < size(); i++) {
			rec = r(i);
			for (j = 0; j < rec.size(); j++) {
				logger.debug(rec.gField(j).getName() + "\t");
			}
		}
	}

	/**
	 * 根据字段fieldName的值，取不重复的行 如：rsUser中，统计共有几种职业：rsUser.distinct("occupation");
	 * 
	 * @param fieldName
	 * @return
	 */
	public RecordSet distinct(String fieldName) {
		RecordSet rs = new RecordSet();
		for (int i = 0; i < this.size(); i++) {
			Record rd = this.r(i);
			String getValue = rd.getString(fieldName);
			rs.addRecord(getValue, rd);
		}
		return rs;
	}

	public T get(int index) {
		return this.r(index);
	}

	/**
	 * 统计某一列的值
	 * 
	 * @param cellIndex
	 *                要统计的列的索引号，从0开始
	 * @return 返回统计的值。如果记录数为0，返回0； 如果统计列不存在或统计列为非数值列，返回0
	 */
	public double getDoubleValue(int cellIndex) {
		return getDoubleValue(cellIndex, this.size());
	}

	/**
	 * 取累加的double值
	 * 
	 * @param cellIndex
	 *                累加字段的索引
	 * @param rowCount
	 *                行数
	 * @return @
	 */
	public double getDoubleValue(int cellIndex, int rowCount) {
		double value = 0;
		if (rowCount > 0) {
			int i = 0;
			for (i = 0; i < rowCount; i++) {
				value = value + this.r(i).gDoubleValue(cellIndex);
			}
		}
		return value;
	}

	/**
	 * 取累加的double值
	 * 
	 * @param fieldName
	 * @return @
	 */
	public double getDoubleValue(String fieldName) {
		return getDoubleValue(fieldName, this.size());
	}

	/**
	 * 取累加的double值
	 * 
	 * @param fieldName
	 *                累加字段的名称
	 * @param rowCount
	 *                行数
	 * @return @
	 */
	public double getDoubleValue(String fieldName, int rowCount) {
		double value = 0;
		if (rowCount > 0) {
			int i = 0;
			for (i = 0; i < rowCount; i++) {
				value = value + this.r(i).gDoubleValue(fieldName);
			}
		}

		return value;
	}

	/**
	 * 取累加的int值
	 * 
	 * @param cellIndex
	 * @return @
	 */
	public int getIntValue(int cellIndex) {
		return this.getIntValue(cellIndex, this.size());
	}

	/**
	 * 取累加的int值
	 * 
	 * @param cellIndex
	 * @param rowCount
	 * @return @
	 */
	public int getIntValue(int cellIndex, int rowCount) {
		return (int) java.lang.Math.round(this.getDoubleValue(cellIndex, rowCount));
	}

	/**
	 * 取累加的int值
	 * 
	 * @param fieldName
	 * @return @
	 */
	public int getIntValue(String fieldName) {
		return getIntValue(fieldName, this.size());
	}

	/**
	 * 取累加的int值
	 * 
	 * @param fieldName
	 * @param rowCount
	 * @return @
	 */
	public int getIntValue(String fieldName, int rowCount) {
		return (int) this.getDoubleValue(fieldName, rowCount);
	}

	/**
	 * 数据库里符合条件的记录数，由ResultSet遍历自动统计的结果，不一定准确。是否准确请调用isPageCountRight()方法
	 * 
	 * @return int
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 取一条记录
	 * 
	 * @param index
	 * @return
	 */
	public T getRecord(int index) {

		return (T) records.get(index);
	}

	public T getRecord(int index, T rdInstead) {
		if (index < records.size()) {
			return (T) records.get(index);
		} else {
			return rdInstead;
		}

	}

	/**
	 * 取一条记录
	 * 
	 * @param rdName
	 * @return
	 */
	public T getRecord(String rdName) {
		if (this.containsKey(rdName)) {
			return (T) records.get(rdName);
		}
		throw new AppException("记录：" + rdName + "不存在");
	}

	public T getRecord(String rdName, T rdInstead) {
		if (this.containsKey(rdName)) {
			return (T) records.get(rdName);
		} else {
			return rdInstead;
		}
	}

	public String getRecordName(int index) {
		return this.records.getKey(index);
	}

	public Record getString(int index) {
		return getString(index, 0, this.size());
	}

	public Record getString(int index, int fromRow, int rowCount) {
		if (this.size() > 0) {
			return getString(this.r(0).gField(index).getName(), fromRow, rowCount);
		} else {
			return new Record();
		}
	}

	public Record getString(String fieldName) {
		return getString(fieldName, 0, this.size());
	}

	/**
	 * 取某一列的值
	 * 
	 * @param fieldName
	 * @param fromRow
	 *                开始取值行，从0开始
	 * @param rowCount
	 *                行数
	 * @return @
	 */
	public Record getString(String fieldName, int fromRow, int rowCount) {
		Record rdR = new Record();
		if (this.size() > 0) {
			int hasReadCount = 0;
			for (int i = 0; i < rowCount; i++) {
				if (i >= fromRow) {
					rdR.set("no." + rdR.hashCode() + "_" + i, this.r(i).getString(fieldName,""));
					hasReadCount++;
				}
				if (hasReadCount >= rowCount) {
					break;
				}
			}
		}
		return rdR;
	}

	/**
	 * 根据字段fieldName的值分组 如:rsUser中，根据职业分组：rsUser.group("occupation");
	 * 
	 * @param fieldName
	 * @return Record对象，其元素为RecordSet对象
	 */
	public Record group(String fieldName) {
		return this.group(fieldName, null);
	}

	/**
	 * 根据字段fieldName的值分组 如:rsUser中，根据职业分组：rsUser.group("occupation","userid");那么返回的Record对象的key="occupation字段的值",value=根据职业分组的RecordSet rs。rs的recordName="userId字段的值"
	 * 
	 * @param fieldName
	 *                分组的字段名
	 * @param fieldNameOfRecordName
	 *                分组后获得的RecordSet对象的recordName="fieldNameOfRecordName字段的值"。如果为null,则用原先的recordName。fieldNameOfRecordName必须是对象里的一个存在字段
	 * @return Record对象，其元素为RecordSet对象
	 */
	public Record group(String fieldName, String fieldNameOfRecordName) {
		Record rdR = new Record();
		for (int i = 0; i < this.size(); i++) {
			Record rd = this.r(i);
			String rdName = this.getRecordName(i);
			if (CommonUtil.isNotEmpty(fieldNameOfRecordName)) {
				rdName = rd.getString(fieldNameOfRecordName);
			}

			String getValue = rd.getString(fieldName);
			RecordSet rsOne = null;
			if (rdR.containsKey(getValue)) {
				rsOne = (RecordSet) rdR.getValue(getValue);
			} else {
				rsOne = new RecordSet();
				rdR.set(getValue, rsOne);
			}
			rsOne.addRecord(rdName, rd);
		}
		return rdR;
	}

	public RecordSet groupTree(String fieldName, String parentName) {
		return groupTree(fieldName, parentName, "","");
	}

	/**
	 * 根据层次关系，生成树
	 * 
	 * @param fieldName
	 *                ID字段名，必须是保证唯一
	 * @param parentName
	 *                父记录ID
	 * @param topValue
	 *                顶层记录的父记录ID值
	 * @return RecordSet对象，每行包含一颗树。如果某记录包含子记录，则子记录元素名为"__rsTree"
	 */
	public RecordSet<T> groupTree(String fieldName, String parentName, String topValue,String childName) {
		if (topValue == null) {
			topValue = "";
		}
		if (CommonUtil.isEmpty(childName)) {
			childName = "__rsTree";
		}
		this.setValue("leaf", true);
		RecordSet<T> rs = new RecordSet<T>();
		for (int i = 0; i < this.size(); i++) {
			T rd = this.r(i);
			String getValue = rd.getString(fieldName);
			String pv = rd.getString(parentName,"");
			if (pv.equals(topValue)) {
				rd.set("leaf",false);
				rs.addRecord(getValue, rd);
			} else {
				Record rdParent = this.r(pv,null);
				if (rdParent == null) {
					continue;
				}
				rdParent.set("leaf",false);
				RecordSet<T> rsOne = null;
				if (rdParent.containsKey(childName)) {
					rsOne = (RecordSet<T>) rdParent.getValue(childName);
				} else {
					rsOne = new RecordSet<T>();
					rdParent.set(childName, rsOne);
				}
				rsOne.addRecord(getValue, rd);
			}
		}
		return rs;
	}

	public int indexOf(Object obj) {
		return this.records.indexOf((T) obj);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return size() < 1;
	}

	public Iterator<T> iterator() {

		return this.records.iterator();
	}

	public Set<String> keySet() {
		return this.records.keySet();
	}

	public int lastIndexOf(Object obj) {
		if (obj instanceof Record) {
			return records.lastIndexOf(obj);
		}
		throw new AppException("RecordSet:无效的lastIndexOf(Object obj)调用，元素值必须是Record对象");
	}

	public ListIterator<T> listIterator() {
		return this.records.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return this.records.listIterator(index);
	}
	public static void order22(List<? extends Record> rs,String fieldName, boolean isIgnoreCase, boolean isNumber, boolean isAsc) {
		Collections.sort(rs, new RecordSetComparator(fieldName,isIgnoreCase,isNumber,isAsc));
	}
	private static class RecordSetComparator implements Comparator<Record> {
		private String fieldName;
		private boolean isIgnoreCase;
		private boolean isNumber;
		private boolean isAsc;
		public RecordSetComparator(String fieldName, boolean isIgnoreCase, boolean isNumber, boolean isAsc) {
			this.fieldName = fieldName;
			this.isIgnoreCase = isIgnoreCase;
			this.isNumber = isNumber;
			this.isAsc = isAsc;
		}
		public int compare(Record o1, Record o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			if (isNumber) {
				String v1 = r1.getString(fieldName, "0");
				String v2 = r2.getString(fieldName, "0");
				
				double d1 = Double.parseDouble(v1);
				double d2 = Double.parseDouble(v2);
				if (isAsc) {
					if (d1 > d2) {
						return 1;
					}
					else if (d1 < d2){
						return -1;
					}
					
				} else {
					if (d1 < d2) {
						return -1;
					}
					else if (d1 > d2){
						return 1;
					}
				}
			}
			else {
				String v1 = r1.getString(fieldName,"");
				String v2 = r2.getString(fieldName,"");
				if (isIgnoreCase) {
					v1 = v1.toLowerCase();
					v2 = v2.toLowerCase();
				}
				if (isAsc) {
					if (v1.compareTo(v2) > 0) {
						return 1;
					}
					else if (v1.compareTo(v2) < 0) {
						return 1;
					}
				} else {
					if (v1.compareTo(v2) < 0) {
						return -1;
					}
					else if (v1.compareTo(v2) > 0) {
						return -1;
					}
				}
			}
			return 0;
		}
	}
//	/**
//	 * 排序
//	 * 
//	 * @param fieldName
//	 *                依据的字段名
//	 * @param isIgnoreCase
//	 *                是否忽略大小写
//	 * @param isNumber
//	 *                是否是数值
//	 */
//	public void order(String fieldName, boolean isIgnoreCase, boolean isNumber, boolean isAsc) {
//		int size = this.size();
//		if (isNumber) {
//			for (int i = 0; i < size - 1; i++) {
//				Record rd = r(i);
//				String v1 = rd.getString(fieldName, "0");
//				double d1 = Double.parseDouble(v1);
//				for (int j = i + 1; j < size; j++) {
//					String v2 = r(j).getString(fieldName, "0");
//					double d2 = Double.parseDouble(v2);
//					if (isAsc) {
//						if (d1 > d2) {
//							// 交换位置
//							this.records.changePosition(records.getName(j), i);
//							d1 = d2;
//						}
//					} else {
//						if (d1 < d2) {
//							// 交换位置
//							this.records.changePosition(records.getName(j), i);
//							d1 = d2;
//						}
//					}
//
//				}// for j
//			}// for i
//		} else {
//			for (int i = 0; i < size - 1; i++) {
//				Record rd = r(i);
//				String v1 = rd.getString(fieldName);
//				if (isIgnoreCase) {
//					v1 = v1.toLowerCase();
//				}
//				for (int j = i + 1; j < size; j++) {
//					String v2 = r(j).getString(fieldName);
//					if (isIgnoreCase) {
//						v2 = v2.toLowerCase();
//					}
//					if (isAsc) {
//						if (v1.compareTo(v2) > 0) {
//							// 交换位置
//							this.records.changePosition(records.getName(j), i);
//							v1 = v2;
//						}
//					} else {
//						if (v1.compareTo(v2) < 0) {
//							// 交换位置
//							this.records.changePosition(records.getName(j), i);
//							v1 = v2;
//						}
//					}
//
//				}// for j
//			}// for i
//		}
//	}

	

	public Class<? extends Record> getRecordClass() {
		return recordClass;
	}



	
	private void parseResultSet(ResultSet rs, String rdNameField, String charsetDb, String charsetClient) throws Exception {
		String rdName = null;
		ResultSetMetaData mData = rs.getMetaData();
		int fieldCount = mData.getColumnCount();

		Record rec = this.genTByRecordClass(this.recordClass);
		for (int i = 1; i <= fieldCount; i++) {
			String fieldName = mData.getColumnName(i);

			int fieldType = mData.getColumnType(i);

			Object objV = this.getDataByJdbcType(rs, fieldType, i);

			Field field = null;
			String fieldLabel = mData.getColumnLabel(i);
			if (CommonUtil.isNotEmpty(fieldLabel)) {
				fieldName = fieldLabel;
			}
			if (fieldName.indexOf(".") > 0) {
				String[] aProperty = fieldName.split("\\.");
				Record rdRoot = null;
				
				for (int j = 0;j<aProperty.length;j++) {
					String property = aProperty[j];
					Record rdProperty = null;
					if (j == 0) {

						if (rec.containsKey(property)) {
							if (rec.get(property) instanceof Record) {
								rdRoot = (Record)rec.getValue(property);
							}
							else {
								rdRoot = new Record();
								rec.put("_"+property.toLowerCase(),rdRoot);
							}
						}
						else {
							rdRoot = new Record();
							rec.put(property,rdRoot);
						}
						rdProperty = rdRoot;
						
					}
					else if (j != aProperty.length - 1) {
						if (rdRoot.containsKey(property)) {
							rdProperty = (Record)rdRoot.getValue(property);
						}
						else {
							rdProperty = new Record();
							rdRoot.put(property,rdProperty);
						}
						rdRoot = rdProperty;
					}
					else {
						field = new Field(property, fieldType, objV);
						rdRoot.pField(field);
					}
					
				}
				
				field = new Field(fieldName.replaceAll("\\.", "_"), fieldType, objV);
				rec.pField(field);
			}
			field = new Field(fieldName, fieldType, objV);
			rec.pField(field);
			
			
			if (rdNameField != null && rdNameField.length() > 0 && fieldName.equalsIgnoreCase(rdNameField)) {
				rdName = rec.getString(fieldName);
			}
		} // for
		if (rdName != null) {

			this.records.setValue(rdName, (T) rec);
		} else {
			this.records.add((T) rec);
		}
	}

	private Object getDataByJdbcType(ResultSet rs, int fieldType, int i) throws Exception {
		boolean isNumber = false;
		Object objV = null;
		switch (fieldType) {
		case Types.DATE:
			objV = rs.getDate(i);
			break;
		case Types.TIME:
			objV = rs.getTime(i);
			break;
		case Types.TIMESTAMP:
			objV = rs.getTimestamp(i);
			break;
		case Types.NUMERIC:
			isNumber = true;
			objV = rs.getBigDecimal(i);
			break;
		case Types.DECIMAL:
			isNumber = true;
			objV = rs.getBigDecimal(i);
			break;
		case Types.DOUBLE:
			isNumber = true;
			objV = rs.getDouble(i);
			break;
		case Types.FLOAT:
			isNumber = true;
			objV = rs.getDouble(i);
			break;
		case Types.REAL:
			isNumber = true;
			objV = rs.getFloat(i);
			break;

		case Types.CLOB:
			objV = rs.getClob(i);
			// strValue = rs.getString(i);
			break;
		case Types.BLOB:
			objV = rs.getBlob(i);
			break;
		case Types.ARRAY:
			objV = rs.getArray(i);
			break;
		case Types.DATALINK:
			objV = rs.getObject(i);
			break;
		case Types.JAVA_OBJECT:
			objV = rs.getObject(i);
			break;
		case Types.OTHER:
			objV = rs.getObject(i);
			break;
		case Types.STRUCT:
			objV = rs.getObject(i);
			break;
		case Types.REF:
			objV = rs.getRef(i);
			break;

		case Types.BINARY:
			objV = rs.getBytes(i);
			break;
		case Types.VARBINARY:
			objV = rs.getBytes(i);
			break;
		case Types.LONGVARBINARY:
			objV = rs.getBytes(i);
			break;

		case Types.NULL:
		case Types.DISTINCT:
		case Types.ROWID:

		case Types.SQLXML:
			objV = rs.getObject(i);
			break;

		case Types.NCLOB:
			objV = rs.getNClob(i);
			break;
		case Types.NCHAR:
			objV = rs.getNString(i);
			break;
		case Types.NVARCHAR:
			objV = rs.getNString(i);
			break;
		case Types.LONGNVARCHAR:
			objV = rs.getNString(i);
			break;

		case Types.INTEGER:
			isNumber = true;
			objV = rs.getInt(i);
			break;
		case Types.BIGINT:
			isNumber = true;
			objV = rs.getLong(i);
			break;
		case Types.SMALLINT:
			isNumber = true;
			objV = rs.getInt(i);
			break;
		case Types.TINYINT:
			isNumber = true;
			objV = rs.getInt(i);
			break;

		case Types.BIT:
			objV = rs.getBoolean(i);
			break;
		case Types.BOOLEAN:
			objV = rs.getBoolean(i);
			break;
		case Types.LONGVARCHAR:
			objV = rs.getString(i);
			break;
		case Types.VARCHAR:
			objV = rs.getString(i);
			break;
		case Types.CHAR:
			objV = rs.getString(i);
			break;

		default:
			objV = rs.getObject(i);
			break;
		}
		if (isNumber) {
			if (objV == null) {
				objV = 0;
			}
		}

		return objV;

	}

	public T r(int index) {
		return this.getRecord(index);
	}

	public T r(int index, T rdInstead) {
		return this.getRecord(index, rdInstead);
	}

	public T r(String rdName) {
		return this.getRecord(rdName);
	}

	public T r(String rdName, T rdInstead) {
		return this.getRecord(rdName, rdInstead);
	}

	/**
	 * 删除指定索引的行
	 * 
	 * @param index
	 */
	public T remove(int index) {
		T rd = this.r(index);
		this.records.remove(index);
		return rd;
	}

	/**
     *
     */
	public boolean remove(Object obj) {
		if (obj instanceof Record) {
			int size = this.size();
			for (int i = 0; i < size; i++) {
				if (obj.equals(this.r(i))) {
					this.remove(i);
					return true;
				}
			}
		} else {
			throw new AppException("RecordSet:无效的remove(Object obj)调用，元素值必须是Record对象");
		}
		return false;
	}

	/**
	 * 删除指定名称的行
	 * 
	 * @param rdName
	 */
	public void remove(String rdName) {
		this.records.remove(rdName);
	}

	public boolean removeAll(Collection<?> c) {
		java.util.Iterator<?> it = c.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!this.remove(obj)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 删除记录集中所有名称为name的字段
	 * 
	 * @param name
	 */
	public void removeField(String name) {
		int i = 0, len = this.size();
		Record rec = null;
		Field field = null;
		for (i = 0; i < len; i++) {
			rec = this.r(i);
			field = rec.gField(name);
			if (field != null) {
				rec.remove(name);
			}
		}
	}

	public boolean retainAll(Collection c) {
		return this.records.retainAll(c);
	}

	public T set(int index, T obj) {
		this.records.setValue(index, (T) obj);
		return obj;
	}

	public void setAutoIncrement(int index, boolean isAutoIncrement) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).gField(index).setAutoIncrement(isAutoIncrement);
		}
	}

	public void setAutoIncrement(String fieldName, boolean isAutoIncrement) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).gField(fieldName).setAutoIncrement(isAutoIncrement);
		}
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 
	 * @param index
	 * @param Record
	 */
	public void setRecord(int index, T Record) {
		this.records.setValue(index, Record);
	}
	
	/**
	 * 设置记录集的每条记录的名字。如果名字发生冲突，后面的记录将覆盖前面的记录，记录集的总数也会减少
	 * @param fieldNameOfRecordName 作为名字的字段名
	 * @return 返回一个新的RecordSet对象
	 */
	public <R extends Record> RecordSet<R> setRecordName(String fieldNameOfRecordName) {
		return new RecordSet<R>(this,fieldNameOfRecordName);
		
	}
	public <R extends Record>  RecordSet<R> setRecordName(Class<R> c) {
		return new RecordSet<R>(this,c);
		
	}
	public <R extends Record> RecordSet<R> setRecordName(Class<R> c,String fieldNameOfRecordName) {
		return new RecordSet<R>(this,c,fieldNameOfRecordName);
		
	}
	public RecordSet setString(String fieldName, Record rdReal) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).set(fieldName, this.r(i).getStringMapping(fieldName, rdReal));
		}
		return this;

	}

	/**
	 * 用于映射诸如：0——》男，1——》女等等关系
	 * 
	 * @param fieldName
	 *                如说明，其值如0，1等
	 * @param rdReal
	 *                存放有映射关系的对象
	 * @param instead
	 */
	public RecordSet setString(String fieldName, Record rdReal, String instead) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).set(fieldName, this.r(i).getStringMapping(fieldName, rdReal, instead));
		}
		return this;
	}

	public RecordSet setString(String fieldName, String newFieldName, Record rdReal) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).set(newFieldName, this.r(i).getStringMapping(fieldName, rdReal));
		}
		return this;
	}

	/**
	 * 用于映射诸如：0——》男，1——》女等等关系
	 * 
	 * @param fieldName
	 *                如说明，其值如0，1等
	 * @param newFieldName
	 *                新字段名称（也可以时现有的名字）
	 * @param rdReal
	 *                存放有映射关系的对象
	 * @param instead
	 */
	public RecordSet setString(String fieldName, String newFieldName, Record rdReal, String instead) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).set(newFieldName, this.r(i).getStringMapping(fieldName, rdReal, instead));
		}
		return this;
	}

	public void setTotalCount(final int totalCount) {
		this.totalCount = totalCount;
		// 计算页数
		int pageCount = totalCount / pageSize;
		if (totalCount % pageSize != 0) {
			pageCount++;
		}
		this.setPageCount(pageCount);

	}

	public RecordSet setType(int index, int dataType) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).pType(index, dataType);
		}
		return this;
	}

	public RecordSet setType(String fieldName, int dataType) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).pType(fieldName, dataType);
		}
		return this;
	}

	public RecordSet setValue(int index, Object obj) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).gField(index).setValue(obj);
		}
		return this;
	}

	public RecordSet setValue(String fieldName, Object obj) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).set(fieldName, obj);
		}
		return this;
	}

	public void addField(String fieldName, Object getValue) {
		int len = this.size();
		for (int i = 0; i < len; i++) {
			this.r(i).set(fieldName, getValue);
		}
	}

	/**
	 * 取行数
	 * 
	 * @return
	 */
	public int size() {
		return records.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		List<T> list = this.records.subList(fromIndex, toIndex);
		RecordSet<T> rsR = new RecordSet<T>();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			rsR.addRecord(list.get(i));

		}
		return rsR;
	}

	/**
	 * 取字段名为feildName的列，并且此字段的值为fieldValue的所有行 如：rsUser中，取性别为男的记录:rsUser.subRs("sex","男",true);
	 * 
	 * @param fieldName
	 * @param fieldValue
	 * @param ignoreCase
	 *                是否忽略大小写
	 * @return
	 */
	public RecordSet subRs(String fieldName, String fieldValue, boolean ignoreCase) {
		RecordSet rs = new RecordSet();
		for (int i = 0; i < this.size(); i++) {
			Record rd = this.r(i);
			String getValue = rd.getString(fieldName);
			if (ignoreCase) {
				if (getValue.equalsIgnoreCase(fieldValue)) {
					rs.addRecord(rd);
				}
			} else {
				if (getValue.equals(fieldValue)) {
					rs.addRecord(rd);
				}
			}
		}
		return rs;
	}

	public Object[] toArray() {
		return this.records.getArrayList().toArray();
	}

	public Object[] toArray(Object[] objA) {
		return this.records.getArrayList().toArray(objA);
	}

	public T[] toArray(T[] objA) {
		return this.records.getArrayList().toArray(objA);
	}

	public List toBeanList(Class c) {
		return toBeanList(c, false);
	}

	public List toBeanList(Class c, boolean fillZero) {
		List list = new ArrayList();

		int size = size();
		for (int i = 0; i < size; i++) {
			Object bean = this.r(i).toBean(c, fillZero);
			list.add(bean);
		}
		return list;
	}

	/**
	 * 转换为Record对象
	 * 
	 * @param fieldNameOfName
	 *                作为转换后的Record对象的key值的字段名
	 * @param fieldNameOfValue
	 *                　作为转换后的Record对象的value值的字段名
	 * @return
	 */
	public Record toRd(String fieldNameOfName, String fieldNameOfValue) {
		return CommonUtil.rsToRd(this, fieldNameOfName, fieldNameOfValue);
	}
	public String toString() {
		return this.d("",false);
	}
	

	/**
	 * 将记录集分解成字符串。记录间用chObj分隔，字段间用chItem分隔
	 * 
	 * @param rdName
	 * @param chObj
	 * @param chItem
	 * @return
	 */
	public String toStringCh(String rdName, String chObj, String chItem) {
		StringBuffer sb = new StringBuffer();
		int size = this.size();
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				sb.append(chObj);
			}
			sb.append(r(i).getString(rdName));
			sb.append("=");
			sb.append(r(i).toStringCh(chItem));

		}
		return sb.toString();
	}

	/**
	 * 将每条记录里指定的2个字段的值，组合成name=value的（名值对）格式，并且用strItem分隔
	 * 
	 * @param idField
	 *                作为值部门
	 * @param nameField
	 * @param strItem
	 * @return
	 */
	public String toStringNv(String nameField, String valueField, String strItem) {
		StringBuffer sb = new StringBuffer();
		int size = this.size();
		for (int i = 0; i < size; i++) {
			sb.append(strItem);
			sb.append(this.r(i).getString(nameField));
			sb.append("=");
			sb.append(this.r(i).getString(valueField));
		}
		return sb.length() > 0 ? sb.toString().substring(strItem.length()) : sb.toString();
	}
	
	
	/**
	 * 新建一个字段，新建的字段的值现有的另一个字段的值
	 * @param newFieldName 要新增的字段名
	 * @param fieldName 新增字段的值的来源字段
	 */
	public void addFieldMap(String newFieldName,String fieldName) {
		int size = this.size();
		for (int i = 0;i<size;i++) {
			Record rd = this.r(i);
			if (rd.containsKey(fieldName)) {
				rd.set(newFieldName,rd.gType(fieldName),rd.getValue(fieldName));
			}
		}
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public String toJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		int sizeRs = this.size();
		for (int j = 0;j<sizeRs;j++) {
			Record rdNew = this.getRecord(j);
			if (j>0) {
				sb.append(",");
				
			}
			sb.append(rdNew.toJSON());
		}
		sb.append("]");
		return sb.toString();
	}
	public Page<T> toPage() {
		Page<T> page = new Page<T>();
		return toPage(page);
	}
	public Page<T> toPage(Page<?> page) {
		page.setList((List)this);
		page.setPageNo(this.getPageNo());
		page.setPageSize(this.getPageSize());
		page.setCount(this.getTotalCount());
		page.setPageCount(this.getPageCount());
		return (Page<T>)page;
	}
}
