package com.xh.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Map类，名称不可以为null
 * 如果key以下划线开头或结尾，则值忽略大小写。否则key值忽略大小写，也忽略非开头的下划线
 * 如key："abc_D____E"和"abcde"相同。"_abc_D___E"和"_abc_d___e"相同，和"abcde"不相同
 * 保留原始的Key值，通过调用setOutputRealKeyFlag(true)可以输出原始key
 * <p>
 * Title:集合包装
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author adriftor
 * @version 2.0
 */

public class LongCol<V> implements Serializable, Map<String, V> {

	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(LongCol.class);
	private static int nameInc = 0;

	
	
	/**
	 * 对KEY是否进行处理。如果为true，将忽略key里的非开头的下划线
	 */
//	private boolean shortKeyFlag = true;
	
	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */

	protected ArrayList<String> aName = null; // 存储具体对象的名字=lowerKey
	protected FyHashMap<V> htData = null;
//	protected LinkedHashMap<String, String> hmName = null;//key=shortKey,value=lowerKey
	
	public static void main(String[] argv) {
		try {
			LongCol<String> fc = new LongCol<String>();
			fc.put("aAa", "a");
			fc.put("aA__a", "a1");
			fc.put("a_A__a", "aa1");
			fc.put("aA_Ba", "a2");
			fc.d();
			System.out.println(fc.keySet());
			System.out.println(fc.keySetReal());
			System.out.println(fc.toMap());
			System.out.println(fc.get("A___AA"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public LongCol() {
		this(true,15);	
	}
	public LongCol(int size) {
		this(true,size);	
	}
	/**
	 * 
	 * @param shortKeyFlag 如果为true,则key值将忽略大小写和下划线存取。否则key值只忽略大小写存取。默认为true
	 * @param storeRealKeyFlag
	 *            如果为true,则会保存真实的key值,但花费存储空间,默认为false
	 */
	public LongCol(boolean outputRealKeyFlag) {
		this(outputRealKeyFlag,15);
	}

	/**
	 * 
	 * @param shortKeyFlag 如果为true,则key值将忽略大小写和下划线存取。否则key值只忽略大小写存取。默认为true
	 * @param storeRealKeyFlag
	 *            如果为true,则会保存真实的key值,但花费存储空间,默认为false
	 */
	public LongCol(boolean outputRealKeyFlag,int size) {
		aName = new ArrayList<String>(size); // 存储具体对象的名字
		htData = new FyHashMap(size);
		htData.setOutputRealKeyFlag(outputRealKeyFlag);
		
	}
	
	/**
	 * 加入另一个FyCol对象的全部元素
	 * 
	 * @param fcCol
	 */
	public LongCol<V> add(LongCol<V> fcCol) {
		return add(fcCol, true);
	}

	/**
	 * 加入另一个FyCol对象的全部元素
	 * 
	 * @param fcCol
	 * @param byKey
	 *            是否根据key加入,如果为true,那么相同名称的元素将会被覆盖;如果为false,则不会覆盖,
	 *            但新加入元素的key值将有系统产生
	 * @return
	 */
	public LongCol<V> add(LongCol<V> fcCol, boolean byKey) {
		int i = 0;
		int count = fcCol.size();
		if (byKey) {
			String key = null;
			for (i = 0; i < count; i++) {
				key = fcCol.getKey(i);
				setValue(key, fcCol.get(i));
			}
		} else {
			for (i = 0; i < count; i++) {
				this.add(fcCol.get(i));
			}
		}

		return this;
	}


	/**
	 * 
	 * @param v
	 * @return
	 */
	public boolean add(V v) {
		String key = this.genInnerKey();
		setValue(key, v);
		return true;
	}


	/**
	 * 
	 * @param cl
	 * @return
	 */
	public boolean addAll(java.util.Collection<V> cl) {
		boolean flag = false;
		java.util.Iterator<V> it = cl.iterator();
		while (it.hasNext()) {
			V v = it.next();
			this.add(v);
			flag = true;
		}
		return flag;
	}

	public boolean addAll(Map<? extends String, ? extends V> map) {
		boolean flag = false;
		java.util.Iterator<? extends String> it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			this.setValue(key, map.get(key));
			flag = true;
		}
		return flag;
	}



	/**
	 * 清空
	 */
	public void clear() {
		htData.clear();
		aName.clear();

	}


	/**
	 * 是否包含名称为key的元素
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(Object key) {
		return htData.containsKey(key);
		
	}

	/**
	 * 是否包含值为value的元素
	 * 
	 * @param value
	 * @return
	 */
	public boolean containsValue(Object value) {
			return htData.containsValue(value);
	}

	/**
	 * 显示数据
	 */
	public void d() {
			for (int i = 0; i < aName.size(); i++) {
				logger.error(aName.get(i) + "=" + htData.get(aName.get(i)));
			}
	}

	/**
	 * 销毁
	 */
	public void destroy() {
		aName = null;
		htData = null;

	}

	public Set<Map.Entry<String, V>> entrySet() {
		return htData.entrySet();
	}

	/**
	 * 生成唯一的key名.名字根据hashCode、计数值、日期生成，各FyCol对象生成的名字是唯一的
	 * 
	 * @return String
	 */
	public String genInnerKey() {
		String name = this.hashCode() + "-" + nameInc + "-"
				+ System.currentTimeMillis();
		while (this.containsKey(name)) {
			nameInc++;
			name = this.hashCode() + "-" + nameInc + "-"
					+ System.currentTimeMillis();
		}
		if (nameInc > 10000000) {
			nameInc = 1;
		}
		return name;
	}

	/**
	 * 取值
	 * 
	 * @param index
	 * @return
	 */
	public V get(int index) {
		return htData.get(aName.get(index));

	}

	public V get(Object key) {
		return htData.get(key);
		
	}


	public List<V> getArrayList() {
		return  new ArrayList<V>(htData.values());

	}



	

	/**
	 * 返回值为v的key值
	 * 
	 * @param v
	 *            Object 值
	 * @return String 如果不包含值为v的元素，返回null
	 */
	public String getKeyByValue(V v) {
		if (containsValue(v)) {
			return getKey(indexOf(v));
		}
		return null;
	}

	public String getKey(int index) {
		if ( ! this.isOutputRealKeyFlag()) {
			return aName.get(index);
		}
		return htData.getRealKey(aName.get(index));

	}

	public String getRealKey(int index) {
		return htData.getRealKey(aName.get(index));
		
	}

	/**
	 * 返回原始的key
	 * @param key
	 * @return 返回原始的key，没有返回null
	 */
	public String getRealName(String key) {
		 return htData.getRealKey(key);
		
	}

	/**
	 * 取字符串值
	 * 
	 * @param index
	 * @return
	 */
	public String getString(int index) {
		Object v = this.get(index);
		if (v == null)
			return null;
		return v.toString();
	}

	/**
	 * 取字符串值
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		Object v = this.get(key);
		if (v == null) {
			return null;
		}
		return v.toString();
	}

	/**
	 * 如果不存在key名称的元素或为长度为零的字符串，则返回用户自定的defaultValue
	 */
	public String getString(String key, String insteadValue) {
		V v = this.get(key);
		if (v == null || v.toString().length() == 0) {
			return insteadValue;
		}
		return v.toString();
	}

	/**
	 * 取值为obj的元素的索引，如果不存在，返回－1
	 * 
	 * @param v
	 * @return
	 */
	public int indexOf(V v) {
			return this.getArrayList().indexOf(v);
	}

	/**
	 * 返回名称为key的元素的索引
	 * 
	 * @param key
	 * @return 返回key的索引位置，没有返回-1
	 */
	public int indexOfKey(String key) {
		key = htData.getKey(key).toLowerCase();
		return aName.indexOf(key);
		
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return aName.size() < 1;

	}

	/**
	 * 迭代
	 * 
	 * @return
	 */
	public java.util.Iterator<V> iterator() {
			return htData.values().iterator();
	}


	public Set<String> keySet() {
		return htData.keySet();

	}

	public Set<String> keySetReal() {
		return htData.keySetReal();
	}

	public int lastIndexOf(Object v) {
			return this.getArrayList().lastIndexOf(v);
	}
	
	public ListIterator<V> listIterator() {
			return getArrayList().listIterator();
	}

	public ListIterator<V> listIterator(int index) {
			return getArrayList().listIterator(index);

	}

	public V put(String key, V v) { // #######
		this.setValue(key, v);

		return v;
	}

	public void putAll(Map<? extends String, ? extends V> map) {
		this.addAll(map);
	}

	/**
	 * 删除一个元素
	 * 
	 * @param index
	 */
	public void remove(int index) {
		String key = getKey(index);
		remove(key);
		
	}

	/**
	 * 删除名为key的所有元素
	 * 
	 * @param key
	 * @return
	 */
	public V remove(Object k) {
		String key = k.toString().toLowerCase();
		if (this.containsKey(key)) {
			key = htData.getKey(key);
			
			int index = indexOfKey(key);

			aName.remove(index);
			V v = htData.remove(key);

			return v;
		}
		return null;

	}

	

	/**
	 * 删除指定集合里所有的元素
	 * 
	 * @param cl
	 * @return
	 */
	public boolean removeAll(java.util.Collection<V> cl) {
		boolean flag = false;
		java.util.Iterator<V> it = cl.iterator();
		while (it.hasNext()) {
			V v = it.next();
			if (this.containsValue(v)) {
				String key = getKeyByValue(v);
				remove(key);
			}
			flag = true;
		}
		return flag;
	}


	/**
	 * 修改指定索引位置的值。
	 * 
	 * @param index
	 * @param v
	 */
	public LongCol<V> setValue(int index, V v) {
		this.setValue(aName.get(index), v);
		return this;
	}

	public LongCol<V> set(int index, V v) {

		htData.put(aName.get(index), v);
		return this;
	}

	/**
	 * 修改值。如果指定的元素不存在，则新加入此元素
	 * 
	 * @param key
	 *            元素名称
	 * @param v
	 *            值
	 */
	public LongCol<V> setValue(Object objName, V v) {
		String key = objName.toString();
		if (containsKey(key)) {
			htData.put(key, v);
		}
		else {
			htData.put(key, v);
			aName.add(htData.getKey(key));
		}
		
		return this;
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return aName.size();

	}

	public List<V> subList(int fromIndex, int toIndex) {
			return getArrayList().subList(fromIndex, toIndex);

	}

	public Object[] toArray() {
			return getArrayList().toArray();

	}

	public V[] toArray(V[] objA) {
			return getArrayList().toArray(objA);

	}

	/**
	 * 转换成Map对象，key值为原来赋值时的key
	 * 
	 * @return
	 */
	public Map<String, V> toMap() {
		return htData.toMap();
	}


	public Collection<V> values() {
		return Collections.unmodifiableCollection(this.htData.values());
	}


	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	public boolean isOutputRealKeyFlag() {
		return htData.isOutputRealKeyFlag();
	}
	
	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	public void setOutputRealKeyFlag(boolean outputRealKeyFlag) {
		htData.setOutputRealKeyFlag(outputRealKeyFlag);
	}
	
	
}
