package com.coolweather.app.util;



import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.text.TextUtils;


public class Utility {
	/**
	 * 处理服务器返回的省份信息
	 */
public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
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
 * 处理服务器返回的市级信息
 */
public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response, int provinceId){
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
 * 处理服务器返回的县级信息
 */
public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
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

}

