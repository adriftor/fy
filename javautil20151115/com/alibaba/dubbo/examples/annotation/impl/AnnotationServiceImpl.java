/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.examples.annotation.impl;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.examples.annotation.api.AnnotationService;
import com.xh.system.model.PRight;
import com.xh.util.AppException;
import com.xh.util.db.CommonService;

/**
 * AsyncServiceImpl
 * 
 * @author william.liangf
 */
@Service
@Transactional
public class AnnotationServiceImpl extends CommonService<PRight> implements AnnotationService {

//	
//	@Resource
//	private RightService rightService;
	
    public String sayHello(String name) {
        System.out.println("async provider received: " + name);
        return "annotation: hello, " + name;
    }

    @Transactional(readOnly=false)
    public PRight sayRecord(String name) {
        System.out.println("Record: " + name);
        PRight rd = new PRight();
        rd.put("f1",1);
        rd.put("f2","健康的圣诞节的神经受到法国国家 ");
        this.query("select * from sys_right").d();
        this.update("update sys_right set rightname='b"+System.currentTimeMillis()+"' where rightid=1");
        boolean bFlag =true;
        if (bFlag) {
        	throw new AppException("test");
        }
        return rd;
    }
}