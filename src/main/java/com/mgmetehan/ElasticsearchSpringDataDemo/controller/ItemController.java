package com.mgmetehan.ElasticsearchSpringDataDemo.controller;

import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import com.mgmetehan.ElasticsearchSpringDataDemo.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/init-index")
    public void addItemsFromJson() {
        itemService.addItemsFromJson();
    }

    @GetMapping("/findAll")
    public Iterable<Item> findAll() {
        return itemService.getItems();

    }

    //Tum indexleri getir
    @GetMapping("/matchAll")
    public String matchAll() {
        try {
            return itemService.matchAllServices();
        } catch (IOException e) {
            log.error("Error while getting all items", e);
            throw new RuntimeException(e);
        }
    }

    // Belirtigimiz indexleri getir
    @GetMapping("/matchAllItems")
    public List<Item> matchAllItems() {
        return itemService.matchAllItemsServices();
    }

}
