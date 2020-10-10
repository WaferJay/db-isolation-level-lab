package com.wanfajie.lab.isolation;

import java.util.List;

import com.wanfajie.lab.db.DBSession;
import com.wanfajie.lab.db.EachRowResultSetParser;
import com.wanfajie.lab.db.SessionFactory;
import com.wanfajie.lab.db.TransactionSessionFactory;

class PhantomReadTransaction1 extends AbstractTransaction {

    public PhantomReadTransaction1(SessionFactory factory) {
        super("事务1", factory);
    }

    public List<String> getEmployeeNames(DBSession se) throws Exception {
        List<String> names = se.query("select name from employee;", (EachRowResultSetParser<String>) rs -> {
            return rs.getString(1);
        });
        return names;
    }

    private StringBuilder joinNames(StringBuilder sb, List<String> names) {
        for (String name : names) {
            sb.append(name)
              .append("、");
        }
        sb.deleteCharAt(sb.length()-1);  // 移除末尾多余顿号
        return sb;
    }

    @Override
    public void onTransaction(DBSession se) throws Exception {
        List<String> beforeUpdateNames = getEmployeeNames(se);
        StringBuilder sb = new StringBuilder("我查询到员工有");
        joinNames(sb, beforeUpdateNames);
        say(sb.toString());

        doSomething(2000);
        boolean isPhantomRead = false;
        List<String> names = getEmployeeNames(se);
        sb = new StringBuilder("我再次查询了所有员工, 发现");
        if (names.size() != beforeUpdateNames.size()) {
            int diff = names.size() - beforeUpdateNames.size();
            if (diff > 0) {
                sb.append("多了")
                  .append(diff)
                  .append("人, 多出来的是");
                names.removeAll(beforeUpdateNames);
                joinNames(sb, names);
            } else {
                sb.append("少了")
                  .append(Math.abs(diff))
                  .append("人, 少了的是");
                beforeUpdateNames.removeAll(names);
                joinNames(sb, beforeUpdateNames);
            }
            isPhantomRead = true;
        } else {
            sb.append("没有变化");
        }
        say(sb.toString());
        if (isPhantomRead) {
            say("我出现了**幻读**!");
        }
    }
}

class PhantomReadTransaction2 extends AbstractTransaction {

    public PhantomReadTransaction2(SessionFactory factory) {
        super("事务2", factory);
    }

    public String randomName() {
        char initial = (char) (Math.random() * ('Z'-'A') + 'A');
        StringBuilder sb = new StringBuilder();
        sb.append(initial);
        int count = (int) (Math.random() * 8 + 4);
        for (int i=0; i<count; i++) {
            sb.append((char) (Math.random() * ('z'-'a') + 'a'));
        }
        return sb.toString();
    }
    
    @Override
    public void onTransaction(DBSession se) throws Exception {
        doSomething(1000);
        se.insert("insert into employee (name, salary) values (?, ?)", stmt -> {
            stmt.setString(1, randomName());
            stmt.setDouble(2, 3000);
        });
        say("    我插入并**提交**了一条新员工的记录");
    }
}

public class PhantomRead {
    
    private static SessionFactory createSessionFactory() {
        SessionFactory factory = new TransactionSessionFactory.Builder()
                .url(DBConfig.DB_URL)
                .username(DBConfig.USERNAME)
                .password(DBConfig.PASSWORD)
                .repeatableRead()  // NOTE: 隔离级别为可重复读
                .build();
        return factory;
    }

    public static void startTransactions(SessionFactory factory) throws InterruptedException {
        Thread thread1 = new Thread(new PhantomReadTransaction1(factory));
        Thread thread2 = new Thread(new PhantomReadTransaction2(factory));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }
    
    public static void main(String[] args) throws InterruptedException {
        startTransactions(createSessionFactory());
    }

}
