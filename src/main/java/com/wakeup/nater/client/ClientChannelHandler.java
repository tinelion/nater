package com.wakeup.nater.client;

import com.wakeup.nater.annotation.ChannelHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 22:35
 */
@ChannelHandler
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DefaultHttpRequest) {
            HttpRequest request = (DefaultHttpRequest)msg;
            ctx.pipeline().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.copiedBuffer("hehe", Charset.forName("utf8")), request.headers(), request.headers()));
        } else {
            System.out.println("丢弃消息");
            ctx.writeAndFlush(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
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
}
