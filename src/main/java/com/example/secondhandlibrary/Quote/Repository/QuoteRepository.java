package com.example.secondhandlibrary.Quote.Repository;


import com.example.secondhandlibrary.Quote.Entity.QuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<QuoteEntity, Long> {
}
