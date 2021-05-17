package com.emporium.pojo;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Table(name ="tb_rush")
@Data
public class RushMerchandise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long skuId;
    private Date startTime;
    private Date endTime;
    private Double rushPrice;
    private String title;
    private String image;
    private Boolean enable;

    @Transient
    private Integer stock;
    @Transient
    private Integer seckillTotal;
}
