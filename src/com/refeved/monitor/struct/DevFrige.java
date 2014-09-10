package com.refeved.monitor.struct;

public class DevFrige extends Device{
	private String mTemperature;
	
	
	public String getmTemperature() {
		return mTemperature;
	}

	public void setmTemperature(String Temperature) {
		this.mTemperature = Temperature;
	}

	public DevFrige(String type,String environmentType, String id, String location,String status,String description,String low,String high ,String temp,String sn) {
		super(type, environmentType, id, location, status,description, low, high,sn);
		setmTemperature(temp);
		// TODO Auto-generated constructor stub
		
	}


	
}
