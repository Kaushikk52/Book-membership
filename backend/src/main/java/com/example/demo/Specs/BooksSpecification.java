package com.example.demo.Specs;

import com.example.demo.models.Book;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BooksSpecification {

    public static Specification<Book> findByCriteria(Map<String,Object> filters){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filters.containsKey("category")){
                List<String> categories = (List<String>) filters.get("category");
                for(String category : categories){
                    predicates.add(criteriaBuilder.like(root.get("categories"),"%"+category+"%"));
                }
            }

            if(filters.containsKey("status")){
                String status = (String) filters.get("status");
                predicates.add(criteriaBuilder.equal(root.get("status"),status));
            }

            if(filters.containsKey("name")){
                String name = (String) filters.get("name");
                predicates.add(criteriaBuilder.equal(root.get("name"),name));
            }

            if(filters.containsKey("author")){
                String author = (String) filters.get("author");
                predicates.add(criteriaBuilder.equal(root.get("author"),author));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

    }
}
