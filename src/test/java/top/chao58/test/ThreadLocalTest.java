package top.chao58.test;

import org.junit.Test;

public class ThreadLocalTest {

    ThreadLocal<String> threadLocal = new ThreadLocal<>();


    @Test
    public void test1() {
        threadLocal.set(Thread.currentThread().getName() + threadLocal);
        System.out.println(threadLocal.get());
        new Thread(() -> {
            threadLocal.set(Thread.currentThread().getName() + threadLocal);
            System.out.println(threadLocal.get());
        }, "A").start();
    }

}
