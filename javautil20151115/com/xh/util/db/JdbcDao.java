package com.xh.util.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.xh.util.AppException;
import com.xh.util.Field;
import com.xh.util.Record;
import com.xh.util.RecordSet;
import com.xh.util.RecordTable;


public abstract class JdbcDao extends AbstractDao {

	/**
	 * 存放表结构数据。如果扩展此类，而且不是使用同一个数据库时，需要另外定义一个此类变量和修改getTableBuffere函数 以免造成表的混乱
	 */
	private static RecordSet rsTableBuffer = new RecordSet();

	

	public boolean execute(String sql, Record rdParam) {
		boolean bFlag = false;
		Connection innerCon = this.getConnection();
		PreparedStatement pst = null;
		try {
			logger.debug(sql);
			Record rdSql = SqlUtil.getSpecSql(sql);
			sql = rdSql.getString("sqlJdbc");
			String sqlFy = rdSql.getString("sqlFy");
			pst = innerCon.prepareStatement(sql);
			SqlUtil.setSqlParameter(sqlFy, rdParam, pst, this);
			bFlag = pst.execute();

			return bFlag;
		} catch (AppException ae) {
			logger.debug(sql);
			// rdParam.d();
			throw ae;
		} catch (Exception e) {
			// rdParam.d();
			throw new AppException("执行错误" + sql + "\n" + rdParam.toString(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}



	

	/**
	 * 获取表的元数据，
	 * 
	 * @param tableName
	 *            String 数据库表
	 * @return Record 包含元数据信息的集合，也包括tableName,主键值信息
	 */
	public Record getResultSetMetaData(String tableName) {

		// 如果允许使用缓存，则先检查缓存
		if (isUseCache() && getTableBuffer().containsKey(tableName)) {
			return (Record) getTableBuffer().r(tableName);
		} else { // 缓存中没有，读数据库
			Connection innerCon = this.getConnection();
			
			Statement st = null;
			ResultSet rs = null;
			String sql = null;

			Record rdR = new Record();
			try {
				st = innerCon.createStatement();
				sql = "select * from " + tableName + " where 1=2";
				rs = st.executeQuery(sql);

				ResultSetMetaData md = rs.getMetaData();
				for (int i = 1; i <= md.getColumnCount(); i++) {
					String colName = md.getColumnName(i).toLowerCase();
					int colType = md.getColumnType(i);
					Field field = new Field(colName, colType, "");

					try {
						// 长度
						int scale = md.getScale(i);
						field.setScale(scale);
					} catch (Exception ex2) {
						field.setScale(2);
					}
					// 精度
					try {
						int precision = md.getPrecision(i);
						field.setPrecision(precision);
					} catch (Exception ex2) {
						field.setPrecision(4);
					}

					field.setLength(md.getColumnDisplaySize(i));
					field.setAutoIncrement(md.isAutoIncrement(i));
					field.setColumnTypeName(md.getColumnTypeName(i));
					field.setCaseSensitive(md.isCaseSensitive(i));
					field.setCurrency(md.isCurrency(i));
					field.setSigned(md.isSigned(i));
					field.setColumnClassName(md.getColumnClassName(i));

					// 是否允许null值
					int nullable = md.isNullable(i);
					if (nullable == ResultSetMetaData.columnNoNulls) {
						field.setNullable(false);
					} else {
						field.setNullable(true);
					}
					rdR.pField(field);
				}

				// 查找主键
				java.sql.DatabaseMetaData dm = innerCon.getMetaData();
				ResultSet rsetPrimary = dm
						.getPrimaryKeys(null, null, tableName);
				RecordSet rsPri = new RecordSet(rsetPrimary);
//				String primaryKeys = "";
				for (int i = 0; i < rsPri.size(); i++) {
					String primaryKey = rsPri.r(i).getString("column_name");
					rdR.gField(primaryKey).setPrimaryKey(true);
					
					//设置主键
					rdR.set("_primary_key",primaryKey);
//					primaryKeys += "," +  primaryKey;
				}
				
//				rdR.setTableName(tableName);
//				if (primaryKeys.length() > 0) {
//					primaryKeys = primaryKeys.substring(1);
//					rdR.setPrimaryKey(primaryKeys);
//				}
				

			} catch (SQLException sqlEx) {
				throw new AppException("获取元数据错误", sqlEx);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
				try {
					if (st != null) {
						st.close();
					}
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}

			}

			if (isUseCache()) {
				getTableBuffer().setValue(tableName, rdR);
			}
			return rdR;
		}
	}

	/**
	 * 返回数据库表缓存
	 * 
	 * @return FyCol 数据库表缓存集合
	 */
	public RecordSet getTableBuffer() {
		return rsTableBuffer;
	}

	/**
	 * 处理Statement.getGeneralKey()方法,结果放进rdGenKey属性里
	 * 
	 * @param st
	 *            Statement
	 * @return 返回自动增长值;如果无自动增长值,返回-1;
	 */
	private long handleGenKey(Statement st) {
		ResultSet resultSet = null;
		long iReturn = -1;
		try {
			resultSet = st.getGeneratedKeys();
			if (resultSet.next()) {
				iReturn = resultSet.getLong(1);

				/*
				 * rdGenKey.put(rSet.getString(1), rSet.getString(1)); while
				 * (rSet.next()) { rdGenKey.put(rSet.getString(1),
				 * rSet.getString(1)); }
				 */
			}
			else {
				throw new AppException("无法获取自增长键值！");
			}
		} catch (Exception ex) {
			throw new AppException("获取自增长键值出错！",ex);
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return iReturn;
	}

	
	
	/**
	 * 查询
	 * 
	 * @param sql
	 *            String SQL语句，支持用":fieldName"指定参数的格式
	 * @param rdParam
	 *            Record 参数集合
	 * @param pageNo
	 *            int 起始页，从1开始
	 * @param pageSize
	 *            int 每页的记录数量
	 * @param rdNameField
	 *            String 参数集合
	 * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	 */
	public RecordSet<Record> query(String sql, Record rdParam, int pageNo,
			int pageSize, String rdNameField, boolean statCount) {
		RecordSet<Record> rds = null;
		Connection innerCon = this.getConnection();
		PreparedStatement pst = null;
		try {
			pageNo = pageNo < 1 ? 1 :pageNo;
			pageSize = pageSize < 1 ? 10 :pageSize;
			String sqlBak = sql;
			boolean isPageHandle = false;
			String sqlPage = this.makePageSql(sql, pageNo, pageSize);
			if (sqlPage != null) {
				sql = sqlPage;
				isPageHandle = true;
			}
			Record rdSql = SqlUtil.getSpecSql(sql);
			sql = rdSql.getString("sqlJdbc");
			String sqlFy = rdSql.getString("sqlFy");
//			logger.info("%%"+sql);
			pst = innerCon
					.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			// this.setFieldDataTypeBySelectSql(sqlFy, rdParam);

			SqlUtil.setSqlParameter(sqlFy, rdParam, pst, this);
			ResultSet rs = pst.executeQuery();
			if (rdNameField != null && rdNameField.length() > 0) {
				if (isPageHandle) {
					rds = new RecordSet(rs, 1, pageSize, rdNameField, true,
							this.getCharsetDb(), this.getCharsetClient());
					rds.setPageNo(pageNo);
				}
				else {
					rds = new RecordSet(rs, pageNo, pageSize, rdNameField, true,
							this.getCharsetDb(), this.getCharsetClient());
				}
				
			} else {
				if (isPageHandle) {
					rds = new RecordSet(rs, 1, pageSize, null, true, this
							.getCharsetDb(), this.getCharsetClient());
					rds.setPageNo(pageNo);
				}
				else {
					rds = new RecordSet(rs, pageNo, pageSize, null, true, this
							.getCharsetDb(), this.getCharsetClient());
				}
				
			}

			if (statCount) {
			
				String sqlCount = null;
				sqlCount = makeCountSql(sqlBak, false);//#########
				int recordCount = this.queryForInt(sqlCount, rdParam);
				rds.setTotalCount(recordCount);

			}
		} catch (AppException ae) {
			throw ae;
		} catch (Exception e) {
			throw new AppException("查询错误" + sql + "\n" + rdParam.toString(), e);
		} finally {
			
			//清掉根据索引进行PreparedStatement参数设置
			rdParam.remove(SqlUtil.PARAM_BY_INDEX_FIELD_NAME);
			
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return rds;
	}

	/**
	 * 查询
	 * 返回的Record对象，将和参数rdParam的CLASS类型一致
	 * @param sql
	 *            String SQL语句，支持用":fieldName"指定参数的格式
	 * @param rdParam
	 *            T 参数集合
	 * @param pageNo
	 *            int 起始页，从1开始
	 * @param pageSize
	 *            int 每页的记录数量
	 * @param rdNameField
	 *            String 参数集合
	 * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	 */
	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, int pageNo,
			int pageSize, String rdNameField, boolean statCount) {
		RecordSet rds = null;
		Connection innerCon = this.getConnection();
		PreparedStatement pst = null;
		try {
			pageNo = pageNo < 1 ? 1 :pageNo;
			pageSize = pageSize < 1 ? 10 :pageSize;
			logger.debug(sql);
			String sqlBak = sql;
			boolean isPageHandle = false;
			String sqlPage = this.makePageSql(sql, pageNo, pageSize);
			if (sqlPage != null) {
				sql = sqlPage;
				isPageHandle = true;
			}
			Record rdSql = SqlUtil.getSpecSql(sql);
			sql = rdSql.getString("sqlJdbc");
			String sqlFy = rdSql.getString("sqlFy");
//			logger.error("%%"+sql);
			pst = innerCon
					.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			// this.setFieldDataTypeBySelectSql(sqlFy, rdParam);

			SqlUtil.setSqlParameter(sqlFy, rdParam, pst, this);
			ResultSet rs = pst.executeQuery();
			if (rdNameField != null && rdNameField.length() > 0) {
				if (isPageHandle) {
					rds = new RecordSet(rs, 1, pageSize, rdNameField, true,
							this.getCharsetDb(), this.getCharsetClient(),rdParam.getClass());
					rds.setPageNo(pageNo);
				}
				else {
					rds = new RecordSet(rs, pageNo, pageSize, rdNameField, true,
							this.getCharsetDb(), this.getCharsetClient(),rdParam.getClass());
				}
				
			} else {
				if (isPageHandle) {
					rds = new RecordSet(rs, 1, pageSize, null, true, this
							.getCharsetDb(), this.getCharsetClient(),rdParam.getClass());
					rds.setPageNo(pageNo);
				}
				else {
					rds = new RecordSet(rs, pageNo, pageSize, null, true, this
							.getCharsetDb(), this.getCharsetClient(),rdParam.getClass());
				}
				
			}

			if (statCount) {
				String sqlCount = null;
				sqlCount = makeCountSql(sqlBak, false);//#########
				int recordCount = this.queryForInt(sqlCount, rdParam);
				rds.setTotalCount(recordCount);

			}
		} catch (AppException ae) {
			throw ae;
		} catch (Exception e) {
			throw new AppException("查询错误" + sql + "\n" + rdParam.toString(), e);
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return rds;
	}
	

	/**
	 * 重置数据库表缓存
	 */
	public void resetTableBuffer() {
		rsTableBuffer.clear();
	}

	/**
	 * 根据数据库元数据，设置Record字段的数据类型
	 * @param tableName 数据库表名
	 * @param rdParam 需要设定数据格式的Record对象
	 */
	public void setFieldDataTypeByTable(String tableName,Record rdParam) {
		Record rdMeta = this.getResultSetMetaData(tableName);
		int sizeMeta = rdMeta.size();
		for (int i = 0; i < sizeMeta; i++) {
			String fieldName = rdMeta.gName(i);
			if (rdParam.containsKey(fieldName)) {
				int fieldTypeMeta = rdMeta.gField(fieldName)
						.getType();
				rdParam.gField(fieldName).setType(fieldTypeMeta);
			}
		}
	}

	/**
	 * 数据库更新、新增、删除等操作
	 * 
	 * @param sql
	 *            String SQL语句，支持用":fieldName"指定参数的格式
	 * @param rdParam
	 *            Record 参数集合
	 * @param genKey
	 *            是否处理可能产生的序列值
	 * @return long
	 *         如果要求返回自动增长值(参数genKey为true),则返回自动增长值,如果没有产生增长值,扔出异常;否则返回实际被更新的数量
	 */
	public long update(String sql, Record rdParam, boolean genKey) {
		long updateCount = 0;
		Connection innerCon = this.getConnection();

		PreparedStatement pst = null;
		try {

			logger.debug(sql);
			Record rdSql = SqlUtil.getSpecSql(sql);
			sql = rdSql.getString("sqlJdbc");
			String sqlFy = rdSql.getString("sqlFy");
			pst = innerCon.prepareStatement(sql);
			SqlUtil.setSqlParameter(sqlFy, rdParam, pst, this);
			updateCount = pst.executeUpdate();
			// updateCount = this.getJdbcTemplate().update(sql, new
			// MyPreparedStatementSetter(sql,rdParam,this));

			if (genKey) {
				updateCount = handleGenKey(pst);
			}

			return updateCount;
		} catch (AppException ae) {
			ae.printStackTrace();
			logger.debug(sql);
			throw ae;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("执行错误" + sql + "\n" + rdParam.toString(), e);
		} finally {
			
			//清掉根据索引进行PreparedStatement参数设置
			rdParam.remove(SqlUtil.PARAM_BY_INDEX_FIELD_NAME);
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public int[] updateBatch(String sql, RecordSet rs) {
		Connection innerCon = this.getConnection();
		Record rdParam = null;
		PreparedStatement pst = null;
		int rdCount = rs.size();
		try {
			logger.debug(sql);
			Record rdSql = SqlUtil.getSpecSql(sql);
			sql = rdSql.getString("sqlJdbc");
			String sqlFy = rdSql.getString("sqlFy");
	
			if (rdCount > 0) {
				pst = innerCon.prepareStatement(sql);
			}

			for (int i = 0; i < rdCount; i++) {
				rdParam = rs.r(i);
				SqlUtil.setSqlParameter(sqlFy, rdParam, pst, this);
				pst.addBatch();
				// pst.clearParameters();
			}
			if (pst != null) {
				return pst.executeBatch();
			}

			// handleGenKey(pst);

			return null;
		} catch (AppException ae) {
			logger.debug(sql);
			// rdParam.d();
			throw ae;
		} catch (Exception se) {
			// rs.d();
			se.printStackTrace();
			throw new AppException("设置批处理错误" + sql, se);
		} finally {
			//清掉根据索引进行PreparedStatement参数设置
			rdParam.remove(SqlUtil.PARAM_BY_INDEX_FIELD_NAME);
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] argv) {
		LongDao dao = new LongDao();
		String sql = "select *"+
		"\nfrom aaaa";
		System.out.println(dao.makeCountSql(sql, false));
	}
}
