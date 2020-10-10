package com.wanfajie.lab.db;

public interface Transaction {
    public void onTransaction(DBSession se) throws Exception;
}
