package com.phonekart.auth;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class ElasticSearchClient {
    private static ElasticsearchClient client;
    private static final String ELASTICSEARCH_HOST = "localhost";
    private static final int ELASTICSEARCH_PORT = 9200;
    private static final String ELASTICSEARCH_SCHEME = "http"; // or "https"

    public static synchronized ElasticsearchClient getClient() {
        if (client == null) {
            try {
                // Configure the low-level REST client
                RestClient restClient = RestClient.builder(
                    new HttpHost(ELASTICSEARCH_HOST, ELASTICSEARCH_PORT, ELASTICSEARCH_SCHEME))
                    .setHttpClientConfigCallback(httpClientBuilder -> {
                        if ("https".equalsIgnoreCase(ELASTICSEARCH_SCHEME)) {
                            try {
                                SSLContext sslContext = SSLContextBuilder.create()
                                    .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                                    .build();
                                httpClientBuilder.setSSLContext(sslContext);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to configure SSL", e);
                            }
                        }
                        return httpClientBuilder;
                    })
                    .build();

                // Create the transport with Jackson mapper
                ElasticsearchTransport transport = new RestClientTransport(
                    restClient,
                    new JacksonJsonpMapper()
                );

                // Create the new ElasticsearchClient
                client = new ElasticsearchClient(transport);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create Elasticsearch client", e);
            }
        }
        return client;
    }

    public static void close() {
        if (client != null) {
            try {
                client._transport().close();
                client = null;
            } catch (Exception e) {
                throw new RuntimeException("Failed to close Elasticsearch client", e);
            }
        }
    }
}