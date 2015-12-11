package com.tangcheng.hefenweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tc on 2015/12/10.
 */
public class HefenWeatherDBOpenHelper extends SQLiteOpenHelper{
    /**
     * Province表建表语句
     * */
    public static final String CREATE_PROVINCE = "create table Province(" +
                                                    "id integer primary key autoincrement," +
                                                    "provinceName text," +
                                                    "provinceCode text)";
    /**
     * City表建表语句
     * */
    public static final String CREATE_CITY = "create table City(" +
                                                    "id integer primary key autoincrement," +
                                                    "cityName text," +
                                                    "cityCode text," +
                                                     "provinceId integer)";
    /**
     * County表建表语句
     * */
    public static final String CREATE_COUNTY= "create table County(" +
                                                "id integer primary key autoincrement," +
                                                "countyName text," +
                                                "countyCode text," +
                                                "cityId integer)";
    public HefenWeatherDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);        //创建Province表
        db.execSQL(CREATE_CITY);            //创建City表
        db.execSQL(CREATE_COUNTY);          //创建County表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
