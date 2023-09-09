package com.mgmetehan.ElasticsearchSpringDataDemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchRequestDto {
    private List<String> fields;
    private String searchValue;
}
