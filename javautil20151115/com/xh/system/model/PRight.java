package com.xh.system.model;

import java.sql.Types;
import java.util.Map;

import com.xh.util.RecordTable;

/**
 * 表名：P_RIGHT<br>
 */
public class PRight extends RecordTable {
	public static final String TABLE_NAME = "P_RIGHT";
	public static final String PRIMARY_KEY = "right_code";

	public PRight() {
	}

	public PRight(Map map) {
		super(map);
	}

	static {
		RecordTable.registerTable(PRight.class, new RecordTable.TableInfo("P_RIGHT", "right_code"));
	}

	/**
	 * 列名：RIGHT_CODE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：50<br>
	 * 备注：权限编码<br>
	 */
	public void setRightCode(String rightCode) {
		this.put("right_code", Types.VARCHAR, rightCode);
	}

	/**
	 * 列名：RIGHT_CODE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：50<br>
	 * 备注：权限编码<br>
	 */
	public String getRightCode() {
		return this.getString("right_code");
	}

	/**
	 * 列名：RIGHT_NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：40<br>
	 * 备注：权限ID<br>
	 */
	public void setRightName(String rightName) {
		this.put("right_name", Types.VARCHAR, rightName);
	}

	/**
	 * 列名：RIGHT_NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：40<br>
	 * 备注：权限ID<br>
	 */
	public String getRightName() {
		return this.getString("right_name");
	}

	/**
	 * 列名：REMARKS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：500<br>
	 * 备注：备注<br>
	 */
	public void setRemarks(String remarks) {
		this.put("remarks", Types.VARCHAR, remarks);
	}

	/**
	 * 列名：REMARKS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：500<br>
	 * 备注：备注<br>
	 */
	public String getRemarks() {
		return this.getString("remarks");
	}

	/**
	 * 列名：PARENT_CODE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：50<br>
	 * 备注：组织结构中的父权限code<br>
	 */
	public void setParentCode(String parentCode) {
		this.put("parent_code", Types.VARCHAR, parentCode);
	}

	/**
	 * 列名：PARENT_CODE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：50<br>
	 * 备注：组织结构中的父权限code<br>
	 */
	public String getParentCode() {
		return this.getString("parent_code");
	}
	/**
	 * 列名：RIGHT_CODE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：50<br>
	 * 备注：权限编码<br>
	 */
	// private String rightCode;
	/**
	 * 列名：RIGHT_NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：40<br>
	 * 备注：权限ID<br>
	 */
	// private String rightName;
	/**
	 * 列名：REMARKS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：500<br>
	 * 备注：备注<br>
	 */
	// private String remarks;
	/**
	 * 列名：PARENT_CODE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：50<br>
	 * 备注：组织结构中的父权限code<br>
	 */
	// private String parentCode;
}
