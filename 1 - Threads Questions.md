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

-------------------------------------------------

        a.start();
        b.start();
    }
}
