package com.thinkgem.jeesite.modules.sys.entity;

import com.xh.util.*;
import java.util.Date;
import java.util.Map;
import java.sql.Types;
import java.math.BigDecimal;

/**
 * 表名：SYS_USER<br>
 */
public class SysUser extends RecordTable {
	public static final String TABLE_NAME = "SYS_USER";
	public static final String PRIMARY_KEY = "UserID";

	public SysUser() {
	}

	public SysUser(Map map) {
		super(map);
	}

	static {
		RecordTable.registerTable(SysUser.class, new RecordTable.TableInfo("SYS_USER", "UserID"));
	}

	/**
	 * 列名：USERID<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：业务ID,用于关联学生、教师等<br>
	 */
	public void setUserID(Integer userID) {
		this.put("userid", Types.INTEGER, userID);
	}

	/**
	 * 列名：USERID<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：业务ID,用于关联学生、教师等<br>
	 */
	public Integer getUserID() {
		return this.gIntValue("userid");
	}

	/**
	 * 列名：ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：编号,用于熊管理<br>
	 */
	public void setId(String id) {
		this.put("id", Types.VARCHAR, id);
	}

	/**
	 * 列名：ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：编号,用于熊管理<br>
	 */
	public String getId() {
		return this.getString("id");
	}

	/**
	 * 列名：COMPANY_ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：归属公司<br>
	 */
	public void setCompanyId(String companyId) {
		this.put("company_id", Types.VARCHAR, companyId);
	}

	/**
	 * 列名：COMPANY_ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：归属公司<br>
	 */
	public String getCompanyId() {
		return this.getString("company_id");
	}

	/**
	 * 列名：OFFICE_ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：归属部门<br>
	 */
	public void setOfficeId(String officeId) {
		this.put("office_id", Types.VARCHAR, officeId);
	}

	/**
	 * 列名：OFFICE_ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：归属部门<br>
	 */
	public String getOfficeId() {
		return this.getString("office_id");
	}

	/**
	 * 列名：LOGIN_NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：登录名<br>
	 */
	public void setLoginName(String loginName) {
		this.put("login_name", Types.VARCHAR, loginName);
	}

	/**
	 * 列名：LOGIN_NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：登录名<br>
	 */
	public String getLoginName() {
		return this.getString("login_name");
	}

	/**
	 * 列名：PASSWORD<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：密码<br>
	 */
	public void setPassword(String password) {
		this.put("password", Types.VARCHAR, password);
	}

	/**
	 * 列名：PASSWORD<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：密码<br>
	 */
	public String getPassword() {
		return this.getString("password");
	}




	/**
	 * 列名：NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：姓名<br>
	 */
	public void setName(String name) {
		this.put("name", Types.VARCHAR, name);
	}

	/**
	 * 列名：NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：姓名<br>
	 */
	public String getName() {
		return this.getString("name");
	}

	/**
	 * 列名：EMAIL<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：邮箱<br>
	 */
	public void setEmail(String email) {
		this.put("email", Types.VARCHAR, email);
	}

	/**
	 * 列名：EMAIL<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：邮箱<br>
	 */
	public String getEmail() {
		return this.getString("email");
	}

	/**
	 * 列名：PHONE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：电话<br>
	 */
	public void setPhone(String phone) {
		this.put("phone", Types.VARCHAR, phone);
	}

	/**
	 * 列名：PHONE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：电话<br>
	 */
	public String getPhone() {
		return this.getString("phone");
	}

	/**
	 * 列名：MOBILE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：手机<br>
	 */
	public void setMobile(String mobile) {
		this.put("mobile", Types.VARCHAR, mobile);
	}

	/**
	 * 列名：MOBILE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：手机<br>
	 */
	public String getMobile() {
		return this.getString("mobile");
	}

	/**
	 * 列名：USERTYPE<br>
	 * 类型：INT<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：用户类型，参考常量定义文档，只出现：1=公共用户，4=教职工，5=学生，6=家长
	 * (1=公共用户,2=区管理员,3=校管理员,4=教职工,5=学生,6=家长,7=校长,8=级长,9=班主任,10=科长,11=备长)<br>
	 */
	public void setUserType(Integer userType) {
		this.put("usertype", Types.INTEGER, userType);
	}

	/**
	 * 列名：USERTYPE<br>
	 * 类型：INT<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：用户类型，参考常量定义文档，只出现：1=公共用户，4=教职工，5=学生，6=家长
	 * (1=公共用户,2=区管理员,3=校管理员,4=教职工,5=学生,6=家长,7=校长,8=级长,9=班主任,10=科长,11=备长)<br>
	 */
	public Integer getUserType() {
		return this.gIntValue("usertype");
	}

