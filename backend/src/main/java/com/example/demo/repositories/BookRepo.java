package com.example.demo.repositories;

import com.example.demo.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book,String>, JpaSpecificationExecutor<Book> {
    List<Book> findByIsDeletedFalse();

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.takenBy")
    List<Book> findAllWithUsers();

    Optional<Book> findByIdAndIsDeletedFalse(String id);
}
