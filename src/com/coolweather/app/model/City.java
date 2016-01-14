package com.coolweather.app.model;


public class City {
	private int id;
	private String cityName;
	private String cityCode;
	private int provinceId;
	public int getId() {
	return id;
	}
	public void setId(int id) {
	this.id = id;
	}
	public String getCityName() {
	return cityName;
	}
	public void setCityName(String cityName) {
	this.cityName = cityName;
	}
	public void setCityCode(String code){
		cityCode = code;
	}
	public String getCityCode() {
	return cityCode;
	}
	public int getProvinceId(){
		return provinceId;
	}
	public void setProvinceId(int id){
		provinceId = id;
	}
}

