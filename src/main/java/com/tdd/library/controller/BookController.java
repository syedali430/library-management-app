package com.tdd.library.controller;

import com.tdd.library.model.Book;
import com.tdd.library.model.Member;
import com.tdd.library.repository.BookRepository;
import com.tdd.library.view.swing.BookFormSwingView;

import java.util.List;

public class BookController {

    private final BookRepository bookRepo;
    private final BookFormSwingView bookFormSwingView;

    public BookController(BookRepository bookRepo, BookFormSwingView bookFormSwingView) {
        this.bookRepo = bookRepo;
        this.bookFormSwingView = bookFormSwingView;
    }

    public void loadBooks() {
        List<Book> books = bookRepo.findAll();
        bookFormSwingView.displayBooks(books);
    }

    public void saveBook(Book book) {
        bookRepo.save(book);
        loadBooks();
    }

    public void updateBook(Book book) {
        Book existing = bookRepo.findByIsbn(book.getIsbn());
        if (existing != null) {
            existing.setTitle(book.getTitle());
            existing.setAuthor(book.getAuthor());
            existing.setBorrowedBy(book.getBorrowedBy());
            bookRepo.update(existing);
            loadBooks();
        }
    }

    public void deleteBook(Book book) {
        bookRepo.delete(book);
        loadBooks();
    }

    public void deleteBookByIsbn(String isbn) {
        Book book = bookRepo.findByIsbn(isbn);
        if (book != null) {
            bookRepo.delete(book);
            loadBooks();
        }
    }

    public void populateMembers(List<Member> members) {
    	bookFormSwingView.populateMembers(members);
    }

    public void refreshMembers() {
        List<Member> members = bookRepo.getSessionFactory().openSession()
            .createQuery("FROM Member", Member.class).list();
        bookFormSwingView.populateMembers(members);
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepo.findByIsbn(isbn);
    }
}
