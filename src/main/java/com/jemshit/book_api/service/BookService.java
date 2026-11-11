package com.jemshit.book_api.service;

import com.jemshit.book_api.dto.BookRequestDTO;
import com.jemshit.book_api.dto.BookResponseDTO;
import com.jemshit.book_api.exception.BookNotFoundException;
import com.jemshit.book_api.exception.DuplicateBookException;
import com.jemshit.book_api.mapper.BookMapper;
import com.jemshit.book_api.model.Book;
import com.jemshit.book_api.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository,  BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::convertToResponseDTO)
                .toList();
    }

    public BookResponseDTO insertBook(BookRequestDTO bookRequestDTO) {
        
        if(bookRepository.existsByIsbn(bookRequestDTO.isbn())) {
            throw new DuplicateBookException("Book with ISBN " + bookRequestDTO.isbn() + " already exists");
        }

        return bookMapper.convertToResponseDTO(bookRepository.save(bookMapper.convertToBookEntity(bookRequestDTO)));
    }

    public BookResponseDTO findBookById(Long id) {
        Book book =  bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        return bookMapper.convertToResponseDTO(book);
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public BookResponseDTO updateBook(BookRequestDTO bookRequestDTO, Long id) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));

        existingBook.setTitle(bookRequestDTO.title());
        existingBook.setAuthor(bookRequestDTO.author());
        existingBook.setIsbn(bookRequestDTO.isbn());

        return bookMapper.convertToResponseDTO(bookRepository.save(existingBook));
    }

    public List<BookResponseDTO> findBooksByTitle(String title) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getTitle().equals(title))
                .map(bookMapper :: convertToResponseDTO)
                .toList();
    }

    public List<BookResponseDTO> findBooksByAuthor(String author) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getAuthor().equals(author))
                .map(bookMapper ::convertToResponseDTO)
                .toList();
    }

    public BookResponseDTO searchBookByISBN(String ISBN) {
        return bookRepository.findAll().stream()
                .filter(b -> b.getIsbn().equals(ISBN))
                .map(bookMapper :: convertToResponseDTO)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + ISBN + " not found"));
    }

}
