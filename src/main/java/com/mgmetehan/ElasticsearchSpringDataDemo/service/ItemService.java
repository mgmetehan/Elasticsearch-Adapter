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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public String getAllItemsFromAllIndexes() {
        Query query = ESUtil.createMatchAllQuery();
        SearchResponse<Map> searchResponse = null;
        try {
            searchResponse = elasticsearchClient.search(s -> s.query(query), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Elasticsearch sorgusu: {}", query.toString());
        log.info("Elasticsearch cevabi: {}", searchResponse.toString());

        return searchResponse.hits().hits().toString();
        /*
        List<String> hits = searchResponse.hits().hits()
                .stream()
                .map(hit -> hit.source().toString())
                .collect(Collectors.toList());

        return String.join("\n", hits);
        */
    }

    public List<Item> matchAllItemsServices() {
        try {
            Supplier<Query> supplier = ESUtil.supplier();
            SearchResponse<Item> searchResponse = elasticsearchClient.search(s -> s.index("items_index").query(supplier.get()), Item.class);
            log.info("elasticsearch query is " + supplier.get().toString());

            log.info("elasticsearch response is " + searchResponse.toString());
            List<Hit<Item>> listOfHits = searchResponse.hits().hits();
            List<Item> listOfItems = new ArrayList<>();
            for (Hit<Item> hit : listOfHits) {
                listOfItems.add(hit.source());
            }
            return listOfItems;
        } catch (IOException e) {
            log.error("Error while getting all items", e);
            throw new RuntimeException(e);
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
        return searchResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
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

    public SearchResponse<Item> autoSuggestItem(String name) throws IOException {
        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(name);
        SearchResponse<Item> searchResponse = elasticsearchClient.search(s -> s.index("items_index").query(supplier.get()), Item.class);
        log.info(" elasticsearch auto suggestion query" + supplier.get().toString());
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
