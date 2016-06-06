package com.xh.util.db;


import java.io.Serializable;

import com.xh.util.AppException;
import com.xh.util.RecordTable;
import com.xh.util.Record;
import com.xh.util.RecordSet;


/**
 * 数据库操作接口
 * <p>Title: </p>
 * @author adriftor
 * @version 1.2
 */
public interface Dao {
	

	    public abstract void begin();

	    public abstract boolean commit();



	    public  boolean execute(String sql);

	    public  boolean execute(String sql,Record rd);
	    /**
	     * 目标编码
	     * @return String
	     */
	    public  String getCharsetClient();
	    /**
	     * 数据库编码
	     * @return String
	     */
	    public  String getCharsetDb();

	    /**
	     * 获取数据库连接控制方式;控制涉及数据库连接事务控制\连接关闭
	     *  如果为false,则release、begin、commit、rollback函数将无效
	     * @return
	     */
	    public  boolean getUserControlConnection();

	    public  java.sql.Connection getConnection();


	    /**
	     * 当前数据库类型
	     * @return int
	     */
	    public  int getDatabaseType();


	    /**
	     * 获取表元数据
	     * @param tableName String 数据库表
	     * @return Record 包含元数据信息的集合
	     */
	    public  Record getResultSetMetaData(String tableName);

	    /**
	     * 返回数据库表缓存
	     * @return FyCol 数据库表缓存集合
	     */
	    public  RecordSet getTableBuffer();

	    /**
	     * 新增
	     * @param tableName String 数据库表名
	     * @param rdParam Record 参数
	     * @return int 操作结果数量
	     */
	    public  int insertData(String tableName, Record rdParam);

	    /**
	     * 新增
	     * @param tableName String 数据库表名
	     * @param rdParam Record 参数
	     * @param noUpdateField 不填入值的字段列表。字段间有逗号分隔
	     * @return int 操作结果数量
	     */
	     public  int insertData(String tableName, Record rdParam, String noUpdateField);

	    /**
	     * 新增
	     * @param tableName String 数据库表名
	     * @param rdParam Record 参数
	     * @param noUpdateField 不填入值的字段列表。字段间有逗号分隔
	     * @param genKey 是否处理可能产生的序列值
	     * @return long 操作结果数量，,如果要获取新生成的自动键值，则返回自动键值
	     */
	    public  long insertData(String tableName, Record rdParam, String noUpdateField,boolean genKey);

	    /**
	     * 批量新增
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @return int[] 结果数组
	     */
	    public  int[] insertDataBatch(String tableName, RecordSet rs);

	    /**
	     * 批量新增
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param sameSql 所有记录是否有一样多的字段数量；如果为true,则第一条记录生成的insert语句使用于其他记录。其他记录将不再单独产生SQL语句；同时
	     *                新增操作也按批量方式进行。如果为false,则每条记录就将产生SQL语句，新增操作将不做批量方式进行
	     * @return int[] 结果数组。如果sameSql为false,返回null值
	     */
	    public  int[] insertDataBatch(String tableName, RecordSet rs, boolean sameSql);


	    /**
	     * 批量新增
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param noUpdateField 不填入值的字段列表。字段间有逗号分隔
	     * @return int[] 结果数组
	     */
	    public  int[] insertDataBatch(String tableName, RecordSet rs, String noUpdateField);
	   

	    /**
	     * 批量新增
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param noUpdateField 不填入值的字段列表。字段间有逗号分隔
	     * @param sameSql 所有记录是否有一样多的字段数量；如果为true,则第一条记录生成的insert语句使用于其他记录。其他记录将不再单独产生SQL语句；同时
	     *                新增操作也按批量方式进行。如果为false,则每条记录就将产生SQL语句，新增操作将不做批量方式进行
	     * @return int[] 结果数组。如果sameSql为false,返回null值
	     */
	    public  int[] insertDataBatch(String tableName, RecordSet rs, String noUpdateField, boolean sameSql);

	    

	    /**
	     * 是否允许提交
	     * @return boolean
	     */
	    public  boolean isCanCommit();
	    /**
	     * 数据库连接是否允许关闭
	     * @return boolean
	     */
	    public  boolean isCloseConnection();

	    

