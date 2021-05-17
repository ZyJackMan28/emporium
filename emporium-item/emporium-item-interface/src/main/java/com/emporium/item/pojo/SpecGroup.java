package com.emporium.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "tb_spec_group")
@Data
public class SpecGroup {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    //商品分类id
    private Long cid;

    private  String name;

    //页面需要规格参数--规格参数组
    @Transient
    private List<SpecParam> params;
}
