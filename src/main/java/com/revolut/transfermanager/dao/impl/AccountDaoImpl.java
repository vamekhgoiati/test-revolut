package com.revolut.transfermanager.dao.impl;

import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.db.util.DBUtil;
import com.revolut.transfermanager.model.Account;
import org.h2.jdbc.JdbcException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountDaoImpl implements AccountDao {

    private static final Logger logger = Logger.getLogger(AccountDaoImpl.class.getSimpleName());

    // Common Queries
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM ACCOUNTS WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM ACCOUNTS";
    private static final String INSERT_QUERY = "INSERT INTO ACCOUNTS (ID, NAME, BALANCE, VERSION) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE ACCOUNTS SET NAME = ?, BALANCE = ?, VERSION = ? WHERE ID = ? AND VERSION = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM ACCOUNTS WHERE id = ?";
    private static final String UPDATE_BALANCE_QUERY = "UPDATE ACCOUNTS SET BALANCE = ?, VERSION = ? WHERE ID = ? AND VERSION = ?";

    @Override
    public Account getById(String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY);
            preparedStatement.setString(1, id);

            rs = preparedStatement.executeQuery();
            if (rs.next() && rs.getString("id") != null) {
                return new Account(rs.getString("id"),
                        rs.getString("name"),
                        rs.getLong("balance"),
                        rs.getLong("version"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error getting result", e);
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(rs);
            DBUtil.close(connection);
        }
    }

    @Override
    public List<Account> getByIds(List<String> ids) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Account> accounts = new ArrayList<>();
        try {
            connection = DBUtil.getConnection();
            StringBuilder statement = new StringBuilder("SELECT * FROM ACCOUNTS WHERE id IN (");

            for (int i = 0; i < ids.size(); i++) {
                if (i > 0) {
                    statement.append(",");
                }
                statement.append(" ?");
            }

            statement.append(")");

            preparedStatement = connection.prepareStatement(statement.toString());
            for (int i = 0; i < ids.size(); i++) {
                preparedStatement.setString(i + 1, ids.get(i));
            }

            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(rs.getString("id"),
                        rs.getString("name"),
                        rs.getLong("balance"),
                        rs.getLong("version")));
            }

            return accounts;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error getting result", e);
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(rs);
            DBUtil.close(connection);
        }
    }

    @Override
    public List<Account> getAll() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Account> accounts = new ArrayList<>();
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);

            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(rs.getString("id"),
                        rs.getString("name"),
                        rs.getLong("balance"),
                        rs.getLong("version")));
            }

            return accounts;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error getting result", e);
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(rs);
            DBUtil.close(connection);
        }
    }

    @Override
    public Account saveAccount(Account account) {
        if (account.getId() == null) {
            account.setId(UUID.randomUUID().toString());
            account.setVersion(1L);
            createAccount(account);
        } else {
            updateAccount(account);
        }

        return account;
    }

    private void updateAccount(Account account) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_QUERY);
            preparedStatement.setString(1, account.getName());
            preparedStatement.setLong(2, account.getBalance());
            preparedStatement.setLong(3, account.getVersion() + 1);
            preparedStatement.setString(4, account.getId());
            preparedStatement.setLong(5, account.getVersion());

            if (preparedStatement.executeUpdate() != 1) {
                throw new RuntimeException("Couldn't update account with id = " + account.getId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error updating account", e);
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(connection);
        }
    }

    private void createAccount(Account account) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_QUERY);
            preparedStatement.setString(1, account.getId());
            preparedStatement.setString(2, account.getName());
            preparedStatement.setLong(3, account.getBalance());
            preparedStatement.setLong(4, account.getVersion());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error creating account", e);
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(connection);
        }
    }

    @Override
    public void deleteAccount(String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY);
            preparedStatement.setString(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error deleting account", e);
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(connection);
        }
    }

    @Override
    public boolean makeTransfer(Account accountFrom, Account accountTo, Long amount) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            int rowCount = 0;

            preparedStatement = connection.prepareStatement(UPDATE_BALANCE_QUERY);
            preparedStatement.setLong(1, accountFrom.getBalance() - amount);
            preparedStatement.setLong(2, accountFrom.getVersion() + 1);
            preparedStatement.setString(3, accountFrom.getId());
            preparedStatement.setLong(4, accountFrom.getVersion());
            rowCount += preparedStatement.executeUpdate();

            preparedStatement.setLong(1, accountTo.getBalance() + amount);
            preparedStatement.setLong(2, accountTo.getVersion() + 1);
            preparedStatement.setString(3, accountTo.getId());
            preparedStatement.setLong(4, accountTo.getVersion());

            rowCount += preparedStatement.executeUpdate();
            if (rowCount == 2) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                throw new RuntimeException("Error updating accounts, try again");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error updating accounts, try again");
        } finally {
            DBUtil.close(preparedStatement);
            DBUtil.close(connection);
        }

    }
}
