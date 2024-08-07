package com.example.secondhandlibrary.Quote.Service;

import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Repository.BookRepository;
import com.example.secondhandlibrary.Quote.Entity.QuoteEntity;
import com.example.secondhandlibrary.Quote.Repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private QuoteRepository quoteRepository;

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

    public List<QuoteEntity> findCurrentMonthQuotes() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        return quoteRepository.findQuotesByMonth(currentYear, currentMonth);
    }

    public void deleteQuote(Long id) {
        quoteRepository.deleteById(id);
    }
}
