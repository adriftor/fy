package com.xh.util.db;


/**
 * 包装数据库操作，如增、删、改、查
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: </p>
 * @author adriftor
 * @version 1.0
 */
import java.io.Serializable;
import java.sql.Connection;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import com.xh.util.CommonUtil;
import com.xh.util.RecordSet;
import com.xh.util.RecordTable;

@SuppressWarnings("unchecked")
public class SpringDao<T extends RecordTable> extends JdbcDao {
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	public SpringDao() {
		this.setDatabaseType(DaoConfig.DATABASE_TYPE_MYSQL);
		this.setUserControlConnection(true);
	}
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		if (this.jdbcTemplate == null || dataSource != this.jdbcTemplate.getDataSource()) {
			this.jdbcTemplate = createJdbcTemplate(dataSource);
			initTemplateConfig();
		}
	}

	protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	public final DataSource getDataSource() {
		return (this.jdbcTemplate != null ? this.jdbcTemplate.getDataSource() : null);
	}
	
	
	public final void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		initTemplateConfig();
	}

	public final JdbcTemplate getJdbcTemplate() {
	  return this.jdbcTemplate;
	}
	public final NamedParameterJdbcTemplate getNamedParamterJdbcTemplate() {
		if (this.namedParameterJdbcTemplate == null) {
			this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
		}
		return this.namedParameterJdbcTemplate;
	}
	protected void initTemplateConfig() {
	}

	protected void checkDaoConfig() {
		if (this.jdbcTemplate == null) {
			throw new IllegalArgumentException("'dataSource' or 'jdbcTemplate' is required");
		}
	}

	protected final SQLExceptionTranslator getExceptionTranslator() {
		return getJdbcTemplate().getExceptionTranslator();
	}

	public final Connection getConnection() throws CannotGetJdbcConnectionException {
		return DataSourceUtils.getConnection(getDataSource());
	}

	public final void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, getDataSource());
	}
	
	public T get(Serializable id) {
		return (T) super.get(CommonUtil.getGenericClass(getClass()), id);
	}
	public RecordSet<T> getAll() {
		return super.getAll(CommonUtil.getGenericClass(getClass()));
	}
}
