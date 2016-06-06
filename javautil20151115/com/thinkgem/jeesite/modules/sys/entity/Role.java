/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.entity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 角色Entity
 * @author ThinkGem
 * @version 2013-12-05
 */
public class Role extends DataEntity<Role> {
	
	private static final long serialVersionUID = 1L;
	private Office office;	// 归属机构
	private String name; 	// 角色名称
	private String enname;	// 英文名称
	private String roleType;// 权限类型
	private String dataScope;// 数据范围
	
	private String oldName; 	// 原角色名称
	private String oldEnname;	// 原英文名称
	private String sysData; 		//是否是系统数据
	private String useable; 		//是否是可用
	
	private User user;		// 根据用户ID查询角色列表

//	private List<User> userList = Lists.newArrayList(); // 拥有用户列表
	private List<Menu> menuList = Lists.newArrayList(); // 拥有菜单列表
	private List<Office> officeList = Lists.newArrayList(); // 按明细设置数据范围

	// 数据范围（1：所有数据；2：所在公司及以下数据；3：所在公司数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
	public static final String DATA_SCOPE_ALL = "1";
	public static final String DATA_SCOPE_COMPANY_AND_CHILD = "2";
	public static final String DATA_SCOPE_COMPANY = "3";
	public static final String DATA_SCOPE_OFFICE_AND_CHILD = "4";
	public static final String DATA_SCOPE_OFFICE = "5";
	public static final String DATA_SCOPE_SELF = "8";
	public static final String DATA_SCOPE_CUSTOM = "9";
	
	
	private String roleID;		// RoleID
	private String showInSchool;		// ShowInSchool
	private String publicUserType;		// PublicUserType
	private String publicDuty;		// PublicDuty
	private String uIPermissions;		// UIPermissions
	private String aPIPermissions;		// APIPermissions
	private String studentEditPermissions;		// StudentEditPermissions
	private String teacherEditPermissions;		// TeacherEditPermissions
	private String index;		// Index
	private String status;		// Status
	private String parentType;		// ParentType
	private String parentID;		// ParentID
	
	public Role() {
		super();
		this.dataScope = DATA_SCOPE_SELF;
		this.useable=Global.YES;
	}
	
	public Role(String id){
		super(id);
	}
	
	public Role(User user) {
		this();
		this.user = user;
	}

	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public String getSysData() {
		return sysData;
	}

