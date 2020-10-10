package com.wanfajie.lab.db;

import java.sql.SQLException;

public interface SessionFactory {
    public DBSession createSession() throws SQLException;
    public void releaseSession(DBSession session);
}
