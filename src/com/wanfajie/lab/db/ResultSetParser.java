package com.wanfajie.lab.db;

import java.sql.ResultSet;

public interface ResultSetParser<T> {
    public T parse(ResultSet rs) throws Exception;
}
