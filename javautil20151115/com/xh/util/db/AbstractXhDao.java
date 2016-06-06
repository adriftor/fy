package com.xh.util.db;

import com.thinkgem.jeesite.common.persistence.Page;
import com.xh.util.Record;
import com.xh.util.RecordSet;
import com.xh.util.RecordTable;

/**
 *
 * <p>
 * Title:
 * </p>
 * 
 * @author adriftor
 * @version 1.1
 */
public abstract class AbstractXhDao extends AbstractDao {
	

	public RecordSet<Record> query(String sql, Record rdParam, Page page) {
		return query(sql, rdParam, page.getPageNo(), page.getPageSize(), null);
	}

	public RecordSet<Record> query(String sql, Record rdParam, Page page, String rdNameField) {
		return query(sql, rdParam, page.getPageNo(), page.getPageSize(), rdNameField, false);
	}

	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, Page page) {
		return queryT(sql, rdParam, page.getPageNo(), page.getPageSize(), null);
	}

	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, Page page,
			String rdNameField) {
		return queryT(sql, rdParam, page.getPageNo(), page.getPageSize(), rdNameField, false);
	}
	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, Page page,
			String rdNameField,boolean statFlag) {
		return queryT(sql, rdParam, page.getPageNo(), page.getPageSize(), rdNameField, statFlag);
	}
	

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param page.getPageNo()
	 *            页号,从1开始
	 * @param page.getPageSize()
	 *            页大小
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet<Record> queryByIndex(String sql, Page page, Object[] objs) {
		return queryByIndex(sql, page.getPageNo(), page.getPageSize(), "", false, objs);
	}

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param page.getPageNo()
	 *            页号,从1开始
	 * @param page.getPageSize()
	 *            页大小
	 * @param fieldNameOfRecord
	 *            其值作为记录名字的字段
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet<Record> queryByIndex(String sql, Page page, String fieldNameOfRecord,
			Object[] objs) {
		return queryByIndex(sql, page.getPageNo(), page.getPageSize(), fieldNameOfRecord, false, objs);
	}

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param page.getPageNo()
	 *            页号,从1开始
	 * @param page.getPageSize()
	 *            页大小
	 * @param fieldNameOfRecord
	 *            其值作为记录名字的字段
	 * @param statCount
	 *            是否统计记录数量
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet<Record> queryByIndex(String sql, Page page, String fieldNameOfRecord,
			boolean statCount, Object[] objs) {
		Record rd = new Record();
		if (objs != null) {
			int index = 0;
			for (Object obj : objs) {
				rd.put("" + (index++), obj);
			}
		}
		// 设置按索引位置设置参数
		rd.put(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, SqlUtil.PARAM_BY_INDEX_YES);
		return this.query(sql, rd, page.getPageNo(), page.getPageSize(), fieldNameOfRecord, statCount);
	}

	public RecordSet query(String sql, Object pojo, Page page) {
		return this.query(sql, pojo, page, "", false);
	}

	public RecordSet query(String sql, Object pojo, Page page, String fieldNameOfReocrd) {
		return this.query(sql, pojo, page, fieldNameOfReocrd, false);
	}

	public RecordSet query(String sql, Object pojo, Page page, String fieldNameOfReocrd,
			boolean statCount) {
		Record rd = Record.beanToRd(pojo);
		return this.query(sql, rd, page.getPageNo(), page.getPageSize(), fieldNameOfReocrd, statCount);
	}

	
	
}
