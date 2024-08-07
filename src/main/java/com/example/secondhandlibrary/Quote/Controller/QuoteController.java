package com.example.secondhandlibrary.Quote.Controller;

import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Service.BookRepositoryService;
import com.example.secondhandlibrary.Quote.DTO.BookFinderDTO;
import com.example.secondhandlibrary.Quote.DTO.QuoteDTO;
import com.example.secondhandlibrary.Quote.Entity.QuoteEntity;
import com.example.secondhandlibrary.Quote.Service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/quote")
public class QuoteController {


    @Autowired
    private BookRepositoryService bookRepositoryService;

    @Autowired
    private QuoteService quoteService;

    //인용구작성할 도서 찾기 폼으로 이동
    @GetMapping("/BookFinder")
    public String quoteBookFinder(Model model){
        List<BookEntity> bookEntities = bookRepositoryService.getAllBooks();
        model.addAttribute("bookEntities", bookEntities);
        return  "Quote/BookFinder";
    }

    @PostMapping("/BookFinder")
    public String quoteBookFinder(BookFinderDTO bookFinderDTO, Model model){
        List<BookEntity> bookEntities =  bookRepositoryService.bookFinder(bookFinderDTO.getBookName());
        if (bookEntities != null){
            model.addAttribute("bookEntities", bookEntities);
            return  "Quote/BookFinder";
        }
        return "Quote/BookFinder";
    }

    @GetMapping("/{bookId}/quoteForm")
    public String quoteForm(@PathVariable("bookId") Long id , Model model){
        BookEntity bookEntity = bookRepositoryService.getBook(id);
        if (bookEntity != null){
            model.addAttribute("book",bookEntity);
            return "Quote/QuoteForm";
        }
        return  "Quote/BookFinder";
    }

    @PostMapping("/{bookId}/quoteForm")
    public String createQuote(@PathVariable("bookId") Long id , QuoteDTO quoteDTO){
        System.out.println(quoteDTO.getQuote());
        System.out.println(quoteDTO.getAuthor());
        quoteService.createQuote(id , quoteDTO.getAuthor(), quoteDTO.getQuote());
        return  "Quote/BookFinder"; //나중에 마이페이지로 이동
    }

    @GetMapping("/quoteShow")
    public String quoteShow(Model model){
        List<QuoteEntity> quoteEntities = quoteService.findCurrentMonthQuotes();

        model.addAttribute("quoteEntities" ,quoteEntities);

        return "Quote/QuoteShow";

    }

    @GetMapping("/delete/{quoteId}")
    public String quoteDelete(@PathVariable("quoteId") Long id){
        System.out.println("test");
        quoteService.deleteQuote(id);
        return "redirect:/quote/quoteShow";
    }
}
