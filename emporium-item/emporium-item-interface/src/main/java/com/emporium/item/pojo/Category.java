package com.emporium.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

//报错缺少依赖，通用mapper 依赖了JPA(操作数据库)
@Table(name="tb_category")
@Data
public class Category {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;
    // getter和setter略
    // 注意isParent的get和set方法
}
