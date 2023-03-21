package com.tistory.jaimemin.jdbc.service;

import com.tistory.jaimemin.jdbc.domain.Member;
import com.tistory.jaimemin.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;

    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비즈니스 로직 수행
            transfer(fromId, toId, money);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status); // 실패 시 롤백

            throw new IllegalStateException(e);
        }
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
