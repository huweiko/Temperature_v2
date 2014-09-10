package com.refeved.monitor;


import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
public class AppContext extends Application{ 
	final static public int ABNORMAL = 1;//��ʾ�쳣��־
	final static public int DEAL = 2;    //��ʾ������־
	public String ApplicationName;
	public String UserName;
	public String AppVesion;
	public String AID;
	public String UserID;
	public String ExternalServerIP;
	public String InwardServerIP;
	public int SwitchLog;
	public boolean SettingsRunOnStartup;
	public boolean SettingsRunInBackGround;
	public int SettingsRefreshFrequency;
	public boolean SettingsNotification;
	public boolean SettingsOnlyMeasurement;
	public int SettingsCheckFrequency;
	public Intent BackgroundServiceIntent;
	public boolean IsOnlyShowAbnormalDevice;//�Ƿ�ֻ��ʾ�쳣�豸
    @Override 
    public void onCreate() { 
        // TODO Auto-generated method stub 
        super.onCreate(); 
        //UIHealper.DisplayToast(this, "Initial process start!");
        ApplicationName = getString(R.string.app_name);
        Resources res = getResources();   
        Configuration config=new Configuration();   
        config.setToDefaults();   
        res.updateConfiguration(config,res.getDisplayMetrics() ); 
        SharedPreferences settings = getSharedPreferences(getString(R.string.settings_filename),MODE_PRIVATE); 
		String checkFrequency = settings.getString(getString(R.string.settings_check_frequency),"0"); 
		String refreshFrequency = settings.getString(getString(R.string.settings_refresh_frequency),"5"); 
		
		try{
			SettingsRunOnStartup = settings.getBoolean(getString(R.string.settings_run_on_startup),false); 
			SettingsRunInBackGround = settings.getBoolean(getString(R.string.settings_run_in_background),false); 
			SettingsNotification = settings.getBoolean(getString(R.string.settings_notification),false); 
			SettingsOnlyMeasurement = settings.getBoolean(getString(R.string.settings_only_measurement),false); 
			IsOnlyShowAbnormalDevice = settings.getBoolean(getString(R.string.settings_only_show_abnormal_device),false); 
			SettingsCheckFrequency = Integer.parseInt(checkFrequency);
			SettingsRefreshFrequency = Integer.parseInt(refreshFrequency);
			AID = settings.getString(getString(R.string.settings_aid),"1"); 
			SwitchLog = settings.getInt(getString(R.string.settings_log_button_status),DEAL); 
			ExternalServerIP = settings.getString(getString(R.string.settings_external_server_IP),"58.64.200.105"); 
			InwardServerIP = settings.getString(getString(R.string.settings_inward_server_IP),"172.30.1.102"); 
			UserID = settings.getString(getString(R.string.settings_userID),"60");
			AppVesion = getVersionName();
		}catch (Exception e) {
			SettingsRunOnStartup = false;
			SettingsRunInBackGround = false;
			SettingsNotification = false;
			SettingsOnlyMeasurement = false;
			SettingsCheckFrequency = 0;
			SettingsRefreshFrequency = 0;
			AID = null;
			ExternalServerIP = null;
			InwardServerIP = null;
			SwitchLog = 0;
			UserID = null;
			IsOnlyShowAbnormalDevice = false;
        }

//		UIHealper.DisplayToast(this, "Initial process Done!");
		
    } 
    /*
     * ��ȡӦ�ð汾��
     * return ���ذ汾��
     * */
	public String getVersionName() throws Exception
	{
	           // ��ȡpackagemanager��ʵ��
	           PackageManager packageManager = getPackageManager();
	           // getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
	           PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
	           String version = packInfo.versionName;
	           return version;
	}
}
