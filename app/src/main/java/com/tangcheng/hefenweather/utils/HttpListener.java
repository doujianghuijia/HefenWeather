package com.tangcheng.hefenweather.utils;

/**
 * Created by tc on 2015/12/14.
 * 用于实现操作获取到的response
 */
public interface HttpListener {
    void onFinsh(String response);
    void onError(Exception e);
}
