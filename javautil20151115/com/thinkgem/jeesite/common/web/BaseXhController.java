/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.thinkgem.jeesite.common.persistence.Page;
import com.xh.util.CommonUtil;
import com.xh.util.Record;
import com.xh.util.RecordTable;

/**
 * 控制器支持类
 * @author ThinkGem
 * @version 2013-3-23
 */
public abstract class BaseXhController<R extends RecordTable> extends BaseController {
	private static ThreadLocal<RecordTable> tlEntity = new ThreadLocal<RecordTable>();
	@ModelAttribute
	public void initEntity(HttpServletRequest req) {
		try {
			Class<R> c = CommonUtil.getGenericClass(getClass());
			R r = super.getEntityByReq(req, c);
			tlEntity.set(r);
		} catch (Exception e) {
			
		} 

	}
	public R getEntity() {
		return (R) tlEntity.get();
	}
	
}
