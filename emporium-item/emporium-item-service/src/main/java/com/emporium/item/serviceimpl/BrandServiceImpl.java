package com.emporium.item.serviceimpl;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.vo.PageResult;
import com.emporium.item.mapper.BrandMapper;
import com.emporium.item.pojo.Brand;
import com.emporium.item.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;


    @Override
    public PageResult<Brand> queryBrandByKey(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //以前分页，需要手写sql,进行，pagehelper已经帮我们写好分页功能
        //分页查询，分页助手已经帮我们拼接好limit
        PageHelper.startPage(page,rows);
        //条件过滤,key 是用户随便写，根据哪个字段 sql select * from tb_brand where 'name' like '%华为%' or "letter = 'xxx' order by id desc;
        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            //根据名称查询或者字母查询,数据目前都是大写
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        //排序,排序的关键字
        if(StringUtils.isNotBlank(sortBy)){
            //字段, 有升序，降序，这里用三元运算符拼接
            String orderbyKey = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderbyKey);
        }
        //查询, Page<Brand> page 实际就是list
        List<Brand> list = brandMapper.selectByExample(example);
        //查询一定有值吗
        if(CollectionUtils.isEmpty(list)){
            throw new EpException(EnumsStatus.BRAND_IS_NOT_FOUND);
        }
        //解析分页结果
        PageInfo<Brand> pageList = new PageInfo<>(list);
        PageResult<Brand> brandPageResult = new PageResult<>(pageList.getTotal(), list);
        //这里不是返回list,而是pageResult对象里面还有两个成员，分页助手查询已经包含了总条数。分页助手继承了ArrayList<E>
        return brandPageResult;
    }

    @Transactional
    public void insertBrand(Brand brand, List<Long> cids) {
        //insertSelective 是剔除空的, 注意brand id是自增长，所以我们最好设置为null, 不是我们的字段
        brand.setId(null);
        int count = brandMapper.insert(brand);
        // count为0，新增失败。count，新增完成id自动回显
        if(count == 0){
            throw new EpException(EnumsStatus.BRAND_IS_INSERT_FAILED);
        }
        //新增中间表 而中间表没有实体类 cids是多个，不是一个 中间表只有两个字段,需要自己写sql，可以利用mybatis注解的方式
        /*
        *  insert into tb_category_brand values(1,2)
        * */
        for (Long cid : cids) {
            //根据mybatis注解,注意虽然brand id被置为null,但会自动回显
            Integer insertCount = brandMapper.insertCategoryBrand(cid, brand.getId());
            if(insertCount == 0){
                throw new EpException(EnumsStatus.BRAND_CATEGORY_IS_INSERT_FAILED);
            }
        }
    }

    @Override
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (null==brand){
            throw new EpException(EnumsStatus.BRAND_IS_NOT_FOUND);
        }
        return brand;
    }

    /*
    * 根据品牌id 查询商品品牌列表，但注意cid1,cid2,cid3 前台需要的是id
    * 查当前分类下的所有品牌
    * category --brand_category  --brand
    * 关联查询,关联brand_id ,因为品牌表里面没有cid
    * select * from tb_brand where cid = ? 但没有cid，需要join inner join (表中至少有一个匹配)
    * select b.id, b.name,b.letter from tb_brand b inner join tb_category_brand cb on b.id = cb.brand_id where cb.category_id = ?
    *
    * 如果不用mapper 可以在配置文件里写sql,在配置文件里面查找
    * */
    @Override
    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list = brandMapper.queryBrandByCid(cid);
        if(CollectionUtils.isEmpty(list)){
            throw new EpException(EnumsStatus.BRAND_IS_NOT_FOUND);
        }
        return list;
    }

    @Override
    public List<Brand> queryBrandByIds(List<Long> ids) {
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(brands)){
            throw new EpException(EnumsStatus.BRAND_IS_NOT_FOUND);
        }
        return brands;
    }
}
