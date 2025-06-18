package com.phonekart.auth;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElasticSearchOperations {
    private static final Logger logger = Logger.getLogger(ElasticSearchOperations.class.getName());
    static final String PRODUCTS_INDEX = "products";
    private static volatile ElasticsearchClient client;
    
    // Thread-safe client initialization
    public static ElasticsearchClient getClient() {
        if (client == null) {
            synchronized (ElasticSearchOperations.class) {
                if (client == null) {
                    try {
                        RestClient restClient = RestClient.builder(
                            new HttpHost("localhost", 9200, "http"))
                            .setRequestConfigCallback(builder -> 
                                builder.setConnectTimeout(5000)
                                      .setSocketTimeout(60000))
                            .build();
                        
                        ElasticsearchTransport transport = new RestClientTransport(
                            restClient, new JacksonJsonpMapper());
                        
                        client = new ElasticsearchClient(transport);
                        logger.info("Elasticsearch client initialized successfully");
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Failed to initialize Elasticsearch client", e);
                        throw new RuntimeException("Elasticsearch client initialization failed", e);
                    }
                }
            }
        }
        return client;
    }

    public static void ensureProductsIndexExists() {
        try {
            ElasticsearchClient client = getClient();
            
            if (!indexExists(PRODUCTS_INDEX)) {
                createProductIndex();
                // Wait for index to be ready
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to ensure products index exists", e);
            throw new RuntimeException("Index verification failed", e);
        }
    }

    private static boolean indexExists(String indexName) throws IOException {
        return getClient().indices().exists(e -> e.index(indexName)).value();
    }

    public static void createProductIndex() {
        try {
            ElasticsearchClient client = getClient();
            
            CreateIndexResponse response = client.indices().create(c -> c
                .index(PRODUCTS_INDEX)
                .settings(s -> s
                    .numberOfShards("1")
                    .numberOfReplicas("1")
                )
                .mappings(m -> m
                    .properties("productid", p -> p.integer(i -> i))
                    .properties("title", p -> p.text(t -> t.analyzer("standard")))
                    .properties("brand", p -> p.text(t -> t.analyzer("standard")))
                    .properties("model", p -> p.text(t -> t.analyzer("standard")))
                    .properties("battery_capacity", p -> p.text(t -> t))
                    .properties("screen_size", p -> p.text(t -> t))
                    .properties("storagespace", p -> p.text(t -> t))
                    .properties("ram", p -> p.text(t -> t))
                    .properties("camera_mp", p -> p.text(t -> t))
                    .properties("prod_description", p -> p.text(t -> t.analyzer("standard")))
                    .properties("price", p -> p.integer(i -> i))
                    .properties("username", p -> p.keyword(k -> k))
                )
            );

            if (!response.acknowledged()) {
                throw new IOException("Index creation not acknowledged by cluster");
            }
            logger.info("Products index created successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create products index", e);
            throw new RuntimeException("Index creation failed", e);
        }
    }

    public static void deleteIndex(String indexName) {
        try {
            ElasticsearchClient client = getClient();
            client.indices().delete(d -> d.index(indexName).ignoreUnavailable(true));
            logger.info("Deleted index: " + indexName);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to delete index: " + indexName, e);
            throw new RuntimeException("Index deletion failed", e);
        }
    }


    public static List<Integer> searchProducts(String searchText) {
        try {
            ensureProductsIndexExists();
            ElasticsearchClient client = getClient();
            
            SearchResponse<Map> response = client.search(s -> s
                .index(PRODUCTS_INDEX)
                .query(q -> q
                    .multiMatch(m -> m
                        .query(searchText)
                        .fields("title", "brand", "model", "prod_description",
                                "ram", "storagespace", "camera_mp", "screen_size")
                        .fuzziness("AUTO") // ✅ Added typo tolerance here
                    )
                ),
                Map.class
            );

            List<Integer> productIds = new ArrayList<>();
            for (@SuppressWarnings("rawtypes") Hit<Map> hit : response.hits().hits()) {
                try {
                    Integer productId = (Integer) hit.source().get("productid");
                    if (productId != null) {
                        productIds.add(productId);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Invalid productid in document: " + hit.id(), e);
                }
            }
            return productIds;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Search failed for: " + searchText, e);
            throw new RuntimeException("Search operation failed", e);
        }
    }


    public static void close() {
        if (client != null) {
            try {
                client._transport().close();
                client = null;
                logger.info("Elasticsearch client closed successfully");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to close Elasticsearch client", e);
            }
        }
    }
}