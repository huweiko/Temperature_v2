/*
 * �ļ�����LoginActivity.java
 * ���ܣ���½����
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-17
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
	private String mRadioGroupStatus;//����ip��ѡ��״̬
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
				UIHealper.DisplayToast(appContext,"��½��ʱ��");
				Looper.loop(); 	
			}

		}
	}
	DialogThread startThread;
	private CheckBox m_autoLogin; //�Զ���¼ѡ��
	private CheckBox m_remPasswawrd;//��ס����ѡ��
	private SharedPreferences sp;
	@SuppressLint("WorldReadableFiles") public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        startThread = new DialogThread();
        appContext = (AppContext) getApplication();
        FILE_NAME = appContext.getString(R.string.settings_filename);
        //�ر��Զ����������뷨
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
		//���絥��ѡ���
		m_RadioGroupNetSelect = (RadioGroup) findViewById(R.id.RadioGroupNetSelect);  
		m_RadioButtonInwardNet = (RadioButton) findViewById(R.id.RadioButtonInwardNet);  
		m_RadioButtonExternalNet = (RadioButton) findViewById(R.id.RadioButtonExternalNet); 
		//���ʵ������   
		sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
	    InitRadioButtonStatus();
	  //��ȡintent����ֵ
	  	Bundle extras = getIntent().getExtras();
	  	if(extras != null){
	  		if(extras.getByte("LoginStatus") == 1){
	  			sp.edit().putBoolean("AUTO_ISCHECK", false).commit(); 
	  		}
	  	}
        //�жϼ�ס�����ѡ���״̬   
	    if(sp.getBoolean("ISCHECK", true)){  
			  //����Ĭ���Ǽ�¼����״̬   
			  m_remPasswawrd.setChecked(true);  
			  unameEt.setText(sp.getString("USER_NAME", ""));  
			  upassEt.setText(sp.getString("PASSWORD", ""));  
			  //�ж��Զ���½��ѡ��״̬   
			  if(sp.getBoolean("AUTO_ISCHECK", true))  
			  {  
			         //����Ĭ�����Զ���¼״̬   
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
        //������ס�����ѡ��ť�¼�   
        m_remPasswawrd.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (m_remPasswawrd.isChecked()) {  
                      
                    System.out.println("��ס������ѡ��");  
                    sp.edit().putBoolean("ISCHECK", true).commit();  
                      
                }else {  
                      
                    System.out.println("��ס����û��ѡ��");  
                    sp.edit().putBoolean("ISCHECK", false).commit();  
                      
                }  
  
            }  
        });  
	              
        //�����Զ���¼��ѡ���¼�   
        m_autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
                if (m_autoLogin.isChecked()) {  
                    System.out.println("�Զ���¼��ѡ��");  
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();  
  
                } else {  
                    System.out.println("�Զ���¼û��ѡ��");  
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
				//���Ҫ���������
				editor.putString("RadioNetStatus",mRadioGroupStatus);
				//ȷ�ϱ���
				editor.commit();
			}
			
		});
		
		setting_button.setOnClickListener(new Button.OnClickListener()
	    {
	    	public void onClick(View v)
	    	{ 
	    		Intent intent = new Intent();
				/* ָ��intentҪ�������� */
	    		intent.setClass(LoginActivity.this, LoginSettingActivity.class);
	    		/* ����һ���µ�Activity */
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
					
					UIHealper.DisplayToast(appContext,"�û��������벻��Ϊ�գ�");
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
		//���ļ��л�ȡ���������
		mRadioGroupStatus = sharedPreferences.getString("RadioNetStatus", InwardServerIP);
		if(mRadioGroupStatus.equals(ExternalServerIP)){
			m_RadioButtonExternalNet.setChecked(true);
		}else{
			m_RadioButtonInwardNet.setChecked(true);
		}
	}
	private void createDialog() {
		//����ProgressDialog����
		m_pDialog = new ProgressDialog(LoginActivity.this,R.style.dialog);

		// ���ý�������񣬷��ΪԲ�Σ���ת��
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		// ����ProgressDialog ��ʾ��Ϣ
		m_pDialog.setMessage("��ǰIP��"+WebClient.mCurrentServerIP+"\n������֤��Ϣ...");
		// ����ProgressDialog �Ľ������Ƿ���ȷ
		m_pDialog.setIndeterminate(false);
		
		// ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
		m_pDialog.setCancelable(true);

		// ��ProgressDialog��ʾ
		m_pDialog.show();
	}  
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,  Intent data)  
	{   
		super.onActivityResult(requestCode, resultCode, data);

	}

	
	//�����û���������
	protected void onSaveContent() 
	{
		String usernameContent = unameEt.getText().toString();
		String passwordContent = upassEt.getText().toString();
		sp.edit().putString("USER_NAME", usernameContent).commit();
		sp.edit().putString("PASSWORD", passwordContent).commit();
	}
	//����û���������
	protected void onClearContent() 
	{
		sp.edit().putString("USER_NAME", "").commit();
		sp.edit().putString("PASSWORD", "").commit();
	}
	//���չ㲥
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
						UIHealper.DisplayToast(appContext,"�û������������");
					}
					else if(resXml.equals("null")){
						if(m_pDialog != null)
							m_pDialog.cancel();
						UIHealper.DisplayToast(appContext,"��½ʧ�ܣ����������Ƿ���ͨ��");
					}
					else
					{	
						try
						{
							//��¼�ɹ��ͼ�ס�����Ϊѡ��״̬�ű����û���Ϣ   
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
							//���Ҫ���������
							editor.putString(getString(R.string.settings_userID), appContext.UserID);
							//ȷ�ϱ���
							editor.commit();
							if(m_pDialog != null){
								m_pDialog.cancel();	
							}
//							UIHealper.DisplayToast(appContext,"��½�ɹ�");
			        		/* ָ��intentҪ�������� */
			        		intent.setClass(LoginActivity.this, MainActivity.class);
			        		/* ����һ���µ�Activity */
			        		startActivity(intent);
			        		/* �رյ�ǰ��Activity */
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