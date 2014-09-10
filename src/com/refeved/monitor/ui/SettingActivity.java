package com.refeved.monitor.ui;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.net.BackgroundService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingActivity extends BaseActivity implements OnCheckedChangeListener, OnClickListener{
	private CheckBox mCheckBoxStartup;
	private CheckBox mCheckBoxBackground;
	private CheckBox mCheckBoxNotification;
	private RelativeLayout mRelativeLayoutRefreshFrequency;
	private RelativeLayout mRelativeLayoutCheckFrequency;
	private ImageButton mImageButtonSettingActivityBack;
	private TextView mTextViewRefreshFrequencyInterval;
	private TextView mTextViewCheckFrequencyInterval;
	
	private static final int CLICK_STARTUP = 1;
	private static final int CLICK_BACKGROUND = 2;
	private static final int CLICK_NOTIFICATION = 3;
	private static final int CLICK_REFRESH_FREQUENCY = 4;
	private static final int CLICK_CHECK_FREQUENCY = 5;
	private static final int CLICK_BACK = 6;
	private SharedPreferences settings;
	
	private AppContext appContext;
	private String SETTINGS_RUN_ON_STARTUP ;
	private String SETTINGS_RUN_IN_BACKGROUND ;
	private String SETTINGS_NOTIFICATION ;
	private String SETTINGS_REFRESH_FREQUENCY ;
	private String SETTINGS_CHECK_FREQUENCY;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(getString(R.string.settings_filename),MODE_PRIVATE); 
        SETTINGS_RUN_ON_STARTUP = getString(R.string.settings_run_on_startup);
    	SETTINGS_RUN_IN_BACKGROUND = getString(R.string.settings_run_in_background);
    	SETTINGS_NOTIFICATION = getString(R.string.settings_notification);
    	SETTINGS_REFRESH_FREQUENCY = getString(R.string.settings_refresh_frequency);
    	SETTINGS_CHECK_FREQUENCY = getString(R.string.settings_check_frequency);
       
        appContext = (AppContext) getApplication();
        setContentView(R.layout.activity_setting);
        mCheckBoxStartup = (CheckBox) findViewById(R.id.CheckBoxStartup);  
        mCheckBoxStartup.setOnCheckedChangeListener(this);
        mCheckBoxStartup.setTag(CLICK_STARTUP);
       
        mCheckBoxBackground = (CheckBox) findViewById(R.id.CheckBoxBackground);  
        mCheckBoxBackground.setOnCheckedChangeListener(this);
        mCheckBoxBackground.setTag(CLICK_BACKGROUND);
       
        mCheckBoxNotification = (CheckBox) findViewById(R.id.CheckBoxNotification);  
        mCheckBoxNotification.setOnCheckedChangeListener(this);
        mCheckBoxNotification.setTag(CLICK_NOTIFICATION);
        
        mRelativeLayoutRefreshFrequency = (RelativeLayout) findViewById(R.id.RelativeLayoutRefreshFrequency);      
        mRelativeLayoutRefreshFrequency.setOnClickListener(this);
        mRelativeLayoutRefreshFrequency.setTag(CLICK_REFRESH_FREQUENCY);
        
        mRelativeLayoutCheckFrequency = (RelativeLayout) findViewById(R.id.RelativeLayoutCheckFrequency);      
        mRelativeLayoutCheckFrequency.setOnClickListener(this);
        mRelativeLayoutCheckFrequency.setTag(CLICK_CHECK_FREQUENCY);
        
        mImageButtonSettingActivityBack = (ImageButton) findViewById(R.id.ImageButtonSettingActivityBack);      
        mImageButtonSettingActivityBack.setOnClickListener(this);
        mImageButtonSettingActivityBack.setTag(CLICK_BACK);
        
        mTextViewRefreshFrequencyInterval = (TextView) findViewById(R.id.TextViewRefreshFrequencyInterval);
        mTextViewCheckFrequencyInterval = (TextView) findViewById(R.id.TextViewCheckFrequencyInterval);
        if(appContext.SettingsRefreshFrequency == 0){
        	mTextViewRefreshFrequencyInterval.setText(getResources().getString(R.string.current_Interval)+"Never");
        }else{
        	mTextViewRefreshFrequencyInterval.setText(getResources().getString(R.string.current_Interval)+appContext.SettingsRefreshFrequency+"s");
        }
        if(appContext.SettingsCheckFrequency == 0){
        	mTextViewCheckFrequencyInterval.setText(getResources().getString(R.string.current_Interval)+"Never");
        }else{
        	mTextViewCheckFrequencyInterval.setText(getResources().getString(R.string.current_Interval)+appContext.SettingsCheckFrequency+"s");
        }
        
        mCheckBoxStartup.setChecked(settings.getBoolean(SETTINGS_RUN_ON_STARTUP,false));
        mCheckBoxBackground.setChecked(settings.getBoolean(SETTINGS_RUN_IN_BACKGROUND,false));
        mCheckBoxNotification.setChecked(settings.getBoolean(SETTINGS_NOTIFICATION,false));
        
    }
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		int mTagValue = (Integer) buttonView.getTag();
		switch(mTagValue){
		case CLICK_STARTUP:
			appContext.SettingsRunOnStartup = isChecked;
			settings.edit().putBoolean(SETTINGS_RUN_ON_STARTUP, isChecked).commit();
			break;
		case CLICK_BACKGROUND:
			appContext.SettingsRunInBackGround = isChecked;
			settings.edit().putBoolean(SETTINGS_RUN_IN_BACKGROUND, isChecked).commit();
			break;
		case CLICK_NOTIFICATION:{
			appContext.SettingsNotification = isChecked;
			settings.edit().putBoolean(SETTINGS_NOTIFICATION, isChecked).commit();
			if(appContext.SettingsNotification)
			{
				if(appContext.BackgroundServiceIntent == null)
				{
					appContext.BackgroundServiceIntent = new Intent(appContext,BackgroundService.class);
					appContext.startService(appContext.BackgroundServiceIntent);
				}
			}
			else if( !appContext.SettingsNotification )
			{
				if(appContext.BackgroundServiceIntent != null)
				{
					appContext.stopService(appContext.BackgroundServiceIntent);
					appContext.BackgroundServiceIntent = null;
				}
			}
		}
		break;
		default:break;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		int tag = (Integer) v.getTag();
		
		switch (tag)
		{
			case CLICK_REFRESH_FREQUENCY:
			{
		        final String[] itemsString = getResources().getStringArray(R.array.refresh_sort_options);
		        final String[] itemInt = getResources().getStringArray(R.array.refresh_sort_options_values);
		        new AlertDialog.Builder(this).setTitle("选择间隔时间").setItems(itemsString, new DialogInterface.OnClickListener() { 
		            public void onClick(DialogInterface dialog, int item) { 
		            	appContext.SettingsRefreshFrequency = Integer.parseInt(itemInt[item]);
		            	settings.edit().putString(SETTINGS_REFRESH_FREQUENCY, itemInt[item]).commit();
		            	mTextViewRefreshFrequencyInterval.setText(getResources().getString(R.string.current_Interval)+itemsString[item]);
		            } 
		        }).show();//显示对话框 
			}
			break;
			case CLICK_CHECK_FREQUENCY:
			{
		        final String[] itemsString = getResources().getStringArray(R.array.check_frequency_sort_options);
		        final String[] itemInt = getResources().getStringArray(R.array.check_frequency_sort_options_values);
		        new AlertDialog.Builder(this).setTitle("选择间隔时间").setItems(itemsString, new DialogInterface.OnClickListener() { 
		            public void onClick(DialogInterface dialog, int item) { 
		            	appContext.SettingsCheckFrequency = Integer.parseInt(itemInt[item]);
		            	settings.edit().putString(SETTINGS_CHECK_FREQUENCY, itemInt[item]).commit();
		            	mTextViewCheckFrequencyInterval.setText(getResources().getString(R.string.current_Interval)+itemsString[item]);
		            } 
		        }).show();//显示对话框 
			}
			break;
			case CLICK_BACK:
			{
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			}
			break;
			default:break;
		}
	
	
	}
}