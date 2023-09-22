package com.mgmetehan.ElasticsearchSpringDataDemo.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@UtilityClass
public class ESUtil {

    public static Query createMatchAllQuery() {
        return Query.of(q -> q.matchAll(new MatchAllQuery.Builder().build()));
    }

    public static Supplier<Query> supplier() {
        Supplier<Query> supplier = () -> Query.of(q -> q.matchAll(matchAllQuery()));
        return supplier;
    }

    public static MatchAllQuery matchAllQuery() {
        var matchAllQuery = new MatchAllQuery.Builder();
        return matchAllQuery.build();
    }

    public static Supplier<Query> buildQueryForFieldAndValue(String fieldName, String searchValue) {
        return () -> Query.of(q -> q.match(buildMatchQueryForFieldAndValue(fieldName, searchValue)));
    }

    public static MatchQuery buildMatchQueryForFieldAndValue(String fieldName, String searchValue) {
        return new MatchQuery.Builder().field(fieldName).query(searchValue).build();
    }

    public static Supplier<Query> supplierQueryForBoolQuery(String name, String brand) {
        Supplier<Query> supplier = () -> Query.of(q -> q.bool(boolQuery(name, brand)));
        return supplier;
    }

    public static BoolQuery boolQuery(String name, String brand) {

        var boolQuery = new BoolQuery.Builder();
        return boolQuery.filter(termQuery("name", name)).must(termQuery("brand", brand)).build();
    }

    //termQuery kesin eslesme icin kullanilir
    public static List<Query> termQuery(String field, String value) {
        final List<Query> terms = new ArrayList<>();
        var termQuery = new TermQuery.Builder();
        terms.add(Query.of(q -> q.term(termQuery.field(field).value(value).build())));
        return terms;
    }

    //matchQuery daha esnek bir arama secenegi sunar ve belge iceriginde benzer terimleri bulabilir
    public static List<Query> matchQuery(String field, String value) {
        final List<Query> matches = new ArrayList<>();
        var matchQuery = new MatchQuery.Builder();
        matches.add(Query.of(q -> q.match(matchQuery.field(field).query(value).build())));
        return matches;
    }

    public static Supplier<Query> createSupplierAutoSuggest(String name) {
        Supplier<Query> supplier = () -> Query.of(q -> q.match(createAutoSuggestMatchQuery(name)));
        return supplier;
    }

    public static MatchQuery createAutoSuggestMatchQuery(String name) {
        var autoSuggestQuery = new MatchQuery.Builder();
        return autoSuggestQuery.field("name").query(name).analyzer("standard").build();
    }

    public Query buildAutoSuggestQuery(String name) {
        return Query.of(q -> q.match(createAutoSuggestMatchQuery(name)));
    }

    public MatchQuery buildAutoSuggestMatchQuery(String name) {
        return new MatchQuery.Builder()
                .field("name")
                .query(name)
                .analyzer("custom_index")
                .build();
    }
}
