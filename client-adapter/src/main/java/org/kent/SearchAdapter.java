package org.kent;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mgmetehan.ElasticsearchSpringDataDemo.util.ESUtil;
import lombok.RequiredArgsConstructor;
import org.kent.client.EntityType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class is a client adapter for the ElasticsearchClient.
 * @Author Furkan Özmen
 */

@Component
@RequiredArgsConstructor
public class SearchAdapter {

    private final ElasticsearchClient elasticsearchClient;

    public SearchResponse<?>  searchAll(EntityType<?> entityType) throws IOException {
        Query query = ESUtil.createMatchAllQuery();
        Class<?> entityClass = entityType.getEntityClass();
        return elasticsearchClient.search(q -> q.query(query), entityClass);
    }


    public SearchResponse<?> searchWihQuery(EntityType<?> entityType, Query query) throws IOException {
        Class<?> entityClass = entityType.getEntityClass();
        return elasticsearchClient.search(q -> q.query(query), entityClass);
    }

    public SearchResponse<?> searchWihQuery(EntityType<?> entityType, Query query, int size) throws IOException {
        Class<?> entityClass = entityType.getEntityClass();
        return elasticsearchClient.search(q -> q.query(query).size(size), entityClass);
    }

    public SearchResponse<?> searchWihQuery(EntityType<?> entityType, Query query, int size, int from) throws IOException {
        Class<?> entityClass = entityType.getEntityClass();
        return elasticsearchClient.search(q -> q.query(query).size(size).from(from), entityClass);
    }

    public SearchResponse<?> findByName(EntityType<?> entityType, String name) throws IOException {
        Query query = ESUtil.matchQuery("name", name);
        Class<?> entityClass = entityType.getEntityClass();
        return elasticsearchClient.search(q -> q.query(query), entityClass);
    }

    public SearchResponse<?> findByName(EntityType<?> entityType, String name, int size) throws IOException {
        Query query = ESUtil.matchQuery("name", name);
        Class<?> entityClass = entityType.getEntityClass();
        return elasticsearchClient.search(q -> q.query(query).size(size), entityClass);
    }


}
