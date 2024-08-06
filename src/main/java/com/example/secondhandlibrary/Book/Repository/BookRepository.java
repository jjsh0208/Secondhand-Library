package com.example.secondhandlibrary.Book.Repository;


import com.example.secondhandlibrary.Book.Entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity,Long> {

    // bookName이 존재하는지 여부를 반환하는 메서드
    boolean existsByBookName(String bookName);

    @Query("SELECT b.bookName FROM BookEntity b")
    List<String> findAllBookNames();

    //이름으로 저장된 도서 객체를 찾아오는 메서드
    BookEntity findSingleByBookName(String bookName);

    //bookName 이 포함된 도서 조회
    List<BookEntity> findAllByBookNameContaining(String bookName);
}
