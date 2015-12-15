package com.tangcheng.hefenweather.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tc on 2015/12/14.
 * 开启线程进行http查询，将得到的回复通过接口的onFinish传回
 */
public class HttpUtil {
    public static void getHttpResponse(final String address, final HttpListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    InputStream in = connection.getInputStream();
                    BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while((line=bufr.readLine())!=null){
                        line+="\r\n";
                        sb.append(line);
                    }
                    if(listener != null){
                        listener.onFinsh(sb.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
