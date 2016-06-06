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

import com.xh.util.AppException;
import com.xh.util.Record;
import com.xh.util.RecordSet;

@SuppressWarnings("unchecked")
public class LongSqlServerDao extends LongDao {
	static ThreadLocal<Connection> tlCon = new ThreadLocal<Connection>();

	public LongSqlServerDao() {
		this.setDatabaseType(DaoConfig.DATABASE_TYPE_SQLSERVER);
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
			
//			String url = "jdbc:mysql://192.168.1.142:3306/platform?characterEncoding=utf-8&useUnicode=true";
//			String strDriver = "com.mysql.jdbc.Driver";
//			Class.forName(strDriver);
//			innerCon = DriverManager.getConnection(url, "root", "xinhe123");
//			tlCon.set(innerCon); 
			
			String strDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
			String url="jdbc:sqlserver://192.168.1.180:1433;DatabaseName=ArchiveSystem";
			Class.forName(strDriver);
			innerCon = DriverManager.getConnection(url, "sa", "1");
			tlCon.set(innerCon); 
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
		LongSqlServerDao dao = new LongSqlServerDao();
		try {
			dao.begin();
			RecordSet rs = dao.query("select * from sys_office2");
			dao.insertDataBatch("sys_office", rs,true);
//			RecordSet<Record> rs = dao.query("select * from p_memberuser",new Record(),1,1);
//			System.out.println(rs.d());
//			dao.begin();
//			String sql = "update test_inc set name='"+Thread.currentThread().hashCode()+"' where datef=?";
//			dao.updateByIndex(sql, CommonUtil.strToDate("2014-12-24"));
			
			//RdKqCountLeave rd = new RdKqCountLeave( dao.query("select * from pt_staff a left join kq_count_leave b on a.staff_id=b.staff_id where a.staff_id='0c7d9700-6d68-4dc7-93d9-1eb7a5fca65d'").getRecord(0));
			//System.out.println(rd.getWorkDay().intValue());

//			System.out.println("\n a   b c\n d\n ".trim().replaceAll("\\s+", ","));
//			dao.queryByIndex("select * from xc_secure_meeting where meeting_id <>?",1,2,"","status2").d();
//			System.out.println(dao.queryForStringByIndex("select * from kq_kqj where sn=? and status=1", "0025142701321"));
			
			/**
			String sql = "select  * from kq_data where staff_id='29605e94-4acc-4150-91ba-a5f64bb575b7' and kq_bc_id='ba5e8a5a-c057-4fa9-b036-7a595a560ebd' and bc_date='2014-12-24' order by time_id";
			RecordSet rs = dao.query(sql);
			for (int i = 0;i<rs.size();i++) {
				rs.getRecord(i).set("id",UUID.randomUUID().toString());
			}
			rs.setValue("staff_name", "xxx");
			
			rs.setValue("pb_id", "39a9904c-7311-47c4-b6ff-b5198847989c");
			rs.setValue("staff_id", "73ca00aa-a984-4a00-ac84-4310c16838f5");
			rs.setValue("bc_id", "adc812f4-a57e-45e7-a621-2671c3caf601");
			rs.setValue("team_id", "cd38df25-5cc9-4a91-9eff-b43063c02070");
			rs.setValue("org_id", "00770f76-c0e4-42ff-9249-056846fd1ae9");
			rs.setValue("bc_type", "0");
			rs.setValue("kq_bc_id", "f2fa79c2-4cde-4e8d-b046-ca4731d1cc42");
			rs.setValue("time_id", "f344e612-7f44-406d-aa86-89a884aa034c");
			
			dao.update("delete from kq_data where staff_id='73ca00aa-a984-4a00-ac84-4310c16838f5'  and bc_date='2014-12-24'");
			dao.insertDataBatch("kq_data", rs);
			*/
			dao.commit();
		}
		catch(Exception ex) {
			dao.rollback();
			ex.printStackTrace();
		}
		finally {
			dao.releaseCon();
		}
	}
}
