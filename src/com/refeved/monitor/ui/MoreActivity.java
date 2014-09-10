package com.refeved.monitor.ui;

import java.util.HashMap;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.update.ParseXmlService;
import com.refeved.monitor.update.UpdateManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreActivity extends BaseActivity implements OnClickListener{
	private AppContext appContext;
	private RelativeLayout mRelativeLayoutUpdate;
	private RelativeLayout mRelativeLayoutQRcodeScanning;
	private RelativeLayout mRelativeLayoutSetting;
	private TextView mTextviewUserName;
	private TextView mTextViewUpdateVersion;
	private ImageView mImageViewBack;
	private ImageButton mImageButtonUserGravatar;
	private final int CLICK_DEVICE_UPDATE = 0;
	private final int CLICK_SYSTEM_SETTING = 1;
	private final int CLICK_QRCODE_SCANNING = 2;
	private final int CLICK_BACK = 3;
	private final int CLICK_USER_GRAVATAR = 4;
	private UpdateManager manager;
	
	@SuppressLint("HandlerLeak")
	public Handler mHandler=new Handler()  
	{  
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
			case 1:
				mTextViewUpdateVersion.setText(getResources().getString(R.string.soft_update_no));
				break;
			default:  
				break;            
			}  
			super.handleMessage(msg);  
		}  
	};
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETVERSIONINFO))
			{
				String resXml = intent.getStringExtra(WebClient.Param_resXml);
				try{
					// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
					ParseXmlService service = new ParseXmlService();
					HashMap<String, String> HashMap = service.parseXml(resXml);
					int res = manager.checkUpdate(HashMap);
					if(res == 1){
						Message message=new Message();  
						message.what = 1;  
						mHandler.sendMessage(message);  
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}	
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev_more);
		Init_MoreActivity();
		mTextviewUserName.setText(appContext.UserName);
		mTextViewUpdateVersion.setText("V"+appContext.AppVesion);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETVERSIONINFO);
		appContext.registerReceiver(receiver, filter);
		manager = new UpdateManager(MoreActivity.this);
	}
	private void Init_MoreActivity() {
		// TODO Auto-generated method stub
		appContext = (AppContext) getApplication();
		mRelativeLayoutUpdate = (RelativeLayout) findViewById(R.id.RelativeLayoutUpdate);
		mRelativeLayoutQRcodeScanning = (RelativeLayout) findViewById(R.id.RelativeLayoutQRcodeScanning);
		mRelativeLayoutSetting = (RelativeLayout) findViewById(R.id.RelativeLayoutSetting);
		mTextviewUserName = (TextView) findViewById(R.id.more_activity_title_Textview);
		mTextViewUpdateVersion = (TextView) findViewById(R.id.TextViewUpdateVersion);
		mImageViewBack = (ImageView) findViewById(R.id.more_activity_back);
		mImageButtonUserGravatar = (ImageButton) findViewById(R.id.ImageButtonUserGravatar);
		mRelativeLayoutUpdate.setOnClickListener(this);
		mRelativeLayoutUpdate.setTag(CLICK_DEVICE_UPDATE);
		mRelativeLayoutQRcodeScanning.setOnClickListener(this);
		mRelativeLayoutQRcodeScanning.setTag(CLICK_QRCODE_SCANNING);
		mRelativeLayoutSetting.setOnClickListener(this);
		mRelativeLayoutSetting.setTag(CLICK_SYSTEM_SETTING);
		mImageViewBack.setOnClickListener(this);
		mImageViewBack.setTag(CLICK_BACK);
		mImageButtonUserGravatar.setOnClickListener(this);
		mImageButtonUserGravatar.setTag(CLICK_USER_GRAVATAR);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int tag = (Integer) v.getTag();
		
		switch (tag)
		{
			case CLICK_DEVICE_UPDATE:
			{
				
				WebClient client = WebClient.getInstance(appContext);
				client.sendMessage(appContext, WebClient.Method_getVersionInfo, null);
			}
			break;
			case CLICK_QRCODE_SCANNING:
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(this,CaptureActivity.class);
				startActivityForResult(intent, RESULT_OK);
			}
			break;
			case CLICK_SYSTEM_SETTING:
			{
				Intent intent = new Intent(this,SettingActivity.class);
				startActivityForResult(intent, RESULT_OK);	
			}
			break;
			case CLICK_BACK:
			{
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			}
			break;
			case CLICK_USER_GRAVATAR:
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(this,UserInfoActivity.class);
				startActivityForResult(intent, RESULT_OK);
			}
			break;
			default:break;
		}
	
	}
	protected void onDestroy() 
	{
		super.onDestroy();
	}
}