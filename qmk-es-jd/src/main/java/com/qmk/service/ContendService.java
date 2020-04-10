package com.qmk.service;

import com.alibaba.fastjson.JSON;
import com.qmk.pojo.Content;
import com.qmk.utils.HtmlParseUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContendService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //1.解析数据放入ES索引库中
    public Boolean parseContent(String keyword) throws IOException {
        List<Content> al= HtmlParseUtils.parse(keyword);
        //2、把查询到数据批量放入es索引中
        BulkRequest bulkRequest=new BulkRequest();
        bulkRequest.timeout("60m");
        for(Content temp: al){
            bulkRequest.add(
                    new IndexRequest("jd_goods")
                    .source(JSON.toJSONString(temp), XContentType.JSON));
        }
        BulkResponse bulkResponse=restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    //2.获取数据实现搜索功能
    public List<Map<String,Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException, InterruptedException {
        if(pageNo<1) pageNo=1;

        //System.out.println(keyword);
        //条件搜索
        SearchRequest searchRequest=new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        //Thread.sleep(5000);

        //精确匹配

        //TermQueryBuilder termQueryBuilder= QueryBuilders.termQuery("name",keyword);
        MatchQueryBuilder termQueryBuilder=  QueryBuilders.matchQuery("name",keyword);

        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);


        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse= restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //解析结果
        List<Map<String,Object>> list=new LinkedList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
             Map<String, HighlightField> highlightFields=documentFields.getHighlightFields();
             HighlightField highlightField=highlightFields.get("name");
             Map<String,Object> sourceAsMap=documentFields.getSourceAsMap();
             if(highlightField!=null){
                 Text[] fragments=highlightField.fragments();
//                 for(Text text: fragments) {
//                     System.out.println(text.toString());
//                     System.out.println("=================================");
//                 }
                // System.out.println("**************************************");
                 String n_name="";
                 for(Text text: fragments){
                     n_name+=text;
                 }
                 sourceAsMap.put("name",n_name);
             }

            list.add(sourceAsMap);
        }

        //返回结果
        return list;

    }
}
