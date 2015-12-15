package com.tangcheng.hefenweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangcheng.hefenweather.R;
import com.tangcheng.hefenweather.db.HefenWeatherDBUtil;
import com.tangcheng.hefenweather.model.AreaData;
import com.tangcheng.hefenweather.utils.HttpListener;
import com.tangcheng.hefenweather.utils.HttpUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tc on 2015/12/14.
 */
public class ChooseActivity extends Activity {
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> adapter;                   //listView适配器
    private List<String> dataList = new ArrayList<String>();    //提供给listView的数据list
    private HefenWeatherDBUtil dbUtil;        //数据库操作工具类
    private ProgressDialog progressDialog;  //进度框
    private List<String> provinceList;      //省列表
    private List<String> cityList;          //市列表
    private List<String> countyList;        //县列表
    public static final int PROVINCE_LEVEL = 0;     //省等级标志，用于判定点击listview
    public static final int CITY_LEVEL = 1;     //市等级标志，用于判定点击listview
    public static final int COUNTY_LEVEL = 2;     //县等级标志，用于判定点击listview
    private int current_level = 0;     //当前等级标志，用于判定点击listview
    private String select_province;     //选中的省
    private String select_city;     //选中的市
    private String select_county;     //选中的县
    public static final List<String> zhixiashiList = new ArrayList<String>(){
        {add("北京");add("上海");add("天津");add("重庆");}};        //初始化直辖市列表
    public static final String URL_ADDRESS = "http://10.139.7.193:8080/manager/data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); //获得sharedPreference
        if(sp.getBoolean("hasChecked",false)){      //判断是否已经选择了城市，如果是则不进行城市选择，这里注意放在setContentView之前
            Intent intent = new Intent(this,DisplayActivity.class);
            startActivity(intent);
            finish();       //结束activity
            return;      //后续代码不执行
        }
        setContentView(R.layout.choose_activity);
        textView = (TextView) findViewById(R.id.textView_main_activity);
        listView = (ListView) findViewById(R.id.listView_mian_activity);
        dbUtil = HefenWeatherDBUtil.getInstance(this);          //得到数据库操作工具类的实例
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(current_level == PROVINCE_LEVEL){            //当界面在省级别时，点击查询下属市
                    select_province = provinceList.get(position);
                    queryCities();
                }else if(current_level == CITY_LEVEL){          //当界面在市级别时，点击查询下属县
                    select_city = cityList.get(position);
                    queryConties();
                }else if(current_level == COUNTY_LEVEL){        //
                    select_county = countyList.get(position);
                    Intent intent = new Intent(ChooseActivity.this,DisplayActivity.class);
                    intent.putExtra("countyName",select_county);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询省信息，更新listview，本地没有数据则从网上下载
     */
    public void queryProvinces(){
        current_level = PROVINCE_LEVEL;
        provinceList = dbUtil.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(String province:provinceList){
                dataList.add(province);                 //这里主要必须要add进，不能直接=，不然没法更新数据，即界面没有显示
            }                                           //这应该与list是引用类型有关
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
        }else{
            queryFromServer();
        }
    }

    /**
     * 查询市信息，与省类似，这里考虑了直辖市的特殊情况，当点击直辖市时，直接将selectCity设置为直辖市名，调用querycounty
     * 这里考虑到点击back时需要回到上一次，将查询函数设置为无参数类型，通过全局变量select_province来实现这一想法
     */
    public void queryCities(){
        if(zhixiashiList.contains(select_province)){            //直辖市特殊情况
            select_city = select_province;
            queryConties();
        }else{
            current_level = CITY_LEVEL;                         //非直辖市一般情况，与省类似
            String provinceName = select_province;
            cityList = dbUtil.loadCities(provinceName);
            if(cityList.size()>0){
                dataList.clear();
                for(String city:cityList){
                    dataList.add(city);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                textView.setText(provinceName);
            }else{
                Toast.makeText(this,"查询市错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 查询县数据，与上述两种类似，
     */
    public void queryConties(){
        current_level = COUNTY_LEVEL;
        String cityName = select_city;
        countyList = dbUtil.loadCounties(cityName);
        if(countyList.size()>0){
            dataList.clear();
            for(String county:countyList){
                dataList.add(county);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(cityName);
        }else{
            Toast.makeText(this,"查询县错误",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从网上查询数据，调用httputil工具类进行数据读取，这里我们利用声明接口httplistener
     * 实现从子线程将数据传回UI线程，这里调用runonUiThread来实现，这样就能将数据展现在界面上
     */
    public void queryFromServer(){
        showProgressDialog();
        HttpUtil.getHttpResponse(URL_ADDRESS, new HttpListener() {
            @Override
            public void onFinsh(String response) {
                InputStream in = new ByteArrayInputStream(response.getBytes());
                Boolean flag = false;
                BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
                String line = "";
                try {
                    while((line = bufr.readLine())!= null){
                        String[] stringSplit = line.split(",");                 //对数据进行分割，放入对象中
                            AreaData areaData = new AreaData();
                            areaData.setCode(stringSplit[0]);
                            areaData.setPinyin(stringSplit[1]);
                            areaData.setCounty(stringSplit[2]);
                            areaData.setCity(stringSplit[3]);
                            areaData.setProvince(stringSplit[4]);
                            dbUtil.saveAreaData(areaData);                      //存入数据库
                            flag = true;
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                if(flag){
                    runOnUiThread(new Runnable() {              //回到主线程
                        @Override
                        public void run() {
                            closeProgressDialog();              //关闭progressdialog
                            queryProvinces();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {              //回到UI线程
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        },null);
    }

    /**
     * 显示progressDialog，这里是通过代码生成view控件
     */
    public void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("首次运行，正在从网上获取资料.....");
            progressDialog.setCanceledOnTouchOutside(false);                //点击进度框之外进度框不消失
        }
        progressDialog.show();                                              //显示进度框
    }

    /**
     * 关闭progressDialog
     */
    public void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();           //dismiss方法会释放对话框所占的资源，而hide方法不会。activity退出前必须调用dismiss方法关闭对话框。
        }                                       //如果对话框上有progressbar,你会发现，调用dismiss方法后，再调用show方法，出来的对话框，上面的progressbar不再会转动，而调用hide方法的则没有问题
    }

    /**
     * 重写点击back的方法，实现后退的功能，考虑到直辖市的特殊情况，在从县回到市时进行了判断
     */
    @Override
    public void onBackPressed() {
        if(current_level == COUNTY_LEVEL){
            if(zhixiashiList.contains(select_province)){
                queryProvinces();
            }else{
                queryCities();
            }
        }else if(current_level == CITY_LEVEL){
            queryProvinces();
        }else{
            finish();
        }
    }
}
