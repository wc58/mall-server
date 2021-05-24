package top.chao58.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chao58.service.EsProductService;
import top.chao58.util.R;
import top.chao58.vo.EsProduct;

import java.util.List;

@Api(tags = "ElasticSearch服务")
@RequestMapping("/es")
@RestController
public class EsProductController {

    @Autowired
    private EsProductService esProductService;


    @ApiOperation(value = "批量导入", notes = "将数据库中的数据批量导入ElasticSearch中")
    @GetMapping("/import")
    public R importALl() {
        esProductService.importALl();
        return R.ok();
    }

    @ApiOperation(value = "删除指定文档")
    @DeleteMapping("/deleteById")
    public R deleteById(@Parameter(description = "文档id", required = true) @RequestParam String id) {
        Integer result = esProductService.delete(id);
        if (result.equals(1)) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "删除指定多个文档")
    @DeleteMapping("/batchDeleteIds")
    public R batchDeleteIds(@Parameter(description = "多个文档id", required = true) @RequestParam List<String> ids) {
        Integer result = esProductService.delete(ids);
        if (result.equals(ids.size())) {
            return R.ok();
        } else {
            return R.error().setMessage("删除文档数量和指定数量不一致");
        }
    }

    @ApiOperation(value = "删除索引")
    @DeleteMapping("/deleteIndex")
    public R deleteIndex(@Parameter(description = "索引名") @RequestParam String indexName) {
        boolean result = esProductService.deleteIndex(indexName);
        if (result) {
            return R.ok();
        } else {
            return R.error().setMessage("索引不存在，或删除失败");
        }
    }

    @ApiOperation(value = "添加指定文档", notes = "根据id，从数据库添加ElasticSearch中")
    @PostMapping("/createById")
    public R createById(@Parameter(description = "文档id", required = true) @RequestParam String id) {
        EsProduct esProduct = esProductService.create(id);
        if (esProduct != null) {
            return R.ok().put("esProduct", esProduct);
        } else {
            return R.error().setMessage("添加文档失败");
        }
    }


    @ApiOperation(value = "搜索文档", notes = "根据关键字搜索文档")
    @GetMapping("/search")
    public R search(@Parameter(description = "关键字", required = true) @RequestParam String keyword,
                    @Parameter(description = "起始数") @RequestParam(required = false, defaultValue = "1") Integer fromPage,
                    @Parameter(description = "查询数") @RequestParam(required = false, defaultValue = "5") Integer size) {
        R.Page<EsProduct> page = esProductService.search(keyword, fromPage, size);
        if (page != null) {
            return R.ok().page(page);
        } else {
            return R.error().setMessage("查询失败");
        }
    }


}
