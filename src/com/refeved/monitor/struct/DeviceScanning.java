package com.refeved.monitor.struct;
public class DeviceScanning{
	
	public String mDescribe;//�豸����
	public String mAid;//�豸��ַaid
	public String mType;//�豸���� 6�������¶��豸  7������ʪ���豸
	public String mClow;//��ʪ������
	public String mChigh;//��ʪ������
	public String mCdif;//�¶�ƫ��
	public String mSN;//�豸���к�
	
	public DeviceScanning(String Describe, String Aid, String Type, String Clow, String Chigh, String Cdif, String SN ){
		mDescribe = Describe;
		mAid = Aid;
		mType = Type;
		mClow = Clow;
		mChigh = Chigh;
		mCdif = Cdif;
		mSN = SN;
	}
};