	/**
	 * 列名：PHOTO<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：2000<br>
	 * 备注：用户头像<br>
	 */
	public void setPhoto(String photo) {
		this.put("photo", Types.VARCHAR, photo);
	}

	/**
	 * 列名：PHOTO<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：2000<br>
	 * 备注：用户头像<br>
	 */
	public String getPhoto() {
		return this.getString("photo");
	}

	/**
	 * 列名：LOGIN_IP<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：100<br>
	 * 备注：最后登陆IP<br>
	 */
	public void setLoginIp(String loginIp) {
		this.put("login_ip", Types.VARCHAR, loginIp);
	}

	/**
	 * 列名：LOGIN_IP<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：100<br>
	 * 备注：最后登陆IP<br>
	 */
	public String getLoginIp() {
		return this.getString("login_ip");
	}

	/**
	 * 列名：LOGIN_DATE<br>
	 * 类型：DATETIME<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：最后登陆时间<br>
	 */
	public void setLoginDate(Date loginDate) {
		this.put("login_date", Types.TIMESTAMP, loginDate);
	}

	/**
	 * 列名：LOGIN_DATE<br>
	 * 类型：DATETIME<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：最后登陆时间<br>
	 */
	public Date getLoginDate() {
		return this.gDate("login_date");
	}

	/**
	 * 列名：LOGIN_FLAG<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：64<br>
	 * 备注：是否可登录<br>
	 */
	public void setLoginFlag(String loginFlag) {
		this.put("login_flag", Types.VARCHAR, loginFlag);
	}

	/**
	 * 列名：LOGIN_FLAG<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：64<br>
	 * 备注：是否可登录<br>
	 */
	public String getLoginFlag() {
		return this.getString("login_flag");
	}

	/**
	 * 列名：CREATE_BY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：创建者<br>
	 */
	public void setCreateBy(String createBy) {
		this.put("create_by", Types.VARCHAR, createBy);
	}

	/**
	 * 列名：CREATE_BY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：创建者<br>
	 */
	public String getCreateBy() {
		return this.getString("create_by");
	}

	/**
	 * 列名：CREATE_DATE<br>
	 * 类型：TIMESTAMP<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：创建时间<br>
	 */
	public void setCreateDate(Date createDate) {
		this.put("create_date", Types.TIMESTAMP, createDate);
	}

	/**
	 * 列名：CREATE_DATE<br>
	 * 类型：TIMESTAMP<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：创建时间<br>
	 */
	public Date getCreateDate() {
		return this.gDate("create_date");
	}

	/**
	 * 列名：UPDATE_BY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：更新者<br>
	 */
	public void setUpdateBy(String updateBy) {
		this.put("update_by", Types.VARCHAR, updateBy);
	}

	/**
	 * 列名：UPDATE_BY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：更新者<br>
	 */
	public String getUpdateBy() {
		return this.getString("update_by");
	}

	/**
	 * 列名：UPDATE_DATE<br>
	 * 类型：TIMESTAMP<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：更新时间<br>
	 */
	public void setUpdateDate(Date updateDate) {
		this.put("update_date", Types.TIMESTAMP, updateDate);
	}

	/**
	 * 列名：UPDATE_DATE<br>
	 * 类型：TIMESTAMP<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：更新时间<br>
	 */
	public Date getUpdateDate() {
		return this.gDate("update_date");
	}

	/**
	 * 列名：REMARKS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：255<br>
	 * 备注：备注信息<br>
	 */
	public void setRemarks(String remarks) {
		this.put("remarks", Types.VARCHAR, remarks);
	}

	/**
	 * 列名：REMARKS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：255<br>
	 * 备注：备注信息<br>
	 */
	public String getRemarks() {
		return this.getString("remarks");
	}

	/**
	 * 列名：DEL_FLAG<br>
	 * 类型：CHAR<br>
	 * 允许null：NO<br>
	 * 长度：1<br>
	 * 备注：删除标记<br>
	 */
	public void setDelFlag(String delFlag) {
		this.put("del_flag", Types.CHAR, delFlag);
	}

	/**
	 * 列名：DEL_FLAG<br>
	 * 类型：CHAR<br>
	 * 允许null：NO<br>
	 * 长度：1<br>
	 * 备注：删除标记<br>
	 */
	public String getDelFlag() {
		return this.getString("del_flag");
	}

	/**
	 * 列名：REALNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：真实姓名<br>
	 */
	public void setRealName(String realName) {
		this.put("realname", Types.VARCHAR, realName);
	}

