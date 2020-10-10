package com.wanfajie.lab.isolation;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========= Read Unommitted =========");
        DirtyRead.main(null);
        
        System.out.println("========= Read Committed =========");
        NonRepeatableRead.main(null);
        
        System.out.println("========== Repeatable Read ==========");
        PhantomRead.main(null);
        
        System.out.println("========== Serializable ==========");
        SerializableIsolation.main(null);
    }

}
