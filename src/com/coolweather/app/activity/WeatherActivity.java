package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class WeatherActivity extends Activity implements OnClickListener{
	/**
	* 切换城市按钮
	*/
	private Button switchCity;
	/**
	* 更新天气按钮
	*/
	private Button refreshWeather;
	private LinearLayout weatherInfoLayout;
	/**
	* 用于显示城市名
	* */
	private TextView cityNameText;
	/**
	* 用于显示发布时间
	*/
	private TextView publishText;
	/**
	* 用于显示当前日期
	*/
	private TextView currentDateText;
	/**
	* 用于显示天气描述信息
	*/
	private TextView weatherDespText;
	/**
	* 用于显示气温1
	*/
	private TextView temp1Text;
	/**
	* 用于显示气温2
	*/
	private TextView temp2Text;


@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.weather_layout);
	// 初始化各控件
	weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
	cityNameText = (TextView) findViewById(R.id.city_name);
	publishText = (TextView) findViewById(R.id.publish_text);
	weatherDespText = (TextView) findViewById(R.id.weather_desp_text);
	temp1Text = (TextView) findViewById(R.id.temp1);
	temp2Text = (TextView) findViewById(R.id.temp2);
	currentDateText = (TextView) findViewById(R.id.current_date);
	switchCity = (Button)findViewById(R.id.home);
	refreshWeather = (Button)findViewById(R.id.refresh);
	switchCity.setOnClickListener(this);
	refreshWeather.setOnClickListener(this);
	String countyCode = getIntent().getStringExtra("county_code");
	if(!TextUtils.isEmpty(countyCode)){
		// 有县级代号时就去查询天气
		publishText.setText("同步中....");
		cityNameText.setVisibility(View.INVISIBLE);
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		queryWeatherCode(countyCode);
	}
	else {//没有else注意后果，不会加载数据。从本次取得数据都为空
		//没有县级代号直接显示本地存储的天气信息
		showWeather();
	}

}
/**
* 查询县级代号所对应的天气代号->查天气。
*/
private void queryWeatherCode(String countyCode){
	String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";	
	queryFromServer(address, "countyCode");
	
}
private void queryWeatherInfo(String weatherCode){
	String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html"; 
	queryFromServer(address, "weatherCode");
}
private void queryFromServer(final String address,final String type){//final
	HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			if("countyCode".equals(type)){
				if(!TextUtils.isEmpty(response)){
					String[] array = response.split("\\|");
					if(array != null && array.length == 2){
					String weatherCode = array[1];
					queryWeatherInfo(weatherCode);
					}
				}
			}
			else if ("weatherCode".equals(type)) {
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						showWeather();
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
					publishText.setText("同步失败");
				}
			});
		}
	});
}
private void showWeather(){
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	cityNameText.setText(preferences.getString("city_name", ""));
	publishText.setText("今天 "+ preferences.getString("publish_time", "")+ " 发布");
	weatherDespText.setText(preferences.getString("weather_Desp", ""));
	currentDateText.setText(preferences.getString("current_time", ""));
	temp1Text.setText(preferences.getString("temp1", ""));
	temp2Text.setText(preferences.getString("temp2", ""));
	cityNameText.setVisibility(View.VISIBLE);
	weatherInfoLayout.setVisibility(View.VISIBLE);
	
}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.home:
		Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
		break;
	case R.id.refresh:
		publishText.setText("同步中...");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
		String weatherCode = prefs.getString("weather_code", "");
		if(!TextUtils.isEmpty(weatherCode)){
			queryWeatherInfo(weatherCode);
		}
		break;
	default:
		break;
	}
}

}
