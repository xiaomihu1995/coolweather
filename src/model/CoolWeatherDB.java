package model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import db.CoolWeatherOpenHelper;
//单例类
public class CoolWeatherDB {
public static final  String DB_NAME = "cool_weather";//数据库名

private static final int VERSION = 1;//数据库版本
private static CoolWeatherDB coolWeatherDB;
private SQLiteDatabase db;
/**
* 将构造方法私有化
*/
private CoolWeatherDB(Context context) {
	// TODO Auto-generated constructor stub
	CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
	db = helper.getReadableDatabase();
}
/**
* 获取CoolWeatherDB的实例。
*/
 public synchronized static CoolWeatherDB getInstance(Context context){
	 if(coolWeatherDB == null){
	 coolWeatherDB = new CoolWeatherDB(context);
	 }
	 return coolWeatherDB;
 }
 /**
 * 将Province实例存储到数据库。
 */
 public void saveProvince(Province province){
	 if(province != null){
		 ContentValues values = new ContentValues();
		 values.put("province_name", province.getProvinceName());
		 values.put("province_code", province.getProvinceCode());
		 db.insert("province", null, values);
	 }
 }
 /**
 * 从数据库读取全国所有的省份信息。
 */
 public List<Province> getProvince(){
	List<Province> list = new ArrayList<Province>();
	Cursor cursor = db.query("province", null, null, null, null, null, null);
	if(cursor.moveToFirst()){
		do {
			Province province = new Province();
			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			list.add(province);
		} while (cursor.moveToNext());	
	}
	return list;
 }
 /**
  * 将City实例存储到数据库。
  */
 public void saveCity(City city){
	 if(city != null){
		 ContentValues values = new ContentValues();
		 values.put("id", city.getId());
		 values.put("city_name", city.getCityName());
		 values.put("city_code", city.getCityCode());
		 values.put("province_id", city.getProvinceId());
		 db.insert("city", null, values);
		 
	 }
 }
 /**
  * 从数据库读取全国所有的城市信息。
  */
public List<City> getCity(int provinceId){
	List<City> list = new ArrayList<City>();
	Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
	if(cursor.moveToFirst()){
		do {
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			city.setProvinceId(provinceId);
			list.add(city);
		} while (cursor.moveToNext());
	}
	return list;
}
/**
* 将County实例存储到数据库。
*/
public void saveCounty(County county) {
	if (county != null) {
	ContentValues values = new ContentValues();
	values.put("county_name", county.getCountyName());
	values.put("county_code", county.getCountyCode());
	values.put("city_id", county.getCityId());
	db.insert("County", null, values);
	}
}
/**
* 从数据库读取某城市下所有的县信息。
*/
public List<County> loadCounties(int cityId) {
	List<County> list = new ArrayList<County>();
	Cursor cursor = db.query("County", null, "city_id = ?",new String[] { String.valueOf(cityId) }, null, null, null);
	if (cursor.moveToFirst()) {
	do {
	County county = new County();
	county.setId(cursor.getInt(cursor.getColumnIndex("id")));
	county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
	county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
	county.setCityId(cityId);
	list.add(county);
	} while (cursor.moveToNext());
	}
	return list;
	}
	
}
