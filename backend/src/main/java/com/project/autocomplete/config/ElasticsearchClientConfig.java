package com.project.autocomplete.config;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchClientConfig {

  @Value("${elasticsearch.host}")
  private String host;

  @Value("${elasticsearch.port}")
  private int port;

  @Value("${elasticsearch.protocol}")
  private String protocol;

  @Value("${elasticsearch.username}")
  private String userName;

  @Value("${elasticsearch.password}")
  private String password;

  @Bean(destroyMethod = "close")
  public RestHighLevelClient restClient() {

    var provider = new BasicCredentialsProvider();
    provider.setCredentials(AuthScope.ANY, getCredentials());

    RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, protocol))
        .setHttpClientConfigCallback(client -> client.setDefaultCredentialsProvider(provider))
        .setDefaultHeaders(compatibilityHeaders());

    return new RestHighLevelClient(builder);
  }

  private UsernamePasswordCredentials getCredentials() {
    return new UsernamePasswordCredentials(userName, password);
  }

  private Header[] compatibilityHeaders() {
    return new Header[]{
        new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"),
        new BasicHeader(HttpHeaders.CONTENT_TYPE,
            "application/vnd.elasticsearch+json;compatible-with=7")
    };
  }
}