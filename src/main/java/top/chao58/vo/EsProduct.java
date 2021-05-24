package top.chao58.vo;

import lombok.Data;

@Data
public class EsProduct {

    // 文档id
    private Long id;
    // 产品序号 keyword
    private String productSn;
    // 品牌名称 keyword
    private String brandName;
    // 品牌分类名称 keyword
    private String productCategoryName;
    // 商品名称 ik_max_word
    private String name;
    // 商品标题 ik_max_word
    private String subTitle;
    // 商品关键词 ik_max_word
    private String keywords;


}
