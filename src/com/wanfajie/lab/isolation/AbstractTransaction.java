package com.wanfajie.lab.isolation;

import java.sql.SQLException;

import com.wanfajie.lab.db.DBSession;
import com.wanfajie.lab.db.SessionFactory;
import com.wanfajie.lab.db.Transaction;

public abstract class AbstractTransaction implements Runnable, Transaction {

    private SessionFactory factory;
    private String name;
    
    public AbstractTransaction(String name, SessionFactory factory) {
        this.factory = factory;
        this.name = name;
    }

    public void doSomething(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void say(String msg) {
        System.out.println(name + ": " + msg);
    }

    @Override
    public void run() {
        DBSession se;
        try {
            se = factory.createSession();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            se.transaction(this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            factory.releaseSession(se);
        }
    }
}
