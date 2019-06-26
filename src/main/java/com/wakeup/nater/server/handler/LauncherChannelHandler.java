package com.wakeup.nater.server.handler;

import com.wakeup.nater.annotation.ChannelHandler;
import com.wakeup.nater.core.BusyMan;
import com.wakeup.nater.core.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * @Description
 * @Author Alon
 * @Date 2019/5/31 22:35
 */
@io.netty.channel.ChannelHandler.Sharable
public class LauncherChannelHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(LauncherChannelHandler.class);
    private static final String KEY = "hello";

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private BusyMan busyMan;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageType messageType = checkMessageType(ctx, msg);

        switch (messageType) {
            case RESPONSE:
                onResponse(ctx, msg);
                break;

            case CONNECTION_CREATION:
                onCreateConnection(ctx, msg);
                break;

            case INVALID:
                //onInvalid(ctx, msg);
                onResponse(ctx, msg);
                break;
        }
    }


    private MessageType checkMessageType(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DefaultHttpResponse) {
            String channelId = ChannelManager.getChannelId(ctx.channel());
            if (channelManager.exsist(channelId)) {
                return MessageType.RESPONSE;
            } else {
                return MessageType.INVALID;
            }
        } else if ((msg instanceof ByteBuf)) {
            if (msg.equals(Unpooled.copiedBuffer(KEY, UTF_8))) {
                return MessageType.CONNECTION_CREATION;
            } else {
                return MessageType.INVALID;
            }
        }
        return MessageType.INVALID;
    }

    private void onCreateConnection(ChannelHandlerContext ctx, Object msg) {
        String channelId = ChannelManager.getChannelId(ctx.channel());
        if (!channelManager.exsist(channelId)) {
            channelManager.addChannel(ctx.channel());
        }
        String log = "建立NAT连接: " + ctx.pipeline().channel().localAddress()
                + " <---> " + ctx.pipeline().channel().remoteAddress();
        log = log.replaceAll("/", "");
        logger.info(log);

        ctx.pipeline().addLast("encoder", new HttpRequestEncoder());
        ctx.pipeline().addLast("decoder", new HttpResponseDecoder());
    }


    private void onResponse(ChannelHandlerContext ctx, Object msg) {
        try {
           // busyMan.putResponse((HttpResponse) msg);
            logger.info(msg.toString());
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    private void onInvalid(ChannelHandlerContext ctx, Object msg) {
        logger.debug("invalid message ignored");
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

    private enum MessageType {
        CONNECTION_CREATION, RESPONSE, INVALID
    }

    public void setChannelManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    public void setBusyMan(BusyMan busyMan) {
        this.busyMan = busyMan;
    }
}
