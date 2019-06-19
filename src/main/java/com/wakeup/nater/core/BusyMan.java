package com.wakeup.nater.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/15 17:12
 */
@Component
public class BusyMan implements Runnable {
    private static final String REQUEST_ID = "requestId";
    private Queue<HttpRequest> requests = new ConcurrentLinkedQueue<HttpRequest>();
    private Queue<HttpResponse> responses = new ConcurrentLinkedQueue<HttpResponse>();
    private Map<Long, ChannelPromise> inChannelMap = new ConcurrentHashMap<Long, ChannelPromise>();

    private boolean started = false;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Autowired
    private ChannelManager channelManager;


    public boolean start() {
        if (started) {
            return true;
        }

        synchronized (this) {
            if (!started) {
                threadPool.submit(this);
                return true;
            } else {
                return false;
            }
        }
    }

    public ChannelPromise doService(Channel channel, HttpRequest httpRequest) {
        Long requestId = new Date().getTime();
        requests.offer(httpRequest);
        HttpHeaders headers = httpRequest.headers();
        headers.add(REQUEST_ID, requestId);
        ChannelPromise promise = channel.newPromise();
        inChannelMap.put(requestId, promise);

        return promise;
    }

    public void putResponse(HttpResponse response){
        responses.offer(response);
    }


    public boolean doResponse(HttpResponse response) {
        if (response == null) {
            return false;
        }
        HttpHeaders httpHeaders = response.headers();

        if (httpHeaders == null || httpHeaders.isEmpty()) {
            return false;
        }
        Object requestId = httpHeaders.get(REQUEST_ID);

        if (requestId == null) {
            return false;
        }

        ChannelPromise promise = inChannelMap.get(requestId);

        if (promise == null) {
            return false;
        }

        promise.channel().writeAndFlush(response);

        return true;
    }


    public void run() {
        HttpRequest request = null;
        Thread.currentThread().setName("BusyMan-Thread");
        while (true) {
            try {
                Channel channel = channelManager.getChannelInBalance();

                if ((channel != null) && (request = requests.poll()) != null) {
                    channel.writeAndFlush(request);
                }

                //进一个请求尝试出两个响应，防止响应堆积
                if (doResponse(responses.poll())) {
                    doResponse(responses.poll());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
