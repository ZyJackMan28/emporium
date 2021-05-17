package com.emporium.item.mapper;

import com.emporium.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

//继承通用mapper PK=Primary Key
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {
    /*
    借助中间表查到where条件category_id
* */
    @Select("SELECT * FROM tb_category where id in (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);
    @Delete("DELETE FROM tb_category_brand WHERE category_id=#{cid}")
    void deleteByCategoryIdInCategoryBrand(Long cid);

    @Select("SELECT * FROM tb_category WHERE id = (SELECT MAX(id) FROM tb_category)")
    List<Category> queryLastId();
}
