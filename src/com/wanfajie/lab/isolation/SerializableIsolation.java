package com.wanfajie.lab.isolation;

import com.wanfajie.lab.db.SessionFactory;
import com.wanfajie.lab.db.TransactionSessionFactory;

public class SerializableIsolation {

    public static void main(String[] args) throws InterruptedException {
        SessionFactory factory = new TransactionSessionFactory.Builder()
                .url(DBConfig.DB_URL)
                .username(DBConfig.USERNAME)
                .password(DBConfig.PASSWORD)
                .serializable()
                .build();  // NOTE: 隔离级别为序列化

        // 脏读、不可重复读、幻读均不会发生
        System.out.println("----------------");
        DirtyRead.startTransactions(factory);
        
        System.out.println("----------------");
        NonRepeatableRead.startTransactions(factory);
        
        System.out.println("----------------");
        PhantomRead.startTransactions(factory);
    }

}
