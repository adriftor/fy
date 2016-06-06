package com.xh.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DomainTest {

	private String testP1;

	public String getTestP1() {
		return testP1;
	}

	public void setTestP1(String testP1) {
		this.testP1 = testP1;
	}
	
}
