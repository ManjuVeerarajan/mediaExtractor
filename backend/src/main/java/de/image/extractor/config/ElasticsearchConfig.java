package de.image.extractor.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class ElasticsearchConfig {
   @Bean
    public ElasticsearchClient elasticsearchClient() throws KeyManagementException, NoSuchAlgorithmException {
       CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
       credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "rQQtbktwzFqAJS1h8YjP"));

      SSLContext sslContext = SSLContext.getInstance("TLS");
      TrustManager[] trustAllCerts = new TrustManager[]{
              new X509TrustManager() {
                 public X509Certificate[] getAcceptedIssuers() { return null; }
                 public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                 public void checkServerTrusted(X509Certificate[] certs, String authType) {}
              }
      };
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

       RestClient restClient = RestClient.builder(new HttpHost("5.75.227.63", 9200, "https"))
               .setHttpClientConfigCallback(httpClientBuilder ->
                       httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                               .setSSLContext(sslContext)
                               .setSSLHostnameVerifier((hostname, session) -> true)) // Ignore SSL
               .build();

       ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
       return new ElasticsearchClient(transport);
   }
}