	/**
	 * 列名：REALNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：真实姓名<br>
	 */
	public String getRealName() {
		return this.getString("realname");
	}

	/**
	 * 列名：SEX<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：性别，参考常量定义文档 (1=男,2=女)<br>
	 */
	public void setSex(Integer sex) {
		this.put("sex", Types.INTEGER, sex);
	}

	/**
	 * 列名：SEX<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：性别，参考常量定义文档 (1=男,2=女)<br>
	 */
	public Integer getSex() {
		return this.gIntValue("sex");
	}

	/**
	 * 列名：SUMMARY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：250<br>
	 * 备注：摘要，内容：xxx学校的教职工/学生/学生家长<br>
	 */
	public void setSummary(String summary) {
		this.put("summary", Types.VARCHAR, summary);
	}

	/**
	 * 列名：SUMMARY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：250<br>
	 * 备注：摘要，内容：xxx学校的教职工/学生/学生家长<br>
	 */
	public String getSummary() {
		return this.getString("summary");
	}

	/**
	 * 列名：IDCARD<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：身份证<br>
	 */
	public void setIDCard(String iDCard) {
		this.put("idcard", Types.VARCHAR, iDCard);
	}

	/**
	 * 列名：IDCARD<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：身份证<br>
	 */
	public String getIDCard() {
		return this.getString("idcard");
	}

	/**
	 * 列名：BIRTHDAY<br>
	 * 类型：DATETIME<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：出生年月日，格式：CCYYMMDD，如19980401。<br>
	 */
	public void setBirthday(Date birthday) {
		this.put("birthday", Types.TIMESTAMP, birthday);
	}

	/**
	 * 列名：BIRTHDAY<br>
	 * 类型：DATETIME<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：出生年月日，格式：CCYYMMDD，如19980401。<br>
	 */
	public Date getBirthday() {
		return this.gDate("birthday");
	}

	/**
	 * 列名：ADDRESS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：250<br>
	 * 备注：联系地址<br>
	 */
	public void setAddress(String address) {
		this.put("address", Types.VARCHAR, address);
	}

	/**
	 * 列名：ADDRESS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：250<br>
	 * 备注：联系地址<br>
	 */
	public String getAddress() {
		return this.getString("address");
	}

	/**
	 * 列名：NATION<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：民族，参见国标GB/T 3304-1991《中国各民族名称的罗马字母拼写法和代码》，另加选项：其他<br>
	 */
	public void setNation(String nation) {
		this.put("nation", Types.VARCHAR, nation);
	}

	/**
	 * 列名：NATION<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：民族，参见国标GB/T 3304-1991《中国各民族名称的罗马字母拼写法和代码》，另加选项：其他<br>
	 */
	public String getNation() {
		return this.getString("nation");
	}

	/**
	 * 列名：STATUS<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：<br>
	 */
	public void setStatus(Integer status) {
		this.put("status", Types.INTEGER, status);
	}

	/**
	 * 列名：STATUS<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：<br>
	 */
	public Integer getStatus() {
		return this.gIntValue("status");
	}

	/**
	 * 列名：PNGPHOTO<br>
	 * 类型：BLOB<br>
	 * 允许null：YES<br>
	 * 长度：65535<br>
	 * 备注：<br>
	 */
	public void setPngPhoto(String pngPhoto) {
		this.put("pngphoto", Types.LONGVARBINARY, pngPhoto);
	}

	/**
	 * 列名：PNGPHOTO<br>
	 * 类型：BLOB<br>
	 * 允许null：YES<br>
	 * 长度：65535<br>
	 * 备注：<br>
	 */
	public String getPngPhoto() {
		return this.getString("pngphoto");
	}

	/**
	 * 列名：FIRSTNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：姓<br>
	 */
	public void setFirstName(String firstName) {
		this.put("firstname", Types.VARCHAR, firstName);
	}

	/**
	 * 列名：FIRSTNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：姓<br>
	 */
	public String getFirstName() {
		return this.getString("firstname");
	}

	/**
	 * 列名：LASTNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：名<br>
	 */
	public void setLastName(String lastName) {
		this.put("lastname", Types.VARCHAR, lastName);
	}

	/**
	 * 列名：LASTNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：名<br>
	 */
	public String getLastName() {
		return this.getString("lastname");
	}

	/**
	 * 列名：PHONEAREANUM<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：20<br>
	 * 备注：电话分区号<br>
	 */
	public void setPhoneAreaNum(String phoneAreaNum) {
		this.put("phoneareanum", Types.VARCHAR, phoneAreaNum);
	}

