package com.jemshit.book_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "user_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JoinColumn(nullable = false)
    @ManyToOne
    private Book book;

    @NotNull
    @JoinColumn(nullable = false)
    @ManyToOne
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String reviewText;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private double rating;

    public Review(){}

    public Review(Book book, User user, String reviewText, double rating) {
        this.book = book;
        this.user = user;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Review review)) return false;
        return Objects.equals(book, review.book) && Objects.equals(user, review.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, user);
    }

    @Override
    public String toString() {
        return "Book: " + book + "\n Reviewer: " + user + "\n Review: " + reviewText + "\n Rating: " + rating;
    }
}
