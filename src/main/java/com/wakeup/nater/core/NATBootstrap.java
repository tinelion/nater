package com.wakeup.nater.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/17 22:55
 */
@Component
public class NATBootstrap implements Runnable {
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Autowired
    @Qualifier(value = "server")
    private IService servier;

    @Autowired
    @Qualifier(value = "client")
    private IService client;

    @Autowired
    private BusyMan busyMan;

    @PostConstruct
    private void init(){
        threadPool.submit(this);
    }

    public void run() {
        CommandReader.Info info = new CommandReader.Info();
        info.setServer(false);

        IService service;
        if (info.isServer()) {
            service = servier;
            busyMan.start();
        } else {
            service = client;
        }
        service.init(info);

        service.start();
    }
}
