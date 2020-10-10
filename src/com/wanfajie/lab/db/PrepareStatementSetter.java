package com.wanfajie.lab.db;

import java.sql.PreparedStatement;

public interface PrepareStatementSetter {
    public void prepare(PreparedStatement stmt) throws Exception;
}
