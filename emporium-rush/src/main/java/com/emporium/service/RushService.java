package com.emporium.service;

import com.emporium.client.AddressClient;
import com.emporium.client.MerchandiseClient;
import com.emporium.client.OrderClient;
import com.emporium.mapper.SkuMapper;
import com.emporium.mapper.StockMapper;
import com.emporium.pojo.Order;
import com.emporium.item.pojo.Sku;
import com.emporium.item.pojo.Stock;
import com.emporium.mapper.SeckillMapper;
import com.emporium.pojo.OrderDetail;
import com.emporium.pojo.RushMerchandise;
import com.emporium.pojo.SeckillParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RushService {

    @Autowired
    private MerchandiseClient merchandiseClient;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private SkuMapper skuMapper;


    /**
     * 添加抢购商品
     *
     */
    @Transactional
    public void addSeckillGoods(SeckillParameter seckillParameter) {
        //抢购时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        seckillParameter.setStartTime(calendar.getTime());
        calendar.add(Calendar.HOUR,1);
        seckillParameter.setEndTime(calendar.getTime());

        //根据spu_id查询商品
        Long id = seckillParameter.getId();
        Sku sku = merchandiseClient.querySkuById(id);
        //插到将其放入抢购商品中
        RushMerchandise rushMerchandise = new RushMerchandise();
        rushMerchandise.setId(null);
        rushMerchandise.setEnable(true);
        rushMerchandise.setStartTime(seckillParameter.getStartTime());
        rushMerchandise.setEndTime(seckillParameter.getEndTime());
        rushMerchandise.setImage(sku.getImages());
        rushMerchandise.setSkuId(sku.getId());
        rushMerchandise.setTitle(sku.getTitle());
        rushMerchandise.setStock(seckillParameter.getCount());
        rushMerchandise.setRushPrice(sku.getPrice()*seckillParameter.getDiscount());
        this.seckillMapper.insert(rushMerchandise);

        //3.更新对应的库存信息，tb_stock
        Stock stockp = stockMapper.selectByPrimaryKey(sku.getId());
        Integer old_seckillStock = stockp.getSeckillStock();
        Integer count = seckillParameter.getCount();

        stockp.setSeckillStock(old_seckillStock + count);
        stockp.setSeckillTotal(old_seckillStock + count);
        stockp.setStock(stockp.getStock() - seckillParameter.getCount());
        this.stockMapper.updateByPrimaryKeySelective(stockp);
    }

    public List<RushMerchandise> queryRushMerchandise() {
        Example example = new Example(RushMerchandise.class);
        example.createCriteria().andEqualTo("enable",true);
        return this.seckillMapper.selectByExample(example);
    }

    //秒杀订单
    //需要封装订单对象
    public Long createOrder(RushMerchandise rushMerchandise) {
        Order order = new Order();
        order.setPaymentType(1);
        order.setTotalPay(rushMerchandise.getRushPrice().longValue());
        order.setActualPay(rushMerchandise.getRushPrice().longValue());
        order.setPostFee(0L);
        order.setReceiver("marry");
        order.setReceiverMobile("15812312312");
        order.setReceiverCity("西安");
        order.setReceiverDistrict("碑林区");
        order.setReceiverState("陕西");
        order.setReceiverZip("000000000");
        order.setInvoiceType(0);
        order.setSourceType(2);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setSkuId(rushMerchandise.getSkuId());
        orderDetail.setNum(1);
        orderDetail.setTitle(rushMerchandise.getTitle());
        orderDetail.setImage(rushMerchandise.getImage());
        orderDetail.setPrice(rushMerchandise.getRushPrice().longValue());
        orderDetail.setOwnSpec(this.skuMapper.selectByPrimaryKey(rushMerchandise.getSkuId()).getOwnSpec());

        order.setOrderDetails(Arrays.asList(orderDetail));


        String seck = "seckill";
        ResponseEntity<List<Long>> responseEntity = this.orderClient.createOrder(seck,null);


        if (responseEntity.getStatusCode() == HttpStatus.OK){
            //库存不足，返回null
            return null;
        }
        //修改秒杀商品的库存

        return responseEntity.getBody().get(0);

    }
}
