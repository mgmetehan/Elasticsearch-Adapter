package com.mgmetehan.ElasticsearchSpringDataDemo.service;

import com.mgmetehan.ElasticsearchSpringDataDemo.model.Item;
import com.mgmetehan.ElasticsearchSpringDataDemo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final JsonDataService jsonDataService;


    public void addItemsFromJson() {
        List<Item> Items = jsonDataService.readItemsFromJson();
        for (Item Item : Items) {
            itemRepository.save(Item);
        }
    }

    public Iterable<Item> getItems() {
        return itemRepository.findAll();
    }
}
