package com.wanfajie.lab.isolation;

import com.wanfajie.lab.db.DBSession;
import com.wanfajie.lab.db.SessionFactory;
import com.wanfajie.lab.db.TransactionSessionFactory;

class DirtyReadTransaction1 extends AbstractTransaction {

    public DirtyReadTransaction1(SessionFactory factory) {
        super("事务1", factory);
    }

    public double querySalary(DBSession se) throws Exception {
        double salary = se.query("select salary from employee where name = 'Mike';", rs -> {
            rs.next();
            return rs.getDouble(1);
        });
        return salary;
    }

    @Override
    public void onTransaction(DBSession se) throws Exception {
        doSomething(1000);
        double salaryUncommitted = querySalary(se);
        say("我读到了Mike的薪水，值为" + salaryUncommitted);
        doSomething(2000);
        double salary = querySalary(se);
        if (salaryUncommitted != salary) {
            say("我尝试再次读取薪水值, 结果值从" + salaryUncommitted + "变为了" + salary + "!");
            say("我出现了**脏读**!");
        } else {
            say("我尝试再次读取薪水值, 结果没变化");
        }
    }
}

class DirtyReadTransaction2 extends AbstractTransaction {

    public DirtyReadTransaction2(SessionFactory factory) {
        super("事务2", factory);
    }

    @Override
    public void onTransaction(DBSession se) throws Exception {
        se.update("update employee set salary = salary + 1000 where name = 'Mike';");
        say("    我给Mike涨工资了, 给他涨了1000, 但现在**还没**提交修改");
        doSomething(2000);
        say("    我核对了一下, 发现涨工资的不是Mike, 我**回退**了修改");
        se.rollback();
    }
}

public class DirtyRead {

    private static SessionFactory createSessionFactory() {
        SessionFactory factory = new TransactionSessionFactory.Builder()
                .url(DBConfig.DB_URL)
                .username(DBConfig.USERNAME)
                .password(DBConfig.PASSWORD)
                .readUncommitted()  // NOTE: 隔离级别为读未提交
                .build();
        return factory;
    }
    
    public static void startTransactions(SessionFactory factory) throws InterruptedException {
        Thread thread1 = new Thread(new DirtyReadTransaction1(factory));
        Thread thread2 = new Thread(new DirtyReadTransaction2(factory));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }
    
    public static void main(String[] args) throws InterruptedException {
        startTransactions(createSessionFactory());
    }
}
