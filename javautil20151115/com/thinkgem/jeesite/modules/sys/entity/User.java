package com.thinkgem.jeesite.modules.sys.entity;

import com.google.common.collect.Lists;
import com.xh.util.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.sql.Types;
import java.math.BigDecimal;

/**
 * 表名：SYS_USER<br>
 */
public class User extends SysUser {

	public Role getRole() {
		return (Role)this.get("role");
	}

	public void setRole(Role role) {
		this.put("role",role);
	}

	public List<Role> getRoleList() {
		List<Role> roleList = (List<Role>) this.getValue("roleList",null);
		if (roleList == null) {
			roleList = Lists.newArrayList(); 
		}
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.put("roleList",roleList);
	}

	public boolean isAdmin(){
		return isAdmin(this.getId());
	}
	
	public static boolean isAdmin(String id){
		return id != null && "1".equals(id);
	}
	
	
	public Office getCompany() {
		Office company = (Office) this.getValue("company",null);
		if (company == null) {
			company = new Office();
			if (this.getCompanyId().length() > 0) {
				company.setId(this.getCompanyId());
			}
			if (this.isNotEmpty("company_name")) {
				company.setName(this.getString("company_name"));
			}
			this.put("company",company);
		}
		
		return company;
	}

	public void setCompany(Office company) {
		
		this.setCompanyId(company.getId());
		this.set("company_name",company.getName());
		this.put("company",company);
	}

	public Office getOffice() {
		Office office = (Office) this.getValue("company",null);
		if (office == null) {
			office = new Office();
			if (this.getOfficeId().length() > 0) {
				office.setId(this.getOfficeId());
			}
			if (this.isNotEmpty("office_name")) {
				office.setName(this.getString("office_name"));
			}
			this.put("office",office);
		}
		
		return office;
	}

	public void setOffice(Office office) {
		this.setOfficeId(office.getId());
		this.set("office_name",office.getName());
		this.put("office",office);
	}

	public String getOldLoginName() {
		return this.getString("oldLoginName");
	}

	public void setOldLoginName(String oldLoginName) {
		this.put("oldLoginName",oldLoginName);
	}

	public String getNewPassword() {
		return this.getString("newPassword");
	}

	public void setNewPassword(String newPassword) {
		this.put("newPassword",newPassword);
	}

	public List<String> getRoleIdList() {
		List<String> roleIdList = Lists.newArrayList();
		List<Role> roleList = this.getRoleList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}
	public User(Role role){
		this.setRole(role);
	}
	public void setRoleIdList(List<String> roleIdList) {
		List<Role> roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roleList.add(role);
		}
		this.setRoleList(roleList);
	}
	
	public User(String id) {
		this.setId(id);
	}

	public User(String id, String loginName){
		this.setId(id);
		this.setLoginName(loginName);
	}
	
	public User() {
	}
	public User(Map map) {
		super(map);
	}

}
