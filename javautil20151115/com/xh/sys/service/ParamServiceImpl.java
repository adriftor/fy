package com.xh.sys.service;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.xh.sys.entity.SysParam;
import com.xh.util.CommonUtil;
import com.xh.util.Record;
import com.xh.util.RecordSet;
import com.xh.util.db.CommonService;

@SuppressWarnings("unchecked")
@Service
@Transactional(readOnly=true)
public class ParamServiceImpl  extends  CommonService<SysParam> implements ParamService {
	
	
	public static final Logger logger = Logger.getLogger(ParamServiceImpl.class);
	@Transactional(readOnly = false)
	public int saveEntity(SysParam domain) {
		if (domain.getAutoId() <= 0) {
			this.save(domain);
		}
		else {
			this.update(domain);
		}		
		return 1;
	}
	
	public Page<SysParam> listPage(SysParam domain) {
		String sql = "select * from sys_param where 1=1";
		if (CommonUtil.isNotEmpty(domain.getParamName())) {
			sql += " and paramName like '%"+domain.getStringForSqlLike("paramName")+"%'";
		}
		if (CommonUtil.isNotEmpty(domain.getParamCode())) {
			sql += " and ParamCode=#{ParamCode}";
		}
		
		RecordSet<SysParam> rs = this.queryT(sql, domain,domain.getPage());
		return rs.toPage();
	}


	
}
