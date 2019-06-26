package com.wakeup.nater.core;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/17 22:55
 */
@Component
public class NATStarter implements Runnable {
    private static final String LOGO_PATH = "src\\main\\resources\\static\\logo";
    private static final CommandReader.Info CMD = new CommandReader.Info();
    private static Logger logger = LoggerFactory.getLogger(NATStarter.class);
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
    private void init() {
        threadPool.submit(this);
    }

    public void run() {


        IService service;
        if (CMD.isServer()) {
            service = servier;
            busyMan.start();
        } else {
            service = client;
        }
        service.init(CMD);

        service.start();
    }

    public static void readyTogo() {
        try {
            CommandReader.readCommand(CMD);
            logger.debug("开始启动" + (CMD.isServer() ? "服务端(Starting NAT server)" : "客户端(Starting NAT client)"));
            if (CMD.isServer()){
                logger.debug("服务监听端口：" + CMD.getServerPort());
            } else {
                logger.debug("");
            }
            logger.debug(System.lineSeparator() + "\033[32m" +
                    FileUtils.readFileToString(new File(LOGO_PATH), Charset.forName("utf-8")) + "\033[0m");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
