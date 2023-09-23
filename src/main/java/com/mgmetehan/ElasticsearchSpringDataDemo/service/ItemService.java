package com.mgmetehan.ElasticsearchSpringDataDemo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.mgmetehan.ElasticsearchSpringDataDemo.dto.SearchRequestDto;
import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import com.mgmetehan.ElasticsearchSpringDataDemo.repository.ItemRepository;
import com.mgmetehan.ElasticsearchSpringDataDemo.util.ESUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final JsonDataService jsonDataService;
    private final ElasticsearchClient elasticsearchClient;

    public Item createIndex(Item item) {
        return itemRepository.save(item);
    }

    public void addItemsFromJson() {
        log.info("Adding Items from Json");
        List<Item> Items = jsonDataService.readItemsFromJson();
        for (Item Item : Items) {
            itemRepository.save(Item);
        }
    }

    public Iterable<Item> getItems() {
        log.info("Getting Items");
        return itemRepository.findAll();
    }

    public List<Item> getAllItemsFromAllIndexes() {
        Query query = ESUtil.createMatchAllQuery();
        SearchResponse<Item> searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(s -> s.query(query), Item.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Elasticsearch sorgusu: {}", query.toString());
        log.info("Elasticsearch cevabi: {}", searchResponse.toString());

        return extractItemsFromResponse(searchResponse);
    }

    public List<Item> getAllDataFromIndex() {
        try {
            var supplier = ESUtil.createMatchAllQuery();
            SearchResponse<Item> searchResponse = elasticsearchClient.search(
                    s -> s.index("items_index").query(supplier), Item.class);

            log.info("Elasticsearch query is {}", supplier.toString());
            log.info("Elasticsearch response is {}", searchResponse.toString());

            return extractItemsFromResponse(searchResponse);
        } catch (IOException e) {
            log.error("Error while getting all items", e);
            throw new RuntimeException("Failed to retrieve items", e);
        }
    }


    public List<Item> searchItemsByFieldAndValue(SearchRequestDto searchRequestDto) {
        SearchResponse<Item> searchResponse = null;
        try {
            Supplier<Query> querySupplier = ESUtil.buildQueryForFieldAndValue(searchRequestDto.getFieldName(),
                    searchRequestDto.getSearchValue());//sorgu olustur

            searchResponse = elasticsearchClient.search(s -> s.index("items_index")
                    .query(querySupplier.get()), Item.class);//sorguyu calistir ve cevabi alir

            log.info("Elasticsearch sorgusu: {}", querySupplier.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extractItemsFromResponse(searchResponse);
    }

    public List<Item> extractItemsFromResponse(SearchResponse<Item> searchResponse) {
        return searchResponse
                .hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<Item> searchItemsByNameAndBrand(String name, String brand) {
        try {
            return itemRepository.searchByNameAndBrand(name, brand);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public SearchResponse<Item> boolQuery(String name, String brand) throws IOException {
        Supplier<Query> supplier = ESUtil.supplierQueryForBoolQuery(name, brand);
        SearchResponse<Item> searchResponse = elasticsearchClient.search(s -> s.index("items_index").query(supplier.get()), Item.class);
        log.info("elasticsearch query is " + supplier.get().toString());
        return searchResponse;
    }

    public Set<String> findSuggestedItemNames(String itemName) {
        Query autoSuggestQuery = ESUtil.buildAutoSuggestQuery(itemName);
        log.info("Elasticsearch auto-suggestion query: {}", autoSuggestQuery.toString());

        try {
            return elasticsearchClient.search(s -> s.index("items_index").query(autoSuggestQuery), Item.class)
                    .hits()
                    .hits()
                    .stream()
                    .map(Hit::source)
                    .map(Item::getName)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> autoSuggestItemsByNameWithQuery(String name) {
        List<Item> items = itemRepository.customAutocompleteSearch(name);
        return items.stream().map(Item::getName).collect(Collectors.toList());
    }
}
