package com.xh.util.db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xh.util.AppException;
import com.xh.util.CommonUtil;
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
public abstract class AbstractDao implements Dao {

	public static final Log logger = LogFactory.getLog(AbstractDao.class);
	DaoConfig daoConfig = new DaoConfig(DaoConfig.DATABASE_TYPE_MYSQL, true);

	

	public boolean execute(String sql) {
		return execute(sql, new Record());
	}

	public String getCharsetClient() {
		return daoConfig.getCharsetClient();
	}

	public String getCharsetDb() {
		return daoConfig.getCharsetDb();
	}

	/**
	 * 获取数据库连接控制方式;控制涉及数据库连接事务控制\连接关闭
	 * 如果为false,则release、begin、commit、rollback函数将无效
	 * 
	 * @return
	 */
	public boolean getUserControlConnection() {
		return this.daoConfig.isUserControlConnection();
	}

	public int getDatabaseType() {
		return daoConfig.getDatabaseType();
	}

	public int insertData(String tableName, Record rdParam) {
		return (int) insertData(tableName, rdParam, "", false);
	}

	public int insertData(String tableName, Record rdParam, String noUpdateField) {
		return (int) insertData(tableName, rdParam, noUpdateField, false);
	}

	public long insertData(String tableName, Record rdParam, String noUpdateField, boolean genKey) {
		String sql = SqlUtil.getInsertSql(tableName, rdParam, noUpdateField, this);
		return update(sql, rdParam, genKey);
	}

	public int[] insertDataBatch(String tableName, RecordSet rs) {
		return insertDataBatch(tableName, rs, "", false);
	}

	public int[] insertDataBatch(String tableName, RecordSet rs, boolean sameSql) {
		return insertDataBatch(tableName, rs, "", sameSql);
	}

	public int[] insertDataBatch(String tableName, RecordSet rs, String noUpdateField) {
		return insertDataBatch(tableName, rs, noUpdateField, false);
	}

	/**
	 * 批量新增
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rs
	 *            RecordSet 记录集
	 * @param noUpdateField
	 *            不填入值的字段列表。字段间有逗号分隔
	 * @param sameSql
	 *            所有记录是否有一样多的字段数量；如果为true,则第一条记录生成的insert语句使用于其他记录。
	 *            其他记录将不再单独产生SQL语句；同时
	 *            新增操作也按批量方式进行。如果为false,则每条记录就将产生SQL语句，新增操作将不做批量方式进行
	 * @return int[] 结果数组。如果sameSql为false,返回null值
	 */
	public int[] insertDataBatch(String tableName, RecordSet rs, String noUpdateField, boolean sameSql) {
		if (rs.size() > 0) {
			int size = rs.size();
			if (sameSql) {
				String sql = SqlUtil.getInsertSql(tableName, rs.r(0), noUpdateField, this);
				int fieldCount = rs.r(0).size();
				for (int i = 0; i < fieldCount; i++) {
					rs.setType(i, rs.r(0).gField(i).getType());
				}
				return updateBatch(sql, rs);
			} else {
				for (int i = 0; i < size; i++) {
					insertData(tableName, rs.r(i), noUpdateField);
				}
			}
		}
		return null;
	}

	public boolean isCanCommit() {
		return this.daoConfig.isCanCommit();
	}

	public boolean isCloseConnection() {
		return this.daoConfig.isCloseConnection();
	}

	public boolean isFillZero() {
		return daoConfig.isFillZero();
	}

	public boolean isUseCache() {
		return daoConfig.isUseCache();
	}

	public boolean isUseTransaction() {
		return daoConfig.isUseTransaction();
	}

	public boolean isBracketFieldName() {
		return daoConfig.isBracketFieldName();
	}

