1. Print you name by char-to-char with2 different threads

Ans:

public class Test {

    static String str = "NIRAJ";
    static int i = 0;
    static boolean t1 = true;

    public static void main(String[] args) {

        Thread a = new Thread(() -> {
            while (i < str.length()) {
                synchronized (str) {
                    while (!t1)
                        try { str.wait(); } catch (Exception e) {}

                    if (i < str.length())
                        System.out.println("T1 : " + str.charAt(i++));

                    t1 = false;
                    str.notify();
                }
            }
        });

        Thread b = new Thread(() -> {
            while (i < str.length()) {
                synchronized (str) {
                    while (t1)
                        try { str.wait(); } catch (Exception e) {}

                    if (i < str.length())
                        System.out.println("T2 : " + str.charAt(i++));

                    t1 = true;
                    str.notify();
                }
            }
        });

        a.start();
        b.start();
    }
}

-------------------------------------------------


2. Singleton Class

package org.example.singleton;

public class TVSet {
    private static volatile TVSet tvSetInstance = null;
 
    private TVSet() {
        System.out.println("TV Set Instance created");
    }

    public static TVSet getTvSetInstance() {
        if (tvSetInstance == null) {
            synchronized (TVSet.class) {
                if (tvSetInstance == null) {
                    tvSetInstance = new TVSet();
                }
            }
        }
        return tvSetInstance;
    }
}

-------------------------------------------------

3. Deadlock

class DeadlockDemo {

    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread 1 acquired lock1");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread 1 waiting for lock2");

                synchronized (lock2) {
                    System.out.println("Thread 1 acquired lock2");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread 2 acquired lock2");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread 2 waiting for lock1");

                synchronized (lock1) {
                    System.out.println("Thread 2 acquired lock1");
                }
            }
        });

        t1.start();
        t2.start();
    }
}


