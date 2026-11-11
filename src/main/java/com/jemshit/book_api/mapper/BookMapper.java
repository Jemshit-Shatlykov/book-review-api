package com.jemshit.book_api.mapper;

import com.jemshit.book_api.dto.BookRequestDTO;
import com.jemshit.book_api.dto.BookResponseDTO;
import com.jemshit.book_api.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookResponseDTO convertToResponseDTO(Book book) {
        return  new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn()
        );
    }

    public Book convertToBookEntity(BookRequestDTO bookRequestDTO) {
        Book book = new Book();

        book.setTitle(bookRequestDTO.title());
        book.setAuthor(bookRequestDTO.author());
        book.setIsbn(bookRequestDTO.isbn());

        return book;
    }
}
