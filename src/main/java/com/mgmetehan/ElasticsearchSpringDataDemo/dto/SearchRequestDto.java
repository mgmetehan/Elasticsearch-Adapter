package com.mgmetehan.ElasticsearchSpringDataDemo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDto {
    private String fieldName;
    private String searchValue;
}
