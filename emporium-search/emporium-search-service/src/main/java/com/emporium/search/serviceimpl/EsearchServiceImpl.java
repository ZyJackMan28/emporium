package com.emporium.search.serviceimpl;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.utils.JsonUtils;
import com.emporium.common.utils.NumberUtils;
import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.*;
import com.emporium.pojo.SearchMerchandise;
import com.emporium.pojo.SearchRequest;
import com.emporium.pojo.SearchResult;
import com.emporium.search.client.BrandClient;
import com.emporium.search.client.CategoryClient;
import com.emporium.search.client.MerchandiseClient;
import com.emporium.search.client.SpecificationClient;
import com.emporium.search.repository.MerchandiseRepository;
import com.emporium.search.service.EsearchService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class EsearchServiceImpl implements EsearchService {

    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private MerchandiseClient merchandiseClient;
    @Autowired
    private MerchandiseRepository merchandiseRepository;
    @Autowired
    private ElasticsearchTemplate template;

    //数据同步到索引库，商品第一次被搜索走这个
    @Override
    public SearchMerchandise buildMerchandise(Spu spu) {
        //抽取，spuid, var
        Long spuId = spu.getId();
        //查询出分类，因为本身异常已经处理过
        List<Category> categoryList = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //拿分类名称
        List<String> categoryNames = categoryList.stream().map(Category::getName).collect(Collectors.toList());
        //查询出品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (null == brand) {
            throw new EpException(EnumsStatus.BRAND_IS_NOT_FOUND);
        }
        //查询出
        //1. 处理搜索字段  进行拼接品牌，分类，规格
        String all = spu.getTitle() + StringUtils.join(categoryNames, " ") + brand.getName();
        //查询skus
        List<Sku> skusList = merchandiseClient.querySkuBySpuId(spuId);
        if (CollectionUtils.isEmpty(skusList)) {
            throw new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        //查询出skuslist 不是最终目的，需要聚合api,取需要的
        // Set<Long> priceSet = skusList.stream().map(Sku::getPrice).collect(Collectors.toSet());
        //处理skus json 因为直接sku字段太多，可以自己用一个map只使用部分属性， 对象是key-value   map key-value (key不固定)
        List<Map<String, Object>> skus = new ArrayList<>();
        //因为流处理，在=或在循环里任意使用一个，但处理sku里面要获得价格，所以减少一个流处理
        Set<Long> priceSet = new HashSet<>();
        for (Sku sku : skusList) {

            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //每个sku只需要一个图片，因为是以，分割，提前判断是否为空指针，

            map.put("images", StringUtils.substringBefore(sku.getImages(), ","));

            skus.add(map);
            //处理价格
            priceSet.add(sku.getPrice());
        }

        //处理规格参数，规格参数是一个map key-value
        //查询规格参数,我们只要绑定的cid3--商品分类（一个分类下多个）这里的paramskey在json里面都是key
        List<SpecParam> params = specificationClient.queryParam(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new EpException(EnumsStatus.SPEC_PARAM_IS_NOT_FOUND);
        }
        //查询商品详情 value
        SpuDetail spuDetail = merchandiseClient.queryCommodityDetailById(spuId);
        //重新按照新数据库来处理规格参数，两个json格式转换成我们需要的对象
        //通用规格参数
        Map<String, String> genericSpecs = JsonUtils.toMap(spuDetail.getGenericSpec(), String.class, String.class);
        //特殊规格参数
        Map<String, List<String>> specialSpecs = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<String>>>() {
        });
        //用来存储规格参数的key  和 value
        Map<String, Object> specs = new HashMap<>();
       params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值,因为get(里面是String,而我们表里面只是Long)
                String value = genericSpecs.get(param.getId().toString());
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                specs.put(param.getName(), value);
            } else {
                specs.put(param.getName(), specialSpecs.get(param.getId().toString()));
            }
        });
        //遍历params
     /*   for (SpecParam param : params) {
            String key = param.getName();
            Object value = "";
            if (param.getGeneric()) {
                value = genericSpecs.get(param.getId().toString());
                if (param.getNumeric()) {
                    //如果是数值，处理段
                    value = chooseSegment(value.toString(), param);
                }

            } else {
                value = specialSpecs.get(param.getId().toString());

            }
            specs.put(key, value);
        }*/
        /*//通用规格参数,json格式是一个数组，数组里有两个参数 即组-参数 group  params[] 这两个
        //获取通用规格参数,将json 转成对象
       List<Map<String, Object>> maps = JsonUtils.nativeRead(spuDetail.getGenericSpec(), new TypeReference<List<Map<String, Object>>>() {
        });
        //用map存储可以搜索的属性
        Map<String, Object> specMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            List<Map<String,Object>>  paramsList = (ArrayList<Map<String, Object>>) map.get("params");
            for (Map<String, Object> pargeneramsMap : paramsList) {
                Boolean searchable = (Boolean) paramsMap.get("searchable");
                if (searchable){
                    if (paramsMap.get("v")!=null)
                        specMap.put((String) paramsMap.get("k"),paramsMap.get("v"));
                    else if (paramsMap.get("options")!=null)
                        specMap.put((String) paramsMap.get("k"),paramsMap.get("options"));
                }
            }
        }*/
            SearchMerchandise merchandise = new SearchMerchandise();
            merchandise.setBrandId(spu.getBrandId());
            merchandise.setCid1(spu.getCid1());
            merchandise.setCid2(spu.getCid2());
            merchandise.setCid3(spu.getCid3());
            merchandise.setCreateTime(spu.getCreateTime());
            merchandise.setId(spuId);
            merchandise.setAll(all); // 搜索字段 包含品牌/标题/分类/规格等
            merchandise.setPrice(priceSet);  //   sku价格集合
            merchandise.setSkus(JsonUtils.toString(skus));  // sku 集合 json  ,因为skuList里的sku属性太多了
            merchandise.setSpecs(specs); //  规格参数
            merchandise.setSubTitle(spu.getSubTitle());

            return merchandise;
        }

    /* 前台搜索分页功能
    * */
    public PageResult<SearchMerchandise> search(SearchRequest request) {
        String keywords = request.getKey();
        if(StringUtils.isBlank(keywords)){
            //如果没有搜索条件
            return null;
        }
        //用到分页，page size
        int page = request.getPage();
        int size = request.getSize();
        //用到es Api
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //对结果应_source过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        // i.分页
        queryBuilder.withPageable(PageRequest.of(page-1,size));
        // ii.过滤
        // 记录--即查询条件 + 继续加过滤条件
        //kibana 搜素条件和过滤条件不能放一块 query--bool--must--<查询条件> filter查询条件
        //QueryBuilder principal = QueryBuilders.matchQuery("all", keywords);
        QueryBuilder principal = buildprincipalQuery(request);
        queryBuilder.withQuery(principal);
        // iv. 再查询前，聚合品牌分类信息
        //聚合api,//聚合--先聚合品牌，再聚合分类
        String categoryaggname = "category_aggs";
        String brandaggname = "brand_aggs";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandaggname).field("brandId"));
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryaggname).field("cid3"));
        // iii.最后查询--再聚合处理后查询的结果是pageResult
        //Page<SearchMerchandise> searchResult = merchandiseRepository.search(queryBuilder.build());
        AggregatedPage<SearchMerchandise> result = template.queryForPage(queryBuilder.build(), SearchMerchandise.class);
        //将结果封装城pageResult 解析
        // i.分页结果
        //总条数
        long totalElements = result.getTotalElements();
        //总页数
        int totalPage = result.getTotalPages();
        //当前页内容
        List<SearchMerchandise> content = result.getContent();

        // ii.解析聚合结果
        Aggregations aggs = result.getAggregations();
        //单独写一个方法用来取聚合解析
        List<Category> categories = parseAggCategory(aggs.get(categoryaggname));
        List<Brand> brands = parseAggBrand(aggs.get(brandaggname));
        //至此，需要返回searchResult

        //List<Map<String, Object>> specs 完成规格参数聚合
        List<Map<String,Object>> specs = null;
        if(categories.size() == 1 && categories !=null){
            //商品分类存在且=1，可以聚合参数  N.B: 在原来搜索的基础上进行聚合
            specs = buildSpecificationAgg(categories.get(0).getId(),principal);
        }
        return new SearchResult(totalElements, totalPage,content,categories,brands,specs);

    }

    /*
    * 新增索引库（mq）
    * */
    @Override
    public void createOrUpdateIndexToeS(Long spuId) {
        //注意以下代码会出现异常，运行异常（此时不处理--spring捕获--acked不处理，回滚，重试）
        //查出spu
        Spu spu = merchandiseClient.querySpuById(spuId);
        //1.构建商品
        SearchMerchandise searchMerchandise = buildMerchandise(spu);
        //2.再存入索引库
        merchandiseRepository.save(searchMerchandise);
    }

    @Override
    public void deleteFromIndex(Long spuId) {
        //从repository
        merchandiseRepository.deleteById(spuId);
    }

    private QueryBuilder buildprincipalQuery(SearchRequest request) {
        //i.创建boolean查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));

        //过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key = "specs." + key+".keyword";
            }
            String value = entry.getValue();
            //分类和品牌   ---spec-->long和string
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }

        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder principal) {
        List<Map<String,Object>> specs = new ArrayList<>();
        //先知道对什么进行聚合，所以通过client查询出要聚合的规格参数，比如用户搜索手机后--点oppo-->
        List<SpecParam> params = specificationClient.queryParam(null, cid, true);
        //进行聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //带上查询条件
        queryBuilder.withQuery(principal);
        //然后聚合
        for (SpecParam param : params) {
            //要保证规格参数
            String name = param.getName();
            //聚合--field是按照什么字段聚合
           queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        //获取并解析聚合结果
        AggregatedPage<SearchMerchandise> result = template.queryForPage(queryBuilder.build(), SearchMerchandise.class);
        //parse aggs
        Aggregations aggs = result.getAggregations();
        //拿到所有聚合结果
        for (SpecParam param : params) {
            String name = param.getName();
            StringTerms terms = aggs.get(name);
            //terms里面必然有bucket,取里面的数据---待选项
            List<String> options = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            //因为map在页面的结构是
            /*
            * [
            *   {
            *       "key":"规格参数名"，
            *       "options":["规格参数值","规格参数值"]
            *   }
            * ]
            * */
            Map<String,Object> map = new HashMap<>();
            map.put("key",name);
            map.put("options",options);

            specs.add(map);
        }

        return specs;
    }

    //ctrl + H multiBucketAggregation里面有terms
    private List<Brand> parseAggBrand(LongTerms terms) {
        //stream聚合出list
        try {
            List<Long> ids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            //至此查询
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        }catch (Exception e){
            log.error("搜索服务查询品牌异常：",e);
            return null;
        }

    }

    //用Aggregation 子类拿到term bucket
    private List<Category> parseAggCategory(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            //至此查询
            List<Category> categories = categoryClient.queryCategoryByIds(ids);
            return categories;
        }catch (Exception e){
            log.error("搜索服务查询分类异常：",e);
            return null;
        }
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
