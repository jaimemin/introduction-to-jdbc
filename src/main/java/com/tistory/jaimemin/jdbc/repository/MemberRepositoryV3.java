package com.tistory.jaimemin.jdbc.repository;

import com.tistory.jaimemin.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 * DataSourceUtils.getConnection()
 * DataSourceUtils.releaseConnection()
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public Member save(Member member) throws SQLException {
        String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate();

            return member;
        } catch (SQLException e) {
            log.error("[MemberRepositoryV0.save] ERROR {}", e);

            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE member_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setMoney(resultSet.getInt("money"));

                return member;
            } else {
                throw new NoSuchElementException("member not found [memberId=" + memberId + "]");
            }
        } catch (SQLException e) {
            log.error("[MemberRepositoryV0.findById] ERROR {}", e);

            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET money=? WHERE member_id=?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultSize = preparedStatement.executeUpdate();

            log.info("[MemberRepositoryV0.update] resultSize: {}", resultSize);
        } catch (SQLException e) {
            log.error("[MemberRepositoryV0.save] ERROR {}", e);

            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "DELETE FROM member WHERE member_id=?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, memberId);
            int resultSize = preparedStatement.executeUpdate();

            log.info("[MemberRepositoryV0.delete] resultSize: {}", resultSize);
        } catch (SQLException e) {
            log.error("[MemberRepositoryV0.save] ERROR {}", e);

            throw e;
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils 사용 필요
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 주의! 트랜잭션 동기화 사용하려면 DataSourceUtils 사용 필요
        Connection connection = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", connection, connection.getClass());

        return connection;
    }
}