	    /**
	     * 对于新增和修改操作，当传递过来的数值型字段为空串的情况下，是否填入"0"
	     * @return boolean
	     */
	    public  boolean isFillZero();


	    /**
	     * 是否缓存数据库表结构
	     * @return boolean
	     */
	    public  boolean isUseCache();
	    
	    /**
	     * 数据库操作是否按事务方式进行。如果为false，则begin()、rollback()、commit()函数将不进行任何操作
	     * @return boolean
	     */
	    public  boolean isUseTransaction();

	    public boolean isBracketFieldName();
	    /**
	     * 生成统计数据库记录的SQL语句
	     * @param sql String 查询SQL语句
	     * @return String 统计数据库记录的SQL语句
	     */
	    public  String makeCountSql(String sql,boolean hasLimit);

	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public  RecordSet<Record> query(String sql);

	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public  RecordSet<Record> query(String sql, Record rdParam);

	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param pageNo int 起始页，从1开始
	     * @param pageSize int 每页的记录数量
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public  RecordSet<Record> query(String sql, Record rdParam, int pageNo, int pageSize);

	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param pageNo int 起始页，从1开始
	     * @param pageSize int 每页的记录数量
	     * @param rdNameField String 参数集合
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public  RecordSet<Record> query(String sql, Record commonRecord, int pageNo, int pageSize, String rdNameField);
	    

