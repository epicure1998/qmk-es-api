package com.qmk;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.sql.SQLOutput;

@SpringBootTest
class QmkEsApiApplicationTests {

	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	@Test
	void testCreateIndex() throws IOException {
		//1、创建索引请求
		CreateIndexRequest request=new CreateIndexRequest("qiumengke_index");
		//2、客户端执行请求IndicesClient,请求后获得响应
		CreateIndexResponse createIndexResponse=
				client.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(createIndexResponse.isAcknowledged());
	}

	//查询测试
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request=new GetIndexRequest("qiumengke_index");
		System.out.println(client.indices().exists(request,RequestOptions.DEFAULT));
	}

	//删除测试
	@Test
	void testDeleteIndex() throws IOException {
		DeleteIndexRequest request=new DeleteIndexRequest("qiumengke_index");
		System.out.println(client.indices().delete(request,RequestOptions.DEFAULT).isAcknowledged());

	}


	@Test
	void contextLoads() {
	}

}
