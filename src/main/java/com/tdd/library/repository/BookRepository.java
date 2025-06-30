package com.tdd.library.repository;

import com.tdd.library.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Transaction;

import java.util.List;

public class BookRepository {

    private final SessionFactory sessionFactory;

    public BookRepository() {
        this.sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public void save(Book book) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(book);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(Book book) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(book);
        session.getTransaction().commit();
        session.close();
    }

    public List<Book> findAll() {
        Session session = sessionFactory.openSession();
        List<Book> books = session.createQuery("FROM Book", Book.class).list();
        session.close();
        return books;
    }

    public Book findByIsbn(String isbn) {
        Session session = sessionFactory.openSession();
        Book book = session.createQuery("FROM Book WHERE isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .uniqueResult();
        session.close();
        return book;
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public void update(Book book) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(book);
            tx.commit();
        }
    }


}
