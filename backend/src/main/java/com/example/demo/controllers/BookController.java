package com.example.demo.controllers;

import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.services.BookService;
import com.example.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/api/book")
public class BookController {
    private final BookService bookServ;
    private final UserService userServ;

    @PostMapping(value = "/add")
    public ResponseEntity<Map<String,Object>> addBook(@RequestBody Book book){
            Map<String,Object> response = new HashMap<>();
            Book addedBook = bookServ.addBook(book);
            log.info("✔ Book added");
            response.put("message","Book added");
            response.put("book",addedBook);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

    @GetMapping(value= "/all")
    public ResponseEntity<Map<String,Object>> getAll(){
        Map<String,Object> response = new HashMap<>();
        List<Book> bookList = bookServ.getAllBooks();
        log.info("✔ All books retrieved");
        response.put("message","All books retrieved");
        response.put("books",bookList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "filter")
    public ResponseEntity<Map<String,Object>> getFilteredBooks(
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String name
            ){
        Map<String,Object> response = new HashMap<>();
        Map<String,Object> filters = new HashMap<>();
        if(categories != null && !categories.isEmpty()) filters.put("category",categories);
        if(status != null) filters.put("status",status);
        if(author != null) filters.put("author",author);
        if(name != null) filters.put("name",name);

        List<Book> filteredBooks = bookServ.getFilteredBooks(filters);
        if(filteredBooks.isEmpty()){
            log.warn("No Books found");
            response.put("message", "No Books found");
        }else{
            log.info("Retrieved filtered Books");
            response.put("message", "Retrieved filtered Books : "+filteredBooks.size());
        }
        response.put("books",filteredBooks);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/borrow/{id}")
    public ResponseEntity<Map<String,Object>> borrowBook(@PathVariable String id,Principal principal){
            Map<String,Object> response = new HashMap<>();
            User currentUser = (User)  userServ.loadUserByUsername(principal.getName());
            Book borrowedBook = bookServ.borrowBook(id,currentUser.getId());
            log.info("✔ Book borrowed");
            response.put("message","Book borrowed");
            response.put("book",borrowedBook);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/return/{id}")
    public ResponseEntity<Map<String,Object>> returnBook(@PathVariable String id,Principal principal){
        Map<String,Object> response = new HashMap<>();
        User currentUser = (User)  userServ.loadUserByUsername(principal.getName());
        Book returnedBook = bookServ.returnBook(id, currentUser.getId());
        log.info("✔ Book returned");
        response.put("message","Book returned");
        response.put("book",returnedBook);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/reports")
    public ResponseEntity<Map<String,Object>> getCategoryReport(@RequestParam(required = false) int limit){
        Map<String,Object> response = new HashMap<>();
        Map<String,Object> reports = bookServ.getCategoryReports(limit);
        log.info("✔ Book reports retrieved");
        response.put("message","Book reports retrieved");
        response.put("reports",reports);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Map<String,Object>> removeBook(@PathVariable String id){
            Map<String,Object> response = new HashMap<>();
            Book deletedBook = bookServ.deleteBook(id);
            log.info("✔ Book deleted successfully");
            response.put("message","Book deleted successfully");
            response.put("book",deletedBook);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
