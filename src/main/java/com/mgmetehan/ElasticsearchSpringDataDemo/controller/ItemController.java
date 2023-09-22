package com.mgmetehan.ElasticsearchSpringDataDemo.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.mgmetehan.ElasticsearchSpringDataDemo.dto.SearchRequestDto;
import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import com.mgmetehan.ElasticsearchSpringDataDemo.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public Item createIndex(@RequestBody Item item) {
        return itemService.createIndex(item);
    }

    @PostMapping("/init-index")
    public void addItemsFromJson() {
        itemService.addItemsFromJson();
    }

    @GetMapping("/findAll")
    public Iterable<Item> findAll() {
        return itemService.getItems();
    }

    //Tum indexleri getir
    @GetMapping("/allIndexes")
    public String getAllItemsFromAllIndexes() {
        return itemService.getAllItemsFromAllIndexes();
    }

    // Belirtigimiz indexleri getir
    @GetMapping("/matchAllItems")
    public List<Item> matchAllItems() {
        return itemService.matchAllItemsServices();
    }

    @GetMapping("/search")
    public List<Item> searchItemsByFieldAndValue(@RequestBody SearchRequestDto searchRequestDto) {
        List<Item> foundItems = itemService.searchItemsByFieldAndValue(searchRequestDto);
        log.info("Elasticsearch arama sonuçları: {}", foundItems);
        return foundItems;
    }

    @GetMapping("/search/{name}/{brand}")
    public List<Item> searchItemsByNameAndBrandWithQuery(@PathVariable String name, @PathVariable String brand) {
        try {
            List<Item> foundItems = itemService.searchItemsByNameAndBrand(name, brand);
            log.info("Elasticsearch arama sonuclari: {}", foundItems);
            return foundItems;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/boolQuery/{name}/{brand}")
    public List<Item> boolQuery(@PathVariable String name, @PathVariable String brand) throws IOException {

        SearchResponse<Item> searchResponse = itemService.boolQuery(name, brand);
        log.info(searchResponse.hits().hits().toString());

        List<Hit<Item>> listOfHits = searchResponse.hits().hits();
        List<Item> listOfItems = new ArrayList<>();
        for (Hit<Item> hit : listOfHits) {
            listOfItems.add(hit.source());
        }
        return listOfItems;
    }

    @GetMapping("/autoSuggest/{name}")
    public Set<String> autoSuggestItemsByName(@PathVariable String name) {
        return itemService.findSuggestedItemNames(name);
    }

    @GetMapping("/suggestions/{name}")
    public List<String> autoSuggestItemsByNameWithQuery(@PathVariable String name) {
        return itemService.autoSuggestItemsByNameWithQuery(name);
    }
}
