package com.emporium.item.web;

import com.emporium.item.pojo.Category;
import com.emporium.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    /*
     *  注意一旦你新增这个节点改节点就应该是父节了
     * @parame 新增分类 category,请求参数node
     * @return 
     * @exception 
     * @author silenter
     * @date 2019/9/30 11:14
     */
    @PostMapping
    public ResponseEntity<Void> insertCategory(Category category){
        //System.out.println(category);
        categoryService.insertCategory(category);
       return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
     *
     * @parame 新增是根据对象增，修改就根据对象的属性来改，（修改的前提是先有才能改根据id改）
     * @return  put是幂等操作
     * @exception
     * @author silenter
     * @date 2019/9/30 12:47
     *
     */
    /**
     * 保存
     * @return
     */
    /**
     * 更新
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCategory(Category category){
        this.categoryService.updateCategory(category);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    @GetMapping("list")
    //Restful风格使用ResponseEntity<T>
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam(value = "pid",defaultValue = "0") Long pid){
        //根据父id查询商品分类
        //restful 风格成功， ok 里面确实少数据需要service
        if (pid == -1){
            return ResponseEntity.ok(categoryService.queryLastId());
        }else{
            return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
        }


    }
    /*
     * 根据cid删除分类
     * @parame id
     * @return
     * @exception
     * @author silenter
     * @date 2019/9/30 15:07
     */
    @DeleteMapping("/cid/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("id") long id){
        //逻辑写在servcie层
        categoryService.deleteCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 2) 品牌修改回显分类功能， 前台页面主要难点在于vue 数据的回显 父子值的传递
    /*
     *  修改品牌，可以看到商品的分类没有回显，因为商品分类没有数据，需要我们进行从
     *  数据库中查询出来
     *  @param bid
     *  @return
     * */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
        List<Category> list = categoryService.queryByBrandId(bid);
        return ResponseEntity.ok(list);
    }

    /*
    *  索引库的数据来自数据库，但搜索微服务不能直接查询,而是调用微服务
    *  spu,skus,spu details, --all[字段]
    * */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategorybyIds(@RequestParam("ids") List<Long> ids){
        //根据ids查询商品分类的接口
        return ResponseEntity.ok(categoryService.queryCategorybyIds(ids));
    }
}
