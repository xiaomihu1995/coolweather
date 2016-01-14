package com.coolweather.app.activity;


import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
public static final int LEVEL_PROVINCE = 0;
public static final int LEVEL_CITY = 1;
public static final int LEVEL_COUNTY = 2;


private TextView titleText;
private ListView listView;
private ArrayAdapter<String> adapter;
private CoolWeatherDB coolWeatherDB;
private List<String> dataList = new ArrayList<String>();
private List<Province> provinceList;
private List<City> cityList;
private List<County> countyList;
private Province selectedProvince;
private City selectedCity;
private int currentLevel;
private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		titleText = (TextView)findViewById(R.id.title_text);
		listView = (ListView)findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCity();
				}
				else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounty();
				}
			}
		});
		
		queryProvince();
	}
	/**
	* 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryProvince(){
		provinceList = coolWeatherDB.getProvince();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}
		else {
			queryFromServer(null, "province");
		}
	}
	/**
	* 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryCity(){
		cityList = coolWeatherDB.getCity(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}
		else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	/**
	* 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	*/
	private void queryCounty(){
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}
		else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	/**
	* 根据传入的代号和类型从服务器上查询省市县数据。
	*/
	private void queryFromServer(final String code,final String type){
		String address ;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}
		else {
			address = "http://www.weather.com.cn/data/list3/city.xml" ;			
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if("province".equals(type)){
					Utility.handleProvinceResponse(coolWeatherDB, response);
				}
				else if ("city".equals(type)) {
					Utility.handleCityResponse(coolWeatherDB, response, selectedProvince.getId());
				}
				else if("county".equals(type)){
					Utility.handleCountyResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
				     runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}
							else if ("city".equals(type)) {
								queryCity();
							}
							else if ("county".equals(type)) {
								queryCounty();
							}
							
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/**
	* 显示进度对话框
	*/
	private void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);//触碰对话框外部区域不会消失，可以相应back键，而SetCancelable不可以
		}
		progressDialog.show();
	}
	/**
	* 关闭进度对话框
	*/
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	/**
	* 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	*/
	@Override
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			queryCity();
		}
		else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		}
		finish();
	}
	

}
