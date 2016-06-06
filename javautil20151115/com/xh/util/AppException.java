package com.xh.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author adriftor
 * @version 1.0
 */
public class AppException extends java.lang.RuntimeException {

	public static final Log logger = LogFactory.getLog(AppException.class);


	private Throwable errEx = null;

	private String errMsg = "";
	
	public AppException() {
	}

	public AppException(String msg) {
		this.errMsg = msg;
	}
	public AppException(String msg, Throwable ex) {
		this.errMsg = msg;
		this.errEx = ex;
	}

	public AppException(Throwable ex) {
		this.errEx = ex;

	}
	public String getErrMsg() {
		return errMsg;
	}

	public String getLastMessage() {
		String msg = getMessage();
		if (msg != null && msg.indexOf("--->>") >=0) {
			msg = msg.substring(msg.lastIndexOf("--->>")+5);
		}
		return msg;
	}

	public String getMessage() {
		String msg = super.getMessage();
		if (msg == null) {
			msg = this.errMsg;
		}
		if (errEx != null) {
			msg += "--->>"+ errEx.getMessage();
		}
		return msg;
	}
	public String getTraceStack() {
		if (this.errEx != null) {
			return CommonUtil.stackTrace(errEx);
		}
		return "";
		
	}
	public void printStackTrace() {
		
		if (errEx != null) {
			errEx.printStackTrace();
		}
		else {
			super.printStackTrace();
		}
	}

	public void printStackTrace(java.io.PrintStream ps) {
		super.printStackTrace(ps);
		if (errEx != null) {
			errEx.printStackTrace(ps);
		}
	}
	public void printStackTrace(java.io.PrintWriter pw) {
		super.printStackTrace(pw);
		if (errEx != null) {
			errEx.printStackTrace(pw);
		}
	}

	
}
