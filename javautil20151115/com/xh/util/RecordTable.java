package com.xh.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
/**
 * Record类子类，用于数据库表的对应定义
 * @author adriftor
 *
 */
public abstract class RecordTable extends Record {
	
	private static final long serialVersionUID = 1L;
	
//	public abstract void registerTable();
	private static Map<String,TableInfo> mapTable = new HashMap<String,TableInfo>();
	
	public RecordTable() {
		super();
	}
	public RecordTable(Map map) {
		super(map);
	}
	
	
	public String getTableName() {
		return getTableName(getClass());
	}
	public String getPrimaryKey() {
		return getPrimaryKey(getClass());
	}
	
	public static void registerTable(Class<?>c,TableInfo tableInfo) {
		Iterator<TableInfo> it = mapTable.values().iterator();
		Set<Map.Entry<String,TableInfo>> set = mapTable.entrySet();
		for (Map.Entry<String,TableInfo> entry:set) {
			TableInfo info = entry.getValue();
			if (info.getTableName().equalsIgnoreCase(tableInfo.getTableName())) {
				throw new AppException(c.getName()+":关联数据库表"+tableInfo.getTableName()+"无效！数据库表"+tableInfo.getTableName()+"已经和类"+entry.getKey()+"关联！");
			}
		}
		mapTable.put(c.getName(), tableInfo);
	}
	
	public static String getTableName(Class<? extends RecordTable> c) {
		if (mapTable.containsKey(c.getName())) {
			TableInfo tableInfo = mapTable.get(c.getName());
			return tableInfo.getTableName();
		}
	
		Class<?> cp = c.getSuperclass();
		while (cp != null && cp != Object.class && RecordTable.class.isAssignableFrom(cp)) {
			if (mapTable.containsKey(cp.getName())) {
				TableInfo tableInfo = mapTable.get(cp.getName());
				return tableInfo.getTableName();
			}
			cp = cp.getSuperclass();
		}
		return null;
	}
	public static String getPrimaryKey(Class<? extends RecordTable> c) {
		if (mapTable.containsKey(c.getName())) {
			TableInfo tableInfo = mapTable.get(c.getName());
			return tableInfo.getPrimaryKey();
		}
		
		Class<?> cp = c.getSuperclass();
		while (cp != null && cp != Object.class && RecordTable.class.isAssignableFrom(cp)) {
			if (mapTable.containsKey(cp.getName())) {
				TableInfo tableInfo = mapTable.get(cp.getName());
				return tableInfo.getPrimaryKey();
			}
			cp = cp.getSuperclass();
		}
		return null;
	}
	
	public static class TableInfo {
		public TableInfo(String tableName,String primaryKey) {
			this.tableName = tableName;
			this.primaryKey = primaryKey;
		}
		private String tableName;
		private String primaryKey;
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public String getPrimaryKey() {
			return primaryKey;
		}
		public void setPrimaryKey(String primaryKey) {
			this.primaryKey = primaryKey;
		}
		
	}
	
	

	
	/**
	 * 当前用户
	 */
	protected User currentUser;
	
	
	/**
	 * 自定义SQL（SQL标识，SQL内容）
	 */
	protected Map<String, String> sqlMap;
	
	/**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
	 */
	protected boolean isNewRecord = false;

	
	public User getCurrentUser() {
		if(currentUser == null){
			currentUser = UserUtils.getUser();
		}
		return currentUser;
	}
	
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}


	@JsonIgnore
	@XmlTransient
	public Map<String, String> getSqlMap() {
		if (sqlMap == null){
			sqlMap = Maps.newHashMap();
		}
		return sqlMap;
	}

	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	/**
	 * 插入之前执行方法，子类实现
	 */
	public void preInsert(){
		this.set("updateBy",this.getCurrentUser().getId());
		this.set("createBy",this.getCurrentUser().getId());
	}
	
	/**
	 * 更新之前执行方法，需要手动调用
	 */
	public void preUpdate(){
		this.set("updateBy",this.getCurrentUser().getId());
	}
	
    /**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     * @return
     */
	public boolean getIsNewRecord() {
        return CommonUtil.isEmptyOrZero(this.getString(this.getPrimaryKey()));
    }


	/**
	 * 全局变量对象
	 */
	@JsonIgnore
	public Global getGlobal() {
		return Global.getInstance();
	}
	
	/**
	 * 获取数据库名称
	 */
	@JsonIgnore
	public String getDbName(){
		return Global.getConfig("jdbc.type");
	}
	

    
    @Override
    public String toString() {
    	return super.toString();
//        return this.toJSON();
    }
    
	/**
	 * 删除标记（0：正常；1：删除；2：审核；）
	 */
	public static final String DEL_FLAG_NORMAL = "0";
	public static final String DEL_FLAG_DELETE = "1";
	public static final String DEL_FLAG_AUDIT = "2";
	
}
