package com.coolweather.app.util;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;


public class Utility {
	/**
	 * ������������ص�ʡ����Ϣ
	 */
public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
	if(!TextUtils.isEmpty(response)){
		String[] allProvinces = response.split(",");
		if(allProvinces != null && allProvinces.length > 0){
			for(String p:allProvinces){
				String [] array = p.split("\\|");
				Province province = new Province();
				province.setProvinceCode(array[0]);
				province.setProvinceName(array[1]);
				coolWeatherDB.saveProvince(province);
			}
			return true;
		}	
	}
	return false;
}
/**
 * ������������ص��м���Ϣ
 */
public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response, int provinceId){
	if(!TextUtils.isEmpty(response)){
		String[] allCities = response.split(",");
		if(allCities != null && allCities.length > 0){
			for(String p: allCities){
				String[] array = p.split("\\|");
				City city = new City();
				city.setCityCode(array[0]);
				city.setCityName(array[1]);
				city.setProvinceId(provinceId);
				coolWeatherDB.saveCity(city);
			}
			return true;
		}
	}
	return false;
}
/**
 * ������������ص��ؼ���Ϣ
 */
public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
	if(!TextUtils.isEmpty(response)){
		String[] allCounties = response.split(",");
		if(allCounties != null && allCounties.length > 0){
			for(String p:allCounties){
				String[] array = p.split("\\|");
				County county = new County();
				county.setCountyCode(array[0]);
				county.setCountyName(array[1]);
				county.setCityId(cityId);
				coolWeatherDB.saveCounty(county);
			}
			return true;
		}
	}
	return false;
}

/**
 * {"weatherinfo":
 * {"city":"��ɽ","cityid":"101190404","temp1":"21��","temp2":"9��",
 * "weather":"����תС��","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}}
 */

/**
* �������������ص�JSON���ݣ����������������ݴ洢�����ء�
*/
public static void handleWeatherResponse(Context context,String response){
	try {
		JSONObject jsonObject = new JSONObject(response);
		JSONObject weatherObject = jsonObject.getJSONObject("weatherinfo");
		String cityName = weatherObject.getString("city");
		String weatherCode = weatherObject.getString("cityid");
		String temp1 = weatherObject.getString("temp1");
		String temp2 = weatherObject.getString("temp2");
		String weatherDesp = weatherObject.getString("weather");
		String publishTime = weatherObject.getString("ptime");
		saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��", Locale.CHINA);
	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
	editor.putBoolean("city_selected", true);
	editor.putString("city_name", cityName);
	editor.putString("weather_code", weatherCode);
	editor.putString("temp1", temp1);
	editor.putString("temp2", temp2);
	editor.putString("weather_Desp", weatherDesp);
	editor.putString("publish_time", publishTime);
	editor.putString("current_time", sdf.format(new Date()));
	editor.commit();
}
}

