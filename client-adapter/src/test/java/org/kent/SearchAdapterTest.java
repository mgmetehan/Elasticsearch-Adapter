package org.kent;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import com.mgmetehan.ElasticsearchSpringDataDemo.util.ESUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kent.client.EntityType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SearchAdapterTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private SearchAdapter searchAdapter;

    @Test
    void testSearchAll() throws IOException {
        Mockito.when(elasticsearchClient.search((SearchRequest) any(), any())).thenReturn(null);

        SearchResponse<?> actualResponse = searchAdapter.searchAll(new EntityType<Item>() {
            @Override
            public Class<Item> getEntityClass() {
                return Item.class;
            }
        });
        Mockito.verify(elasticsearchClient).search(q -> q.query(ESUtil.createMatchAllQuery()), Item.class);
    }

}