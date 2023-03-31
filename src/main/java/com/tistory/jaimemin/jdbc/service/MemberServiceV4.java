package com.tistory.jaimemin.jdbc.service;

import com.tistory.jaimemin.jdbc.domain.Member;
import com.tistory.jaimemin.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        transfer(fromId, toId, money);
    }

    private void transfer(String fromId, String toId, int money) {
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
