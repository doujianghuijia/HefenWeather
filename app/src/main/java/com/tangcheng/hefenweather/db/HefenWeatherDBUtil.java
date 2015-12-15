package com.tangcheng.hefenweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tangcheng.hefenweather.model.AreaData;
import com.tangcheng.hefenweather.model.City;
import com.tangcheng.hefenweather.model.County;
import com.tangcheng.hefenweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tc on 2015/12/11.
 * HefenWeather天气数据库操作工具类
 */
public class HefenWeatherDBUtil {
    public static final String DATEBASE_NAME = "HefenWeatherDB";            //数据库名称
    public static final int DATEBASE_VERSION = 1;                           //数据库版本
    private static HefenWeatherDBUtil hefenWeatherDBUtil;                     //工具类实例
    private SQLiteDatabase db;                                                //数据库实例

    /**
     * 私有的构造方法
     * @param context
     */
    private HefenWeatherDBUtil(Context context) {
        HefenWeatherDBOpenHelper openHelper = new HefenWeatherDBOpenHelper(context,DATEBASE_NAME,null,DATEBASE_VERSION);
        db = openHelper.getReadableDatabase();
    }

    /**
     * 获取工具类的一个实例，使用同步锁，
     * @param context
     * @return
     */
    public synchronized static HefenWeatherDBUtil getInstance(Context context){
        if(hefenWeatherDBUtil == null){
            hefenWeatherDBUtil = new HefenWeatherDBUtil(context);
        }
        return hefenWeatherDBUtil;
    }

//    /**
//     * 保存省
//     * @param province
//     */
//    public void saveProvince(Province province){
//        ContentValues values = new ContentValues();
//        values.put("provinceName",province.getProvinceName());
//        values.put("provinceCode",province.getProvinceCode());
//        db.insert("Province",null,values);
//    }
//
//    /**
//     * 得到数据库中的所有省信息返回
//     * @return
//     */
//    public List<Province> loadProvinces(){
//        List<Province> list = new ArrayList<Province>();
//        Cursor cursor = db.query("Province", null, null, null, null, null, null);
//        if(cursor.moveToFirst()){
//            do{
//                Province province = new Province();
//                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
//                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
//                list.add(province);
//            }while(cursor.moveToNext());
//        }
//        return list;
//    }
//
//    /**
//     * 保存市
//     * @param city
//     */
//    public void saveCity(City city){
//        ContentValues values = new ContentValues();
//        values.put("cityName",city.getCityName());
//        values.put("cityCode",city.getCityCode());
//        values.put("provinceId", city.getProvinceId());
//        db.insert("City",null,values);
//    }
//
//    /**
//     * 得到数据库中的所有市信息返回
//     * @param provinceId
//     * @return
//     */
//    public List<City> loadCitys(int provinceId){
//        List<City> list = new ArrayList<City>();
//        Cursor cursor = db.query("City", null, "provinceId=?", new String[]{String.valueOf(provinceId)}, null, null, null);
//        if(cursor.moveToFirst()){
//            do{
//                City city = new City();
//                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
//                city.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
//                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("provinceId")));
//                list.add(city);
//            }while(cursor.moveToNext());
//        }
//        return list;
//    }
//
//    /**
//     * 保存县
//     * @param county
//     */
//    public void saveCounty(County county){
//        ContentValues values = new ContentValues();
//        values.put("countyName",county.getCountyName());
//        values.put("countyCode",county.getCountyCode());
//        values.put("CityId",county.getCityId());
//        db.insert("County",null,values);
//    }
//
//    /**
//     * 得到数据库中的所有县信息返回
//     * @param cityId
//     * @return
//     */
//    public List<County> loadCounties(int cityId){
//        List<County> list = new ArrayList<County>();
//        Cursor cursor = db.query("County",null,"city=?",new String[]{String.valueOf(cityId)},null,null,null);
//        if(cursor.moveToFirst()){
//            County county = new County();
//            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
//            county.setCountyName(cursor.getString(cursor.getColumnIndex("countyName")));
//            county.setCountyCode(cursor.getString(cursor.getColumnIndex("countyCode")));
//            county.setCityId(cursor.getInt(cursor.getColumnIndex("cityId")));
//            list.add(county);
//        }
//        return list;
//    }

    /**
     * 保存AreaData数据
     * @param areaData
     */
    public void saveAreaData(AreaData areaData){
        ContentValues values = new ContentValues();
        values.put("code",areaData.getCode());
        values.put("pinyin",areaData.getPinyin());
        values.put("county",areaData.getCounty());
        values.put("city",areaData.getCity());
        values.put("province",areaData.getProvince());
        db.insert("AreaData",null,values);
    }

    /**
     * 查询省份信息
     * @return
     */
    public List<String> loadProvinces(){
        List<String> list = new ArrayList<String>();
        String loadProvincesSQL = "select distinct t.province from AreaData t";
        Cursor cursor = db.rawQuery(loadProvincesSQL, null);
        if(cursor !=null) {
            if (cursor.moveToFirst()) {
                do {
                    String province = cursor.getString(cursor.getColumnIndex("province"));
                    list.add(province);
                } while (cursor.moveToNext());
            }
            return list;
        }else{
            return null;
        }
    }

    /**
     * 通过省名查询下属的所有市
     * @param provinceName
     * @return
     */
    public List<String> loadCities(String provinceName){
        List<String> list = new ArrayList<String>();
        String loadCitySQL = "select distinct t.city from AreaData t where t.province=?";
        Cursor cursor = db.rawQuery(loadCitySQL, new String[]{provinceName});
        if(cursor.moveToFirst()){
            do{
                String city = cursor.getString(cursor.getColumnIndex("city"));
                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }


    /**
     * 通过市名查询下属的所有县
     * @param cityName
     * @return
     */
    public List<String> loadCounties(String cityName){
        List<String> list = new ArrayList<String>();
        String loadCountySQL = "select distinct t.county from AreaData t where t.city=?";
        Cursor cursor = db.rawQuery(loadCountySQL, new String[]{cityName});
        if(cursor.moveToFirst()){
            do{
                String county = cursor.getString(cursor.getColumnIndex("county"));
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /**
     * 通过县名得到代号
     * @param countyName
     * @return
     */
    public String loadCode(String countyName){
        String loadCodeSQL = "select t.code from AreaData t where t.county=?";
        Cursor cursor = db.rawQuery(loadCodeSQL,new String[]{countyName});
        String code = "";
        if(cursor.moveToFirst()){
            do{
                code = cursor.getString(cursor.getColumnIndex("code"));
            }while(cursor.moveToNext());
        }
        return code;

    }

}