	    public RecordSet<Record> query(String sql, Record rdParam, int pageNo,
						int pageSize, String rdNameField, boolean statCount) ;
	    
	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param pageNo int 起始页，从1开始
	     * @param pageSize int 每页的记录数量
	     * @param rdNameField String 参数集合
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam);
	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param pageNo int 起始页，从1开始
	     * @param pageSize int 每页的记录数量
	     * @param rdNameField String 参数集合
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, int pageNo, int pageSize);
	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param pageNo int 起始页，从1开始
	     * @param pageSize int 每页的记录数量
	     * @param rdNameField String 参数集合
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public <T extends RecordTable> RecordSet<T> queryT(String sql, T rdParam, int pageNo,int pageSize, String rdNameField, boolean statCount) ;
	    /**
	     * 查询
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param pageNo int 起始页，从1开始
	     * @param pageSize int 每页的记录数量
	     * @param rdNameField String 参数集合
	     * @return RecordSet 记录集。当数据库匹配记录数量为0时，返回0长度的记录集。返回值不可能是null值
	     */
	    public <T extends RecordTable> RecordSet<T> queryT(String sql, T commonRecord, int pageNo, int pageSize, String rdNameField);

	    /**
	     * 查询double值
	     * @param sql String SQL语句
	     * @return double 整数值，如果没记录，返回-1
	     */
	    public  double queryForDouble(String sql);


	    /**
	     * 查询double值
	     * @param sql String SQL语句
	     * @param rd 参数集合
	     * @return double 整数值，如果没记录，返回-1
	     */
	    public  double queryForDouble(String sql, Record rd);


	    /**
	     * 查询整数值
	     * @param sql String SQL语句
	     * @return int 整数值，如果没记录，返回-1
	     */
	    public  int queryForInt(String sql);
	    
	    

	    /**
	     * 查询整数值
	     * @param sql String SQL语句
	     * @param rd 参数集合
	     * @return int 整数值，如果没记录，返回-1
	     */
	    public  int queryForInt(String sql, Record rd);

	    /**
	     * 查询LONG值
	     * @param sql String SQL语句
	     * @return int 整数值，如果没记录，返回-1
	     */
	    public  long queryForLong(String sql);

	    /**
	     * 查询LONG值
	     * @param sql String SQL语句
	     * @param rd 参数集合
	     * @return int 整数值，如果没记录，返回-1
	     */
	    public  long queryForLong(String sql, Record rd);

	    /**
	     * 查询字符串
	     * @param sql String SQL语句
	     * @return String 字符串
	     */
	    public  String queryForString(String sql);

	    /**
	     * 查询字符串
	     * @param sql String SQL语句
	     * @param rd 参数集合
	     * @return String 字符串
	     */
	    public  String queryForString(String sql, Record rd);

	   

	    
	    /**
	     * 释放连接
	     * 对于任何调用了数据库功能的操作，最后都应执行此方法关闭连接，而不应该调用Connection.close()方法关闭
	     */
	    public  void releaseCon();

	    /**
	     * 重置数据库表缓存
	     */
	    public  void resetTableBuffer();

	    /**
	     * 回滚数据库事务
	     */
	    public  void rollback();

	    /**
	     * 设置是否能进行数据库事务提交
	     * @param canCommit boolean
	     */
	    public  void setCanCommit(boolean canCommit);



	    /**
	     * 设置源字符集
	     * @param charsetDb String
	     */
	    public  void setCharsetFrom(String charsetDb);

	    /**
	     * 设置目标字符集
	     * @param charsetClient String
	     */
	    public  void setCharsetTo(String charsetClient);

	    /**
	     * 设置是否可以关闭连接
	     * @param closeCon boolean
	     */
	    public  void setCloseConnection(boolean closeConnection);

	    /**
	     * 设置数据库连接控制方式;控制涉及数据库连接事务控制\连接关闭
	     * 如果为false,则release、begin、commit、rollback函数将无效
	     * @return
	     */
	    public  void setUserControlConnection(boolean conControl);
	    
	    /**
	     * 是否用户自己控制连接
	     * @return
	     */
	    public boolean isUserControlConnection();

	    /**
	     * 设置数据库类型
	     * @param databaseType int
	     */
	    public  void setDatabaseType(int databaseType);


	    /**
	     * 设置对于新增和修改操作，当传递过来的数值型字段为空串的情况下，是否填入"0"
	     * @param fillZero boolean
	     */
	    public  void setFillZero(boolean fillZero);


	    /**
	     * 是否缓存数据库表结构
	     * @param useCache boolean
	     */
	    public  void setUseCache(boolean useCache);

	    /**
	     * 设置数据库操作是否按事务方式进行。如果为false，则begin()、rollback()、commit()函数将不进行任何操作
	     * @param useTransaction boolean
	     */
	    public  void setUseTransaction(boolean useTransaction);
	    
	    
	    //生成数据库字段时，是否用中括号包围，如对于字段名是"key",则用中括号包围"[key]"
		public void setBracketFieldName(boolean wrapFieldName);
	    
	    /**
	     * 数据库更新、新增、删除等操作
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @return int 实际被更新的数量
	     */
	    public  int update(String sql);
	    
		/**
	     * 数据库更新、新增、删除等操作
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @return int 实际被更新的数量
	     */

	    public  int update(String string, Record commonRecord);
		/**
	     * 数据库更新、新增、删除等操作
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param rdParam Record 参数集合
	     * @param genKey 是否处理可能产生的序列值
	     * @return long 实际被更新的数量,如果要获取新生成的自动键值，则返回自动键值
	     */
	    public  long update(String string, Record commonRecord,boolean genKey);    
		/**
	     * 批量更新
	     * @param string sql SQL语句
	     * @param recordSet RecordSet 记录集
	     * @return int[]
	     */
	    public  int[] updateBatch(String sql, RecordSet recordSet);
		/**
	     * 修改
	     * @param tableName String 数据库表名
	     * @param rdParam Record 参数
	     * @param sqlWhere SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	     * @return int 操作结果数量
	     */
	    public  int updateData(String tableName, Record rdParam, String sqlWhere);
		/**
	     * 修改
	     * @param tableName String 数据库表名
	     * @param rdParam Record 参数
	     * @param sqlWhere SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	     * @param noUpdateField 不更新的字段列表。字段间有逗号分隔
	     * @return int 操作结果数量
	     */
	    public  int updateData(String tableName, Record rdParam, String sqlWhere, String noUpdateField);
		/**
	     * 批量更新
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param sqlWhere SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	     * @return int[] 结果数组
	     */
	    public  int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere);
		/**
	     * 批量更新
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param sqlWhere SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	     * @param sameSql 所有记录是否有一样多的字段数量；如果为true,则第一条记录生成的insert语句使用于其他记录。其他记录将不再单独产生SQL语句；同时
	     *                更新操作也按批量方式进行。如果为false,则每条记录就将产生SQL语句，更新操作将不做批量方式进行
	     * @return int[] 结果数组。如果sameSql为false,返回null值
	     */
	    public  int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere, boolean sameSql);
		/**
	     * 批量更新
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param sqlWhere SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	     * @param noUpdateField 不更新的字段列表。字段间有逗号分隔
	     * @return int[] 结果数组
	     */
	    public  int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere, String noUpdateField);
		/**
	     * 批量更新
	     * @param tableName String 数据库表名
	     * @param rs RecordSet 记录集
	     * @param sqlWhere SQL条件；可以为2中格式：字段列表或完整的sql语句条件（不包含"where"关键字）
	     * @param noUpdateField 不更新的字段列表。字段间有逗号分隔
	     * @param sameSql 所有记录是否有一样多的字段数量；如果为true,则第一条记录生成的insert语句使用于其他记录。其他记录将不再单独产生SQL语句；同时
	     *                更新操作也按批量方式进行。如果为false,则每条记录就将产生SQL语句，更新操作将不做批量方式进行
	     * @return int[] 结果数组。如果sameSql为false,返回null值
	     */
	    public  int[] updateDataBatch(String tableName, RecordSet rs, String sqlWhere, String noUpdateField, boolean sameSql);
	    
	    /**
	     * 
	     * 获取ORACLE或POSTGRESQL数据库通用序列的下一个值。或获取SQLSERVER和MYSQL数据库最近一次的自动增长值<br>
	     * 对于SQLSERVER数据库，未指明表名或与全局变量SEQ_NAME值相同时，采用scoPe_Identity()函数获取最近一次自动增长值。如果指明了表名(与全局变量SEQ_NAME不同时)，则获取该表的最近一次的自动增长值<br>
	     * 
	     * @param seqName 序列名或表名(SQLSERVER数据库时为表名)。对于SQL SERVER数据，此值为空或为表名。对于mysql数据库，忽略此值。如果此值为空，则将采用全局变量SEQ_NAME定义的值（sqlserver数据库不采用）。
	     * 通用序列由AbstractDao类的全局变量SEQ_NAME定义
	     * @return 序列下一个值或最近一次自动增长值，其他不支持的数据库，将抛出异常
	     */
	    public String genSeqNextVal(String seqName);

	    public String genSeqNextVal();

	    
	    /**
	     * 以pojo的属性值作为参数来源，执行sql语句
	     * @param sql 
	     * @param pojo Bean对象,sql里的参数值来源
	     * @return
	     */
	    public  int update(String sql, Object pojo);
		/**
	     * 以pojo的属性值作为参数来源，执行sql语句
	     * @param sql String SQL语句，支持用":fieldName"指定参数的格式
	     * @param Object pojo Bean对象,sql里的参数值来源
	     * @param genKey 是否处理可能产生的序列值
	     * @return long 实际被更新的数量,如果要获取新生成的自动键值，则返回自动键值
	     */
	    public  long update(String sql, Object pojo,boolean genKey);    
		/**
	     * 批量更新
	     * @param string sql SQL语句
	     * @param List<Object> bean列表
	     * @return int[]
	     */
