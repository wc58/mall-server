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
        // ?????????????????????
        createIndex();
        // ???????????????????????????
        List<PmsProduct> products = productService.list();
        // ????????????
        ArrayList<EsProduct> esProducts = new ArrayList<>();
        for (PmsProduct product : products) {
            EsProduct esProduct = new EsProduct();
            BeanUtils.copyProperties(product, esProduct);
            esProducts.add(esProduct);
        }
        // ?????????????????????
        bulkCreateDocument(esProducts);
    }


    /**
     * ??????????????????
     *
     * @param products ????????????
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
            log.warn("????????????????????????");
            return;
        }
        log.info("?????????:{}", bulk.getTook());
        for (BulkItemResponse item : bulk.getItems()) {
            log.info("???????????????id???{}???status???{}???type???{}???version???{}", item.getId(), item.status(), item.getResponse().getResult(), item.getVersion());
        }
    }

    /**
     * ???es???????????????
     */
    private void createIndex() {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.mapping(mappingJson, XContentType.JSON);
        CreateIndexResponse response = null;
        try {
            response = esClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.warn("????????????[{}]????????????????????????{}", indexName, e.getMessage());
        }
        if (response == null) {
            log.warn("????????????[{}]?????????????????????????????????", indexName);
        } else {
            log.info("????????????[{}]??????", indexName);
        }
    }

    /**
     * ??????id????????????
     *
     * @param id ??????id
     * @return ???????????????
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
        log.info("???????????????id???{}???result???{}", response.getId(), response.getResult());
        return 1;
    }

    /**
     * ????????????id?????????????????????
     *
     * @param ids ????????????id
     * @return ???????????????
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
            log.info("???????????????id???{}???result???{}", item.getId(), item.getOpType());
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
        log.info("{}???????????????????????????{}", indexName, response.isAcknowledged());
        return response.isAcknowledged();
    }

    @Override
    public EsProduct create(String id) {
        // ????????????
        PmsProduct product = productService.getById(id);
        EsProduct esProduct = new EsProduct();
        BeanUtils.copyProperties(product, esProduct);
        // ??????????????????
        IndexRequest indexRequest = new IndexRequest().index(indexName).id(id).source(JacksonUtils.object2Json(esProduct), XContentType.JSON);
        IndexResponse response = null;
        try {
            response = esClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null) {
            log.warn("????????????????????????");
            return esProduct;
        }
        log.info("???????????????id???{}???result???{}", response.getId(), response.getResult());
        return esProduct;
    }

    @Override
    public R.Page<EsProduct> search(String keyword, Integer fromPage, Integer size) {
        String[] searchFields = {"name", "subTitle", "keywords"};
        SearchRequest searchRequest = new SearchRequest().indices(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // ????????????
        sourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, searchFields));
        // ????????????
        sourceBuilder.from((fromPage - 1) * size);
        sourceBuilder.size(size);
        searchRequest.source(sourceBuilder);

        // ????????????
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
     * ??????????????????
     *
     * @param fromPage ?????????
     * @param size     ?????????
     * @param response ?????????????????????
     * @return ????????????
     */
    private R.Page<EsProduct> packagePage(Integer fromPage, Integer size, SearchResponse response) {
        // ??????????????????
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
