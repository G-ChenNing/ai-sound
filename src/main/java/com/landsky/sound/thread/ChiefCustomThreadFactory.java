package com.landsky.sound.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class ChiefCustomThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    //注释ThreadGroup，省内存，但是会导致不方便集体管理
//    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public ChiefCustomThreadFactory() {
        SecurityManager s = System.getSecurityManager();
//        group = (s != null) ? s.getThreadGroup() :
//                Thread.currentThread().getThreadGroup();
        namePrefix = "pool-chief-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
//        Thread t = new Thread(group, r,
//                namePrefix + threadNumber.getAndIncrement(),
//                0);
        Thread t = new Thread(r,
                namePrefix + threadNumber.getAndIncrement());
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }


}
