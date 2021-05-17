package com.emporium.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_spec_param")
@Data
public class SpecParam {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    /*加column，在select `numeric` 而不是select numeric在sql避免与sql出现字段重复，从而避免歧义*/
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    //如果写城boolean searching字段就被忽略了
    private Boolean searching;
    private String segments;
}
