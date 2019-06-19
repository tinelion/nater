package com.wakeup.nater.core;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/15 16:10
 */
public class ChannelProxy implements Channel {
    private Channel channel;

    public ChannelProxy(Channel channel){
        assert channel != null;
        this.channel = channel;
    }
    public ChannelId id() {
        return channel.id();
    }

    public EventLoop eventLoop() {
        return channel.eventLoop();
    }

    public Channel parent() {
        return channel.parent();
    }

    public ChannelConfig config() {
        return channel.config();
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public boolean isRegistered() {
        return channel.isRegistered();
    }

    public boolean isActive() {
        return channel.isActive();
    }

    public ChannelMetadata metadata() {
        return channel.metadata();
    }

    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    public boolean isWritable() {
        return channel.isWritable();
    }

    public long bytesBeforeUnwritable() {
        return channel.bytesBeforeUnwritable();
    }

    public long bytesBeforeWritable() {
        return channel.bytesBeforeWritable();
    }

    public Unsafe unsafe() {
        return channel.unsafe();
    }

    public ChannelPipeline pipeline() {
        return channel.pipeline();
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    public Channel read() {
        return channel.read();
    }

    public Channel flush() {
        return channel.flush();
    }

    public ChannelFuture bind(SocketAddress localAddress) {
        return channel.bind(localAddress);
    }

    public ChannelFuture connect(SocketAddress remoteAddress) {
        return channel.connect(remoteAddress);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return channel.connect(remoteAddress, localAddress);
    }

    public ChannelFuture disconnect() {
        return channel.disconnect();
    }

    public ChannelFuture close() {
        return channel.close();
    }

    public ChannelFuture deregister() {
        return channel.deregister();
    }

    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return channel.bind(localAddress, promise);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return channel.connect(remoteAddress, promise);
    }

    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return channel.connect(remoteAddress, localAddress, promise);
    }

    public ChannelFuture disconnect(ChannelPromise promise) {
        return channel.disconnect(promise);
    }

    public ChannelFuture close(ChannelPromise promise) {
        return channel.close(promise);
    }

    public ChannelFuture deregister(ChannelPromise promise) {
        return channel.deregister(promise);
    }

    public ChannelFuture write(Object msg) {
        return channel.write(msg);
    }

    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return channel.write(msg, promise);
    }

    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return channel.writeAndFlush(msg, promise);
    }

    public ChannelFuture writeAndFlush(Object msg) {
        return channel.writeAndFlush(msg);
    }

    public ChannelPromise newPromise() {
        return channel.newPromise();
    }

    public ChannelProgressivePromise newProgressivePromise() {
        return channel.newProgressivePromise();
    }

    public ChannelFuture newSucceededFuture() {
        return channel.newSucceededFuture();
    }

    public ChannelFuture newFailedFuture(Throwable cause) {
        return channel.newFailedFuture(cause);
    }

    public ChannelPromise voidPromise() {
        return channel.voidPromise();
    }

    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return channel.attr(key);
    }

    public <T> boolean hasAttr(AttributeKey<T> key) {
        return false;
    }

    public int compareTo(Channel o) {
        return 0;
    }
}
