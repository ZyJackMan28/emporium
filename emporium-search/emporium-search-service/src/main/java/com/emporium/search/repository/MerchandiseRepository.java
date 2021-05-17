package com.emporium.search.repository;

import com.emporium.pojo.SearchMerchandise;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MerchandiseRepository extends ElasticsearchRepository<SearchMerchandise,Long> {
}
