package com.emporium.search.repository;

import com.emporium.EpEsApplication;
import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Spu;
import com.emporium.pojo.SearchMerchandise;
import com.emporium.search.client.MerchandiseClient;
import com.emporium.search.service.EsearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EpEsApplication.class)
public class EsRepositoryTest {
    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private MerchandiseClient merchandiseClient;

    @Autowired
    private EsearchService esearchService;

    @Test
    public void testCreatDataIndex() {
        //创建索引
        elasticsearchTemplate.createIndex(SearchMerchandise.class);
        elasticsearchTemplate.putMapping(SearchMerchandise.class);
    }

    @Test
    public void loadData(){
        //创建玩映射索引，将数据库要被搜索的数据导入索引库
        Integer page = 1;
        Integer rows =100;
        Integer size = 0;
        //循环查询
        do{
            //默认全搜索key=null
            PageResult<Spu> result = merchandiseClient.querySpuByPage(page, rows, true, null);
            //得到分页信息，取分页内容
            List<Spu> spuList = result.getItems();
            if(CollectionUtils.isEmpty(spuList)){
                //查不到就跳出
                break;
            }
            //分装搜索商品
            List<SearchMerchandise> merchandises = spuList.stream().map(esearchService::buildMerchandise).collect(Collectors.toList());

            //存入索引库
            merchandiseRepository.saveAll(merchandises);

            page++;
            //如果当前页100，相当于没页查满
            size = spuList.size();
        } while (size ==100);

        //因为测试结束了,Shutdown in progress 释放资源,导致控制台报关闭feign
    }
}