package com.xh.util.db;


import java.io.Serializable;

import com.xh.util.RecordSet;
import com.xh.util.RecordTable;

public interface BaseXhService {
	public <T extends RecordTable> T get(Serializable id);
	public <T extends RecordTable> RecordSet<T> getAll();
	public <T extends RecordTable> int save(T domain);
	public <T extends RecordTable> int update(T domain);
	public <T extends RecordTable> int saveOrUpdate(T domain);
	public int delete(Serializable ids);
}
