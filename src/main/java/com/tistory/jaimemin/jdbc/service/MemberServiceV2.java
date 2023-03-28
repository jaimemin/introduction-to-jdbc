package com.tistory.jaimemin.jdbc.service;

import com.tistory.jaimemin.jdbc.domain.Member;
import com.tistory.jaimemin.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;

    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false); // 트랜잭션 시작
            // 비즈니스 로직 수행
            transfer(connection, fromId, toId, money);
            connection.commit(); // 성공 시 커밋
        } catch (Exception e) {
            connection.rollback(); // 실패 시 롤백

            throw new IllegalStateException(e);
        } finally {
            releaseConnection(connection);
        }
    }

    private void transfer(Connection connection, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        validateBalance(fromMember, money);

        memberRepository.update(connection, fromId, fromMember.getMoney() - money);
        validateTransfer(toMember);
        memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private static void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true); // 커넥션 풀 고려하여 커밋 모드 롤백
                connection.close();
            } catch (Exception e) {
                log.info("[releaseConnection] ERROR", e);
            }
        }
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
