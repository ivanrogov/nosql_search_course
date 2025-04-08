package com.epam.nosql.config;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class ElasticConfig {

    @Value("${elastic.host}")
    public String HOST;
    @Value("${elastic.port}")
    public int PORT;

    @Bean
    public RestClient restClient() {
        return RestClient.builder(
                new HttpHost(HOST, PORT))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        {
                            try {
                                return httpClientBuilder.setSSLContext(
                                        SSLContextBuilder.create()
                                                .loadTrustMaterial(null, (chain, authType) -> true)
                                                .build()
                                ).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                ).build();
    }
}
