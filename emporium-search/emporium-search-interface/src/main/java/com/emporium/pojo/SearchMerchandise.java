package com.emporium.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.Map;
import java.util.Set;
/*
* 数据库和索引库mapping,索引库名称是merchandise，表的名docs,也就是type,
* 如果不指定文档类型，则id是UUID
*
* */
@Data
@Document(indexName = "mechandises", type = "docs", shards = 1, replicas = 0)
public class SearchMerchandise {
    @Id
    private Long id; // spu_Id
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;// 卖点, 例如--好尽享，美在瞬间等展示给页面看
    private Long brandId;// 品牌id,因为有品牌id才能知道商品 --分类id可以不需要
    private Long cid1;// 1级分类id
    private Long cid2;// 2级分类id
    private Long cid3;// 3级分类id
    private Date createTime;// 创建时间
    //集合对应elastic 是数组，因为spu下多个sku,对应多个价格
    private Set<Long> price;// 价格
    @Field(type = FieldType.Keyword, index = false)
    //展示字段字段 （或者不写，在servcie查也可以），这里把结果放到json中，
    private String skus;// sku信息的json结构
    //规格参数 map 因为可以存档不确定参数 map序列化-->对象， elasticsearch 可以接收对象
    //{"机身内存":"6G"} ---> specs.机身内存.keyword："6G"
    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值 {内存: 4G}
}
