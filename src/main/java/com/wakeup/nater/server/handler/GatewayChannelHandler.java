package com.wakeup.nater.server.handler;

import com.wakeup.nater.core.BusyMan;
import com.wakeup.nater.annotation.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 22:35
 */

public class GatewayChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private BusyMan busyMan;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            busyMan.doService(ctx.channel(), (HttpRequest) msg);
        } else {
            System.out.println("抛弃消息");
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void setBusyMan(BusyMan busyMan) {
        this.busyMan = busyMan;
    }
}
