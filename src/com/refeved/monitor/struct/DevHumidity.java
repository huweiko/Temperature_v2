/*
 * �ļ�����DevHumidity.java
 * ���ܣ��豸ʪ����Ϣ�ṹ��
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.struct;

public class DevHumidity extends Device{
	private String mHumidity;
	public DevHumidity(String type, String environmentType, String id, String location, String status,
			String descprition,String low,String high,String humidity,String sn) {
		super(type, environmentType, id, location, status, descprition, low, high, sn);
		
		mHumidity = humidity;
	}
	public String getmHumidity() {
		return mHumidity;
	}
	public void setmHumidity(String mHumidity) {
		this.mHumidity = mHumidity;
	}

}
