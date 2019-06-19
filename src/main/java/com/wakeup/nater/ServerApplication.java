package com.wakeup.nater;

import com.wakeup.nater.core.BusyMan;
import com.wakeup.nater.core.CommandReader;
import com.wakeup.nater.core.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 21:35
 */
@ComponentScan(value = "com.wakeup.nater")
public class ServerApplication {


    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ServerApplication.class);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
