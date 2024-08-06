package com.example.secondhandlibrary.Book.APIClient;

import com.example.secondhandlibrary.Book.Entity.BookEntity;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class LibraryAPIClient {

    private final AladdinAPIClient aladdinAPIClient;

    private final Executor bookApiTaskExecutor;

    private final String apiKey;

    private final WebClient libraryWebClient;


    @Autowired
    public LibraryAPIClient(
            @Qualifier("libraryWebClient") WebClient webClient,
            @Value("${library.api.key}") String apiKey,
            @Qualifier("bookApiTaskExecutor") Executor  bookApiTaskExecutor,
            AladdinAPIClient aladdinAPIClient) {
        this.libraryWebClient = webClient;
        this.apiKey = apiKey;
        this.bookApiTaskExecutor = bookApiTaskExecutor;
        this.aladdinAPIClient = aladdinAPIClient;
    }

    @Async("bookApiTaskExecutor")
    public CompletableFuture<List<BookEntity>> initializePopularBooks() {

        List<BookEntity> bookEntities = new ArrayList<>();
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        LocalDate startYear = LocalDate.of(currentYear, 1, 1);

        try {
            String responseBody = libraryWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("authKey", apiKey)
                            .queryParam("startDt", startYear)
                            .queryParam("endDt", today)
                            .queryParam("pageSize", "200")
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println(responseBody);


            if (responseBody == null || responseBody.isEmpty()) {
                log.error("Received null or empty response body.");
                return CompletableFuture.completedFuture(bookEntities);
            }

            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONObject responseObj = jsonResponse.getJSONObject("response");

            if (!jsonResponse.has("response") || !responseObj.has("docs")) {
                log.info("No response or docs found.");
                return CompletableFuture.completedFuture(bookEntities);
            }

            JSONArray items = responseObj.getJSONArray("docs");
            Set<String> titles = new HashSet<>();

            List<CompletableFuture<BookEntity>> futures = new ArrayList<>();

            for (int i = 0; i < items.length(); i++) {
                JSONObject docObj = items.getJSONObject(i).getJSONObject("doc");
                String bookName = docObj.getString("bookname");
                System.out.println("원본 이름 : " + bookName);
                if (!titles.contains(bookName)) {
                    titles.add(bookName);
                    futures.add(CompletableFuture.supplyAsync(() -> {
                        try {
                            return processBook(bookName, docObj).get();
                        } catch (InterruptedException | ExecutionException e) {
                            log.error("Error processing book {}", bookName, e);
                            return null;
                        }
                    }, bookApiTaskExecutor));
                }
            }

            // Collect all the results
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        futures.forEach(future -> {
                            try {
                                BookEntity bookEntity = future.get();
                                if (bookEntity != null) {
                                    bookEntities.add(bookEntity);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                log.error("Error getting book entity", e);
                            }
                        });
                        return bookEntities;
                    });

        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(bookEntities);
        }
    }

    private CompletableFuture<BookEntity> processBook(String bookName, JSONObject docObj) {
        return aladdinAPIClient.initializeBookInfo(bookName)
                .thenApply(part -> {
                    if (part == null || part.length != 4) {
                        log.error("Failed to fetch complete book details for {}", bookName);
                        return null;
                    }
                    try {
                        String authors = docObj.getString("authors").replaceAll("[;,]", " ").trim();

                        return BookEntity.builder()
                                .bookName(docObj.getString("bookname"))
                                .author(authors)
                                .pubDate(docObj.getString("publication_year"))
                                .description(part[0])
                                .coverImg(docObj.getString("bookImageURL"))
                                .kdc(docObj.getString("class_no"))
                                .item_ID(Integer.parseInt(part[1]))
                                .price(Integer.parseInt(part[2]))
                                .isbn(part[3])
                                .build();
                    } catch (NumberFormatException | JSONException e) {
                        log.error("Error parsing book details for {}", bookName, e);
                        return null;
                    }
                })
                .exceptionally(e -> {
                    log.error("Error processing book {}", bookName, e);
                    return null;
                });
    }

    public List<BookEntity> searchPopularBooks(String sex, String age, String location, String interest){
        List<BookEntity> newlyRecommendedBooks = new ArrayList<>(); //추천되었지만 디비에 존재하지않는 도서들

        LocalDate today = LocalDate.now(); //서버 시작일 기준 날짜
        int currentYear = today.getYear(); //서비 시작일 기준 년도
        LocalDate startYear = LocalDate.of(currentYear,1,1); //

        try {
            String responseBody = libraryWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("authKey", apiKey)
                            .queryParam("startDt", startYear)
                            .queryParam("endDt", today)
                            .queryParam("gender",sex)
                            .queryParam("age",age)
                            .queryParam("region",location)
                            .queryParam("kdc",interest)
                            .queryParam("pageSize", "200") //200개 조회
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject jsonResponse = new JSONObject(responseBody);

            if (!jsonResponse.has("response")) {
                System.out.println("No response found.");
            }

            JSONObject responseObj = jsonResponse.getJSONObject("response");

            if (!responseObj.has("docs")) {
                System.out.println("No docs found.");
            }

            JSONArray items = responseObj.getJSONArray("docs");
            Set<String> titles = new HashSet<>(); // Set to keep track of unique titles

            for (int i = 0; i < items.length(); i++) {
                if (newlyRecommendedBooks.size() == 10) break; //10개의 도서만 추출

                JSONObject docObj = items.getJSONObject(i).getJSONObject("doc");
                String bookName = docObj.getString("bookname");
                // 중복된 도서가 나올 수 있기 때문에 set으로 중복되지 않은 도서만 추가한다.
                if (!titles.contains(bookName)){
                    titles.add(bookName);
                    String authors = docObj.getString("authors").replaceAll("[;,]", " ").trim();
                    BookEntity bookEntity = BookEntity.builder()
                            .bookName(docObj.getString("bookname"))
                            .author(authors)
                            .pubDate(docObj.getString("publication_year"))
                            .coverImg( docObj.getString("bookImageURL"))
                            .kdc(docObj.getString("class_no")) // Convert to Long if necessary
                            .build();
                    newlyRecommendedBooks.add(bookEntity);
                }
            }
            return  newlyRecommendedBooks;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
