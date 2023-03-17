package com.tistory.jaimemin.jdbc.repository;

import com.tistory.jaimemin.jdbc.connection.DbConnectionUtil;
import com.tistory.jaimemin.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

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
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("resultSet close ERROR {}", e.getMessage());
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("statement close ERROR {}", e.getMessage());
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("connection close ERROR {}", e.getMessage());
            }
        }
    }

    private Connection getConnection() {
        return DbConnectionUtil.getConnection();
    }
}
