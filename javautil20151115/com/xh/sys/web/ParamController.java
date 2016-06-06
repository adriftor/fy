/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.xh.sys.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseXhController;
import com.xh.sys.entity.SysParam;
import com.xh.sys.service.ParamService;
import com.xh.util.CommonUtil;

/**
 * 系统参数Controller
 * @author adriftor
 */
@Controller
@RequestMapping(value = "/sys/param")
public class ParamController extends BaseXhController<SysParam> {

	@Autowired
	private ParamService service;
	
//	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"listPage", ""})
	public String listPage(Model model) {
		Page<SysParam> page = service.listPage(getEntity());
        model.addAttribute("page", page);
		return "/sys/userList";
	}
	
//	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	@ResponseBody
	public SysParam form() {
		if (CommonUtil.isEmpty(getEntity().getParamCode())) {
			return getEntity();
		}
		else {
			SysParam domain = service.get(getEntity().getParamCode());
			return domain;
		}
		
		
	}

//	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	@ResponseBody
	public int save() {
		return service.saveEntity(getEntity());
	}
	
	@RequestMapping(value = "delete")
	public int delete() {
		return service.delete(getEntity().getString("ids"));
	}
	
}
