package com.mgmetehan.ElasticsearchSpringDataDemo.repository;

import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemRepository extends ElasticsearchRepository<Item, String> {
}
