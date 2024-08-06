package com.example.secondhandlibrary.Quote.Entity;

import com.example.secondhandlibrary.Book.Entity.BookEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="Quote")
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long QuoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

//    @OneToOne
//    private MemberEntity member;

    private String author; //인물 혹 글쓴이

    private String quote; //인용구

    private LocalDate quoteDate;

    @PrePersist
    protected void onCreate() {
        if (this.quoteDate == null) {
            this.quoteDate = LocalDate.now();
        }
    }
}
