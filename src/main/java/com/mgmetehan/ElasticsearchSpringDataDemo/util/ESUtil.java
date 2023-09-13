package com.mgmetehan.ElasticsearchSpringDataDemo.util;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public class ESUtil {
    public static Supplier<Query> supplier() {
        Supplier<Query> supplier = () -> Query.of(q -> q.matchAll(matchAllQuery()));
        return supplier;
    }

    public static MatchAllQuery matchAllQuery() {
        val matchAllQuery = new MatchAllQuery.Builder();
        return matchAllQuery.build();
    }

    public static Supplier<Query> supplierWithNameField(String fieldValue){
        Supplier<Query> supplier = ()->Query.of(q->q.match(matchQueryWithNameField(fieldValue)));
        return supplier;
    }

    public static MatchQuery matchQueryWithNameField(String fieldValue){
        val  matchQuery = new MatchQuery.Builder();
        return matchQuery.field("name").query(fieldValue).build();
    }

    public static Supplier<Query> supplierQueryForBoolQuery(String name, Double price) {
        Supplier<Query> supplier = ()->Query.of(q->q.bool(boolQuery(name,price)));
        return supplier;
    }

    public static BoolQuery boolQuery(String name, Double price){
        val boolQuery  = new BoolQuery.Builder();
        return boolQuery.filter(termQuery(name)).must(matchQuery(price)).build();
    }

    public static List<Query> termQuery(String name){
        final List<Query> terms = new ArrayList<>();
        val termQuery = new TermQuery.Builder();
        terms.add(Query.of(q->q.term(termQuery.field("name").value(name).build())));
        return terms;
    }

    public static List<Query> matchQuery(Double price){
        final List<Query> matches = new ArrayList<>();
        val matchQuery = new MatchQuery.Builder();
        matches.add(Query.of(q->q.match(matchQuery.field("price").query(price).build())));
        return matches;
    }
}
