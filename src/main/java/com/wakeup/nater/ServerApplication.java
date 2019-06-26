package com.wakeup.nater;

import com.wakeup.nater.core.NATStarter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 21:35
 */
@ComponentScan(value = "com.wakeup.nater")
public class ServerApplication {


    public static void main(String[] args) {
        NATStarter.readyTogo();
        new AnnotationConfigApplicationContext(ServerApplication.class);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
