package com.wakeup.nater.client;

import com.wakeup.nater.core.CommandReader;
import com.wakeup.nater.core.IService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 21:42
 */
@Component(value = "client")
public class NATClient implements IService {
    private static Logger logger = LoggerFactory.getLogger(NATClient.class);
    private static AtomicBoolean stop = new AtomicBoolean(false);
    private int serverPort;
    private String serverHost;

    @Autowired
    private ChannelMaker channelMaker;

    public boolean init(CommandReader.Info info) {
        this.serverPort = info.getNatPort();
        this.serverHost = info.getHost();

        return true;
    }

    public void start() {

        EventLoopGroup executors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.group(executors)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(serverHost, serverPort))
                .handler(new EmptyChannelHandlerInitializer());

        channelMaker.setStartPoint(bootstrap);
        channelMaker.make();
        logger.debug("客户端启动完成!");
        this.sync();
    }

    private void sync() {
        while (!stop.get()) {
            try {
                doMonitor();
                this.wait();
            } catch (Exception ignore) {

            }
        }
    }

    private void doMonitor() {

    }

    public void stop() {
        stop.compareAndSet(false, true);
    }


    public int getServerPort() {
        return serverPort;
    }
}
