package com.tangcheng.hefenweather.model;

/**
 * Created by tc on 2015/12/14.
 * 省市县区域代码表
 */
public class AreaData {
    private int id;                         //id
    private String code;                    //区域编码
    private String pinyin;                  //区域拼音
    private String county;                  //县名
    private String city;                    //市名
    private String province;                //省名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
}
