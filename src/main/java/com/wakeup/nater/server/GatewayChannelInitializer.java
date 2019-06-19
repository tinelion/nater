package com.wakeup.nater.server;

import com.wakeup.nater.core.BusyMan;
import com.wakeup.nater.server.handler.GatewayChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 22:25
 */
@Component(value = "gatewayChannelInitializer")
public class GatewayChannelInitializer extends ChannelInitializer {
    private static final String ENCODER = "encoder";
    private static final String DECODER = "decoder";


    @Autowired
    private BusyMan busyMan;

    protected void initChannel(Channel channel) throws Exception {
        GatewayChannelHandler gatewayChannelHandler = new GatewayChannelHandler();
        gatewayChannelHandler.setBusyMan(busyMan);

        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(ENCODER, new HttpResponseEncoder());
        pipeline.addLast(DECODER, new HttpRequestDecoder());
        pipeline.addLast(gatewayChannelHandler);
    }
}
