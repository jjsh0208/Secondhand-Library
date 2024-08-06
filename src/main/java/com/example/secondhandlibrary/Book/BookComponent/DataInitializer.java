package com.example.secondhandlibrary.Book.BookComponent;

import com.example.secondhandlibrary.Book.APIClient.LibraryAPIClient;
import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Repository.BookRepository;
import com.example.secondhandlibrary.Book.Service.BookRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private LibraryAPIClient libraryAPIClient;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRepositoryService bookRepositoryService;

    @Override
    public void run(String... args) throws Exception {

        if (bookRepositoryService.getBookTableColumnCount() == 0) {
            CompletableFuture<List<BookEntity>> futureBookList = libraryAPIClient.initializePopularBooks();
            List<BookEntity> bookEntityList = futureBookList.get(); // 비동기 작업 완료를 기다립니다.
            if (bookEntityList != null) {
                bookRepository.saveAll(bookEntityList);
            }
        }
    }
}
