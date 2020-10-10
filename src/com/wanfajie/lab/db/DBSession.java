package com.wanfajie.lab.db;

import java.sql.SQLException;

public interface DBSession {
    public <T> T query(String sql, ResultSetParser<? extends T> parser) throws Exception;

    public void insert(String sql, PrepareStatementSetter setter) throws Exception;

    public int delete(String sql) throws SQLException;

    public int update(String sql) throws SQLException;

    public void transaction(Transaction trans) throws Exception;
    
    public void commit() throws SQLException;

    public void close() throws SQLException;

    public void rollback() throws SQLException;
}
