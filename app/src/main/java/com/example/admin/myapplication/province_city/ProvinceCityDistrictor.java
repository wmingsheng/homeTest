package com.example.admin.myapplication.province_city;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.admin.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/8/11.
 */

public class ProvinceCityDistrictor extends Activity {
    private static final String PROVINCE_CITY_DAO  = "http://180.150.186.42:8080/1.01/achieveDistrictData.do";
    private static final String DATE_NAME = "province_city";
    private String savePath = "";
    private ListView province_lv;
    private ListView city_lv;
    private ListView county_lv;
    private File file;
    private List<String> provinceList;
    private List<List<String>> cityList;
    private List<List<List<String>>> countyList;
    private ArrayAdapter cityAdapter;
    private ArrayAdapter countyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_city);

        initView();
        initDate();
//        String str = readDate();
//        Log.e("mingsheng",str);
        addListeners();
    }

    private void initView() {
        savePath = this.getExternalFilesDir(null).getPath() + "/Download/";
        file = new File(savePath + DATE_NAME + ".txt");
        province_lv = (ListView) findViewById(R.id.aty_layout_province_lv);
        city_lv = (ListView) findViewById(R.id.aty_layout_city_lv);
        county_lv = (ListView) findViewById(R.id.aty_layout_county_lv);
        provinceList = new ArrayList<String>();
        cityList = new ArrayList<List<String>>();
        countyList = new ArrayList<List<List<String>>>();
    }

    private void initDate() {

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(downDate).start();
        }

        encapsulationDate();
        listAddAdapter();
    }

    /*
    * adapter加载
    * */
    private void listAddAdapter() {
        Log.e("mingsheng","---------provinceList---------- " + provinceList.size());
        Log.e("mingsheng","--------cityList----------- " + cityList.size());
        Log.e("mingsheng","-------countyList------------ " + countyList.size());
        ArrayAdapter provinceAdapter = backArrayAdapter(ProvinceCityDistrictor.this,provinceList);
        cityAdapter = backArrayAdapter(ProvinceCityDistrictor.this,cityList.get(0));
        countyAdapter = backArrayAdapter(ProvinceCityDistrictor.this,countyList.get(0).get(0));
        province_lv.setAdapter(provinceAdapter);
        city_lv.setAdapter(cityAdapter);
        county_lv.setAdapter(countyAdapter);
    }

    /*
    *封装ArrayAdapter
    * */
    private ArrayAdapter backArrayAdapter(Context context,List list) {
        return new ArrayAdapter(context,android.R.layout.simple_list_item_1,list);
    }

    /*
    * 添加点击事件
    * */
    int index;
    private void addListeners() {
        province_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                cityAdapter = backArrayAdapter(ProvinceCityDistrictor.this,cityList.get(position));
                countyAdapter = backArrayAdapter(ProvinceCityDistrictor.this,countyList.get(position).get(0));
                city_lv.setAdapter(cityAdapter);
                county_lv.setAdapter(countyAdapter);
            }
        });
        city_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countyAdapter = backArrayAdapter(ProvinceCityDistrictor.this,countyList.get(index).get(position));
                county_lv.setAdapter(countyAdapter);
            }
        });
    }
    /*
    * 下载数据，并存盘
    * */
    private Runnable downDate = new Runnable() {
        @Override
        public void run() {
            try {

                URL url = new URL(PROVINCE_CITY_DAO);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(5 * 1000);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = new byte[2048];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes,0,len);
                    }
                    is.close();
                    fos.close();
                }
            } catch (MalformedURLException e) {
                Log.e("mingsheng","导入的URL出现问题。。。。");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /*
    * 有本地读取数据并返回
    * @return String
    * */
    private String readDate() {
        StringBuffer buffer = new StringBuffer();

        try {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /*
    *省市区的数据进行封装
    * */
    private void encapsulationDate() {
        JSONObject provinceObj = null;
        JSONObject cityObj = null;
        JSONObject countyObj = null;
        List<String> cyList;
        List<List<String>> cnList;
        List<String> cnMList;
        String mDate = readDate();
        if (TextUtils.isEmpty(mDate)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(mDate);
            JSONArray province_array = obj.getJSONArray("provinceList");
            JSONArray city_array = obj.getJSONArray("cityList");
            JSONArray county_array = obj.getJSONArray("districtList");

            long start = System.currentTimeMillis();

            for (int i = 0;i < province_array.length();i++) {
                cyList = new ArrayList<String>();
                cnList = new ArrayList<List<String>>();
                provinceObj = province_array.getJSONObject(i);
                for (int j = 0;j < city_array.length();j++) {
                    cnMList = new ArrayList<String>();
                    cityObj = city_array.getJSONObject(j);
                    String str = provinceObj.getString("ID");
                    for (int x = 0;x < county_array.length();x++) {
                        countyObj = county_array.getJSONObject(x);
                        String cStr = cityObj.getString("ID");
                        if (countyObj.getString("ParentID").equals(cStr)) {
                            cnMList.add(countyObj.getString("Name"));
                        }
                    }
                    if (cityObj.getString("ParentID").equals(str)) {
                    cnList.add(cnMList);
                    cyList.add(cityObj.getString("Name"));
                    }
                }
                if ("台湾省".equals(provinceObj.getString("Name")) ||
                        "香港特别行政区".equals(provinceObj.getString("Name")) ||
                        "澳门特别行政区".equals(provinceObj.getString("Name")) ){
                    continue;
                }

                Log.e("mingsheng","----------cnList.size()------------- " + cnList.size());
                Log.e("mingsheng","----------cyList.size()------------- " + cyList.size());

                provinceList.add(provinceObj.getString("Name"));
                cityList.add(cyList);
                countyList.add(cnList);
            }

            Log.e("mingsheng","--------------------- " + (System.currentTimeMillis() - start));

        } catch (JSONException e) {
            Log.e("mingsheng","定义的JSONObject对象出现问题。。。。");
            e.printStackTrace();
        }
    }
}
