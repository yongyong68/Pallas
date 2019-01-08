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

package com.vip.pallas.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;

public class MyObjectMapper extends ObjectMapper {

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static MyObjectMapper mapper =  new MyObjectMapper();
	public static MyObjectMapper getInstance(){
		return mapper;
	}
	
	private MyObjectMapper(){
		super();
		DateFormat myDateFormat = new SimpleDateFormat(DATE_FORMAT); 
		super.getSerializationConfig().withDateFormat(myDateFormat);
		super.getDeserializationConfig().withDateFormat(myDateFormat);
	}
	
}