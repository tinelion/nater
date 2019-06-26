package com.wakeup.nater.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author Alon
 * @Date 2019/6/1 14:16
 */
public class CommandReader {
    private static final String REINPUT = "请输入正确的命令(Input right command please)";
    private static Logger logger = LoggerFactory.getLogger(CommandReader.class);
    private static Scanner in = new Scanner(System.in);

    public static void readCommand(Info info) {

        String line;

        logger.debug("是否服务端？默认[y](Is this a server or not? by default [y])[y/n]");
        while (true) {
            line = in.nextLine();

            if ("y".equals(line) || "".equals(line)) {
                info.setServer(true);
                break;
            } else if ("n".equals(line)) {
                info.setServer(false);
                break;
            } else {
                logger.debug(REINPUT);
            }
        }

        if (info.isServer) {

        }

        logger.debug("是否启用默认设置？默认[y](Willing to use default settings? by default [y]) [y/n]");
        while (true) {
            line = in.nextLine();

            if ("y".equals(line) || "".equals(line)) {
                return;
            } else if ("n".equals(line)) {
                break;
            } else {
                logger.debug(REINPUT);
            }
        }

        if (info.isServer) {
            readServerInfo(info);
        } else {
            readClientInfo(info);
        }
    }

    private static void readServerInfo(Info info) {
        String line;

        logger.debug("请设置对外端口(server port)[8080]");
        while (true) {
            line = in.nextLine();

            if ("".equals(line)) {
                break;
            }
            try {
                int serverPort = Integer.valueOf(line);
                if (serverPort > 22 && serverPort < Short.MAX_VALUE << 1) {
                    info.setServerPort(serverPort);
                    break;
                }
            } catch (Exception e) {
                logger.debug(REINPUT);
            }
        }

        readNATPort(info);
    }

    private static void readClientInfo(Info info) {
        String line;

        logger.debug("请设置服务端IP,默认127.0.0.1(server port, by default 127.0.0.1)[127.0.0.1]");
        Pattern pattern = Pattern.compile("^(1\\\\d{2}|2[0-4]\\\\d|25[0-5]|[1-9]\\\\d|[1-9])");
        while (true) {
            line = in.nextLine();
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                info.setHost(line);
                break;
            }
        }

        readNATPort(info);
    }


    private static void readNATPort(Info info) {
        String line;

        logger.debug("请设置内网穿透端口(nat server port)[4444]");
        while (true) {
            line = in.nextLine();
            if ("".equals(line)) {
                break;
            }
            try {
                int natPort = Integer.valueOf(line);
                if (natPort > 22 && natPort < Short.MAX_VALUE << 1 && info.getServerPort() != natPort) {
                    info.setNatPort(natPort);
                    break;
                }
            } catch (Exception e) {
                logger.debug(REINPUT);
            }
        }
    }


    public static class Info {
        // 是否服务端
        private boolean isServer = true;
        // 服务端IP
        private String host = "localhost";
        // 服务端对外端口
        private int serverPort = 8080;
        // 内网穿透监听端口
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