//	    public  int[] updateBatch(String sql, List<Object> listPojo);
	    
	    /**
	     * 根据索引位置执行SQL语句
	     * @param sql 
	     * @param objs 参数对象列表
	     * @return
	     */
	    public int updateByIndex(String sql,Object... objs);
	    
	    
	    /**
	     * 以pojo的属性值作为参数来源，执行sql语句查询
	     * @param sql 
	     * @param pojo Bean对象,sql里的参数值来源
	     * @return
	     */
	    public  RecordSet query(String sql, Object pojo);
	    
	    /**
	     * 根据索引位置执行SQL语句查询
	     * @param sql
	     * @param objs 参数对象列表
	     * @return
	     */
		public String queryForStringByIndex(String sql, Object... objs);
		
		/**
		 * 以pojo的属性值作为参数来源，执行sql语句查询
		 * @param sql
		 * @param pojo
		 * @return
		 */
		public String queryForString(String sql, Object pojo);
		
		/**
	     * 根据索引位置执行SQL语句查询
	     * @param sql
	     * @param objs 参数对象列表
	     * @return
	     */
		public int queryForIntByIndex(String sql, Object... objs);
		
		/**
		 * 以pojo的属性值作为参数来源，执行sql语句查询
		 * @param sql
		 * @param pojo
		 * @return
		 */
		public int queryForInt(String sql, Object pojo);
		
		/**
	     * 根据索引位置执行SQL语句查询
	     * @param sql
	     * @param objs 参数对象列表
	     * @return
	     */
		public long queryForLongByIndex(String sql, Object... objs);
		
		/**
		 * 以pojo的属性值作为参数来源，执行sql语句查询
		 * @param sql
		 * @param pojo
		 * @return
		 */
		public long queryForLong(String sql, Object pojo);
		
		/**
	     * 根据索引位置执行SQL语句查询
	     * @param sql
	     * @param objs 参数对象列表
	     * @return
	     */
		public double queryForDoubleByIndex(String sql, Object... objs);
		
		/**
		 * 以pojo的属性值作为参数来源，执行sql语句查询
		 * @param sql
		 * @param pojo
		 * @return
		 */
		public double queryForDouble(String sql, Object pojo);
		
		/**
		 * 查询
		 * @param sql
		 * @param objs
		 * @return
		 */
		public RecordSet queryEx(String sql, Object... params);
		
		/**
		 * 查询
		 * @param sql
		 * @param objs
		 * @return
		 */
		public RecordSet queryByIndex(String sql, Object[] params);
		
		/**
		 * 根据索引位置执行预编译参数的设置,并查询
		 * @param sql
		 * @param pageNo 页号,从1开始
		 * @param pageSize 页大小
		 * @param objs 参数
		 * @return
		 */
		public RecordSet<Record> queryByIndex(String sql,int pageNo,int pageSize, Object[] objs);
		/**
		 * 根据索引位置执行预编译参数的设置,并查询
		 * @param sql
		 * @param pageNo 页号,从1开始
		 * @param pageSize 页大小
		 * @param fieldNameOfRecord 其值作为记录名字的字段
		 * @param objs 参数
		 * @return
		 */
		public RecordSet<Record> queryByIndex(String sql,int pageNo,int pageSize,String fieldNameOfRecord, Object[] objs);
		/**
		 * 根据索引位置执行预编译参数的设置,并查询
		 * @param sql
		 * @param pageNo 页号,从1开始
		 * @param pageSize 页大小
		 * @param fieldNameOfRecord 其值作为记录名字的字段
		 * @param statCount 是否统计记录数量
		 * @param objs 参数
		 * @return
		 */
		public RecordSet<Record> queryByIndex(String sql,int pageNo,int pageSize,String fieldNameOfRecord,boolean statCount, Object[] objs);
	    
		
		/** 
		 * 取一条记录，如果没有返回null
		 * @param sql
		 * @param rdParam
		 * @return 取一条记录，如果没有返回null
		 */
		public Record get(String sql);
		/** 
		 * 取一条记录，如果没有返回null
		 * @param sql
		 * @param rdParam
		 * @return 取一条记录，如果没有返回null
		 */
		public Record get(String sql, Record rdParam) ;
		
		/** 
		 * 取一条记录，如果没有返回null
		 * @param sql
		 * @param rdParam
		 * @return 取一条记录，如果没有返回null
		 */
		public <T extends RecordTable> T get(String sql, T rdParam);
		
		/**
		 * 新增或修改
		 * @param t
		 * @return
		 */
		public <T extends RecordTable> int saveOrUpdate(T t);
		
		/**
		 * 修改
		 * @param t
		 * @return
		 */
		public <T extends RecordTable> int update(T t);
		public <T extends RecordTable> int update(Class<T> c,Record t);
		
		/**
		 * 新增
		 * @param t
		 * @return
		 */
		public <T extends RecordTable> int save(T t);
		public <T extends RecordTable> int save(Class<T> c,Record t);

		
		public <T extends RecordTable> int delete(T t);
		public <T extends RecordTable> int delete(Class<T> c,Record t);
		
		public <T extends RecordTable> T get(Class<T> c,Serializable id);
		
		
		public <T extends RecordTable> RecordSet<T> getAll(Class<T> c) ;
}