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

package com.vip.pallas.console.controller.api;

import com.vip.pallas.console.BaseControllerTest;
import com.vip.pallas.console.vo.ResultVO;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RoutingApiControllerTest extends BaseControllerTest {

    @Test
    public void testselectAll() throws Exception{
        ResultVO<List> resultVO =  callGetApi("/route/index_routing_authorization/all.json", List.class);
        assertThat(resultVO.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testgetAllIndexRouting() throws Exception{
        ResultVO<List> resultVO =  callGetApi("/route/index_routing/all.json", List.class);
        assertThat(resultVO.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testgetAllIndexRoutingTargetGroup() throws Exception{
        ResultVO<List> resultVO =  callGetApi("/route/index_routing_target_group/all.json", List.class);
        assertThat(resultVO.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}