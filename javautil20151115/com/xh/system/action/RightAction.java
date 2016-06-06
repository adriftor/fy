package com.xh.system.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.examples.annotation.api.AnnotationService;
import com.xh.system.model.PRight;
import com.xh.system.service.RightService;
import com.xh.util.RecordSet;

@Controller
@RequestMapping("/right")

public class RightAction { 
	@Resource
	private RightService rightService;
	
	@Reference
	AnnotationService annotationService; 
	@RequestMapping("/listUserByRole")
	@ResponseBody
	public RecordSet<PRight> listUserByRole(HttpServletRequest req) {
		rightService.listUserByRole(1);
//		annotationService.sayRecord(""+System.currentTimeMillis()).d();
//		PRight entity = new PRight(req.getParameterMap());
//		annotationService.sayRecord(""+System.currentTimeMillis());
//		return rightService.listUserByRole(1);
		return null;
	}
	
	
}
