package com.tdd.library.controller;

import com.tdd.library.model.Member;
import com.tdd.library.repository.MemberRepository;
import com.tdd.library.view.swing.MemberFormSwingView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class MemberControllerTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberFormSwingView memberFormSwingView;

    @Mock
    private BookController bookController;

    @InjectMocks
    private MemberController memberController;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        memberController = new MemberController(memberRepository, memberFormSwingView);
        memberController.setBookController(bookController);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testLoadMembers() {
        List<Member> members = Arrays.asList(new Member());
        when(memberRepository.findAll()).thenReturn(members);

        memberController.loadMembers();

        verify(memberRepository).findAll();
        verify(memberFormSwingView).displayMembers(members);
    }

    @Test
    public void testSaveMember() {
        Member member = new Member();
        memberController.saveMember(member);

        verify(memberRepository).save(member);
        verify(memberRepository).findAll();
        verify(memberFormSwingView).displayMembers(anyList());
    }

    @Test
    public void testDeleteMember() {
        Member member = new Member();
        memberController.deleteMember(member);

        verify(memberRepository).deleteMemberAndReleaseBooks(member);
        verify(memberRepository).findAll();
        verify(memberFormSwingView).displayMembers(anyList());
        verify(bookController).loadBooks();
    }

    @Test
    public void testGetMemberByEmail() {
        Member member = new Member();
        when(memberRepository.findByEmail("test@example.com")).thenReturn(member);

        Member result = memberController.getMemberByEmail("test@example.com");

        verify(memberRepository).findByEmail("test@example.com");
        assert result != null;
    }

    @Test
    public void testGetAllMembers() {
        List<Member> members = Arrays.asList(new Member());
        when(memberRepository.findAll()).thenReturn(members);

        List<Member> result = memberController.getAllMembers();

        verify(memberRepository).findAll();
        assert result.equals(members);
    }

    @Test
    public void testFindByEmail() {
        Member member = new Member();
        when(memberRepository.findByEmail("hello@test.com")).thenReturn(member);

        Member result = memberController.findByEmail("hello@test.com");

        verify(memberRepository).findByEmail("hello@test.com");
        assert result != null;
    }
}