package com.example.secondhandlibrary.Book.Service;

import com.example.secondhandlibrary.Book.DTO.MemberSelectionDTO;
import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Repository.BookRepository;
import com.example.secondhandlibrary.Quote.Entity.QuoteEntity;
import com.example.secondhandlibrary.Quote.Repository.QuoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookRepositoryService {

    @Autowired
    APIService apiService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    QuoteRepository quoteRepository;

    public List<BookEntity> searchPopularBooks(MemberSelectionDTO memberSelectionDTO) {
        // DB에 들어있는 객체와는 다른 객체
        List<BookEntity> bookEntities = apiService.searchPopularBooks(
                memberSelectionDTO.getSex(),
                memberSelectionDTO.getAge(),
                memberSelectionDTO.getLocation(),
                memberSelectionDTO.getInterest()
        );

        Set<String> existingBookNames = new HashSet<>(bookRepository.findAllBookNames());

        // 새롭게 추가되는 도서만 필터링
        List<BookEntity> newBooks = bookEntities.stream()
                .filter(book -> !existingBookNames.contains(book.getBookName()))
                .peek(book -> {
                    String[] bookInfo = apiService.retrieveBookInfo(book.getBookName());
                    if (bookInfo.length >= 4) {
                        book.setDescription(bookInfo[0]);
                        book.setItem_ID(Integer.parseInt(bookInfo[1]));
                        book.setPrice(Integer.parseInt(bookInfo[2]));
                        book.setIsbn(bookInfo[3]);
                    } else {
                        log.info("추가 데이터가 존재하지않음 : {}", book.getBookName());
                    }
                })
                .collect(Collectors.toList());

        // 새롭게 추가되는 도서 저장
        bookRepository.saveAll(newBooks);

        // 전체 도서 리스트에서 데이터베이스에 있는 도서만 반환
        return bookEntities.stream()
                .map(book -> bookRepository.findSingleByBookName(book.getBookName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public boolean isBookNameExists(String bookName) {
        return bookRepository.existsByBookName(bookName);
    }

    public long getBookTableColumnCount(){
        return bookRepository.count();
    }

    public BookEntity getBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<BookEntity> bookFinder(String bookName) {
        return bookRepository.findAllByBookNameContaining(bookName);
    }

    public List<BookEntity> getAllBooks() {
        return bookRepository.findAll();
    }

    public void createQuote(Long id, String author, String quote) {
        Optional<BookEntity> bookEntity = bookRepository.findById(id);
        if (bookEntity.isPresent()){
            BookEntity book = bookEntity.get();
            QuoteEntity quoteEntity = QuoteEntity.builder()
                    .book(book)
                    .author(author)
                    .quote(quote)
                    .build();
            quoteRepository.save(quoteEntity);
        }

    }
}
