package top.chao58.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chao58.pojo.PmsProduct;
import top.chao58.service.EsProductService;
import top.chao58.service.PmsProductService;
import top.chao58.util.JacksonUtils;
import top.chao58.util.R;
import top.chao58.vo.EsProduct;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EsProductServiceImpl implements EsProductService {

    private final String indexName = "product";
    private final String mappingJson = "{\n" +
            "\t\"properties\": {\n" +
            "\t\t\"id\": {\n" +
            "\t\t\t\"type\": \"keyword\"\n" +
            "\t\t},\n" +
            "\t\t\"productSn\": {\n" +
            "\t\t\t\"type\": \"keyword\"\n" +
            "\t\t},\n" +
            "\t\t\"brandName\": {\n" +
            "\t\t\t\"type\": \"keyword\"\n" +
            "\t\t},\n" +
            "\t\t\"productCategoryName\": {\n" +
            "\t\t\t\"type\": \"keyword\"\n" +
            "\t\t},\n" +
            "\t\t\"name\": {\n" +
            "\t\t\t\"type\": \"text\",\n" +
            "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "\t\t},\n" +
            "\t\t\"subTitle\": {\n" +
            "\t\t\t\"type\": \"text\",\n" +
            "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "\t\t},\n" +
            "\t\t\"keywords\": {\n" +
            "\t\t\t\"type\": \"text\",\n" +
            "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";

    @Autowired
    private PmsProductService productService;
    @Autowired
    private RestHighLevelClient esClient;


    @Override
    public void importALl() {
        // 准备好索引信息
        createIndex();
        // 查询数据库中的数据
        List<PmsProduct> products = productService.list();
        // 属性复制
        ArrayList<EsProduct> esProducts = new ArrayList<>();
        for (PmsProduct product : products) {
            EsProduct esProduct = new EsProduct();
            BeanUtils.copyProperties(product, esProduct);
            esProducts.add(esProduct);
        }
        // 批处理添加文档
        bulkCreateDocument(esProducts);
    }


    /**
     * 批量添加文档
     *
     * @param products 文档信息
     */
    private void bulkCreateDocument(List<EsProduct> products) {
        BulkRequest bulkRequest = new BulkRequest();
        for (EsProduct product : products) {
            IndexRequest indexRequest = new IndexRequest().index(indexName).id(product.getId() + "").source(JacksonUtils.object2Json(product), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = null;
        try {
            bulk = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bulk == null) {
            log.warn("可能添加文档失败");
            return;
        }
        log.info("总耗时:{}", bulk.getTook());
        for (BulkItemResponse item : bulk.getItems()) {
            log.info("文档操作：id：{}，status：{}，type：{}，version：{}", item.getId(), item.status(), item.getResponse().getResult(), item.getVersion());
        }
    }

    /**
     * 在es中创建索引
     */
    private void createIndex() {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.mapping(mappingJson, XContentType.JSON);
        CreateIndexResponse response = null;
        try {
            response = esClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.warn("创建索引[{}]出现异常，原因：{}", indexName, e.getMessage());
        }
        if (response == null) {
            log.warn("创建索引[{}]失败，可能索引已经存在", indexName);
        } else {
            log.info("创建索引[{}]成功", indexName);
        }
    }

    /**
     * 根据id删除文档
     *
     * @param id 文档id
     * @return 删除的个数
     */
    @Override
    public Integer delete(String id) {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index(indexName).id(id);
        DeleteResponse response;
        try {
            response = esClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        log.info("删除文档：id：{}，result：{}", response.getId(), response.getResult());
        return 1;
    }

    /**
     * 根据多个id，批量删除文档
     *
     * @param ids 多个文档id
     * @return 删除的个数
     */
    @Override
    public Integer delete(List<String> ids) {
        BulkRequest bulkRequest = new BulkRequest();
        for (String id : ids) {
            bulkRequest.add(new DeleteRequest().index(indexName).id(id));
        }
        BulkResponse response;
        try {
            response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        BulkItemResponse[] items = response.getItems();
        for (BulkItemResponse item : items) {
            log.info("删除文档：id：{}，result：{}", item.getId(), item.getOpType());
        }
        return items.length;
    }

    @Override
    public boolean deleteIndex(String indexName) {
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response;
        try {
            response = esClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        log.info("{}索引是否删除成功：{}", indexName, response.isAcknowledged());
        return response.isAcknowledged();
    }

    @Override
    public EsProduct create(String id) {
        // 属性复制
        PmsProduct product = productService.getById(id);
        EsProduct esProduct = new EsProduct();
        BeanUtils.copyProperties(product, esProduct);
        // 构建请求对象
        IndexRequest indexRequest = new IndexRequest().index(indexName).id(id).source(JacksonUtils.object2Json(esProduct), XContentType.JSON);
        IndexResponse response = null;
        try {
            response = esClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            log.warn("可能添加文档失败");
            return esProduct;
        }
        log.info("添加文档：id：{}，result：{}", response.getId(), response.getResult());
        return esProduct;
    }

    @Override
    public R.Page<EsProduct> search(String keyword, Integer fromPage, Integer size) {
        String[] searchFields = {"name", "subTitle", "keywords"};
        SearchRequest searchRequest = new SearchRequest().indices(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 匹配字段
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, searchFields));
        // 分页条件
        sourceBuilder.from((fromPage - 1) * size);
        sourceBuilder.size(size);
        searchRequest.source(sourceBuilder);

        // 开始查询
        SearchResponse response;
        try {
            response = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return packagePage(fromPage, size, response);
    }

    /**
     * 封装返回信息
     *
     * @param fromPage 起始页
     * @param size     查询数
     * @param response 查询返回的响应
     * @return 分页信息
     */
    private R.Page<EsProduct> packagePage(Integer fromPage, Integer size, SearchResponse response) {
        // 返回分页信息
        R.Page<EsProduct> page = new R.Page<>();
        SearchHits hits = response.getHits();
        page.setFromPage(fromPage);
        page.setSize(size);
        long total = hits.getTotalHits().value;
        long totalPage = total % size == 0 ? total / size : total / size + 1;
        for (SearchHit productJson : hits) {
            EsProduct product = JacksonUtils.json2Object(productJson.getSourceAsMap(), EsProduct.class);
            page.add(product);
        }
        page.setTotal(total);
        page.setTotalPage(totalPage);
        return page;
    }
}
