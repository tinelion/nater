package com.wakeup.nater.server;

import com.wakeup.nater.core.BusyMan;
import com.wakeup.nater.core.ChannelManager;
import com.wakeup.nater.server.handler.LauncherChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 22:25
 */
@Component(value = "launcherChannelInitializer")
public class LauncherChannelInitializer extends ChannelInitializer {
    private static final String ENCODER = "encoder";
    private static final String DECODER = "decoder";


    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private BusyMan busyMan;

    protected void initChannel(Channel channel) throws Exception {
        LauncherChannelHandler handler = new LauncherChannelHandler();
        handler.setBusyMan(busyMan);
        handler.setChannelManager(channelManager);

        channel.pipeline().addLast(handler);
    }

}
