package com.wanfajie.lab.isolation;

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
        try {
            factory.transaction(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
