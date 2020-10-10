package com.wanfajie.lab.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TransactionSessionFactory implements SessionFactory {

    protected String dbUrl;
    protected String user;
    protected String pass;
    protected int isolation;
    
    public TransactionSessionFactory(String dbUrl, String user, String pass, int isolation) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.pass = pass;
        this.isolation = isolation;
    }

    protected Connection openConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl, user, pass);
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(isolation);
        return conn;
    }

    @Override
    public DBSession createSession() throws SQLException {
        return new DBSessionImpl(openConnection());
    }

    @Override
    public void releaseSession(DBSession session) {
        try {
            session.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            try {
                session.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Builder {
        private String url;
        private String user;
        private String pass;
        private int isolation;

        public Builder readUncommitted() {
            this.isolation = Connection.TRANSACTION_READ_UNCOMMITTED;
            return this;
        }
        
        public Builder readCommitted() {
            this.isolation = Connection.TRANSACTION_READ_COMMITTED;
            return this;
        }
        
        public Builder repeatableRead() {
            this.isolation = Connection.TRANSACTION_REPEATABLE_READ;
            return this;
        }
        
        public Builder serializable() {
            this.isolation = Connection.TRANSACTION_SERIALIZABLE;
            return this;
        }

        public TransactionSessionFactory build() {
            if (url == null || user == null || isolation == 0) {
                throw new IllegalArgumentException();
            }
            return new TransactionSessionFactory(url, user, pass, isolation);
        }
        
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        public Builder username(String user) {
            this.user = user;
            return this;
        }
        
        public Builder password(String pass) {
            this.pass = pass;
            return this;
        }
    }
}
