package com.xh.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
 * 
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

public class FyHashMap<V> implements Serializable, Map<String, V> {

	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(FyHashMap.class);

	
	/**
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	private boolean outputRealKeyFlag = false;

	protected LinkedHashMap<String, V> htData = null;//key=realKey.toLowerCase()
	protected LinkedHashMap<String, String> hmName = null;//key=shortKey,value=realKey
	
	public static void main(String[] argv) {
		try {
			FyHashMap<String> fc = new FyHashMap<String>();
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

	
	public FyHashMap() {
		htData = new LinkedHashMap<String, V>();
		hmName = new LinkedHashMap<String, String>();

	}
	public FyHashMap(int initialCapacity) {
		htData = new LinkedHashMap<String, V>(initialCapacity);
		hmName = new LinkedHashMap<String, String>(initialCapacity);

	}
	
	
	/**
	 * 清空
	 */
	@Override
	public void clear() {
		htData.clear();
		hmName.clear();

	}



	/**
	 * 是否包含名称为key的元素
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		boolean containFlag = htData.containsKey(key.toString().toLowerCase());
		if (containFlag) {
			return true;
		}
		String shortKey = makeShortKey(key.toString());
		return hmName.containsKey(shortKey);
		
	}

	/**
	 * 返回key当前存储的key值。
	 * getKey("abc_D"),如果之前调用过put("a__bCd"),则返回a__bcd
	 * @param key
	 * @return
	 */
	public String getKey(Object key) {
		String storeKey = hmName.get(makeShortKey(key.toString()));
		if (storeKey != null) {
			return storeKey.toLowerCase();
		}
		return null;
	}
	/**
	 * 返回key当前存储的真实key值。
	 * getKey("abc_D"),如果之前调用过put("a__bCd"),则返回a__bCd
	 * @param key
	 * @return
	 */
	public String getRealKey(Object key) {
		String shortKey = makeShortKey(key.toString());
		return hmName.get(shortKey);

	}
	/**
	 * 是否包含值为value的元素
	 * 
	 * @param value
	 * @return
	 */
	@Override
	public boolean containsValue(Object value) {
			return htData.containsValue(value);
	}

	/**
	 * 显示数据
	 */
	public void d() {
			for (String key:this.htData.keySet()) {
				logger.info(key + "=" + htData.get(key));
			}
	}

	/**
	 * 销毁
	 */
	public void destroy() {
		htData = null;
		hmName = null;

	}
	
	@Override
	public Set<Map.Entry<String, V>> entrySet() {
		return htData.entrySet();
	}


	@Override
	public V get(Object key) {
		V v = htData.get(key.toString().toLowerCase());
		if (v == null  && this.containsKey(key)) {
			
			String shortKey = makeShortKey(key.toString());
			v = htData.get(hmName.get(shortKey).toLowerCase());

			
		}
			
		return v;
		
	}

	/**
	 * 返回真正的key集合
	 * 
	 * @return
	 */
	public Set<String> keySetReal() {
		return Collections.unmodifiableSet(new LinkedHashSet<String>(this.hmName.values()));

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
	 * 
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return htData.size() < 1;

	}



	@Override
	public Set<String> keySet() {
		return htData.keySet();
		
	}

	@Override
	public V put(String key, V v) {
		String shortKey = makeShortKey(key);
		
		//保持和第一次存的key值一致
		if (hmName.containsKey(shortKey)) {
			key = getKey(key);
		}
		else {
			hmName.put(shortKey, key);
		}
		htData.put(key.toLowerCase(), v);
		return v;
	}
	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		java.util.Iterator<? extends String> it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			put(key.toString(), map.get(key));
		}
	}


	/**
	 * 删除名为key的所有元素
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public V remove(Object k) {
		String key = k.toString().toLowerCase();
		if (this.containsKey(key)) {
			key = hmName.get(this.makeShortKey(key)).toLowerCase();
			
			String shortKey = makeShortKey(key);
			hmName.remove(shortKey);

			V v = htData.remove(key);

			return v;
		}
		return null;

	}



	/**
	 * 
	 * @return
	 */
	@Override
	public int size() {
		return htData.size();

	}


	/**
	 * 转换成Map对象，key值为原来赋值时的key
	 * 
	 * @return
	 */
	public Map<String, V> toMap() {
		LinkedHashMap<String, V> hm = new LinkedHashMap<String, V>();
		List<String> listNames = new ArrayList<String>(hmName.values());
		int index = 0;
		for (V value:htData.values()) {
			hm.put(listNames.get(index++), value);
		}
		return hm;
	}

	@Override
	public Collection<V> values() {
		return htData.values();
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
	 * 是否输出真实的key名称，影响keySet(),keys(),getName()
	 * @param outputRealKeyFlag
	 */
	public void setOutputRealKeyFlag(boolean outputRealKeyFlag) {
		this.outputRealKeyFlag = outputRealKeyFlag;
	}
	
	
}
