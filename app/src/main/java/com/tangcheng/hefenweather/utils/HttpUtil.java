package com.tangcheng.hefenweather.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tc on 2015/12/14.
 * 开启线程进行http查询，将得到的回复通过接口的onFinish传回
 */
public class HttpUtil {
    public static void getHttpResponse(final String address, final HttpListener listener,final String apikey){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(5000);            //设置读取超时
                    connection.setConnectTimeout(5000);         //设置连接超时
                    connection.setRequestMethod("GET");         //设置请求方式
                    if(apikey !=null){                          //当连接需要apikey时的特殊情况
                        connection.setRequestProperty("apikey",apikey);
                    }
                    InputStream in = connection.getInputStream();       //获取流信息
                    BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while((line=bufr.readLine())!=null){
                        line+="\r\n";                       //保留原信息中的换行符
                        sb.append(line);
                    }
                    if(listener != null){
                        listener.onFinsh(sb.toString());        //读取连接成功后通过onfinish返回数据
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);                        //读取连接异常通过onerror返回异常
                }finally {
                    if(connection != null){
                        connection.disconnect();                 //关闭连接
                    }
                }
            }
        }).start();           //注意不要忘了开启线程！
    }

    /**
     * 处理json数据调用函数存储到sharedPerference
     * @param context
     * @param jsonString
     */
    public static void handleJsonData(Context context,String jsonString){
        JSONObject obj = JSON.parseObject(jsonString);          //这里主要是要根据具体的json数据格式进行数据解析
        JSONArray arr = obj.getJSONArray("HeWeather data service 3.0");
        JSONObject obj1 = arr.getJSONObject(0);
        JSONObject basic = obj1.getJSONObject("basic");
        JSONObject now = obj1.getJSONObject("now");
        JSONObject cond = now.getJSONObject("cond");
        String txt = cond.getString("txt");
        String tmp = now.getString("tmp");
        JSONObject update = basic.getJSONObject("update");
        String countyName = basic.getString("city");
        String loc = update.getString("loc");
        loc = loc.substring(11);                    //只取出所需要的时分数据
        saveInSharedPerfernce(context,loc,txt,tmp+"℃",countyName);
    }

    /**
     * 存储查询到的数据
     * @param context
     * @param updateloc
     * @param txt
     * @param tmp
     * @param countyName
     */
    public static void saveInSharedPerfernce(Context context,String updateloc,String txt,String tmp,String countyName){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);      //格式化时间，注意字母代指含义
        SharedPreferences.Editor sEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sEditor.putBoolean("hasChecked", true);
        sEditor.putString("updateloc", updateloc);
        sEditor.putString("tmp", tmp);
        sEditor.putString("txt", txt);
        String ex = sdf.format(new Date());
        sEditor.putString("date",sdf.format(new Date()));
        sEditor.putString("countyName",countyName);
        sEditor.commit();           //记得写提交
    }
}
