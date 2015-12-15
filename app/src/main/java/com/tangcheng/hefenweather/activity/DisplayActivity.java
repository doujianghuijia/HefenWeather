package com.tangcheng.hefenweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tangcheng.hefenweather.R;
import com.tangcheng.hefenweather.utils.HttpListener;
import com.tangcheng.hefenweather.utils.HttpUtil;

/**
 * Created by tc on 2015/12/15.
 */
public class DisplayActivity extends Activity{
    private String infoaddr = "http://apis.baidu.com/heweather/weather/free?city="; //查询天气信息的网址
    public static final String APIKEY = "7987db111e02221ec39182e5732af680";     //apikey
    private TextView disp_updateloc;        //更新时间(时分)
    private TextView disp_date;             //更新日期
    private TextView disp_tmp;              //温度
    private TextView disp_txt;              //天气状况
    private TextView disp_countyName;       //城市名称
    private LinearLayout weatherInfoLayout;     //天气信息的layout，用于在更新数据时隐藏不显示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);
        disp_updateloc = (TextView) findViewById(R.id.disp_updateloc);
        disp_date = (TextView) findViewById(R.id.disp_date);
        disp_tmp = (TextView) findViewById(R.id.disp_tmp);
        disp_txt = (TextView) findViewById(R.id.disp_txt);
        disp_countyName = (TextView) findViewById(R.id.disp_countyName);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        String countyName = getIntent().getStringExtra("countyName");
        if(!TextUtils.isEmpty(countyName)){       //判断是否传递了countyName，即是否是通过选择城市进入的，若是，则进行更新
            disp_countyName.setText(countyName);
            disp_updateloc.setText("正在更新.....");
            weatherInfoLayout.setVisibility(View.INVISIBLE);        //更新时不显示天气信息布局
            queryWeatherWithName(countyName);       //查询天气信息
        }else{
            showWeatherInfo();  //不是通过选择城市进入，则直接读取本地存储的天气信息
        }
    }

    /**
     * 通过城市名称获取天气信息
     * @param countyName
     */
    public void queryWeatherWithName(String countyName){
        HttpUtil.getHttpResponse(infoaddr + countyName, new HttpListener() {    //利用工具类获取天气信息
            @Override
            public void onFinsh(String response) {
                HttpUtil.handleJsonData(DisplayActivity.this, response);        //进行json数据解析，在该方法中还进行了数据存储的操作
                runOnUiThread(new Runnable() {      //回到主线程
                    @Override
                    public void run() {
                        showWeatherInfo();      //显示天气信息
                    }
                });
            }

            @Override
            public void onError(Exception e) {  //出错进入此方法，显示“同步失败”
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disp_updateloc.setText("同步失败");
                    }
                });
            }
        }, APIKEY);
    }

    /**
     * 进行天气信息显示工作
     */
    public void showWeatherInfo(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); //从sharedPreferences读取数据
        disp_countyName.setText(sp.getString("countyName",""));
        disp_updateloc.setText(sp.getString("updateloc",""));
        disp_date.setText(sp.getString("date",""));
        disp_txt.setText(sp.getString("txt",""));
        disp_tmp.setText(sp.getString("tmp",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);      //将天气信息布局显示出来
    }
}
