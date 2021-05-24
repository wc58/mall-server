package top.chao58.service;

import top.chao58.util.R;
import top.chao58.vo.EsProduct;

import java.util.List;

public interface EsProductService {

    void importALl();

    Integer delete(String id);

    Integer delete(List<String> ids);

    boolean deleteIndex(String indexName);

    EsProduct create(String id);

    R.Page<EsProduct> search(String keyword, Integer fromPage, Integer size);
}
