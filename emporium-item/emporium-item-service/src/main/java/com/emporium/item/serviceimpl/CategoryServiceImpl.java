package com.emporium.item.serviceimpl;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.item.mapper.CategoryMapper;
import com.emporium.item.pojo.Category;
import com.emporium.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    //注入dao
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> queryCategoryListByPid(Long pid) {
        //根据通用mapper,绑定parentid
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(category);
        //        //判断lists是否为空
        if(CollectionUtils.isEmpty(categoryList)){
            throw new EpException(EnumsStatus.CATAGORY_IS_NOT_FOUND);
        }
        return categoryList;
    }



    @Override
    public List<Category> queryByBrandId(Long bid) {
        List<Category> list = categoryMapper.queryByBrandId(bid);
        //判断list是否查到
        if(CollectionUtils.isEmpty(list)){
            throw new EpException(EnumsStatus.CATAGORY_IS_NOT_FOUND);
        }
        return list;
    }

    /*
        搜索服务调用，根据多个cid查询分类
    * */
    @Override
    public List<Category> queryCategorybyIds(List<Long> ids) {
        //根据ids查询商品分类
        List<Category> list = categoryMapper.selectByIdList(ids);
        return list;
    }
    /*
    * 新增会和修改在一起所以注意
     */
    @Override
    public void insertCategory(Category category) {
        //新增,主键自增，所以新增一个节点需要将id设置为null
        category.setId(null);
        categoryMapper.insert(category);
        //新增后需要将原来的节点设置为父节点
        Category parent = new Category();
        //理清表关系，需要将原来的父节点设置，将自己改为父节点，利用修改功能
        //原来的的节点成为父节点，且id是所插入节点的父id
        /*
         * 子节点parentid 必然是父节点的id
         */
        parent.setId(category.getParentId());
        parent.setIsParent(true);
        //更新节点
        categoryMapper.updateByPrimaryKeySelective(parent);

    }

    @Override
    public void updateCategory(Category category) {
        this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    @Override
    public void deleteCategoryById(long id) {
        /**
         * 如果是父节点，那么删除所有附带子节点，然后要维护中间表。如果是子节点，那么只删除自己，
         * 然后判断父节点孩子的个数，如果不为0，则不做任何修改；如果为0，则修改父节点isParent的值为false，最后维护中间表。
         */
        //
        Category category = categoryMapper.selectByPrimaryKey(id);
        //1.看删除的节点是否是父节点
        if (category.getIsParent()){
            //1.查找所有叶子节点
            List<Category> list = new ArrayList<>();
            //树的结构就是list(childList),思路是查出所有叶子节点(没有子节点的节点)
            queryAllLeafNodes(category,list);
            //2.查找出所有节点
            ArrayList<Category> list2 = new ArrayList<>();
            queryAllNode(category,list2);
            //因为我们封装的list2是所有子节点，而list,是叶子节点
            for (Category c : list2) {
                //
                categoryMapper.delete(c);

            }
            //维护中间表,[category_id]分类和品牌，一个分类对应多个品牌，一个品牌对应多个分类
            //删除一个父分类(包含子节点),category_id中保存的是最底一层的类目id，[因为1对多的关系，一个category_id-->对应多个
            // brand_id]也就是分类的叶子节点id，例如图书下有电子书，电子书下有作者， 而每个作者category_id-->对应多个品牌代言，所以
            //删除而不应该遍历list2,二十遍历list
            for (Category c : list) {
                categoryMapper.deleteByCategoryIdInCategoryBrand(c.getId());

            }

        }else {
            //1.查询此节点的父亲节点的孩子个数==>查询还有几个兄弟节点
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("parentId",category.getId());
            List<Category> list = categoryMapper.selectByExample(example);
            if (list.size()!=1){
                //有兄弟节点，直接删除自己
                categoryMapper.deleteByPrimaryKey(category.getId());
                //维护中间表(自己维护因为没有外键关联)
                categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }else {
                //没有兄弟节点直接根据id删除
                categoryMapper.deleteByPrimaryKey(category.getId());
                //更改本节点为不是父节点
                Category parent = new Category();
                parent.setId(category.getParentId());
                parent.setIsParent(false);

                categoryMapper.updateByPrimaryKeySelective(parent);
                //维护中间表(自己维护因为没有外键关联)
                categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }

        }
    }

    @Override
    public List<Category> queryLastId() {
        List<Category> list = categoryMapper.queryLastId();
        if (CollectionUtils.isEmpty(list)){
            throw new EpException(EnumsStatus.CATAGORY_IS_NOT_FOUND);
        }
        return list;
    }

    /*
    * 注意理解，我们封装的子节点，包含本身的节点，也就是说把一个节点包含自己还有它的子节点都放入到node中
    * 这样的好处是为了本身节点的所有节点
     */
    private void queryAllNode(Category category, ArrayList<Category> node) {
        node.add(category);
        Example example = new Example(Category.class);
        //这里是查出所有子节点，select * from tb_category where parent_id = ?
        example.createCriteria().andEqualTo("parentId",category.getId());
        List<Category> list = categoryMapper.selectByExample(example);
        //依然是递归
        for (Category category1 : list) {
            queryAllNode(category1,node);

        }

    }

    /*
    * 查出该节点下所有叶子节点用于维护中间表tb_category_brand,
    * */
    private void queryAllLeafNodes(Category category, List<Category> leafNode) {
        //递归循环，直到遍历到叶子节点，判断后，是叶子节点的，装到我们定义好的list中
        if (!category.getIsParent()){
            //如果该节点本身不是父节点
            leafNode.add(category);
        }
        //叶子节点必然有一个父节点
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId",category.getId());
        List<Category> list = categoryMapper.selectByExample(example);
        //下面使用递归的思想，因为不知道叶子节点在第几层,会一直循环，
        for (Category category1 : list) {
            queryAllLeafNodes(category1,leafNode);
        }
    }


}
