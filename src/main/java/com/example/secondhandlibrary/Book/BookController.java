package com.example.secondhandlibrary.Book;

import com.example.secondhandlibrary.Book.DTO.MemberSelectionDTO;
import com.example.secondhandlibrary.Book.Entity.BookEntity;
import com.example.secondhandlibrary.Book.Service.APIService;
import com.example.secondhandlibrary.Book.Service.BookRepositoryService;
import com.example.secondhandlibrary.Quote.DTO.BookFinderDTO;
import com.example.secondhandlibrary.Quote.DTO.QuoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {

    @Autowired
    BookRepositoryService bookRepositoryService;

    @Autowired
    APIService apiService;


    @GetMapping("/{id}")
    public String bookInfo(@PathVariable("id") Long id, Model model){
        BookEntity bookEntity = bookRepositoryService.getBook(id);

        model.addAttribute(bookEntity);
        return "Book/bookInfo";
    }

    //사용자 맞춤 도서 추천 서비스 이동
    @GetMapping("/recommendations")
    public String recommendations(){
        return "Book/recommendations";
    }

    //사용자 정보 제출 및 추천 도서 리스트 반환
    @PostMapping("/recommendations")
    public String recommendations(MemberSelectionDTO memberSelectionDto , Model model){

        List<BookEntity> recommendedBooks = bookRepositoryService.searchPopularBooks(memberSelectionDto);

        model.addAttribute("recommendedBooks", recommendedBooks);
        return "Book/recommendationsList";
    }

    @GetMapping("/kdc/{kdc}")
    public String showBooksByGenre(@PathVariable("kdc") String kdc,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   Model model){
        Page<BookEntity> bookEntities = bookRepositoryService.findBooksByGenre(kdc ,page);
        model.addAttribute("bookEntities", bookEntities);

        return "index";
    }

    //-------------------------- 재고위치

    //알라딘 중고 재고 위치 확인
    @GetMapping("/used-inventory-check/{isbn}")
    public String usedInventoryCheck(@PathVariable("isbn") String isbn){
        //재고가있는 지점 반환 재고가 없으면 빈 배열
        //유효성 검사 필수

        apiService.checkUsedStockInBranch(isbn);

        return "redirect:/";
    }

    //---------------------------------------------------- 인용구

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
        bookRepositoryService.createQuote(id , quoteDTO.getAuthor(), quoteDTO.getQuote());
        return  "Quote/BookFinder"; //나중에 마이페이지로 이동
    }

}