package com.xh.system.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.examples.annotation.api.AnnotationService;
import com.xh.system.model.PRight;
import com.xh.util.AppException;
import com.xh.util.RecordSet;
import com.xh.util.db.CommonService;

@Service
@Transactional
public class RightService extends CommonService<PRight> {
//	
	@Reference
	AnnotationService annotationService; 
	
	public static final Logger logger = Logger.getLogger(RightService.class);
	@Transactional
	public RecordSet<PRight> listUserByRole(int roleId) {
		annotationService.sayRecord("");
//		Assert.state(roleId>0);
//		RightDao dao = this.getDao(RightDao.class);
//		RecordSet rs = dao.list(new Record());
//		rs.d();
		this.update("update sys_right set rightname='a"+System.currentTimeMillis()+"' where rightid=1");
        boolean bFlag =true;
        if (bFlag) {
        	throw new AppException("test2");
        }
		return null;
		//return this.queryByIndex("select a.* from p_user a,p_user_role b where a.user_code=b.user_code and b.role_id=? ",new Integer[]{roleId}).setRecordName(PUser.class);
	}	
	
	
}
