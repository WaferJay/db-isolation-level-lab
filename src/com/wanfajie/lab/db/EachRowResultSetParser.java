package com.wanfajie.lab.db;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public interface EachRowResultSetParser<T> extends ResultSetParser<List<T>> {

    public abstract T forEachRow(ResultSet rs) throws Exception;
    
    @Override
    public default List<T> parse(ResultSet rs) throws Exception {
        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst();

        List<T> result = new ArrayList<>(rowCount);
        while (rs.next()) {
            T row = forEachRow(rs);
            result.add(row);
        }
        return result;
    }
}
