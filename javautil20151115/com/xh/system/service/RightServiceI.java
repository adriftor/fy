package com.xh.system.service;

import com.xh.system.model.PRight;
import com.xh.util.RecordSet;


public interface RightServiceI {

	public RecordSet<PRight> listUserByRole(int roleId);
	
	
}
