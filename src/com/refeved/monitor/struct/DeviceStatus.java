package com.refeved.monitor.struct;

public class DeviceStatus {
	public static final int ALARM               = 0x10; //�豸����
	public static final int NET_ARBNORMAL       = 0x08; //�豸�쳣
	public static final int MANUAL_HANDLING     = 0x04; //�˹�����
	public static final int OPEN_DOOR           = 0x02; //������
	public static final int IS_CHARGING         = 0x01; //���ڳ��
	
	public static final String STR_ALARM           = "�豸����";
	public static final String STR_NET_ARBNORMAL   = "�豸�쳣";
	public static final String STR_MANUAL_HANDLING = "�˹�������";
	public static final String STR_OPEN_DOOR       = "������";
	public static final String STR_IS_CHARGING     = "���ڳ��";
	/*
	 * �������¼���豸�쳣������״̬���豸״̬��һ�������������ֽ���λ���ֱ�������״̬
	 * bit[0]  ��1�����ڳ��
	 * bit[1]  ��1��������
	 * bit[2]  ��1���˹�������
	 * bit[3]  ��1�������쳣
	 * bit[4]  ��1���豸����
	 * */
	public static final String str_Device_Status[] = {
			"ʹ�õ��",       //0
			"�豸����" ,       //1
			"�����С�ʹ�õ��" ,       //2
			"������" ,       //3
			"�˹������С�ʹ�õ��" ,       //4
			"�˹�������" ,       //5
			"�˹������С������С�ʹ�õ��" ,       //6
			"�˹������С�������" ,       //7
			"�����쳣" ,       //8
			"�����쳣" ,       //9
			"�����쳣" ,       //10
			"�����쳣" ,       //11
			"�����쳣" ,       //12
			"�����쳣" ,       //13
			"�����쳣" ,       //14
			"�����쳣" ,       //15
			"�豸������ʹ�õ��" ,       //16
			"�豸����" ,       //17
			"�豸�����������С�ʹ�õ��" ,       //18
			"�豸������������" ,       //19
			"�豸�������˹������С�ʹ�õ��" ,       //20
			"�豸�������˹�������" ,       //21
			"�豸�������˹������С�ʹ�õ�ء�������" ,       //22
			"�豸�������˹������С�������" ,       //23
			"�����쳣" ,       //24
			"�����쳣" ,       //25
			"�����쳣" ,       //26
			"�����쳣" ,       //27
			"�����쳣" ,       //28
			"�����쳣" ,       //29
			"�����쳣" ,       //30
			"�����쳣" ,       //31
			};
}