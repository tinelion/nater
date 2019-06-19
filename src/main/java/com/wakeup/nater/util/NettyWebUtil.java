package com.wakeup.nater.util;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/19 20:39
 */
public class NettyWebUtil {
    private static final RestTemplate SENDER = new RestTemplate();


    public static HttpResponse execute(HttpRequest request){
        String url = request.uri();
        HttpMethod method = request.method();
        HttpHeaders headers = request.headers();
        //request.
        return null;
    }
}
