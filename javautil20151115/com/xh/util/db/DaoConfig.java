package com.xh.util.db;

import java.sql.Connection;

public class DaoConfig {
	
    public static final int DATABASE_TYPE_ORACLE = 1;
    public static final int DATABASE_TYPE_SQLSERVER = 2;
    public static final int DATABASE_TYPE_MYSQL = 3;
    public static final int DATABASE_TYPE_SYBASE = 4;
    public static final int DATABASE_TYPE_DB2 = 5;
    public static final int DATABASE_TYPE_POSTGRE = 10;
    public static final int DATABASE_TYPE_OTHER = 100;
    
    public DaoConfig(int databaseType,boolean userControlConnection) {
    	this.databaseType = databaseType;
    	this.userControlConnection = userControlConnection;
    }
    /**
     * 通用序列名
     * <br>默认为HIBERNATE_SEQUENCE
     */
    public static String sequenceName = "HIBERNATE_SEQUENCE";
    


    /**
     * 数据库连接控制方式,涉及数据库连接事务\连接关闭操作.true表示由开发者自己控制. 如果为false,则release、begin、commit、rollback函数将无效
     * <br>默认为true
     */
    protected boolean userControlConnection = true;
    /**
     * 是否使用JDBC事务，此属性影响begin()/commit()/rollback。如果此值为false,则上述函数不执行任何动作
     * <br>默认为true
     */
     protected boolean useTransaction = true;
     
    
	/**
	 * 是否允许提交。注意，如果为false，则commit()函数不会执行提交
	 * <br>默认为true
	 */
	protected boolean canCommit = true;
	/**
     * 数据库连接在执行完成后是否关闭，缺省为关闭
     * <br>默认为true
     */
    protected boolean closeConnection = true;
	
	/**
	 * 代表WEB前端需要的编码
	 * <br>默认为null,表示不用转换
	 */
    protected String charsetClient = null;
    
    /**
     * 代表数据库的编码方式
     * <br>默认为null,表示不用转换
     */
    protected String charsetDb = null;
    
    

    
    
    /**
     * 数据库类型,默认为SQL SERVER
     */
    protected int databaseType = DATABASE_TYPE_MYSQL;

    /**
     * 当使用insertData()/updateData类函数时，当前台传递过来的数值型字段为空串的情况下，是否填入"0"
     * <br>默认为true
     */
    protected boolean fillZero = true;
    
    /**
     * 关闭连接计数器,如果小于等于0,表示要关闭连接;如果大于0,表示不关闭连接;
     */
    protected int countCloseConnection = 0;
    
    

    /**
     * 是否起用表结构缓冲，缺省是起用
     * <br>默认为true
     */
    protected boolean useCache = true;
    
   
    /**
     * 生成数据库字段时，是否用中括号包围，如对于字段名是"key",则用中括号包围"[key]"
     * <br>默认为false
     */
    protected boolean bracketFieldName = false;

	public boolean isCanCommit() {
		return canCommit;
	}

	public void setCanCommit(boolean canCommit) {
		this.canCommit = canCommit;
	}

	public String getCharsetClient() {
		return charsetClient;
	}

	public void setCharsetClient(String charsetClient) {
		this.charsetClient = charsetClient;
	}

	public String getCharsetDb() {
		return charsetDb;
	}

	public void setCharsetDb(String charsetDb) {
		this.charsetDb = charsetDb;
	}

	public boolean isCloseConnection() {
		return closeConnection;
	}

	public void setCloseConnection(boolean closeConnection) {
		int countCloseConnection = this.countCloseConnection;
    	if (closeConnection) {
    		if (countCloseConnection>0) {
    			countCloseConnection--;
    			this.setCountCloseConnection(countCloseConnection);
    		}    		
    	}    
    	else {
			countCloseConnection++;
			this.setCountCloseConnection(countCloseConnection);
		}
	}

	public boolean isUserControlConnection() {
		return userControlConnection;
	}

	public void setUserControlConnection(boolean userControlConnection) {
		this.userControlConnection = userControlConnection;
	}

	public int getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(int databaseType) {
		this.databaseType = databaseType;
	}

	public boolean isFillZero() {
		return fillZero;
	}

	public void setFillZero(boolean fillZero) {
		this.fillZero = fillZero;
	}

	public int getCountCloseConnection() {
		return countCloseConnection;
	}

	public void setCountCloseConnection(int countCloseConnection) {
		this.countCloseConnection = countCloseConnection;
	}




	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isUseTransaction() {
		return useTransaction;
	}

	public void setUseTransaction(boolean useTransaction) {
		this.useTransaction = useTransaction;
	}

	public boolean isBracketFieldName() {
		return bracketFieldName;
	}

	public void setBracketFieldName(boolean bracketFieldName) {
		this.bracketFieldName = bracketFieldName;
	}

	public static String getSequenceName() {
		return sequenceName;
	}

	public static void setSequenceName(String sequenceName) {
		DaoConfig.sequenceName = sequenceName;
	}

    
    
    
    
}