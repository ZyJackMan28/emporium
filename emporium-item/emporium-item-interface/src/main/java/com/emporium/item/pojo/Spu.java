package com.emporium.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    //在页面不展示
    @JsonIgnore
    private Date lastUpdateTime;// 最后修改时间

	//考虑到VO 业务处理麻烦，这里添加页面需要的两个字段,添加注解通用mapper不进入数据库持久化
    @Transient
    private String cname;
    @Transient
    private String bname;

    /*前台请求的参数是json*/
    @Transient
    private List<Sku> skus;

    @Transient
    private SpuDetail spuDetail;
}