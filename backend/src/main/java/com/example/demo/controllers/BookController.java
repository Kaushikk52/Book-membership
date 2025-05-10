package com.example.demo.controllers;

import com.example.demo.models.Book;
import com.example.demo.services.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/api/book")
public class BookController {
    private final BookService bookServ;

        @PostMapping(value = "/add")
        public ResponseEntity<Map<String,Object>> addBook(@RequestBody Book book){
            Map<String,Object> response = new HashMap<>();
            Book addedBook = bookServ.addBook(book);
            log.info("✔ Book added");
            response.put("message","Book added");
            response.put("book",addedBook);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }


}
