/**
 * Copyright 2019 vip.com.
 * <p>
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
 * </p>
 */

package com.vip.pallas.service;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;

import com.vip.pallas.BaseSpringTest;
import com.vip.pallas.mybatis.entity.IndexOperation;
import com.vip.pallas.mybatis.repository.IndexOperationRepository;
import com.vip.pallas.mybatis.repository.SearchServerRepository;

public class DaoServiceTest extends BaseSpringTest {


	@Resource
    private IndexOperationRepository indexOperationRepository;
	@Resource
    private SearchServerRepository searchServerRepository;

    @Test
    public void testSelectClusterByVersionId() throws Exception{
    	IndexOperation record = new IndexOperation();
    	record.setEventDetail("eventDetail");
    	record.setEventName("eventName");
    	record.setEventType("eventType");
    	record.setOperationTime(new Date());
    	record.setOperator("operator");
		indexOperationRepository.insert(record );
    	System.out.println(record);
    }
    
    @Test
    public void testSS() {
    	searchServerRepository.selectHealthyServers(20l);
    }
}