package com.wakeup.nater.core;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/1 14:16
 */
public class CommandReader {

    public Info readCommand() {
        Info info = new Info();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
        Map<String, Object> map = new LinkedHashMap<String, Object>() {
            {
                //put("")
            }};

        while (true) {
            try {
                out.write("Is this a server?[yes by default](y/n)");
                String line = in.readLine();

                if (line.contains("n")) {
                    info.isServer = false;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            try {
                if (info.isServer) {
                    break;
                }
                out.write("Is this a serer?(y/n)");
                String line = in.readLine();

                if (line.contains("y") || line.contains("n")) {
                    info.isServer = line.contains("y");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (; ; ) {
            try {
                out.write("Is this a serer?(y/n)");
                String line = in.readLine();

                if (line.contains("y") || line.contains("n")) {
                    info.isServer = line.contains("y");
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return info;
    }


    public static class Info {
        private boolean isServer = true;
        private String host = "localhost";
        private int serverPort = 8080;
        private int natPort = 4444;


        public boolean isServer() {
            return isServer;
        }

        public String getHost() {
            return host;
        }

        public int getServerPort() {
            return serverPort;
        }

        public void setServer(boolean server) {
            isServer = server;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        public int getNatPort() {
            return natPort;
        }

        public void setNatPort(int natPort) {
            this.natPort = natPort;
        }
    }
}
