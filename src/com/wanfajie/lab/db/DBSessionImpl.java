package com.wanfajie.lab.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSessionImpl implements DBSession {

    private Connection connection;
    
    DBSessionImpl(Connection conn) {
        connection = conn;
    }

    @Override
    public <T> T query(String sql, ResultSetParser<? extends T> parser) throws Exception {
        T result;
        try (Statement stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {

            result = parser.parse(rs);
        }
        return result;
    }

    @Override
    public void insert(String sql, PrepareStatementSetter setter) throws Exception {
        try (PreparedStatement stmt =  connection.prepareStatement(sql)) {
            setter.prepare(stmt);
            stmt.execute();
        }
    }

    @Override
    public int update(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    @Override
    public int delete(String sql) throws SQLException {
        return update(sql);
    }

    @Override
    public void transaction(Transaction trans) throws Exception {
        try {
            trans.onTransaction(this);
            this.commit();
        } finally {
            try {
                this.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }
}
