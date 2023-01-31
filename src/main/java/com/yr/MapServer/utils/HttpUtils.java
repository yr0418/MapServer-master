package com.yr.MapServer.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    /**
     * 获取地图瓦片
     * @param url 地图瓦片请求地址
     *
     * @return 瓦片数据
     */
    public static BufferedImage getImage (String url) throws IOException {
        BufferedImage bufImg;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

            // 连接超时
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(250000);

            // 读取超时 --服务器响应比较慢,增大时间
            conn.setReadTimeout(250000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36");
            conn.connect();

            bufImg = ImageIO.read(conn.getInputStream());

            return bufImg;
        } catch (IOException e) {
            return null;
        }
    }

    private static final int connectionTimeOut = 600000;

    private static final int socketTimeOut = 600000;

    private static final int maxConnectionPerHost = 20;

    private static final int maxTotalConnections = 20;

    private static HttpClient client;

    static {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
        connectionManager.getParams().setSoTimeout(socketTimeOut);
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
        connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
        client = new HttpClient(connectionManager);
    }
}
