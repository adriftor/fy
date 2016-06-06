package com.xh.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Map类，名称不可以为null
 * 如果key以下划线开头或结尾，则值忽略大小写。否则key值忽略大小写，也忽略非开头的下划线
 * 如key："abc_D____E"和"abcde"相同。"_abc_D___E"和"_abc_d___e"相同，和"abcde"不相同
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

public class LinkedHashMapEx<V> extends LinkedHashMap<String,V> implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final Log logger = LogFactory.getLog(LinkedHashMapEx.class);
	
	public static void main(String[] argv) {
		try {
			LinkedHashMapEx<String> fc = new LinkedHashMapEx<String>();
			fc.put("aAa", "a");
			fc.put("aA__a", "a1");
			fc.put("a_A__a", "aa1");
			fc.put("a_A__a_", "aa1tttt");
			fc.put("_a_A__a", "sssaa1");
			fc.put("aA_Ba", "a2");
			fc.d();
			System.out.println(fc.keySet());
			System.out.println(fc.get("A___AA"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	public LinkedHashMapEx() {
		super();

	}
	public LinkedHashMapEx(int initialCapacity) {
		super(initialCapacity);

	}

	@Override
	public V put(String key, V v) {
		super.put(makeShortKey(key), v);
		return v;
	}
	
	@Override
	public V get(Object key) {
		return super.get(makeShortKey(key.toString()));
		
	}
	/**
	 * 是否包含名称为key的元素
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(makeShortKey(key.toString()));
		
	}

	/**
	 * 删除名为key的所有元素
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public V remove(Object key) {
		V v = super.remove(makeShortKey(key.toString()));
		return v;

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
	 * 显示数据
	 */
	public void d() {
			for (String key:this.keySet()) {
				logger.info(key + "=" + get(key));
			}
	}

}
