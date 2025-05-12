package com.example.demo.models;

import com.example.demo.constants.BookAvailability;
import com.example.demo.helpers.StringListConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Book extends Auditable{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "name",nullable = false,length = 30)
    private String name;

    private int stock;

    private String author;

    @Column(name = "categories", columnDefinition = "json")
    @Convert(converter = StringListConverter.class)
    private List<String> categories;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private BookAvailability status;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "book_borrowers",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> takenBy;

    private boolean isDeleted;

    @PrePersist
    private void prePersist() {
        if (this.status == null) {
            this.status = BookAvailability.AVAILABLE;
        }
    }

}
