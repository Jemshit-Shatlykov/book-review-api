package com.jemshit.book_api.controller;

import com.jemshit.book_api.dto.BookRequestDTO;
import com.jemshit.book_api.dto.BookResponseDTO;
import com.jemshit.book_api.service.BookService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponseDTO> getBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public BookResponseDTO addBook(@Valid @RequestBody BookRequestDTO bookRequestDTO) {
        return bookService.insertBook(bookRequestDTO);
    }

    @GetMapping("{id}")
    public BookResponseDTO getBookById(@PathVariable Long id) {
       return bookService.findBookById(id);
    }

    @DeleteMapping("{id}")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
    }

    @PutMapping("{id}")
    public BookResponseDTO updateBook(@Valid @RequestBody BookRequestDTO bookRequestDTO, @PathVariable Long id) {
        return bookService.updateBook(bookRequestDTO, id);
    }

    // Get books by title
    @GetMapping("/search-by-title")
    public List<BookResponseDTO> getBookByTitle(@RequestParam String title) {
        return bookService.findBooksByTitle(title);
    }

    // Get books by author
    @GetMapping("/search-by-author")
    public List<BookResponseDTO> getBooksByAuthor(@RequestParam String author) {
        return bookService.findBooksByAuthor(author);
    }

    // Search book by isbn
    @GetMapping("/search-by-isbn")
    public BookResponseDTO searchBookByIsbn(@RequestParam String isbn) {
        return bookService.searchBookByISBN(isbn);
    }

}
