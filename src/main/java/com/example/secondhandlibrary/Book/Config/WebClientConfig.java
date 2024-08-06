package com.example.secondhandlibrary.Book.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "libraryWebClient")
    public WebClient libraryWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("http://data4library.kr/api/loanItemSrch").build(); //인기 도서 조회
    }

    @Bean(name = "aladdinProductSearchWebClient")
    public WebClient aladdinProductSearchWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx").build(); //알라딘 상품 조회
    }

    @Bean(name = "aladdinUsedStockWebClient")
    public WebClient aladdinUsedStockWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("http://www.aladin.co.kr/ttb/api/ItemOffStoreList.aspx").build(); //알라딘 중고 재고있는 지점 조회
    }

}