	/**
	 * 生成统计数据库记录的SQL语句，不能够处理所有情况 会删除所有order by语句。
	 * 
	 * @param sql
	 *            String 查询SQL语句
	 * @return String 统计数据库记录的SQL语句
	 */
	public String makeCountSql(String sql, boolean hasLimit) {
		String rSql = null;
		sql = sql.trim();
		String strSql = sql.toLowerCase().trim();

		// 处理select distinct类型的总数计算
		if (strSql.matches("select\\s+distinct\\s+.+")) {

			String strFields = sql.substring(strSql.indexOf("distinct") + 8, strSql.indexOf(" from ")).trim();
			
			// 提取字段
			if (!strFields.equals("*")) {// 包含字段
				if (strFields.indexOf(",") >= 0) {// 多个字段，取第一个字段
					strFields = strFields.substring(0, strFields.indexOf(",")).trim();
				}
				if (strFields.indexOf(" ") >= 0) {// 处理username as userZhName等情况
					strFields = strFields.substring(strFields.lastIndexOf(" ")).trim();
				}
				if (strFields.indexOf("*") < 0) {// 处理select distinct a.* 情况
					strFields = " distinct " + strFields;
				}

			}
			rSql = sql.substring(0, sql.indexOf(" ")) + " count(" + strFields + ") as cnt "
					+ sql.substring(strSql.indexOf(" from "));
		} else {
			rSql = sql.substring(0, sql.indexOf(" ")) + " count(*) as cnt " + sql.substring(strSql.indexOf(" from "));
		}

		rSql = rSql.trim();
		strSql = rSql.toLowerCase().trim();

		if (hasLimit && strSql.indexOf(" limit ") >= 0) {
			rSql = rSql.substring(0, strSql.lastIndexOf(" limit ")).trim();
		}

		strSql = rSql.toLowerCase().trim();
		Pattern p = Pattern.compile("[\\s]+order[\\s]+by[\\s]+[^\\)]+", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(rSql);
		rSql = m.replaceAll("");
		// if (strSql.indexOf(" order by ")>=0) {
		// rSql = rSql.substring(0,strSql.lastIndexOf(" order by "));
		// }
		return rSql;
	}

	public RecordSet<Record> query(String sql) {
		return query(sql, new Record(), 1, 10000, null);
	}

	public RecordSet<Record> query(String sql, Record rdParam) {
		return query(sql, rdParam, 1, 10000, null);
	}

	public RecordSet<Record> query(String sql, Record rdParam, int pageNo, int pageSize) {
		return query(sql, rdParam, pageNo, pageSize, null);
	}

	public RecordSet<Record> query(String sql, Record rdParam, int pageNo, int pageSize, String rdNameField) {
		return query(sql, rdParam, pageNo, pageSize, rdNameField, false);
	}

	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam) {
		return queryT(sql, rdParam, 1, 10000, null);
	}

	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, int pageNo, int pageSize) {
		return queryT(sql, rdParam, pageNo, pageSize, null);
	}

	public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, int pageNo, int pageSize,
			String rdNameField) {
		return queryT(sql, rdParam, pageNo, pageSize, rdNameField, false);
	}

	
	public double queryForDouble(String sql) {
		return queryForDouble(sql, new Record());
	}

	/**
	 * 如果没有记录，返回-1；如果返回的记录没有字段，返回0；
	 */
	public double queryForDouble(String sql, Record rd) {
		RecordSet rsTmp = query(sql, rd);
		if (rsTmp.size() > 0) {
			Record rdTmp = rsTmp.r(0);
			Object getValue = rdTmp.getValue(0);
			if (getValue == null) {
				return 0;
			} else {
				return rdTmp.gDoubleValue(0);
			}

		}
		return -1;
	}

	public int queryForInt(String sql) {
		return queryForInt(sql, new Record());
	}

	/**
	 * 如果没有记录，返回-1；如果返回的记录没有字段，返回0；
	 */
	public int queryForInt(String sql, Record rd) {
		RecordSet rsTmp = query(sql, rd);
		if (rsTmp.size() > 0) {

			Record rdTmp = rsTmp.r(0);
			if (rdTmp.size() > 0) {
				Object getValue = rdTmp.getValue(0);
				if (getValue == null) {
					return 0;
				} else {
					return rdTmp.gIntValue(0);
				}

			}
		}
		return -1;
	}

	public long queryForLong(String sql) {
		return queryForLong(sql, new Record());
	}

	/**
	 * 如果没有记录，返回-1；如果返回的记录没有字段，返回0；
	 */
	public long queryForLong(String sql, Record rd) {
		RecordSet rsTmp = query(sql, rd);
		if (rsTmp.size() > 0) {

			Record rdTmp = rsTmp.r(0);
			Object getValue = rdTmp.getValue(0);
			if (getValue == null) {
				return 0;
			} else {
				return rdTmp.gLongValue(0);
			}
		}
		return -1L;
	}

	public String queryForString(String sql) {
		return queryForString(sql, new Record());
	}

	/**
	 * 如果没有记录，返回null；如果返回的记录没有字段，返回null；
	 */
	public String queryForString(String sql, Record rd) {
		RecordSet rsTmp = query(sql, rd);
		if (rsTmp.size() > 0) {
			Record rdTmp = rsTmp.r(0);
			if (rdTmp.size() > 0) {
				return rdTmp.getString(0);
			}
		}
		return null;
	}

	

	public void setCanCommit(boolean canCommit) {
		this.daoConfig.setCanCommit(canCommit);
	}

	public void setCharsetFrom(String charsetDb) {
		this.daoConfig.setCharsetDb(charsetDb);
	}

	public void setCharsetTo(String charsetClient) {
		this.daoConfig.setCharsetClient(charsetClient);
	}

	/**
	 * 设置是否关闭数据库连接
	 * 是否关闭数据库连接,是根据计数器计算的,所以请正确匹配setCloseConnection(false)和setCloseConnection(
	 * true)的调用次数.调用false的次数必须和调用true的次数一致,才可以正确关闭数据库连接
	 */
	public void setCloseConnection(boolean closeConnection) {
		this.daoConfig.setCloseConnection(closeConnection);
	}

	/**
	 * 设置数据库连接控制方式;控制涉及数据库连接事务控制\连接关闭
	 * 如果为false,则release、begin、commit、rollback函数将无效
	 * 
	 * @return
	 */
	public void setUserControlConnection(boolean conControl) {
		this.daoConfig.setUserControlConnection(conControl);
	}

	public void setDatabaseType(int databaseType) {
		this.daoConfig.setDatabaseType(databaseType);
	}

	public void setFillZero(boolean fillZero) {
		this.daoConfig.setFillZero(fillZero);
	}

	public void setUseCache(boolean useCache) {
		this.daoConfig.setUseCache(useCache);
	}

	public void setUseTransaction(boolean useTransaction) {
		this.daoConfig.setUseTransaction(useTransaction);
	}

	public void setBracketFieldName(boolean bracketFieldName) {
		this.daoConfig.setBracketFieldName(bracketFieldName);
	}

	public int update(String sql) {
		Record rd = new Record();
		return (int) update(sql, rd, false);
	}

	public int update(String sql, Record rdParam) {
		return (int) update(sql, rdParam, false);
	}

	/**
	 * 修改
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rdParam
	 *            Record 参数
	 * @param sqlWhere
	 *            SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	 * @return int 操作结果数量
	 */
	public int updateData(String tableName, Record rdParam, String sqlWhere) {

		return updateData(tableName, rdParam, sqlWhere, "");
	}

	/**
	 * 修改
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rdParam
	 *            Record 参数
	 * @param sqlWhere
	 *            SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	 * @param noUpdateField
	 *            不更新的字段列表。字段间有逗号分隔
	 * @return int 操作结果数量
	 */
	public int updateData(String tableName, Record rdParam, String sqlWhere, String noUpdateField) {

		String sql = SqlUtil.getUpdateSql(tableName, rdParam, sqlWhere, noUpdateField, this);
		return (int) update(sql, rdParam);
	}

	/**
	 * 修改
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param pojo
	 *            pojo 参数
	 * @param sqlWhere
	 *            SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	 * @return int 操作结果数量
	 */
	public int updateData(String tableName, Object pojo, String sqlWhere) {

		return updateData(tableName, pojo, sqlWhere, "");
	}

	/**
	 * 修改
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param pojo
	 *            pojo 参数
	 * @param sqlWhere
	 *            SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	 * @param noUpdateField
	 *            不更新的字段列表。字段间有逗号分隔
	 * @return int 操作结果数量
	 */
	public int updateData(String tableName, Object pojo, String sqlWhere, String noUpdateField) {
		Record rdParam = Record.beanToRd(pojo);
		return (int) updateData(tableName, rdParam, sqlWhere, noUpdateField);
	}

	public int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere) {

		return updateDataBatch(tableName, rs, sqlWhere, "", false);
	}

	public int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere, boolean sameSql) {
		return updateDataBatch(tableName, rs, sqlWhere, "", sameSql);
	}

	public int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere, String noUpdateField) {
		return updateDataBatch(tableName, rs, sqlWhere, noUpdateField, false);
	}

	/**
	 * 批量更新
	 * 
	 * @param tableName
	 *            String 数据库表名
	 * @param rs
	 *            RecordSet 记录集
	 * @param sqlWhere
	 *            SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	 * @param noUpdateField
	 *            不更新的字段列表。字段间有逗号分隔
	 * @param sameSql
	 *            所有记录是否有一样多的字段数量；如果为true,则第一条记录生成的insert语句使用于其他记录。
	 *            其他记录将不再单独产生SQL语句；同时
	 *            更新操作也按批量方式进行。如果为false,则每条记录就将产生SQL语句，更新操作将不做批量方式进行
	 * @return int[] 结果数组。如果sameSql为false,返回null值
	 */
	public int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere, String noUpdateField,
			boolean sameSql) {
		if (rs.size() > 0) {
			if (sameSql) {
				Record rd = rs.r(0);
				String sql = SqlUtil.getUpdateSql(tableName, rd, sqlWhere, noUpdateField, this);
				int fieldCount = rd.size();
				for (int i = 0; i < fieldCount; i++) {
					rs.setType(i, rd.gField(i).getType());
				}
				return updateBatch(sql, rs);
			} else {
				int size = rs.size();
				for (int i = 0; i < size; i++) {
					this.updateData(tableName, rs.r(i), sqlWhere, noUpdateField);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * 获取ORACLE或POSTGRESQL数据库通用序列的下一个值。或获取SQLSERVER和MYSQL数据库最近一次的自动增长值<br>
	 * 对于SQLSERVER数据库，未指明表名或与全局变量SEQ_NAME值相同时，采用scoPe_Identity()函数获取最近一次自动增长值。
	 * 如果指明了表名(与全局变量SEQ_NAME不同时)，则获取该表的最近一次的自动增长值<br>
	 * 
	 * 通用序列由AbstractDao类的全局变量SEQ_NAME定义
	 * 
	 * @return 序列下一个值或最近一次自动增长值，其他不支持的数据库，将抛出异常
	 */
	public synchronized String genSeqNextVal() {
		return genSeqNextVal(null);
	}

	public String getUUID() {
		if (this.getDatabaseType() == DaoConfig.DATABASE_TYPE_MYSQL) {
			return this.queryForString("select uuid()").replaceAll("\\-", "");
		} else {
			return UUID.randomUUID().toString().replaceAll("\\-", "");
		}
	}

	/**
	 * 
	 * 获取ORACLE或POSTGRESQL数据库通用序列的下一个值。或获取SQLSERVER和MYSQL数据库最近一次的自动增长值<br>
	 * 对于SQLSERVER数据库，未指明表名或与全局变量SEQ_NAME值相同时，采用scoPe_Identity()函数获取最近一次自动增长值。
	 * 如果指明了表名(与全局变量SEQ_NAME不同时)，则获取该表的最近一次的自动增长值<br>
	 * 
	 * @param seqName
	 *            序列名或表名(SQLSERVER数据库时为表名)。对于SQL
	 *            SERVER数据，此值为空或为表名。对于mysql数据库，忽略此值。如果此值为空，则将采用全局变量SEQ_NAME定义的值（
	 *            sqlserver数据库不采用）。 通用序列由AbstractDao类的全局变量SEQ_NAME定义
	 * @return 序列下一个值或最近一次自动增长值，其他不支持的数据库，将抛出异常
	 * 
	 * 
	 */
	public synchronized String genSeqNextVal(String seqName) {
		String seqNameBak = seqName;
		if (CommonUtil.isEmpty(seqName)) {
			seqName = this.daoConfig.getSequenceName();
		}

		if (this.getDatabaseType() == DaoConfig.DATABASE_TYPE_ORACLE) {
			return this.queryForString("select " + seqName + ".nextval");
		} else if (this.getDatabaseType() == DaoConfig.DATABASE_TYPE_POSTGRE) {
			return this.queryForString("select nextval('" + seqName + "')");
		} else if (this.getDatabaseType() == DaoConfig.DATABASE_TYPE_SQLSERVER) {
			if (CommonUtil.isEmpty(seqNameBak) || seqNameBak.equals(this.daoConfig.getSequenceName())) {
				// select @@identity
				return this.queryForString("select scoPe_Identity()");
			} else {
				return this.queryForString("select IDENT_CURRENT('" + seqName + "')");
			}

		} else if (this.getDatabaseType() == DaoConfig.DATABASE_TYPE_MYSQL) {
			return this.queryForString("select last_insert_id()");
		} else {
			throw new AppException("不支持获取序列值或最近一次的自动增长值!");
		}
	}

	public boolean isUserControlConnection() {
		return this.daoConfig.isUserControlConnection();
	}

	public int getCountCloseConnection() {
		return this.daoConfig.getCountCloseConnection();
	}

	public void setCountCloseConnection(int countCloseConnection) {
		this.daoConfig.setCountCloseConnection(countCloseConnection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#update(java.lang.String, java.lang.Object,
	 * boolean)
	 */

	public long update(String sql, Object pojo, boolean genKey) {
		Record rd = Record.beanToRd(pojo);
		return this.update(sql, rd, genKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#updateByIndex(java.lang.String,
	 * java.lang.Object[])
	 */
	public int updateByIndex(String sql, Object... objs) {
		Record rd = new Record();
		int index = 0;
		for (Object obj : objs) {
			rd.put("" + (index++), obj);
		}
		// 设置按索引位置设置参数
		rd.put(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, SqlUtil.PARAM_BY_INDEX_YES);
		return this.update(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#update(java.lang.String, java.lang.Object)
	 */
	public int update(String sql, Object pojo) {
		Record rd = Record.beanToRd(pojo);
		return this.update(sql, rd);
	}

	// /*
	// * (non-Javadoc)
	// * @see com.zte.eas.gdyd.util.Dao#updateBatch(java.lang.String,
	// java.util.List)
	// */
	// public int[] updateBatch(String sql, List<Object> listPojo) {
	// RecordSet rs = new RecordSet();
	// for (Object obj:listPojo) {
	// Record rd = Record.beanToRd(obj, null, false, null, null, false);
	// rs.addRecord(rd);
	// }
	// return this.updateBatch(sql, rs);
	// }

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet queryEx(String sql, Object... params) {
		return queryByIndex(sql, 1, 10000, "", false, params);
	}

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet queryByIndex(String sql, Object[] params) {
		return queryByIndex(sql, 1, 10000, "", false, params);
	}

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param pageNo
	 *            页号,从1开始
	 * @param pageSize
	 *            页大小
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet<Record> queryByIndex(String sql, int pageNo, int pageSize, Object[] objs) {
		return queryByIndex(sql, pageNo, pageSize, "", false, objs);
	}

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param pageNo
	 *            页号,从1开始
	 * @param pageSize
	 *            页大小
	 * @param fieldNameOfRecord
	 *            其值作为记录名字的字段
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet<Record> queryByIndex(String sql, int pageNo, int pageSize, String fieldNameOfRecord,
			Object[] objs) {
		return queryByIndex(sql, pageNo, pageSize, fieldNameOfRecord, false, objs);
	}

	/**
	 * 根据索引位置执行预编译参数的设置,并查询
	 * 
	 * @param sql
	 * @param pageNo
	 *            页号,从1开始
	 * @param pageSize
	 *            页大小
	 * @param fieldNameOfRecord
	 *            其值作为记录名字的字段
	 * @param statCount
	 *            是否统计记录数量
	 * @param objs
	 *            参数
	 * @return
	 */
	public RecordSet<Record> queryByIndex(String sql, int pageNo, int pageSize, String fieldNameOfRecord,
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
		return this.query(sql, rd, pageNo, pageSize, fieldNameOfRecord, statCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#query(java.lang.String, java.lang.Object)
	 */
	public RecordSet query(String sql, Object pojo) {
		// if (pojo == null) {
		// return query(sql);
		// }
		// else if (pojo.getClass().isArray()) {
		// return queryByIndex(sql, 1,10000,"",false,(Object[])pojo);
		// }
		// else if (pojo.getClass().isPrimitive() ||
		// pojo.getClass().getName().startsWith("java")) {
		// return queryByIndex(sql, 1,10000,"",false,new Object[]{pojo});
		// }
		return this.query(sql, pojo, 1, 10000, "", false);
	}

	public RecordSet query(String sql, Object pojo, int pageNo, int pageSize) {
		return this.query(sql, pojo, pageNo, pageSize, "", false);
	}

	public RecordSet query(String sql, Object pojo, int pageNo, int pageSize, String fieldNameOfReocrd) {
		return this.query(sql, pojo, pageNo, pageSize, fieldNameOfReocrd, false);
	}

	public RecordSet query(String sql, Object pojo, int pageNo, int pageSize, String fieldNameOfReocrd,
			boolean statCount) {
		Record rd = Record.beanToRd(pojo);
		return this.query(sql, rd, pageNo, pageSize, fieldNameOfReocrd, statCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#queryByIndex(java.lang.String,
	 * java.lang.Object[])
	 */
	public String queryForStringByIndex(String sql, Object... objs) {
		Record rd = new Record();
		int index = 0;
		for (Object obj : objs) {
			rd.put("" + (index++), obj);
		}
		// 设置按索引位置设置参数
		rd.put(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, SqlUtil.PARAM_BY_INDEX_YES);
		return this.queryForString(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#query(java.lang.String, java.lang.Object)
	 */
	public String queryForString(String sql, Object pojo) {
		Record rd = Record.beanToRd(pojo);
		return this.queryForString(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#queryByIndex(java.lang.String,
	 * java.lang.Object[])
	 */
	public int queryForIntByIndex(String sql, Object... objs) {
		Record rd = new Record();
		int index = 0;
		for (Object obj : objs) {
			rd.put("" + (index++), obj);
		}
		// 设置按索引位置设置参数
		rd.put(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, SqlUtil.PARAM_BY_INDEX_YES);
		return this.queryForInt(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#query(java.lang.String, java.lang.Object)
	 */
	public int queryForInt(String sql, Object pojo) {
		Record rd = Record.beanToRd(pojo);
		return this.queryForInt(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#queryByIndex(java.lang.String,
	 * java.lang.Object[])
	 */
	public long queryForLongByIndex(String sql, Object... objs) {
		Record rd = new Record();
		int index = 0;
		for (Object obj : objs) {
			rd.put("" + (index++), obj);
		}
		// 设置按索引位置设置参数
		rd.put(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, SqlUtil.PARAM_BY_INDEX_YES);
		return this.queryForLong(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#query(java.lang.String, java.lang.Object)
	 */
	public long queryForLong(String sql, Object pojo) {
		Record rd = Record.beanToRd(pojo);
		return this.queryForLong(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#queryByIndex(java.lang.String,
	 * java.lang.Object[])
	 */
	public double queryForDoubleByIndex(String sql, Object... objs) {
		Record rd = new Record();
		int index = 0;
		for (Object obj : objs) {
			rd.put("" + (index++), obj);
		}
		// 设置按索引位置设置参数
		rd.put(SqlUtil.PARAM_BY_INDEX_FIELD_NAME, SqlUtil.PARAM_BY_INDEX_YES);
		return this.queryForDouble(sql, rd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zte.eas.gdyd.util.Dao#query(java.lang.String, java.lang.Object)
	 */
	public double queryForDouble(String sql, Object pojo) {
		Record rd = Record.beanToRd(pojo);
		return this.queryForDouble(sql, rd);
	}

	/**
	 * 释放连接 对于任何调用了数据库功能的操作，最后都应执行此方法关闭连接，而不应该调用Connection.close()方法关闭
	 */
	public void releaseCon() {

		if (!this.isUserControlConnection()) {
			return;
		}

		int iCloseConnection = this.getCountCloseConnection();
		if (iCloseConnection > 0) {
			return;
		}
		Connection innerCon = this.getConnection();

		if (innerCon != null) {
			try {
				innerCon.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		innerCon = null;

	}

	/**
	 * 回滚数据库事务
	 */
	public void rollback() {
		if (this.isUserControlConnection() && this.isUseTransaction()) {

			// 检查是否允许提交
			Boolean canCommit = this.isCanCommit();
			if (!canCommit) {
				return;
			}
			Connection innerCon = this.getConnection();
			if (innerCon != null) {
				try {
					innerCon.rollback();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
	}

	/**
	 * 启动数据库事务
	 *
	 */
	public void begin() {
		if (this.isUserControlConnection() && this.isUseTransaction()) {
			try {
				Connection innerCon = this.getConnection();
				innerCon.setAutoCommit(false);
			} catch (SQLException se) {
				throw new AppException("设置事务错误", se);
			}
		}
	}

	/**
	 * 提交数据库事务
	 */
	public boolean commit() {

		if (this.isUserControlConnection() && this.isUseTransaction()) {
			try {
				// 检查是否允许提交
				Boolean canCommit = this.isCanCommit();
				if (!canCommit) {
					return false;
				}
				Connection innerCon = this.getConnection();
				if (innerCon != null) {
					innerCon.commit();
				}
				return true;
			} catch (SQLException se) {
				throw new AppException(se);
			}
		}
		return false;
	}

	public Record get(String sql) {
		return get(sql, new Record());
	}
	public Record get(String sql, Record rdParam) {
		RecordSet<?> rs = this.query(sql, rdParam, 1, 1, "", false);
		return rs.getRecord(0, null);

	}
	
	public <T extends RecordTable> T get(String sql, T rdParam) {
		RecordSet<T> rs = this.queryT(sql, rdParam, 1, 1, "", false);
		return rs.getRecord(0, null);

	}
	
	/**
	 * 根据主键获取一条记录
	 * @param entity 必须包含主键值
	 * @return
	 */
	public <T extends RecordTable> T get(T entity) {
		return get(entity,true);
	}
	
	/**
	 * 匹配key/value，返回第一条记录。或者根据主键匹配
	 * @param domainFilter
	 * @param byPrimaryKey 如果为true,则根据主键查询。否则进行key/value匹配
	 * @return
	 */
	public <T extends RecordTable> T get(T domainFilter,boolean byPrimaryKey) {
		StringBuilder sb = new StringBuilder("select * from ");
		sb.append(domainFilter.getTableName());
		sb.append(" where ");
		if ( ! byPrimaryKey) {
			int size = domainFilter.size();
			boolean andFlag = false;
			Record rdTable = this.getResultSetMetaData(domainFilter.getTableName());
			for (int i = 0;i<size;i++) {
				String columnName = domainFilter.gName(i);
				if (rdTable.containsKey(columnName)) {
					if (andFlag) {
						sb.append(" and ");
					}
					columnName = rdTable.gField(columnName).getName();
					sb.append(columnName);
					sb.append("=?");
					andFlag = true;
				}
			}
		}
		else {
			sb.append(domainFilter.getPrimaryKey());
			sb.append("=? ");
		}
		RecordSet<T> rs = this.queryT(sb.toString(), domainFilter, 1, 1, "", false);
		return rs.getRecord(0, null);
	}
	public <T extends RecordTable> T get(Class<T> c,Serializable id) {
		T entity = newT(c);
		entity.put(RecordTable.getPrimaryKey(c),id);
		return get(entity);
	}


	/**
	 * 新增或修改
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends RecordTable> int saveOrUpdate(T entity) {

		if (entity.getString(entity.getPrimaryKey(), "0").equals("0")) {
			return this.insertData(entity.getTableName(), entity);
		} else {
			int sizeDb = this.queryForIntByIndex(
					"select count(1) as cnt from " + entity.getTableName() + " where " + entity.getPrimaryKey() + "=?",
					entity.getString(entity.getPrimaryKey()));
			if (sizeDb > 0) {
				return this.updateData(entity.getTableName(), entity, entity.getPrimaryKey());
			} else {
				return this.insertData(entity.getTableName(), entity);
			}

		}
	}

	public <T extends RecordTable> int saveOrUpdate(Class<T> c, Record entity) {
		return saveOrUpdate(newT(c, entity));
	}

	/**
	 * 修改
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends RecordTable> int update(T entity) {
		return this.updateData(entity.getTableName(), entity, entity.getPrimaryKey());
	}

	public <T extends RecordTable> int update(Class<T> c, Record entity) {
		return this.update(newT(c, entity));
	}

	/**
	 * 新增
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends RecordTable> int save(T entity) {
		return this.insertData(entity.getTableName(), entity);
	}
	public <T extends RecordTable> int save(T entity,boolean genKey) {
		return (int)this.insertData(entity.getTableName(), entity,"",genKey);
	}
	public <T extends RecordTable> int save(T entity,String notInsertFields,boolean genKey) {
		return (int)this.insertData(entity.getTableName(), entity,notInsertFields,genKey);
	}
	public <T extends RecordTable> int save(Class<T> c, Record entity) {
		return this.save(newT(c, entity));
	}

	public <T extends RecordTable> int delete(T entity) {
		return this.update("delete from " + entity.getTableName() + " where " + entity.getPrimaryKey() + "=?", entity);
	}

	public <T extends RecordTable> int delete(Class<T> c, Record entity) {
		return delete(newT(c, entity));
	}

	/**
	 * 获得所有记录
	 */
	public <T extends RecordTable> RecordSet<T> getAll(Class<T> c) {
		T entity = null;
		try {
			entity = c.newInstance();
		} catch (Exception ex) {
			throw new AppException(ex);
		}
		return this.queryT("select * from " + entity.getTableName(), entity);

	}
	
	/**
	 * 根据domainFilter的key/value获得所有匹配的记录
	 * 此查询用于小数据集的查询
	 * @param domainFilter
	 * @return
	 */
	public <T extends RecordTable> RecordSet<T> getAll(T domainFilter) {
		StringBuilder sb = new StringBuilder("select * from ");
		sb.append(domainFilter.getTableName());
		sb.append(" where 1=1 ");
	
		int size = domainFilter.size();
		Record rdTable = this.getResultSetMetaData(domainFilter.getTableName());
		for (int i = 0;i<size;i++) {
			String columnName = domainFilter.gName(i);
			if (rdTable.containsKey(columnName)) {
				columnName = rdTable.gField(columnName).getName();
				sb.append(" and ");
				sb.append(columnName);
				sb.append("=? ");
			}
		}
		return this.queryT(sb.toString(), domainFilter);
	}

	protected <T extends RecordTable> T newT(Class<T> c) {
		try {
			return c.newInstance();
		} catch (Exception ex) {
			throw new AppException("无法创建T对象", ex);
		}
	}

	protected <T extends RecordTable> T newT(Class<T> c, Record rd) {
		try {
			return c.getConstructor(Map.class).newInstance(rd);
		} catch (Exception ex) {
			throw new AppException("无法创建T对象", ex);
		}
	}

	public <T extends RecordTable> int delete(Class<T> c, String ids) {
		int type = newT(c).gPropertyRecord().gField(RecordTable.getPrimaryKey(c)).getType();
		if (type == Types.VARCHAR) {
			ids = CommonUtil.replaceAll(ids, ",", "','");
			ids = "'" + ids + "'";
		}
		return this.update("delete from " + RecordTable.getTableName(c) + " where " + RecordTable.getPrimaryKey(c) + " in ("
				+ ids + ")");
	}
	
	/**
	 * 分页处理。生成数据库分页的SQL语句，如MYDQL数据库加上limit 2,20<br>
	 * 只支持mysql\oracle\postgresql三种数据库<br>
	 * 对于MSYQL、POSTGRESQL数据库，如果SQL语句里已经包含“ limit ”则不再进行分页处理<br>
	 * 对于ORACLE数据，如果SQL语句里已经包含“ rownum”则不再进行分页处理
	 * 对SQL语句里包含group by,count,max,min,agv,sum函数的也不进行分页处理
	 * @param sql
	 * @param pageNo 开始页，从1开始，小于1则默认为1
	 * @param pageSize 页大小，小于1则默认为10
	 * @return 如果没有进行分页处理，返回null。否则返回进行分页处理后的sql语句
	 */
	protected String makePageSql(String sql,int pageNo,int pageSize) {
		if (sql.matches("(?i)\\s*select(\\s+|.+,)(count|max|min|avg|sum|GROUP_CONCAT)\\s*\\(.+")) {
			return null;
		}
		if (sql.matches("(?i)\\s*select.+group\\s+by.+")) {
			return null;
		}
		pageNo = pageNo < 1 ? 1 :pageNo;
		pageSize = pageSize < 1 ? 10 :pageSize;
		boolean isPageHandle = false;
		if (getDatabaseType()==DaoConfig.DATABASE_TYPE_MYSQL) {
			if ( ! sql.matches("(?i)\\s*select.+\\s+limit\\s+\\d+\\s*(,\\s*\\d+\\s*)?")) {
				int pf = pageNo - 1;
				pf = pf * pageSize;
				sql += " limit " + pf + "," + pageSize;
				isPageHandle = true;
			}
		}
		else if (getDatabaseType()==DaoConfig.DATABASE_TYPE_ORACLE) {
			if ( ! sql.matches("(?i)\\s*select.+\\s*rownum\\s*<\\s*\\d+.*")) {
				sql = "select * from ( select fy_table.*, rownum as fy_rownum from ( "
						+ sql
						+ ") fy_table where rownum <"
						+ (pageNo * pageSize + 1)
						+ " ) where fy_rownum>="
						+ ((pageNo - 1) * pageSize + 1);
				isPageHandle = true;

			}
		}
		else if (getDatabaseType()==DaoConfig.DATABASE_TYPE_POSTGRE) {
			if ( ! sql.matches("(?i)\\s*select.+\\s+limit\\s+\\d+\\s*(\\s+offset\\s+\\d+\\s*)?")) {				
				int pf = pageNo - 1;
				pf = pf * pageSize;
				sql += " limit " + pageSize + " offset " + pf;
				isPageHandle = true;
			}
		}
		if (isPageHandle) {
			return sql;
		}
		return null;
	}

}
