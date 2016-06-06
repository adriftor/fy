package com.xh.util.db;


/**
 * 包装数据库操作，如增、删、改、查
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: </p>
 * @author adriftor
 * @version 1.0
 */
import java.sql.Connection;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import com.xh.util.AppException;

@SuppressWarnings("unchecked")
public class SpringDataSourceDao extends JdbcDao {
	private JdbcTemplate jdbcTemplate;
	DataSource dataSource;

	public SpringDataSourceDao(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.setDatabaseType(DaoConfig.DATABASE_TYPE_MYSQL);
		this.setUserControlConnection(true);
	}
	
	
	public final DataSource getDataSource() {
		return this.dataSource;
	}


	public final JdbcTemplate getJdbcTemplate() {
	  return this.jdbcTemplate;
	}

	public final Connection getConnection() throws CannotGetJdbcConnectionException {
		return DataSourceUtils.getConnection(getDataSource());
	}

}
