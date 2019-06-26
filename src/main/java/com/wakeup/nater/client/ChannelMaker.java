package com.wakeup.nater.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 22:56
 */
@Component
public class ChannelMaker {
    private List<Channel> channels = new ArrayList<Channel>();
    private static final String KEY = "hello";
    private Bootstrap startPoint;

    @Autowired
    private ClientChannelHandler clientChannelHandler;


    public int make() {
        for (int i = 0; i < 3; i++) {
            buildConnection();
        }

        return connect(channels);
    }


    private boolean buildConnection() {
        try {
            Channel channel = startPoint.connect().sync().channel();
            if (channel != null) {
                return channels.add(channel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int connect(List<Channel> channels) {
        if (channels.isEmpty()) {
            return 0;
        }
        int sum = 0;

        for (Channel channel : channels) {
            if (connect(channel)) {
                sum++;
            }
        }
        return sum;
    }


    private boolean connect(Channel channel) {
        if (!channel.isActive() || !channel.isWritable()) {
            return false;
        }
        channel.writeAndFlush(Unpooled.copiedBuffer(KEY, CharsetUtil.UTF_8));
        initChannel(channel.pipeline());

        return true;
    }


    public void initChannel(ChannelPipeline pipeline) {
//        pipeline.addLast("encoder", new HttpResponseEncoder());
//        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast(new ClientChannelHandler());

    }

    public void setStartPoint(Bootstrap startPoint) {
        this.startPoint = startPoint;
    }
}
