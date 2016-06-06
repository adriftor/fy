package com.xh.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
 * 集合类，可按名称或索引存。名称不可以为null
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

public class FyCol<V> implements Serializable, Map<String, V> {

	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(FyCol.class);
	private static int nameInc = 0;

	
	
	private boolean storeRealKeyFlag = true;//是否保存真实的key
	/**
	 * 对KEY是否进行处理。如果为true，将忽略key里的非开头的下划线
	 */
//	private boolean shortKeyFlag = true;
	
	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	private boolean outputRealKeyFlag = false;

	protected ArrayList<String> aName = null; // 存储具体对象的名字=lowerKey
	protected LinkedHashMap<String, V> htData = null;
	protected LinkedHashMap<String, String> hmName = null;//key=shortKey,value=lowerKey
	protected ArrayList<String> aRealName = null; // 存储具体对象的真实名字
	
	public static void main(String[] argv) {
		try {
			FyCol<String> fc = new FyCol<String>();
			fc.put("aaa", "a");
			Set<String> set = fc.keySet();
			set.clear();
			System.out.print(set.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public FyCol() {
		this(true,15);	
	}
	public FyCol(int size) {
		this(true,size);	
	}
	/**
	 * 
	 * @param shortKeyFlag 如果为true,则key值将忽略大小写和下划线存取。否则key值只忽略大小写存取。默认为true
	 * @param storeRealKeyFlag
	 *            如果为true,则会保存真实的key值,但花费存储空间,默认为false
	 */
	public FyCol(boolean storeRealKeyFlag) {
		this(storeRealKeyFlag,15);
	}

	/**
	 * 
	 * @param shortKeyFlag 如果为true,则key值将忽略大小写和下划线存取。否则key值只忽略大小写存取。默认为true
	 * @param storeRealKeyFlag
	 *            如果为true,则会保存真实的key值,但花费存储空间,默认为false
	 */
	public FyCol(boolean storeRealKeyFlag,int size) {
		size = size*4/3+2;
		this.storeRealKeyFlag = storeRealKeyFlag;
		
		hmName = new LinkedHashMap<String, String>(size);
		if (storeRealKeyFlag) {
			aRealName = new ArrayList<String>(size);
		}
		
		aName = new ArrayList<String>(size); // 存储具体对象的名字
		htData = new LinkedHashMap<String, V>(size);
		
		
	}
	
	/**
	 * 加入另一个FyCol对象的全部元素
	 * 
	 * @param fcCol
	 */
	public FyCol<V> add(FyCol<V> fcCol) {
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
	public FyCol<V> add(FyCol<V> fcCol, boolean byKey) {
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
	 * 在指定位置插入一个元素
	 * 
	 */
	public FyCol<V> add(int index, V v) {
		String key = genInnerKey();

		String shortKey = makeShortKey(key);

		if (storeRealKeyFlag) {
			aRealName.add(index, key);
		}
		aName.add(index, key);
		hmName.put(shortKey, key);
		htData.put(key, v);
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

	public boolean addAll(int index, Collection<V> c) {
		boolean flag = false;
		java.util.Iterator<V> it = c.iterator();
		while (it.hasNext()) {
			V v = it.next();
			this.add(index++, v);
			flag = true;
		}
		return flag;
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
	 * 将名称为key的元素的索引设置成index
	 * 
	 * @param key
	 *            名称
	 * @param index
	 *            更改后的索引位置
	 */
	public void changePosition2(String key, int index) {
		String keyReal = key;
		key = key.toLowerCase();
		if (containsKey(key)) {
			V v = this.get(key);
			remove(key);

			
			String shortKey = makeShortKey(key);

			if (storeRealKeyFlag) {
				this.aRealName.add(index, keyReal);
			}
			
			this.aName.add(index, key);
			hmName.put(shortKey, key);
			this.htData.put(key, v);
			
			int size = this.size();
			LinkedHashMap<String, V> newMap = new LinkedHashMap<String, V>(size);
			for (int i = 0;i<size;i++) {
				String keyTmp = aName.get(i);
				newMap.put(keyTmp,htData.get(keyTmp));
			}
			this.htData = newMap;
		}
	}


	/**
	 * 清空
	 */
	public void clear() {
		htData.clear();
		aName.clear();
		hmName.clear();
		if (storeRealKeyFlag) {
			aRealName.clear();
		}

	}

	/**
	 * 复制
	 * 
	 * @return
	 */
	public Object clone() {
		FyCol<V> col = new FyCol<V>();
		col.htData = (LinkedHashMap<String, V>) this.htData.clone();
		col.aName = (ArrayList<String>) this.aName.clone();
		col.hmName = (LinkedHashMap<String,String>)this.hmName.clone();
		if (storeRealKeyFlag) {
			col.aRealName = (ArrayList<String>) this.aRealName.clone();
		}


		return col;
	}

	/**
	 * 是否包含值为v的元素
	 * 
	 * @param v
	 * @return
	 */
	public boolean contains(Object v) {
			return htData.containsValue(v);
	}

	/**
	 * 
	 * @param cl
	 * @return
	 */
	public boolean containsAll(java.util.Collection<V> cl) {
			return htData.values().containsAll(cl);

	}

	/**
	 * 是否包含名称为key的元素
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(Object key) {
		boolean containFlag = htData.containsKey(key.toString().toLowerCase());
		if (containFlag) {
			return true;
		}
		String shortKey = makeShortKey(key.toString());
		return hmName.containsKey(shortKey);
		
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

		
		hmName = null;
		if (storeRealKeyFlag) {
			aRealName = null;
		}

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
		V v = htData.get(key.toString().toLowerCase());
		if (v == null  && this.containsKey(key)) {
			
			String shortKey = makeShortKey(key.toString());
			v = htData.get(hmName.get(shortKey));

			
		}
			
		return v;
		
	}


	public List<V> getArrayList() {
			return  new ArrayList<V>(htData.values());

	}

	public List<String> keyList() {
		if (this.storeRealKeyFlag && outputRealKeyFlag) {
			return Collections.unmodifiableList(aRealName);
		}
		else {
			return Collections.unmodifiableList(aName);
		}
		
	}

	

	/**
	 * 返回值为v的key值
	 * 
	 * @param v
	 *            Object 值
	 * @return String 如果不包含值为v的元素，返回null
	 */
	public String getKeyByValue(V v) {
		if (contains(v)) {
			return getKey(indexOf(v));
		}
		return null;
	}

	public String getKey(int index) {
		if (this.storeRealKeyFlag && outputRealKeyFlag) {
			return aRealName.get(index);
		}
		else {
			return aName.get(index);
		}
	}

	/**
	 * 返回真正的key集合,没有转换大小写
	 * 
	 * @return
	 */
	public List<String> keyListReal() {
		if (storeRealKeyFlag) {
			return Collections.unmodifiableList(this.aRealName);
		}
		else {
			return  Collections.unmodifiableList(aName);
		}

	}

	public String getRealKey(int index) {
		if (storeRealKeyFlag) {
			return this.aRealName.get(index);
		}
		else {
			return aName.get(index);
		}
		
	}

	/**
	 * 返回原始的key
	 * @param key
	 * @return 返回原始的key，没有返回null
	 */
	public String getRealKey(String key) {
		if (contains(key)) {
			int index = this.indexOfKey(key);
			if (storeRealKeyFlag) {
				return this.aRealName.get(index);
			}
			else {
				return this.aName.get(index);
			}
		}
		return null;
		
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
		if (containsKey(key)) {
			return aName.indexOf(hmName.get(this.makeShortKey(key)));

		}
		else {
			return -1;
		}
		
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
		if (this.storeRealKeyFlag && outputRealKeyFlag) {
			
			return Collections.unmodifiableSet(new LinkedHashSet<String>(this.aRealName));
		}
		else {
			
			return Collections.unmodifiableSet(this.htData.keySet());
		}
		
		
	}

	public Set<String> keySetReal() {
		Set keySet = null;
		if (storeRealKeyFlag) {
			keySet = Collections.unmodifiableSet(new LinkedHashSet(aRealName));
		}
		else {
			keySet = Collections.unmodifiableSet(htData.keySet());
		}
		
		return keySet;
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
			key = hmName.get(this.makeShortKey(key));
			
			int index = indexOfKey(key);
			String shortKey = makeShortKey(key);
			hmName.remove(shortKey);
			if (storeRealKeyFlag) {
				aRealName.remove(index);
			}
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
			if (this.contains(v)) {
				String key = getKeyByValue(v);
				remove(key);
			}
			flag = true;
		}
		return flag;
	}

	/**
	 * 求合集
	 * 
	 * @param cl
	 * @return
	 */
	public boolean retainAll(java.util.Collection<V> cl) {
		FyCol<V> fc = new FyCol<V>();
		boolean flag = false;
		java.util.Iterator<V> it = cl.iterator();
		while (it.hasNext()) {
			V v = it.next();
			if (this.contains(v)) {
				fc.setValue(getKeyByValue(v), v);
			}
			flag = true;
		}
		this.clear();
		this.add(fc);
		return flag;
	}

	/**
	 * 修改指定索引位置的值。
	 * 
	 * @param index
	 * @param v
	 */
	public FyCol<V> setValue(int index, V v) {
		this.setValue(aName.get(index), v);
		return this;
	}

	public FyCol<V> set(int index, V v) {

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
	public FyCol<V> setValue(Object objName, V v) {

		String key = objName.toString();
		
		if (this.containsKey(key)) {
			key = hmName.get(this.makeShortKey(key));
			htData.put(key, v);
			

		} else {

			String shortKey = makeShortKey(key);
			
			if (storeRealKeyFlag) {
				aRealName.add(key);
			}
			key = key.toLowerCase();
			
			aName.add(key);
			hmName.put(shortKey, key);
			htData.put(key, v);
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
		HashMap<String, V> hm = new HashMap<String, V>();
		if (storeRealKeyFlag) {
			int len = aRealName.size();
			for (int i = 0; i < len; i++) {
				hm.put(aRealName.get(i), this.htData.get(aName.get(i)));
			}
		}
		else {
			return htData;
		}
		
		return hm;
	}


	public Collection<V> values() {
		return Collections.unmodifiableCollection(this.htData.values());
	}


	/**
	 * KEY转换处理
	 * 去掉不是以下划线开头的KEY的下划线
	 * @param key
	 * @return
	 */
	protected String makeShortKey(String key) {
		key = key.toLowerCase();
		if (key.startsWith("_") || key.endsWith("_")) {
			return key;
		}
		if (key.indexOf("_") > 0) {
			key = key.replaceAll("\\_", "");
		}
		return key;
	}
	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	public boolean isOutputRealKeyFlag() {
		return outputRealKeyFlag;
	}
	
	/**
	 * 是否保存原始key（字符串格式）,影响keySet() entrySet()
	 * @return
	 */
	public boolean isStoreRealKeyFlag() {
		return storeRealKeyFlag;
	}
	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	public void setOutputRealKeyFlag(boolean outputRealKeyFlag) {
		this.outputRealKeyFlag = outputRealKeyFlag;
	}
	
	
}
