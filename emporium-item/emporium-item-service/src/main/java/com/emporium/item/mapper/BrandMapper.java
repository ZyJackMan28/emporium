package com.emporium.item.mapper;

import com.emporium.common.mapper.BaseMapper;
import com.emporium.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    //mybatis 注解
    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    Integer insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /*
    * left join(左联接) 返回包括左表中的所有记录和右表中联结字段相等的记录
　　right join(右联接) 返回包括右表中的所有记录和左表中联结字段相等的记录
　　inner join(等值连接) 只返回两个表中联结字段相等的行
    * */
    @Select("SELECT b.id, b.name,b.letter, b.image from tb_brand b INNER JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id = #{cid}")
    List<Brand> queryBrandByCid(@Param("cid") Long cid);
}
