package com.xh.sys.entity;
import java.sql.Types;
import java.util.Map;

import com.xh.util.RecordTable;
/**
*表名：SYS_PARAM<br>
*/
public class SysParam extends RecordTable {
public static final String TABLE_NAME="SYS_PARAM";
public static final String PRIMARY_KEY="autoId";
public SysParam() {
}

public SysParam(Map map) {
super(map);
}
static {
RecordTable.registerTable(SysParam.class,new RecordTable.TableInfo("SYS_PARAM","autoId"));
}
/**
*列名：AUTOID<br>
*类型：INT<br>
*允许null：NO<br>
*长度：0<br>
*备注：<br>
*/
public void setAutoId(Integer autoId) {
this.put("autoid",Types.INTEGER,autoId);
}
/**
*列名：AUTOID<br>
*类型：INT<br>
*允许null：NO<br>
*长度：0<br>
*备注：<br>
*/
public Integer getAutoId() {
return this.gIntValue("autoid");
}
/**
*列名：PARAMCODE<br>
*类型：VARCHAR<br>
*允许null：NO<br>
*长度：80<br>
*备注：编码<br>
*/
public void setParamCode(String paramCode) {
this.put("paramcode",Types.VARCHAR,paramCode);
}
/**
*列名：PARAMCODE<br>
*类型：VARCHAR<br>
*允许null：NO<br>
*长度：80<br>
*备注：编码<br>
*/
public String getParamCode() {
return this.getString("paramcode");
}
/**
*列名：PARAMNAME<br>
*类型：VARCHAR<br>
*允许null：NO<br>
*长度：80<br>
*备注：名称<br>
*/
public void setParamName(String paramName) {
this.put("paramname",Types.VARCHAR,paramName);
}
/**
*列名：PARAMNAME<br>
*类型：VARCHAR<br>
*允许null：NO<br>
*长度：80<br>
*备注：名称<br>
*/
public String getParamName() {
return this.getString("paramname");
}
/**
*列名：PARAMVALUE<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值<br>
*/
public void setParamValue(String paramValue) {
this.put("paramvalue",Types.VARCHAR,paramValue);
}
/**
*列名：PARAMVALUE<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值<br>
*/
public String getParamValue() {
return this.getString("paramvalue");
}
/**
*列名：PARAMVALUE2<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值2<br>
*/
public void setParamValue2(String paramValue2) {
this.put("paramvalue2",Types.VARCHAR,paramValue2);
}
/**
*列名：PARAMVALUE2<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值2<br>
*/
public String getParamValue2() {
return this.getString("paramvalue2");
}
/**
*列名：PARAMVALUE3<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值3<br>
*/
public void setParamValue3(String paramValue3) {
this.put("paramvalue3",Types.VARCHAR,paramValue3);
}
/**
*列名：PARAMVALUE3<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值3<br>
*/
public String getParamValue3() {
return this.getString("paramvalue3");
}
/**
*列名：PARAMVALUE4<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值4<br>
*/
public void setParamValue4(String paramValue4) {
this.put("paramvalue4",Types.VARCHAR,paramValue4);
}
/**
*列名：PARAMVALUE4<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值4<br>
*/
public String getParamValue4() {
return this.getString("paramvalue4");
}
/**
*列名：REMARKS<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：备注信息<br>
*/
public void setRemarks(String remarks) {
this.put("remarks",Types.VARCHAR,remarks);
}
/**
*列名：REMARKS<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：备注信息<br>
*/
public String getRemarks() {
return this.getString("remarks");
}
/**
*列名：AUTOID<br>
*类型：INT<br>
*允许null：NO<br>
*长度：0<br>
*备注：<br>
*/
//private  Integer autoId;
/**
*列名：PARAMCODE<br>
*类型：VARCHAR<br>
*允许null：NO<br>
*长度：80<br>
*备注：编码<br>
*/
//private  String paramCode;
/**
*列名：PARAMNAME<br>
*类型：VARCHAR<br>
*允许null：NO<br>
*长度：80<br>
*备注：名称<br>
*/
//private  String paramName;
/**
*列名：PARAMVALUE<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值<br>
*/
//private  String paramValue;
/**
*列名：PARAMVALUE2<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值2<br>
*/
//private  String paramValue2;
/**
*列名：PARAMVALUE3<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值3<br>
*/
//private  String paramValue3;
/**
*列名：PARAMVALUE4<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：值4<br>
*/
//private  String paramValue4;
/**
*列名：REMARKS<br>
*类型：VARCHAR<br>
*允许null：YES<br>
*长度：255<br>
*备注：备注信息<br>
*/
//private  String remarks;
}
