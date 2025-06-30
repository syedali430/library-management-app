package com.tdd.library.app;

import com.tdd.library.controller.BookController;
import com.tdd.library.controller.MemberController;
import com.tdd.library.repository.BookRepository;
import com.tdd.library.repository.MemberRepository;
import com.tdd.library.view.swing.BookFormSwingView;
import com.tdd.library.view.swing.MemberFormSwingView;

import java.awt.EventQueue;

public class LibraryApp {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            
            BookRepository bookRepo = new BookRepository();
            MemberRepository memberRepo = new MemberRepository();
            
            BookFormSwingView bookFormSwingView = new BookFormSwingView();

            BookController bookController = new BookController(bookRepo, bookFormSwingView);
            bookFormSwingView.setBookController(bookController);

            MemberFormSwingView memberForm = new MemberFormSwingView(() -> {
                bookController.refreshMembers();
            });

            MemberController memberController = new MemberController(memberRepo, memberForm);
            memberController.setBookController(bookController); 

            bookFormSwingView.setMemberFormOpener(() -> memberForm.setVisible(true));

            bookController.loadBooks();
            memberController.loadMembers();

            bookFormSwingView.setVisible(true);
        });
    }
}
