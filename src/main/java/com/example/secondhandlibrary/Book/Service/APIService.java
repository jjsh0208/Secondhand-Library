package com.example.secondhandlibrary.Book.Service;

import com.example.secondhandlibrary.Book.APIClient.AladdinAPIClient;
import com.example.secondhandlibrary.Book.APIClient.LibraryAPIClient;
import com.example.secondhandlibrary.Book.Entity.BookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIService {

    @Autowired
    LibraryAPIClient libraryAPIClient;

    @Autowired
    AladdinAPIClient aladdinAPIClient;

    public List<BookEntity> searchPopularBooks(String sex, String age, String location, String interest) {
        return   libraryAPIClient.searchPopularBooks(sex,age,location,interest);
    }

    public void checkUsedStockInBranch(String isbn) {
        List<String> store =  aladdinAPIClient.findStore(isbn);;
        if (!store.isEmpty()){
            for (String s : store){
                System.out.println(s);
            }
        }else{
            System.out.println("재고가 없습니다.");
        }
    }

    public String[] retrieveBookInfo(String bookName){
        return aladdinAPIClient.retrieveBookInfo(bookName);
    }



}