	/**
	 * 列名：PHONEAREANUM<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：20<br>
	 * 备注：电话分区号<br>
	 */
	public String getPhoneAreaNum() {
		return this.getString("phoneareanum");
	}

	/**
	 * 列名：PHONESUBNUM<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：20<br>
	 * 备注：电话分机号<br>
	 */
	public void setPhoneSubNum(String phoneSubNum) {
		this.put("phonesubnum", Types.VARCHAR, phoneSubNum);
	}

	/**
	 * 列名：PHONESUBNUM<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：20<br>
	 * 备注：电话分机号<br>
	 */
	public String getPhoneSubNum() {
		return this.getString("phonesubnum");
	}

	/**
	 * 列名：COUNTRY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：国家<br>
	 */
	public void setCountry(String country) {
		this.put("country", Types.VARCHAR, country);
	}

	/**
	 * 列名：COUNTRY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：国家<br>
	 */
	public String getCountry() {
		return this.getString("country");
	}

	/**
	 * 列名：PROVINCE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：省<br>
	 */
	public void setProvince(String province) {
		this.put("province", Types.VARCHAR, province);
	}

	/**
	 * 列名：PROVINCE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：省<br>
	 */
	public String getProvince() {
		return this.getString("province");
	}

	/**
	 * 列名：CITY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：市<br>
	 */
	public void setCity(String city) {
		this.put("city", Types.VARCHAR, city);
	}

	/**
	 * 列名：CITY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：市<br>
	 */
	public String getCity() {
		return this.getString("city");
	}

	/**
	 * 列名：COUNTY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：区县<br>
	 */
	public void setCounty(String county) {
		this.put("county", Types.VARCHAR, county);
	}

	/**
	 * 列名：COUNTY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：区县<br>
	 */
	public String getCounty() {
		return this.getString("county");
	}

	/**
	 * 列名：PROVINCENAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：省<br>
	 */
	public void setProvinceName(String provinceName) {
		this.put("provincename", Types.VARCHAR, provinceName);
	}

	/**
	 * 列名：PROVINCENAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：省<br>
	 */
	public String getProvinceName() {
		return this.getString("provincename");
	}

	/**
	 * 列名：CITYNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：市<br>
	 */
	public void setCityName(String cityName) {
		this.put("cityname", Types.VARCHAR, cityName);
	}

	/**
	 * 列名：CITYNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：市<br>
	 */
	public String getCityName() {
		return this.getString("cityname");
	}

	/**
	 * 列名：COUNTYNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：区县<br>
	 */
	public void setCountyName(String countyName) {
		this.put("countyname", Types.VARCHAR, countyName);
	}

	/**
	 * 列名：COUNTYNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：区县<br>
	 */
	public String getCountyName() {
		return this.getString("countyname");
	}

	/**
	 * 列名：PINYIN<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：姓名拼音<br>
	 */
	public void setPinYin(String pinYin) {
		this.put("pinyin", Types.VARCHAR, pinYin);
	}

