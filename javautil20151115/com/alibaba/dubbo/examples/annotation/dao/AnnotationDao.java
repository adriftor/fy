package com.alibaba.dubbo.examples.annotation.dao;

import org.springframework.stereotype.Repository;

import com.xh.system.model.PRight;
import com.xh.util.AppException;
import com.xh.util.db.CommonService;

@Repository
public class AnnotationDao extends CommonService<PRight> {
	public void udpateUserRight() {
		 this.update("update sys_right set rightname='a"+System.currentTimeMillis()+"' where rightid=1");
	        boolean bFlag =true;
	        if (bFlag) {
	        	throw new AppException("test");
	        }
	}
}
