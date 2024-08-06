package com.example.secondhandlibrary;

import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Service.BookRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    BookRepositoryService bookRepositoryService;

    @GetMapping("/")
    public String index(Model model){
        List<BookEntity> bookEntityList =  bookRepositoryService.getAllBooks();
        if (!bookEntityList.isEmpty()){
            model.addAttribute(bookEntityList);
        }
        return "index";
    }
}