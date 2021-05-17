package com.emporium.item.serviceimpl;

import com.emporium.common.dto.CartDto;
import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.vo.PageResult;
import com.emporium.item.mapper.SkuMapper;
import com.emporium.item.mapper.SpuDetailMapper;
import com.emporium.item.mapper.SpuMapper;
import com.emporium.item.mapper.StockMapper;
import com.emporium.item.pojo.*;
import com.emporium.item.service.BrandService;
import com.emporium.item.service.CategoryService;
import com.emporium.item.service.MerchandiseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MerchandiseServiceImpl implements MerchandiseService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 搜索字段过滤
        if(StringUtils.isNotBlank(key)){
            // 根据哪个字段过滤，看表， 主要以`title`
            criteria.andLike("title","%"+key+"%");
        }
        // 上下架过滤
        if (null != saleable){
            criteria.andEqualTo("saleable",saleable);
        }
        // 默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)){
            throw  new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        /*
        *   需要解析查询分类和品牌名称，单独写个方法
        * */
        decipherCategoryAndBrandName(spus);
        //注意分页业务逻辑，这里不能直接返回，而是要把数据包装城分页数据
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //mybatis 分页信息里有个total, 和 items
        return new PageResult<>(pageInfo.getTotal(),spus);
    }
    /*
    *  需要保存哪些
    *  考虑到新增商品，for 遍历
    * */

    @Transactional
    public void saveCommodity(Spu spu) {
        //新增spu 需要处理的字段
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(new Date());
        spu.setSaleable(true);
        spu.setValid(false);

        int count = spuMapper.insert(spu);
        if (count == 0){
            throw new EpException(EnumsStatus.MERCHANDISE_SAVE_FALIURE);
        }
        //新增detail
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        spuDetailMapper.insert(detail);
        //新增sku and stock
        saveSkuAndStock(spu);

        amqpTemplate.convertAndSend("item.insert",spu.getId());


    }
    // ctrl alt + M 抽取方法
    private void saveSkuAndStock(Spu spu) {
        int count;//定义一个库存集合
        List<Stock> stockList = new ArrayList<>();
        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            //批量新增不能返回id, 或者批量新增sku,然后再查出sku_id
            sku.setSpuId(spu.getId());

            count = skuMapper.insert(sku);
            if(count ==0){
                throw new EpException(EnumsStatus.MERCHANDISE_SAVE_FALIURE);
            }

            //新增库存,需要有sku
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            stockList.add(stock);
            /*在一个for循环里新增对数据库独写操作不合适
            count = stockMapper.insert(stock);
            if (count == 0){
                throw new EpException(EnumsStatus.MERCHANDISE_SAVE_FALIURE);
            }*/
        }
        //批量查询idListMapper 删除，采用批量新增, insertListMapper
        count = stockMapper.insertList(stockList);
        if(count != stockList.size()){
            throw new EpException(EnumsStatus.MERCHANDISE_SAVE_FALIURE);
        }
    }

    /*
    * 查询商品详情商品
    * */
    @Override
    public SpuDetail queryCommodityDetailById(Long id) {
        SpuDetail detail = spuDetailMapper.selectByPrimaryKey(id);
        if (null == detail){
            throw new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        return detail;
    }
    /*根据spu id 查询所有sku,还需要stock信息
    一个spu--多个sku
    * */
    @Override
    public List<Sku> querySkuBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        //根据spu_id查询出所有sku列表
        List<Sku> skusList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skusList)){
            throw new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        /*需要将库存信息stock查询出来*/
        /*for (Sku skus : skusList) {
            Stock stock = stockMapper.selectByPrimaryKey(skus.getId());
            if(null == stock){
                throw new EpException(EnumsStatus.MERCHANDISE_SAVE_FALIURE);
            }
            skus.setStock(stock.getStock());
        }*/
        // sku_id 是 stock id, 通过拿到所有sku_id, 批量查询stocklist,然后
        List<Long> ids = skusList.stream().map(Sku::getId).collect(Collectors.toList());
        queryStockInSkus(ids, skusList);
        return skusList;
    }
    /* 查询回显后需要提交
    spu数据可以修改，但是SKU数据无法修改，因为有可能之前存在的SKU现在已经不存在了，或者以前的sku属性都不存在了。比如以前内存有4G，现在没了。
    * */
    @Transactional
    public void updateCommodity(Spu spu) {
        if(null == spu.getId()){
            throw new EpException(EnumsStatus.MERCHANDISE_ID_CANNOT_BE_NULL);
        }
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //删除sku,stock
        List<Sku> skuList = skuMapper.select(sku);
        //如果商品sku集合不为空删除
        if(!CollectionUtils.isEmpty(skuList)){
            skuMapper.delete(sku);
            //删除stock 注意，先使用stream 拿到Stock 的key
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            //然后批量删除
            stockMapper.deleteByIdList(ids);
        }
        //修改spu , 页面没有的字段需要补全
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        //修改detail
       int count =  spuMapper.updateByPrimaryKeySelective(spu);
       if( count == 0){
           throw new EpException(EnumsStatus.MERCHANDISE_UPDATE_FAILED);
       }
       //修改detail
        count = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if( count == 0){
            throw new EpException(EnumsStatus.MERCHANDISE_UPDATE_FAILED);
        }
        //新增sku, stock,可以抽取新增里面的方法
        saveSkuAndStock(spu);

        //修改----发送消息(spu携带大量信息，amqp)
        amqpTemplate.convertAndSend("item.update",spu.getId());

    }

    @Override
    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(null == spu){
            throw new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        //考虑到Spu -->包含sku,detail---->所以再返回spu前set
        //查询sku---之前已经有接口调用
        spu.setSkus(querySkuBySpuId(id));
        //detail
        spu.setSpuDetail(queryCommodityDetailById(id));

        return spu;
    }

    @Override
    public List<Sku> querySkuBySpuIds(List<Long> ids) {
        List<Sku> skus = skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)){
            throw new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        //查询库存 ctl+alt+m
        queryStockInSkus(ids, skus);
        //最后返回skus
        return skus;
    }
    /*
    * 减库存，直接synchronize,只有一个线程，会极大降低效率
    * zookeeper,有分布式锁的功能--加锁的功能，会锁数据库，执行完，数据库锁才释放
    * */
    @Transactional
    public void decreaseStock(List<CartDto> carts) {
        for (CartDto cart : carts) {
            //在数据库内部sql,进行判断,减逻辑在sql里实现
            int count = stockMapper.decreaseStock(cart.getSkuId(), cart.getNum());
            if(count !=1){
                throw new EpException(EnumsStatus.STOCK_NOT_AFFLUENT);
            }
            //库存需要先查出来--一般逻辑

            //判断库存是否充足--if直接判断，会有线程安全 如3个人同时，库存这里判断，3个都判断都大于库存所这里
        }
    }

    @Override
    public Sku querySkuById(Long id) {
        Sku sku = skuMapper.selectByPrimaryKey(id);
        if(null == sku){
            throw new EpException(EnumsStatus.MERCHANDISE_IS_NOT_FOUND);
        }
        return sku;
    }

    //查询库存
    private void queryStockInSkus(List<Long> ids, List<Sku> skus) {
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new EpException(EnumsStatus.MERCHANDISE_STOCK_NOT_FOUND);
        }

        //map  key=sku_id value
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        //Java8 forEach 遍历List + Lambda 查出库存，然后在sku里面注入，注意因为sku表里没有stock 而是Transient,不持久化到数据库的
        /*  流的概念，课程拓展 webflux, 是reactor 框架主流
         * */
        skus.forEach(s -> s.setStock(stockMap.get(s.getId())));
    }

    /*
    *  先要遍历，分类名称，品牌名称
    *  分类名称---数据库3级分类
    * */
    private void decipherCategoryAndBrandName(List<Spu> spus) {
        //处理分类名称,这里查三级分类
        // 所以先需要借助categoryService查询出商品分类列表,因为是一堆id
        //再进行遍历拿到分类名称 Java 8 Stream Map（lambda）stream().map(o -> o.getName())
        //以下代码含义：categoryService.queryByIds(三级分类)全查出来的分类，然后根据stream map->去除所有三级分类的名称
        for (Spu spu : spus) {
            //处理分类名称
            List<String> names = categoryService.queryCategorybyIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            //然后将集合变为字符串
            spu.setCname(StringUtils.join(names,"/"));
            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }

    /*@GetMapping("/spu/detail/{id}")*/
}
