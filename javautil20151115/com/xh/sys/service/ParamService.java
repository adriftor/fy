package com.xh.sys.service;


import com.thinkgem.jeesite.common.persistence.Page;
import com.xh.sys.entity.SysParam;
import com.xh.util.db.BaseXhService;

public interface ParamService extends BaseXhService{
	public int saveEntity(SysParam domain);
	public Page<SysParam> listPage(SysParam domain);
}
