package com.wakeup.nater.core;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/15 16:02
 */
@Component
public class ChannelManager {
    private static Logger logger = LoggerFactory.getLogger(ChannelManager.class);
    private static final int MAX_ID_LEGTH = 100;
    private Map<String, Channel> socketChannelCache = new ConcurrentHashMap<String, Channel>();
    private Random random = new Random(10);

    public static String getChannelId(Channel channel) {
        if (channel == null) {
            return "";
        }
        return channel.remoteAddress().toString().replaceAll("/", "");
    }

    public boolean exsist(String channelId) {
        if (channelId == null || "".equals(channelId) || channelId.length() > MAX_ID_LEGTH) {
            return false;
        }

        return socketChannelCache.containsKey(channelId);
    }

    public Channel getChannelInBalance() {
        if (socketChannelCache.isEmpty()) {
            return null;
        }
        int randomInt = random.nextInt(1000) % socketChannelCache.size() + 1;

        Iterator<Channel> var = socketChannelCache.values().iterator();
        Channel channel = null;

        for (int i = 0; i < randomInt; i++) {
            channel = var.next();
        }
        if (channel == null || !channel.isActive()) {
            return null;
        }

        return channel;
    }


    public void addChannel(Channel channel) {
        if (channel == null || !channel.isOpen()) {
            return;
        }
        String channelId = getChannelId(channel);
        synchronized (this) {
            if (!socketChannelCache.containsKey(channelId)) {
                socketChannelCache.put(channelId, new ChannelProxy(channel));
            }
        }
    }

    public void removeChannel(Channel channel) {
        if (channel == null || !channel.isOpen()) {
            return;
        }
        String channelId = getChannelId(channel);
        synchronized (this) {
            if (!socketChannelCache.containsKey(channelId)) {
                socketChannelCache.remove(channelId);
            }
        }
    }
}
