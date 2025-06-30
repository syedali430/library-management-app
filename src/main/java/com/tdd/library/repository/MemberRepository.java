package com.tdd.library.repository;

import com.tdd.library.model.Book;
import com.tdd.library.model.Member;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class MemberRepository {

    private final SessionFactory sessionFactory;

    public MemberRepository() {
        this.sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public void save(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(member);
            tx.commit();
        }
    }

    public void delete(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(member);
            tx.commit();
        }
    }

    public List<Member> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Member", Member.class).list();
        }
    }

    public Member findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Member WHERE email = :email", Member.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    public void deleteMemberAndReleaseBooks(Member member) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Member attachedMember = session.get(Member.class, member.getId());

            if (attachedMember != null) {
          
                List<Book> borrowedBooks = session.createQuery(
                        "FROM Book WHERE borrowedBy.id = :memberId", Book.class)
                        .setParameter("memberId", member.getId())
                        .list();

                for (Book book : borrowedBooks) {
                    book.setBorrowedBy(null);
                    session.update(book);
                }

                session.delete(attachedMember);
            }

            tx.commit();
        }
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
