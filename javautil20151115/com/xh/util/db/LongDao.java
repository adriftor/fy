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
import java.sql.DriverManager;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.xh.util.AppException;
import com.xh.util.Record;

@SuppressWarnings("unchecked")
public class LongDao extends JdbcDao {
	static ThreadLocal<Connection> tlCon = new ThreadLocal<Connection>();

	public LongDao() {
		this.setDatabaseType(DaoConfig.DATABASE_TYPE_MYSQL);
		this.setUserControlConnection(true);
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return Connection 数据库连接
	 */
	public Connection getConnection() {
	

		Connection innerCon = tlCon.get();
		try {
			
			if (innerCon != null &&( ! innerCon.isClosed())) {
				return innerCon;
			}
			// GetConnection getconn = GetConnection.getInstance();
			// con = getconn.getConn1();
			/*
			 * InitialContext ic = new InitialContext(); DataSource ds =
			 * (DataSource) ic.lookup("sqlserverds");//门户、论坛
			 * 
			 * con = ds.getConnection();
			 */
			/*
			 * //String url = "jdbc:mysql://61.145.116.208:3306/mms"; String url
			 * = "jdbc:mysql://fy:3306/ss?characterEncoding=gbk&useUnicode=true"
			 * ;
			 * //"jdbc:mysql://fy:3306/ss?characterEncoding=gbk&useUnicode=true"
			 * ;
			 * 
			 * String url = "jdbc:mysql://localhost:5505/blog";
			 * 
			 * String strDriver = "com.mysql.jdbc.Driver";
			 * Class.forName(strDriver); con = DriverManager.getConnection(url,
			 * "fy", "dddddd");
			 */
//			String url = "jdbc:mysql://192.168.101.188:3306/sic_test?characterEncoding=utf-8&useUnicode=true";
//
//			String strDriver = "com.mysql.jdbc.Driver";
//			Class.forName(strDriver);
//			innerCon = DriverManager.getConnection(url, "sickf", "sickf68736");
//			String url = "jdbc:postgresql://192.168.4.7:5432/etrans_huaduED_dev";
//
//			String strDriver = "org.postgresql.Driver";
//			Class.forName(strDriver);
//			innerCon = DriverManager.getConnection(url, "postgres", "helloworld");
		
/*			 String url = "jdbc:oracle:thin:@192.168.4.98:1521:anjian"; 
			 String strDriver = "oracle.jdbc.driver.OracleDriver";
			 Class.forName(strDriver);
			 innerCon = DriverManager.getConnection(url,"anjian","anjian");*/
			
			String url = "jdbc:mysql://192.168.1.142:3306/platform?characterEncoding=utf-8&useUnicode=true";
			String strDriver = "com.mysql.jdbc.Driver";
			Class.forName(strDriver);
			innerCon = DriverManager.getConnection(url, "root", "xinhe123");
			tlCon.set(innerCon); 
			
//			String strDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
//			String url="jdbc:sqlserver://192.168.1.180:1433;DatabaseName=ArchiveSystem";
//			Class.forName(strDriver);
//			innerCon = DriverManager.getConnection(url, "sa", "1");
//			tlCon.set(innerCon); 
			/* 
			 * this.tlCon.set(innerCon);
			 * 
			 * String url =
			 * "jdbc:sybase:Tds:172.30.8.133:2640/shenzhen_write?useUnicode=true&amp;characterEncoding=UTF-8"
			 * ; String strDriver = "com.sybase.jdbc3.jdbc.SybDriver";
			 * Class.forName(strDriver); innerCon =
			 * DriverManager.getConnection(url, "DBA", "SZ10sj!@3");
			 * 
			 * this.tlCon.set(innerCon);
			 */

		} catch (Exception se) {
			throw new AppException("数据库连接错误", se);
		}

		/*
		 * else { throw new AppException("没有设置可用连接或原连接已经关闭！"); }
		 */

		return innerCon;

	}
	public static void main(String[] argv) {
		LongDao dao = new LongDao();
		LongSqlServerDao sqlDao = new LongSqlServerDao();
		try {
			String sql = "SELECT a.*,a.id as 'name.id2.id2' from sys_user a where userid<20";
			Record rd = new Record();
			rd.put("nam__e","黄忠家长");
			rd.put("offic_e_id",2);
			System.out.println( new User().getTableName());
//			dao.get(new User(rd),false).d();
//			dao.getAll(new User(rd)).d();
			
			
//			Connection innerCon = dao.getConnection();
//			java.sql.DatabaseMetaData dm = innerCon.getMetaData();
//			ResultSet resultSet = dm.getTables(null, null, "sys_user%", new String[]{"TABLE"});
//			RecordSet rsSet = new RecordSet(resultSet);
//
//			for (int i = 0;i<rsSet.size();i++) {
//				
//				Record rd = rsSet.r(i);
//				String tableName = rd.s("table_name").toLowerCase();
//				if (tableName.equalsIgnoreCase("sys_role") || tableName.equalsIgnoreCase("sys_user_copy")) {
//					continue;
//				}
//				for (int m=1;m<30;m++) {
//					RecordSet rs = sqlDao.query("select a.*,1 as company_id,2 as office_id,userName as login_name,realName as name from "+"sys_account"+" a",new Record(),m,1000);
//					int size =rs.size();
//					if (size == 0) {
//						break;
//					}
//					for (int j = 0;j<size;j++) {
//						rs.getRecord(j).put("id",rs.getRecord(j).getString("userId"));
//					}
//					if (m == 1) {
//						dao.update("delete from "+tableName+" where userId>13");
//					}
//					
//					dao.insertDataBatch(tableName, rs,true);
//				}
//				
//			}
				

			
//			dao.commit();
		}
		catch(Exception ex) {
			dao.rollback();
			ex.printStackTrace();
		}
		finally {
			dao.releaseCon();
			sqlDao.releaseCon();
		}
	}
}
