package com.wakeup.nater.core;

import io.netty.channel.Channel;
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
    private static final int MAX_ID_LEGTH = 100;
    private Map<String, Channel> socketChannelCache = new ConcurrentHashMap<String, Channel>();

    public static String getChannelId(Channel channel){
        if (channel == null){
            return "";
        }
        return channel.remoteAddress().toString().replaceAll("/", "");
    }

    public boolean exsist(String channelId){
        if (channelId == null || "".equals(channelId) || channelId.length() > MAX_ID_LEGTH){
            return false;
        }

        return socketChannelCache.containsKey(channelId);
    }

    public Channel getChannelInBalance(){
        if (socketChannelCache.isEmpty()){
            return null;
        }
        Random random = new Random(socketChannelCache.size());
        int randomInt = random.nextInt() % socketChannelCache.size() + 1;

        Iterator<Channel> var = socketChannelCache.values().iterator();
        Channel channel = null;
        for (int i=0; i <randomInt; i++){
            channel = var.next();
        }
        if (channel == null || !channel.isActive()){
            getChannelInBalance();
        }

        return channel;
    }


    public void addChannel(Channel channel){
        if (channel == null || !channel.isOpen()){
            return;
        }
        String channelId = getChannelId(channel);
        synchronized (this) {
            if (!socketChannelCache.containsKey(channelId)) {
                socketChannelCache.put(channelId, new ChannelProxy(channel));
            }
        }
    }

    public void removeChannel(Channel channel){
        if (channel == null || !channel.isOpen()){
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
