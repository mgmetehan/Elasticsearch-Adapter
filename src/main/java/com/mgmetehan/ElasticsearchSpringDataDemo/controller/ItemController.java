package com.mgmetehan.ElasticsearchSpringDataDemo.controller;

import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import com.mgmetehan.ElasticsearchSpringDataDemo.service.ItemService;
import com.mgmetehan.ElasticsearchSpringDataDemo.service.JsonDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
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

}
