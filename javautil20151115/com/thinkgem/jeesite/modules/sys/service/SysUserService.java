package com.thinkgem.jeesite.modules.sys.service;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.examples.annotation.api.AnnotationService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.xh.system.model.PRight;
import com.xh.util.AppException;
import com.xh.util.RecordSet;
import com.xh.util.db.CommonService;

@Service
@Transactional
public class SysUserService extends CommonService<User> {
//	
	@Reference
	AnnotationService annotationService; 
	
	public static final Logger logger = Logger.getLogger(SysUserService.class);
	@Transactional
	public RecordSet<PRight> listUserByRole(int roleId) {
		this.update("update sys_right set rightname='a"+System.currentTimeMillis()+"' where rightid=1");
        boolean bFlag =true;
        if (bFlag) {
        	throw new AppException("test2");
        }
		return null;
		//return this.queryByIndex("select a.* from p_user a,p_user_role b where a.user_code=b.user_code and b.role_id=? ",new Integer[]{roleId}).setRecordName(PUser.class);
	}	
	
	
}
