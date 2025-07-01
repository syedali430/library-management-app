package com.tdd.library.controller;

import com.tdd.library.model.Book;
import com.tdd.library.model.Member;
import com.tdd.library.repository.BookRepository;
import com.tdd.library.view.swing.BookFormSwingView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookFormSwingView bookFormSwingView;

    @InjectMocks
    private BookController bookController;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testLoadBooks() {
        List<Book> books = Collections.singletonList(new Book());
        when(bookRepository.findAll()).thenReturn(books);

        bookController.loadBooks();

        verify(bookFormSwingView).displayBooks(books);
    }

    @Test
    public void testSaveBook() {
        Book book = new Book();
        bookController.saveBook(book);
        verify(bookRepository).save(book);
        verify(bookRepository).findAll();
    }

    @Test
    public void testUpdateBookWhenExists() {
        Book existing = new Book();
        existing.setIsbn("1234567890");

        Book updated = new Book();
        updated.setIsbn("1234567890");
        updated.setTitle("Updated Title");
        updated.setAuthor("Updated Author");
        updated.setBorrowedBy(new Member());

        when(bookRepository.findByIsbn("1234567890")).thenReturn(existing);

        bookController.updateBook(updated);

        verify(bookRepository).update(existing);
        verify(bookRepository).findByIsbn("1234567890");
        verify(bookRepository).findAll();
    }

    @Test
    public void testUpdateBookWhenDoesNotExist() {
        Book updated = new Book();
        updated.setIsbn("9999999999");

        when(bookRepository.findByIsbn("9999999999")).thenReturn(null);

        bookController.updateBook(updated);

        verify(bookRepository, never()).update(any());
    }

    @Test
    public void testDeleteBookByIsbnWhenExists() {
        Book book = new Book();
        book.setIsbn("1234567890");

        when(bookRepository.findByIsbn("1234567890")).thenReturn(book);

        bookController.deleteBookByIsbn("1234567890");

        verify(bookRepository).delete(book);
        verify(bookRepository).findAll();
    }

    @Test
    public void testDeleteBookByIsbnWhenDoesNotExist() {
        when(bookRepository.findByIsbn("0000000000")).thenReturn(null);

        bookController.deleteBookByIsbn("0000000000");

        verify(bookRepository, never()).delete(any());
    }

    @Test
    public void testGetBookByIsbn() {
        Book book = new Book();
        book.setIsbn("1234567890");

        when(bookRepository.findByIsbn("1234567890")).thenReturn(book);

        bookController.getBookByIsbn("1234567890");

        verify(bookRepository).findByIsbn("1234567890");
    }

    @Test
    public void testPopulateMembers() {
        Member member = new Member();
        List<Member> members = Arrays.asList(member);

        bookController.populateMembers(members);

        verify(bookFormSwingView).populateMembers(members);
    }
}