	/**
	 * 列名：PINYIN<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：姓名拼音<br>
	 */
	public String getPinYin() {
		return this.getString("pinyin");
	}
	/**
	 * 列名：USERID<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：业务ID,用于关联学生、教师等<br>
	 */
	// private Integer userID;
	/**
	 * 列名：ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：编号,用于熊管理<br>
	 */
	// private String id;
	/**
	 * 列名：COMPANY_ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：归属公司<br>
	 */
	// private String companyId;
	/**
	 * 列名：OFFICE_ID<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：归属部门<br>
	 */
	// private String officeId;
	/**
	 * 列名：LOGIN_NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：登录名<br>
	 */
	// private String loginName;
	/**
	 * 列名：PASSWORD<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：密码<br>
	 */
	// private String password;
	/**
	 * 列名：NO<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：100<br>
	 * 备注：工号<br>
	 */
	// private String no;
	/**
	 * 列名：NAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：100<br>
	 * 备注：姓名<br>
	 */
	// private String name;
	/**
	 * 列名：EMAIL<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：邮箱<br>
	 */
	// private String email;
	/**
	 * 列名：PHONE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：电话<br>
	 */
	// private String phone;
	/**
	 * 列名：MOBILE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：200<br>
	 * 备注：手机<br>
	 */
	// private String mobile;
	/**
	 * 列名：USERTYPE<br>
	 * 类型：INT<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：用户类型，参考常量定义文档，只出现：1=公共用户，4=教职工，5=学生，6=家长
	 * (1=公共用户,2=区管理员,3=校管理员,4=教职工,5=学生,6=家长,7=校长,8=级长,9=班主任,10=科长,11=备长)<br>
	 */
	// private Integer userType;
	/**
	 * 列名：PHOTO<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：2000<br>
	 * 备注：用户头像<br>
	 */
	// private String photo;
	/**
	 * 列名：LOGIN_IP<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：100<br>
	 * 备注：最后登陆IP<br>
	 */
	// private String loginIp;
	/**
	 * 列名：LOGIN_DATE<br>
	 * 类型：DATETIME<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：最后登陆时间<br>
	 */
	// private Date loginDate;
	/**
	 * 列名：LOGIN_FLAG<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：64<br>
	 * 备注：是否可登录<br>
	 */
	// private String loginFlag;
	/**
	 * 列名：CREATE_BY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：创建者<br>
	 */
	// private String createBy;
	/**
	 * 列名：CREATE_DATE<br>
	 * 类型：TIMESTAMP<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：创建时间<br>
	 */
	// private Date createDate;
	/**
	 * 列名：UPDATE_BY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：NO<br>
	 * 长度：64<br>
	 * 备注：更新者<br>
	 */
	// private String updateBy;
	/**
	 * 列名：UPDATE_DATE<br>
	 * 类型：TIMESTAMP<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：更新时间<br>
	 */
	// private Date updateDate;
	/**
	 * 列名：REMARKS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：255<br>
	 * 备注：备注信息<br>
	 */
	// private String remarks;
	/**
	 * 列名：DEL_FLAG<br>
	 * 类型：CHAR<br>
	 * 允许null：NO<br>
	 * 长度：1<br>
	 * 备注：删除标记<br>
	 */
	// private String delFlag;
	/**
	 * 列名：REALNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：真实姓名<br>
	 */
	// private String realName;
	/**
	 * 列名：SEX<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：性别，参考常量定义文档 (1=男,2=女)<br>
	 */
	// private Integer sex;
	/**
	 * 列名：SUMMARY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：250<br>
	 * 备注：摘要，内容：xxx学校的教职工/学生/学生家长<br>
	 */
	// private String summary;
	/**
	 * 列名：IDCARD<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：身份证<br>
	 */
	// private String iDCard;
	/**
	 * 列名：BIRTHDAY<br>
	 * 类型：DATETIME<br>
	 * 允许null：YES<br>
	 * 长度：0<br>
	 * 备注：出生年月日，格式：CCYYMMDD，如19980401。<br>
	 */
	// private Date birthday;
	/**
	 * 列名：ADDRESS<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：250<br>
	 * 备注：联系地址<br>
	 */
	// private String address;
	/**
	 * 列名：NATION<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：民族，参见国标GB/T 3304-1991《中国各民族名称的罗马字母拼写法和代码》，另加选项：其他<br>
	 */
	// private String nation;
	/**
	 * 列名：STATUS<br>
	 * 类型：INT<br>
	 * 允许null：NO<br>
	 * 长度：0<br>
	 * 备注：<br>
	 */
	// private Integer status;
	/**
	 * 列名：PNGPHOTO<br>
	 * 类型：BLOB<br>
	 * 允许null：YES<br>
	 * 长度：65535<br>
	 * 备注：<br>
	 */
	// private String pngPhoto;
	/**
	 * 列名：FIRSTNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：姓<br>
	 */
	// private String firstName;
	/**
	 * 列名：LASTNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：名<br>
	 */
	// private String lastName;
	/**
	 * 列名：PHONEAREANUM<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：20<br>
	 * 备注：电话分区号<br>
	 */
	// private String phoneAreaNum;
	/**
	 * 列名：PHONESUBNUM<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：20<br>
	 * 备注：电话分机号<br>
	 */
	// private String phoneSubNum;
	/**
	 * 列名：COUNTRY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：50<br>
	 * 备注：国家<br>
	 */
	// private String country;
	/**
	 * 列名：PROVINCE<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：省<br>
	 */
	// private String province;
	/**
	 * 列名：CITY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：市<br>
	 */
	// private String city;
	/**
	 * 列名：COUNTY<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：区县<br>
	 */
	// private String county;
	/**
	 * 列名：PROVINCENAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：省<br>
	 */
	// private String provinceName;
	/**
	 * 列名：CITYNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：市<br>
	 */
	// private String cityName;
	/**
	 * 列名：COUNTYNAME<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：区县<br>
	 */
	// private String countyName;
	/**
	 * 列名：PINYIN<br>
	 * 类型：VARCHAR<br>
	 * 允许null：YES<br>
	 * 长度：30<br>
	 * 备注：姓名拼音<br>
	 */
	// private String pinYin;
}
