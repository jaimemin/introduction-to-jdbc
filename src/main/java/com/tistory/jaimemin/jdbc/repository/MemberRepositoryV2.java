package com.tistory.jaimemin.jdbc.repository;

import com.tistory.jaimemin.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - ConnectionParam
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV2 {

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

    public Member findById(Connection connection, String memberId) throws SQLException {
        String sql = "SELECT * FROM member WHERE member_id = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
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
            /**
             * parameter로 넘겨받은 conneciton은 여기서 닫지 않고 service에서 닫는다
             */
            close(null, preparedStatement, null);
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

    public void update(Connection connection, String memberId, int money) throws SQLException {
        String sql = "UPDATE member SET money=? WHERE member_id=?";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, memberId);
            int resultSize = preparedStatement.executeUpdate();

            log.info("[MemberRepositoryV0.update] resultSize: {}", resultSize);
        } catch (SQLException e) {
            log.error("[MemberRepositoryV0.save] ERROR {}", e);

            throw e;
        } finally {
            close(null, preparedStatement, null);
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
        JdbcUtils.closeConnection(connection);
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        log.info("get connection={}, class={}", connection, connection.getClass());

        return connection;
    }
}
