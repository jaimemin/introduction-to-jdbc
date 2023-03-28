package com.tistory.jaimemin.jdbc.service;

import com.tistory.jaimemin.jdbc.domain.Member;
import com.tistory.jaimemin.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        validateBalance(fromMember, money);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validateTransfer(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validateBalance(Member fromMember, int money) {
        if (fromMember.getMoney() < money) {
            throw new IllegalStateException("마이너스 통장을 개설하지 않았습니다.");
        }
    }

    private static void validateTransfer(Member toMember) {
        if  (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
