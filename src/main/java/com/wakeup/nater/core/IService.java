package com.wakeup.nater.core;

public interface IService {

    boolean init(CommandReader.Info info);

    void start();


    void stop();
}