	public void setSysData(String sysData) {
		this.sysData = sysData;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=1, max=100)
	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}
	
	@Length(min=1, max=100)
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getDataScope() {
		return dataScope;
	}

	public void setDataScope(String dataScope) {
		this.dataScope = dataScope;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getOldEnname() {
		return oldEnname;
	}

	public void setOldEnname(String oldEnname) {
		this.oldEnname = oldEnname;
	}

//	public List<User> getUserList() {
//		return userList;
//	}
//
//	public void setUserList(List<User> userList) {
//		this.userList = userList;
//	}
//	
//	public List<String> getUserIdList() {
//		List<String> nameIdList = Lists.newArrayList();
//		for (User user : userList) {
//			nameIdList.add(user.getId());
//		}
//		return nameIdList;
//	}
//
//	public String getUserIds() {
//		return StringUtils.join(getUserIdList(), ",");
//	}

	public List<Menu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}

	public List<String> getMenuIdList() {
		List<String> menuIdList = Lists.newArrayList();
		for (Menu menu : menuList) {
			menuIdList.add(menu.getId());
		}
		return menuIdList;
	}

	public void setMenuIdList(List<String> menuIdList) {
		menuList = Lists.newArrayList();
		for (String menuId : menuIdList) {
			Menu menu = new Menu();
			menu.setId(menuId);
			menuList.add(menu);
		}
	}

	public String getMenuIds() {
		return StringUtils.join(getMenuIdList(), ",");
	}
	
	public void setMenuIds(String menuIds) {
		menuList = Lists.newArrayList();
		if (menuIds != null){
			String[] ids = StringUtils.split(menuIds, ",");
			setMenuIdList(Lists.newArrayList(ids));
		}
	}
	
	public List<Office> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Office> officeList) {
		this.officeList = officeList;
	}

	public List<String> getOfficeIdList() {
		List<String> officeIdList = Lists.newArrayList();
		for (Office office : officeList) {
			officeIdList.add(office.getId());
		}
		return officeIdList;
	}

	public void setOfficeIdList(List<String> officeIdList) {
		officeList = Lists.newArrayList();
		for (String officeId : officeIdList) {
			Office office = new Office();
			office.setId(officeId);
			officeList.add(office);
		}
	}

	public String getOfficeIds() {
		return StringUtils.join(getOfficeIdList(), ",");
	}
	
	public void setOfficeIds(String officeIds) {
		officeList = Lists.newArrayList();
		if (officeIds != null){
			String[] ids = StringUtils.split(officeIds, ",");
			setOfficeIdList(Lists.newArrayList(ids));
		}
	}
	
	/**
	 * 获取权限字符串列表
	 */
	public List<String> getPermissions() {
		List<String> permissions = Lists.newArrayList();
		for (Menu menu : menuList) {
			if (menu.getPermission()!=null && !"".equals(menu.getPermission())){
				permissions.add(menu.getPermission());
			}
		}
		return permissions;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	@Length(min=1, max=11, message="RoleID长度必须介于 1 和 11 之间")
	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	
	@Length(min=1, max=1, message="ShowInSchool长度必须介于 1 和 1 之间")
	public String getShowInSchool() {
		return showInSchool;
	}

	public void setShowInSchool(String showInSchool) {
		this.showInSchool = showInSchool;
	}
	
	@Length(min=0, max=11, message="PublicUserType长度必须介于 0 和 11 之间")
	public String getPublicUserType() {
		return publicUserType;
	}

	public void setPublicUserType(String publicUserType) {
		this.publicUserType = publicUserType;
	}
	
	@Length(min=0, max=11, message="PublicDuty长度必须介于 0 和 11 之间")
	public String getPublicDuty() {
		return publicDuty;
	}

	public void setPublicDuty(String publicDuty) {
		this.publicDuty = publicDuty;
	}
	
	public String getUIPermissions() {
		return uIPermissions;
	}

	public void setUIPermissions(String uIPermissions) {
		this.uIPermissions = uIPermissions;
	}
	
	public String getAPIPermissions() {
		return aPIPermissions;
	}

	public void setAPIPermissions(String aPIPermissions) {
		this.aPIPermissions = aPIPermissions;
	}
	
	public String getStudentEditPermissions() {
		return studentEditPermissions;
	}

	public void setStudentEditPermissions(String studentEditPermissions) {
		this.studentEditPermissions = studentEditPermissions;
	}
	
	public String getTeacherEditPermissions() {
		return teacherEditPermissions;
	}

	public void setTeacherEditPermissions(String teacherEditPermissions) {
		this.teacherEditPermissions = teacherEditPermissions;
	}
	
	@Length(min=1, max=11, message="Index长度必须介于 1 和 11 之间")
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	@Length(min=1, max=11, message="Status长度必须介于 1 和 11 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=1, max=11, message="ParentType长度必须介于 1 和 11 之间")
	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	
	@Length(min=1, max=50, message="ParentID长度必须介于 1 和 50 之间")
	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	
	
//	public boolean isAdmin(){
//		return isAdmin(this.id);
//	}
//	
//	public static boolean isAdmin(String id){
//		return id != null && "1".equals(id);
//	}
	
//	@Transient
//	public String getMenuNames() {
//		List<String> menuNameList = Lists.newArrayList();
//		for (Menu menu : menuList) {
//			menuNameList.add(menu.getName());
//		}
//		return StringUtils.join(menuNameList, ",");
//	}
}
