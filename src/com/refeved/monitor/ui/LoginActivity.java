/*
 * 文件名：LoginActivity.java
 * 功能：登陆界面
 * 作者：huwei
 * 创建时间：2013-10-17
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.AppManager;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.UserInfo;

public class LoginActivity extends BaseActivity
{
	ImageButton button_land;
	Button button_setting;
	private AppContext appContext;
	ProgressDialog m_pDialog;
	private EditText unameEt,upassEt;
	private String FILE_NAME;
	private UserInfo mUserInfo;
	private RadioGroup      m_RadioGroupNetSelect;  
	private RadioButton     m_RadioButtonInwardNet,m_RadioButtonExternalNet; 
	public static final String ExternalServerIP = "net.serverip.External";
	public static final String InwardServerIP = "net.serverip.Inward";
	private String mRadioGroupStatus;//服务ip单选框状态
	int m_count = 0;
	//private CustomImageView image = null;
	class DialogThread extends Thread {
		@Override
		public void run() {
			int count = 10;
			while (count-- > 0) {
				try {
					
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(m_pDialog.isShowing())
			{
				m_pDialog.cancel();
				Looper.prepare();     
				UIHealper.DisplayToast(appContext,"登陆超时！");
				Looper.loop(); 	
			}

		}
	}
	DialogThread startThread;
	private CheckBox m_autoLogin; //自动登录选框
	private CheckBox m_remPasswawrd;//记住密码选框
	private SharedPreferences sp;
	@SuppressLint("WorldReadableFiles") public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        startThread = new DialogThread();
        appContext = (AppContext) getApplication();
        FILE_NAME = appContext.getString(R.string.settings_filename);
        //关闭自动弹出的输入法
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.login_activity);
        
        IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_FINDBYLOGIN);
		appContext.registerReceiver(receiver, filter);
		
		TextView setting_button = (TextView)findViewById(R.id.login_setting_button);

        unameEt=(EditText)findViewById(R.id.username);
        upassEt=(EditText)findViewById(R.id.password);
        button_land = (ImageButton)findViewById(R.id.ImageButton_login);
		m_autoLogin = (CheckBox) findViewById(R.id.cb_autoLogin);  
	    m_remPasswawrd = (CheckBox) findViewById(R.id.cb_pasward);
		//网络单项选择框
		m_RadioGroupNetSelect = (RadioGroup) findViewById(R.id.RadioGroupNetSelect);  
		m_RadioButtonInwardNet = (RadioButton) findViewById(R.id.RadioButtonInwardNet);  
		m_RadioButtonExternalNet = (RadioButton) findViewById(R.id.RadioButtonExternalNet); 
		//获得实例对象   
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
	    InitRadioButtonStatus();
	  //获取intent附加值
	  	Bundle extras = getIntent().getExtras();
	  	if(extras != null){
	  		if(extras.getByte("LoginStatus") == 1){
	  			sp.edit().putBoolean("AUTO_ISCHECK", false).commit(); 
	  		}
	  	}
        //判断记住密码多选框的状态   
	    if(sp.getBoolean("ISCHECK", true)){  
			  //设置默认是记录密码状态   
			  m_remPasswawrd.setChecked(true);  
			  unameEt.setText(sp.getString("USER_NAME", ""));  
			  upassEt.setText(sp.getString("PASSWORD", ""));  
			  //判断自动登陆多选框状态   
			  if(sp.getBoolean("AUTO_ISCHECK", true))  
			  {  
			         //设置默认是自动登录状态   
				  	m_autoLogin.setChecked(true);  
					if(mRadioGroupStatus.equals(ExternalServerIP)){
						WebClient.mCurrentServerIP = appContext.ExternalServerIP;
					}else{
						WebClient.mCurrentServerIP = appContext.InwardServerIP;
					}
					String uname = unameEt.getText().toString();
					String password = upassEt.getText().toString();
					if((!uname.equals("")) && (!password.equals("")))
					{
						createDialog();
						WebClient client = WebClient.getInstance(appContext);
						Map<String,String> param = new HashMap<String, String>();
						param.put(uname, password);
						client.sendMessage(appContext, WebClient.Method_findByLogin, param);
					}

			  }  
        }else{
			unameEt.setText("");
			upassEt.setText("");
			  if(sp.getBoolean("AUTO_ISCHECK", true))  
			  {  
				  m_autoLogin.setChecked(true);  
			  }
        }
        //监听记住密码多选框按钮事件   
        m_remPasswawrd.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (m_remPasswawrd.isChecked()) {  
                      
                    System.out.println("记住密码已选中");  
                    sp.edit().putBoolean("ISCHECK", true).commit();  
                      
                }else {  
                      
                    System.out.println("记住密码没有选中");  
                    sp.edit().putBoolean("ISCHECK", false).commit();  
                      
                }  
  
            }  
        });  
	              
        //监听自动登录多选框事件   
        m_autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (m_autoLogin.isChecked()) {  
                    System.out.println("自动登录已选中");  
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();  
  
                } else {  
                    System.out.println("自动登录没有选中");  
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();  
                }  
            }  
        }); 
	    m_RadioGroupNetSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == m_RadioButtonInwardNet.getId()){
					WebClient.mCurrentServerIP = appContext.InwardServerIP;
					mRadioGroupStatus = InwardServerIP;
				}
				else{
					WebClient.mCurrentServerIP = appContext.ExternalServerIP;
					mRadioGroupStatus = ExternalServerIP;
				}
				SharedPreferences sharedPreferences = appContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				//添加要保存的数据
				editor.putString("RadioNetStatus",mRadioGroupStatus);
				//确认保存
				editor.commit();
			}
			
		});
		
		setting_button.setOnClickListener(new Button.OnClickListener()
	    {
	    	public void onClick(View v)
	    	{ 
	    		Intent intent = new Intent();
				/* 指定intent要启动的类 */
	    		intent.setClass(LoginActivity.this, LoginSettingActivity.class);
	    		/* 启动一个新的Activity */
	    		startActivityForResult(intent, 1);
	           
	    	}
	    });

	    button_land.setOnClickListener(new Button.OnClickListener()
	    {
	    	public void onClick(View v)
	    	{ 
	    		String uname = unameEt.getText().toString();
				String password = upassEt.getText().toString();
				if((uname.equals("")) || (password.equals("")))
				{
					
					UIHealper.DisplayToast(appContext,"用户名或密码不能为空！");
				}
				else 
				{
					if(mRadioGroupStatus.equals(ExternalServerIP)){
						WebClient.mCurrentServerIP = appContext.ExternalServerIP;
					}else{
						WebClient.mCurrentServerIP = appContext.InwardServerIP;
					}
					createDialog();
	
					WebClient client = WebClient.getInstance(appContext);
					Map<String,String> param = new HashMap<String, String>();
					param.put(uname, password);
					client.sendMessage(appContext, WebClient.Method_findByLogin, param);
				}
	    	}
	
	
	    });
    }
	private void InitRadioButtonStatus() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		//从文件中获取保存的数据
		mRadioGroupStatus = sharedPreferences.getString("RadioNetStatus", InwardServerIP);
		if(mRadioGroupStatus.equals(ExternalServerIP)){
			m_RadioButtonExternalNet.setChecked(true);
		}else{
			m_RadioButtonInwardNet.setChecked(true);
		}
	}
	private void createDialog() {
		//创建ProgressDialog对象
		m_pDialog = new ProgressDialog(LoginActivity.this,R.style.dialog);

		// 设置进度条风格，风格为圆形，旋转的
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		// 设置ProgressDialog 提示信息
		m_pDialog.setMessage("当前IP："+WebClient.mCurrentServerIP+"\n正在验证信息...");
		// 设置ProgressDialog 的进度条是否不明确
		m_pDialog.setIndeterminate(false);
		
		// 设置ProgressDialog 是否可以按退回按键取消
		m_pDialog.setCancelable(true);

		// 让ProgressDialog显示
		m_pDialog.show();
	}  
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,  Intent data)  
	{   
		super.onActivityResult(requestCode, resultCode, data);

	}

	
	//保存用户名和密码
	protected void onSaveContent() 
	{
		String usernameContent = unameEt.getText().toString();
		String passwordContent = upassEt.getText().toString();
		sp.edit().putString("USER_NAME", usernameContent).commit();
		sp.edit().putString("PASSWORD", passwordContent).commit();
	}
	//清除用户名和密码
	protected void onClearContent() 
	{
		sp.edit().putString("USER_NAME", "").commit();
		sp.edit().putString("PASSWORD", "").commit();
	}
	//接收广播
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_FINDBYLOGIN))
			{

				if(resXml != null)
				{
					if(resXml.equals("error"))
					{
						if(m_pDialog != null)
							m_pDialog.cancel();
						UIHealper.DisplayToast(appContext,"用户名和密码错误！");
					}
					else if(resXml.equals("null")){
						if(m_pDialog != null)
							m_pDialog.cancel();
						UIHealper.DisplayToast(appContext,"登陆失败，请检查网络是否连通！");
					}
					else
					{	
						try
						{
							//登录成功和记住密码框为选中状态才保存用户信息   
							if(m_remPasswawrd.isChecked()){
								onSaveContent();	
							}
							else{
								onClearContent();
							}
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							mUserInfo = UserInfo.getAppManager();
							ConserveUserInfo(rootElement, mUserInfo);
							appContext.UserID = mUserInfo.mUserID;
							appContext.UserName = mUserInfo.mUserName;
							SharedPreferences settings = getSharedPreferences(getString(R.string.settings_filename),Context.MODE_PRIVATE);
							Editor editor = settings.edit();
							//添加要保存的数据
							editor.putString(getString(R.string.settings_userID), appContext.UserID);
							//确认保存
							editor.commit();
							if(m_pDialog != null){
								m_pDialog.cancel();	
							}
//							UIHealper.DisplayToast(appContext,"登陆成功");
			        		/* 指定intent要启动的类 */
			        		intent.setClass(LoginActivity.this, MainActivity.class);
			        		/* 启动一个新的Activity */
			        		startActivity(intent);
			        		/* 关闭当前的Activity */
			        		LoginActivity.this.finish();;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}	
					}
				}
			}		
		}
	};
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			AppManager.getAppManager().AppExit(this);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	private void ConserveUserInfo(Element rootElement, UserInfo mUserInfo2) 
	{
		Element e = null;
		
		if(rootElement != null && mUserInfo2 != null)
		{
			 if((e =rootElement.getChild("USERID")) != null)
			 {
				 mUserInfo2.setUserID(e.getValue());
			 }  
			 if((e =rootElement.getChild("USERNAME")) != null)
			 {
				 mUserInfo2.setUserName(e.getValue());
			 } 
			 if((e =rootElement.getChild("USERPWD")) != null)
			 {
				 mUserInfo2.setPassWord(e.getValue());
			 } 
			 if((e =rootElement.getChild("EMAIL")) != null)
			 {
				 mUserInfo2.setEmail(e.getValue());
			 } 
			 if((e =rootElement.getChild("ALIAS")) != null)
			 {
				 mUserInfo2.setAlias(e.getValue());
			 } 
			 if((e =rootElement.getChild("PHONECODE")) != null)
			 {
				 mUserInfo2.setPhoneCode(e.getValue());
			 } 
		
		}
		
	}

}