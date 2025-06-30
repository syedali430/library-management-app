package com.tdd.library.controller;

import com.tdd.library.model.Member;
import com.tdd.library.repository.MemberRepository;
import com.tdd.library.view.swing.MemberFormSwingView;

import java.util.List;

public class MemberController {

    private final MemberRepository memberRepo;
    private final MemberFormSwingView memberFormSwingView;

    private BookController bookController;

    public MemberController(MemberRepository memberRepo, MemberFormSwingView memberFormSwingView) {
        this.memberRepo = memberRepo;
        this.memberFormSwingView = memberFormSwingView;
        this.memberFormSwingView.setMemberController(this);
    }

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void loadMembers() {
        List<Member> members = memberRepo.findAll();
        memberFormSwingView.displayMembers(members);
    }

    public void saveMember(Member member) {
        memberRepo.save(member);
        loadMembers();
    }

    public void deleteMember(Member member) {
        memberRepo.deleteMemberAndReleaseBooks(member);
        loadMembers();
        if (bookController != null) {
            bookController.loadBooks();
        }
    }

    public Member findByEmail(String email) {
        return memberRepo.findByEmail(email);
    }

    public List<Member> getAllMembers() {
        return memberRepo.findAll();
    }

    public Member getMemberByEmail(String email) {
        return memberRepo.findByEmail(email);
    }
}
