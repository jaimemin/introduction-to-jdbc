package com.tistory.jaimemin.jdbc.service;

import com.tistory.jaimemin.jdbc.domain.Member;
import com.tistory.jaimemin.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate transactionTemplate;

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        transactionTemplate.executeWithoutResult((status) -> {
            try {
                transfer(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void transfer(String fromId, String toId, int money) throws SQLException {
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
