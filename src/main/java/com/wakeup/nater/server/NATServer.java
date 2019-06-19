package com.wakeup.nater.server;

import com.wakeup.nater.core.CommandReader;
import com.wakeup.nater.core.IService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 21:42
 */
@Component(value = "server")
public class NATServer implements IService {
    private static Logger logger = LoggerFactory.getLogger(NATServer.class);
    private int serverPort;
    private int natPort;

    @Autowired
    @Qualifier(value = "gatewayChannelInitializer")
    private ChannelInitializer gatewayChannelInitializer;

    @Autowired
    @Qualifier(value = "launcherChannelInitializer")
    private ChannelInitializer launcherChannelInitializer;


    public boolean init(CommandReader.Info info) {

        if (info.getServerPort() == info.getNatPort()) {
            return false;
        }
        this.serverPort = info.getServerPort();
        this.natPort = info.getNatPort();

        return true;
    }

    public void start() {
        startLauncherEventLoop();
        startGatewayEventLoop();
        logger.info("服务端启动完成");
    }

    private void startGatewayEventLoop() {
        EventLoopGroup executors = new NioEventLoopGroup(3);
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(executors)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .localAddress(serverPort)
                .childHandler(gatewayChannelInitializer);

        bootstrap.bind();
    }


    private void startLauncherEventLoop() {
        EventLoopGroup executors = new NioEventLoopGroup(3);
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(executors)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .localAddress(natPort)
                .childHandler(launcherChannelInitializer);

        bootstrap.bind();
    }

    public void stop() {

    }


    public int getServerPort() {
        return serverPort;
    }


    public int getNatPort() {
        return natPort;
    }
}
