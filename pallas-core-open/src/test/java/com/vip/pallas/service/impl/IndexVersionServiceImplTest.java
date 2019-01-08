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

package com.vip.pallas.service.impl;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.vip.pallas.BaseSpringTest;
import com.vip.pallas.bean.DBSchema;
import com.vip.pallas.bean.IndexParam;
import com.vip.pallas.exception.PallasException;
import com.vip.pallas.mybatis.entity.Cluster;
import com.vip.pallas.mybatis.entity.DataSource;
import com.vip.pallas.mybatis.entity.Index;
import com.vip.pallas.mybatis.entity.IndexVersion;
import com.vip.pallas.mybatis.entity.Page;
import com.vip.pallas.service.ClusterService;
import com.vip.pallas.service.ElasticSearchService;
import com.vip.pallas.service.IndexService;
import com.vip.pallas.service.IndexVersionService;

/**
 * Created by owen on 5/7/2017.
 */
public class IndexVersionServiceImplTest extends BaseSpringTest {

    private static final String TEST_INDEX_NAME = "it-index-" + System.currentTimeMillis();

    private static final String TEST_CLUSTER_NAME = "pallas-qa";


    @Autowired
    private IndexVersionService indexVersionService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ClusterService clusterService;

    private Index testIndex;

    private Cluster testCluser;
    
	@Autowired
	private ElasticSearchService elasticSearchService;
	
    @Before
    public void setUp() throws PallasException {
        if (testIndex == null) {
            Page<Index> page = new Page<>();
            page.setPageNo(0);
            page.setPageSize(1);
            List<Index> list = indexService.findPage(page, TEST_INDEX_NAME, TEST_CLUSTER_NAME);
            Index index;
            if (list.size() > 0) {
                index = list.get(0);
            } else {
                index = new Index();
                DataSource dataSource = new DataSource();

				dataSource.setIp("10.199.203.185");
                dataSource.setPort("3306");
                dataSource.setUsername("root");
                dataSource.setPassword("123456");
                dataSource.setDbname("pallas_console");
                dataSource.setTableName("index");

                index.setDescription("one");
                index.setClusterName(TEST_CLUSTER_NAME);
                List<DataSource> dsList = new ArrayList<>();
                dsList.add(dataSource);
                index.setDataSourceList(dsList);
                index.setIndexName(TEST_INDEX_NAME);
                indexService.insert(index, dsList);
            }
            testIndex = index;
            testCluser = clusterService.findByName(TEST_CLUSTER_NAME);
        }
    }
    @Test
    public void testDBSchema() throws Exception {
    	List<DBSchema> metaDataFromDB = indexVersionService.getMetaDataFromDB(testIndex.getId());
    	assertThat(metaDataFromDB).isNotNull();
    }

    @Test
    public void testESMapping() throws Exception {
    	Map<String, String> fieldTypeMap = indexVersionService.getSchemaMappingAsMap(99999l);
    	assertThat(fieldTypeMap).isNotNull();
    	for (Entry<String,String> kv : fieldTypeMap.entrySet()) {
    		System.out.println(kv.getKey() + ":" +  kv.getValue());
		}
    	
    }

    @Test
    public void testIndexParam() throws Exception {
		IndexParam indexParam = indexService.getIndexParamByVersionId(99999l);
    	assertNull(indexParam);
    }

    @Test
    public void testVersionCRUD() throws Exception {
        IndexVersion indexVersion = new IndexVersion();

        indexVersion.setIndexId(testIndex.getId());
        indexVersion.setClusterId(testCluser.getId());
        indexVersion.setNumOfShards(Byte.valueOf("3"));
        indexVersion.setNumOfReplication(Byte.valueOf("1"));
        indexVersion.setVdpQueue("123");
        indexVersion.setVdp(1);
        indexVersion.setFilterFields(false);
        indexVersion.setCheckSum(false);
        indexVersion.setPreferExecutor("");
        indexVersion.setRoutingKey("no");
        indexVersion.setIdField("id");
        indexVersion.setUpdateTimeField("updated_time");
        indexVersion.setDynamic(false);
        indexVersion.setAllocationNodes("");
        indexVersion.setIndexSlowThreshold(-1l);
        indexVersion.setFetchSlowThreshold(200l);
        indexVersion.setQuerySlowThreshold(200l);
        indexVersion.setRefreshInterval((byte)60);

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("fieldName", "id");
        node.put("fieldType", "keyword");
        node.put("multi", true);
        node.put("search", true);
        node.put("docValue", true);
        node.put("dynamic", false);

        ArrayNode chilren = JsonNodeFactory.instance.arrayNode();
        ObjectNode child = JsonNodeFactory.instance.objectNode();
        chilren.add(child);
        node.put("children", chilren);

        child.put("fieldName", "id");
        child.put("fieldType", "keyword");
        child.put("multi", true);
        child.put("search", true);
        child.put("docValue", true);
        child.put("dynamic", false);

        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        array.add(node);

        ArrayList<Map> arrayMap = (ArrayList<Map>) JSONArray.parseArray(array.toString(), Map.class);
        indexVersionService.insert(indexVersion, arrayMap);

        com.vip.pallas.bean.IndexVersion bean = indexVersionService.findVersionById(indexVersion.getId());

        assertEquals(testIndex.getId(), bean.getIndexId());
        assertEquals(3, bean.getShardNum());
        assertEquals(1, bean.getReplicationNum());
        assertEquals("123", bean.getVdpQueue());
        assertEquals("no", bean.getRoutingKey());
        assertEquals("id", bean.getIdField());
        assertEquals("updated_time", bean.getUpdateTimeField());

        indexVersion.setNumOfReplication(Byte.valueOf("2"));
        indexVersionService.update(indexVersion, arrayMap);

        IndexVersion db = indexVersionService.findById(indexVersion.getId());
        assertEquals(Byte.valueOf("2"), db.getNumOfReplication());

        elasticSearchService.createIndex(TEST_INDEX_NAME, indexVersion.getId());
        boolean isIndexExisted = elasticSearchService.isExistIndex(TEST_INDEX_NAME, indexVersion.getId());
		assertThat(isIndexExisted).isEqualTo(true);
		
        indexVersionService.enableVersion(db.getId());
        IndexVersion iv = indexVersionService.findById(indexVersion.getId());
        assertThat(iv.getIsUsed()).isEqualTo(true);
        
        
        indexVersionService.disableVersion(db.getId());
        iv = indexVersionService.findById(indexVersion.getId());
        assertThat(iv.getIsUsed()).isEqualTo(false);
        
        elasticSearchService.deleteIndex(TEST_INDEX_NAME, indexVersion.getId());
        isIndexExisted = elasticSearchService.isExistIndex(TEST_INDEX_NAME, indexVersion.getId());
		assertThat(isIndexExisted).isEqualTo(false);
		
        indexVersionService.deleteVersion(bean.getId());
    }

    @After
    public void tearDown() throws Exception {
        if (testIndex != null) {
            Page<IndexVersion> page = new Page<>();
            page.setPageNo(0);
            page.setPageSize(9999);
            List<IndexVersion> list = indexVersionService.findPage(page, testIndex.getId());
            for(IndexVersion v : list) {
                indexVersionService.deleteVersion(v.getId());
            }
            indexService.deleteById(testIndex.getId());
        }
    }
}