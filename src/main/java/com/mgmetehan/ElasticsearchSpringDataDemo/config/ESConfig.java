package com.mgmetehan.ElasticsearchSpringDataDemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.mgmetehan.ElasticsearchSpringDataDemo.repository")
@ComponentScan(basePackages = {"com.mgmetehan.ElasticsearchSpringDataDemo"})
public class ESConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;


    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl)
                .build();
    }
}
