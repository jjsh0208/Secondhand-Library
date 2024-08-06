package com.example.secondhandlibrary.Book.Entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String bookName;

    private String author;

    private String pubDate;

    private String description;

    private String coverImg;

    private String kdc;

    private int item_ID; //알라딘 재고번호

    private int price; //세일 구매가  (알라딘 기준)

    private String isbn; //알라딘 도서 고유번호 (재고확인용)

//    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<BoardEntity> boards;

//    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<QuoteEntity> bookMarks;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "library_id")
//    private LibraryEntity library;


}

