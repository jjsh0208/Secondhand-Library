package com.example.secondhandlibrary;

import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Service.BookRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    BookRepositoryService bookRepositoryService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "page", defaultValue = "0") int page){
        Page<BookEntity> bookEntities =  bookRepositoryService.findAllBookEntity(page);
        model.addAttribute("bookEntities" ,bookEntities);
        return "index";
    }

}
