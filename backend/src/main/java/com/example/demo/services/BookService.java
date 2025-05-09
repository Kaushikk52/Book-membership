package com.example.demo.services;

import com.example.demo.constants.BookAvailability;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.models.Book;
import com.example.demo.models.User;
import com.example.demo.repositories.BookRepo;
import com.example.demo.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepo bookRepo;
    private final UserService userServ;
    private final UserRepo userRepo;

    public Book addBook(Book book){
        return bookRepo.save(book);
    }

    public List<Book> getAllBooks(){
        List<Book> bookList = bookRepo.findByIsDeletedFalse();
        if(bookList.isEmpty()){
            throw new NotFoundException("No books found in the repository");
        }
        return bookList;
    }

    public Book getBookById(String id){
        Book book = bookRepo.findByIdAndIsDeletedFalse(id)
               .orElseThrow(() -> new NotFoundException("Book not found: " + id));
        Book updatedBook = updateStatus(book);
        return updatedBook;
    }

    public Book updateStatus(Book book){
        if(book.getStock() < 1){
            book.setStatus(BookAvailability.TAKEN);
        }else {
            book.setStatus(BookAvailability.AVAILABLE);
        }
         return bookRepo.save(book);
    }

    public Book borrowBook(String bookId,String userId){
        Book book = this.getBookById(bookId);
        User user = userServ.getUserById(userId);

        boolean membershipValid = userServ.isMembershipValid(user);
        if(!membershipValid){
            throw new IllegalStateException("User membership is not valid.");
        }

        if (book.getTakenBy().contains(user)) {
            throw new IllegalStateException("User already borrowed this book");
        }

        if(book.getStatus() != BookAvailability.AVAILABLE){
            throw new IllegalStateException("Book is not available.");
        }

        book.setStock(book.getStock() - 1);
        book.getTakenBy().add(user);
        book.setStatus(BookAvailability.TAKEN);
        List<Book> borrowedBooks = user.getBorrowed();
        borrowedBooks.add(book);
        userRepo.save(user);
        return bookRepo.save(book);
    }

    public Book returnBook (String bookId, String userId){
        User user = userServ.getUserById(userId);
        Book book = user.getBorrowed().stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("This user has not borrowed the specified book"));

        if (!book.getTakenBy().remove(user)) {
            throw new IllegalStateException("User didn't borrow this book");
        }

        book.setStock(book.getStock() + 1);
        book.getTakenBy().remove(user);
        if (book.getStock() > 0) {
            book.setStatus(BookAvailability.AVAILABLE);
        }
        return bookRepo.save(book);
    }

    public Book deleteBook(String id){
        Book existingBook = this.getBookById(id);
        existingBook.setDeleted(true);
        return bookRepo.save(existingBook);
    }

}
