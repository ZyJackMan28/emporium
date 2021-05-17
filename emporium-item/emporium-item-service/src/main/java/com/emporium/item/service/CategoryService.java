package com.emporium.item.service;

import com.emporium.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    List<Category> queryCategoryListByPid(Long pid);

    List<Category> queryByBrandId(Long bid);

    List<Category> queryCategorybyIds(List<Long> ids);

    void insertCategory(Category category);

    void updateCategory(Category category);

    void deleteCategoryById(long id);

    List<Category> queryLastId();
}
