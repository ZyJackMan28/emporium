package com.emporium.item.service;

import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Brand;

import java.util.List;

public interface BrandService {

    PageResult<Brand> queryBrandByKey(Integer page, Integer rows, String sortBy, Boolean desc, String key);

    void insertBrand(Brand brand, List<Long> cids);

    Brand queryById(Long id);

    List<Brand> queryBrandByCid(Long cid);

    List<Brand> queryBrandByIds(List<Long> ids);
}
