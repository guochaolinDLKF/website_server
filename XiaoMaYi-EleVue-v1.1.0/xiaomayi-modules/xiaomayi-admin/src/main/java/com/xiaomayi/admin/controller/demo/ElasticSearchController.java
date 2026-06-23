//package com.xiaomayi.admin.controller.demo;
//
//import co.elastic.clients.elasticsearch.core.DeleteResponse;
//import co.elastic.clients.elasticsearch.core.GetResponse;
//import co.elastic.clients.elasticsearch.core.IndexResponse;
//import co.elastic.clients.elasticsearch.core.UpdateResponse;
//import co.elastic.clients.transport.endpoints.BooleanResponse;
//import com.alibaba.fastjson2.JSON;
//import com.xiaomayi.core.utils.R;
//import com.xiaomayi.elasticsearch.dto.ESQueryDTO;
//import com.xiaomayi.elasticsearch.utils.ElasticClientUtils;
//import com.xiaomayi.system.entity.Level;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * <p>
// * ES搜索引擎 前端控制器
// * </p>
// *
// * @author 小蚂蚁云团队
// * @since 2024-05-26
// */
//@Slf4j
//@RestController
//@RequestMapping("/elastic")
//@AllArgsConstructor
//public class ElasticSearchController {
//
//    private final ElasticClientUtils elasticClientUtils;
//
//    /**
//     * 创建索引
//     *
//     * @param indexName 索引名称
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @GetMapping("/createIndex/{indexName}")
//    public R createIndex(@PathVariable String indexName) throws IOException {
//        boolean result = elasticClientUtils.createIndex(indexName);
//        return R.ok(result);
//    }
//
//    /**
//     * 查询索引
//     *
//     * @param indexName 索引名称
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @GetMapping("/indexExists/{indexName}")
//    public R indexExists(@PathVariable String indexName) throws IOException {
//        boolean result = elasticClientUtils.indexExists(indexName);
//        return R.ok(result);
//    }
//
//    /**
//     * 删除索引
//     *
//     * @param indexName 索引名称
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @DeleteMapping("/deleteIndex/{indexName}")
//    public R deleteIndex(@PathVariable String indexName) throws IOException {
//        boolean result = elasticClientUtils.deleteIndex(indexName);
//        return R.ok(result);
//    }
//
//    /**
//     * 创建文档
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @PostMapping("/addDocument")
//    public R addDocument() throws IOException {
//        // 模拟写入数据
//        Level level = new Level();
//        level.setName("测试ES文档写入");
//        level.setStatus(1);
//        level.setSort(1);
//        level.setCreateUser("admin");
//        level.setUpdateUser("admin");
//        IndexResponse indexResponse = elasticClientUtils.addDocument("xm-example", "XM00001", level);
//        return R.ok(indexResponse.result());
//    }
//
//    /**
//     * 查询文档内容
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @GetMapping("/getDocument")
//    public R getDocument() throws IOException {
//        GetResponse response = elasticClientUtils.getDocument("xm-example", "XM00001", Level.class);
//        return R.ok(JSON.toJSONString(response.source()));
//    }
//
//    /**
//     * 检查文档是否存在
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @GetMapping("/existDocument")
//    public R existDocument() throws IOException {
//        BooleanResponse response = elasticClientUtils.existDocument("xm-example", "XM00001");
//        return R.ok(response.value());
//    }
//
//    /**
//     * 更新文档
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @PostMapping("/updateDocument")
//    public R updateDocument() throws IOException {
//        GetResponse response = elasticClientUtils.getDocument("xm-example", "XM00001", Level.class);
//        Level level = (Level) response.source();
//        level.setName("文档标题更新");
//        level.setStatus(2);
//        level.setSort(2);
//        UpdateResponse updateResponse = elasticClientUtils.updateDocument("xm-example", "XM00001", level, Level.class);
//        return R.ok(updateResponse.result());
//    }
//
//    /**
//     * 删除文档
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @DeleteMapping("/deleteDocument")
//    public R deleteDocument() throws IOException {
//        DeleteResponse deleteResponse = elasticClientUtils.deleteDocument("xm-example", "XM00001");
//        return R.ok(deleteResponse.result());
//    }
//
//    /**
//     * 批量写入文档
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @PostMapping("/batchAddDocuments")
//    public R batchAddDocuments() throws IOException {
//        List<Level> levelList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            // 模拟写入数据
//            Level level = new Level();
//            level.setName("文档" + i);
//            level.setStatus(1);
//            level.setSort(0);
//            level.setCreateUser("admin");
//            level.setUpdateUser("admin");
//            // 加入列表
//            levelList.add(level);
//        }
//        elasticClientUtils.batchAddDocuments(levelList, "xm-example");
//        return R.ok();
//    }
//
//    /**
//     * 批量删除文档
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @DeleteMapping("/bulkDeleteDocuments")
//    public R bulkDeleteDocuments() throws IOException {
//        List<String> documentIds = new ArrayList<>();
//        documentIds.add("XM00001");
//        boolean result = elasticClientUtils.bulkDeleteDocuments("xm-example", documentIds);
//        return R.ok(result);
//    }
//
//    /**
//     * 获取分页数据
//     *
//     * @return 返回结果
//     * @throws IOException 异常处理
//     */
//    @GetMapping("/getDocumentPage")
//    public R getDocumentPage() throws IOException {
//        ESQueryDTO esQueryDTO = new ESQueryDTO();
//        esQueryDTO.setIndexName("xm-example");
//        esQueryDTO.setField("name");
//        esQueryDTO.setWord("文档");
//        esQueryDTO.setIndex(0);
//        esQueryDTO.setSize(10);
//        esQueryDTO.setOrder("sort");
//        List<Level> levelList = elasticClientUtils.getDocumentPage(esQueryDTO, Level.class);
//        return R.ok(levelList);
//    }
//
//}
