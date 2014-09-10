package com.refeved.monitor.ui;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.refeved.monitor.achartengine.BudgetDoughnutChart;
import com.refeved.monitor.adapter.DevListViewAdapter;
import com.refeved.monitor.adapter.GridAdapter;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.AppManager;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.BackgroundService;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.AreaTreeNode;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.TreeNode;
import com.refeved.monitor.struct.UserInfo;
import com.refeved.monitor.update.ParseXmlService;
import com.refeved.monitor.update.UpdateManager;

public class MainActivity extends BaseActivity implements
		OnClickListener{
	private final int CLICK_LOG_BUTTON = 0;  //地址按钮
	private final int CLICK_DISTRICT_BUTTON = 1;  //地址按钮
	private final int CLICK_MORE_BUTTON = 2;  //地址按钮
	private final int CLICK_FRESH_BUTTON = 3; //刷新按钮
	public final int SELECT_DISTRICT = 1;
	public final int SELECT_LOG = 2;
	public final int SELECT_MORE = 3;
	
	private final int HANDLER_DEVUPDATE = 1; //设备更新
	private final int HANDLER_SHOW_COUNT = 2; //计数显示
	private final int HANDLER_CLOSE_COUNT = 3; //关闭计数显示
	private final int HANDLER_ISSHOW_REFRESH_BUTTON = 4; //设置是否显示刷新按钮
	private final int HANDLER_NETSET_VISIBLE = 5; //显示断网部件
	private final int HANDLER_NETSET_INVISIBLE = 6; //隐藏断网部件
	private final int HANDLER_UPDATE_DEV_COUNT = 7; //更新设备状态统计
	
	private View mSurveilListEmptyShow;//无设备时的显示内容
	Boolean visiable = false;
	private LinearLayout mNotNetWorking;
	CheckQuitThread checkQuitThread;
	private AppContext appContext;
	private CheckNetConnectThread checkNetThread;
	private RefreshThread mRefreshThread;
	private List<AreaTreeNode> lvDevData = new ArrayList<AreaTreeNode>();
//	public static List<Device> lvAbnormalDevData = new ArrayList<Device>();//异常设备列表
	private List<AreaTreeNode> lvCoolerDevData = new ArrayList<AreaTreeNode>();
	private ListView mDevListView;
	final Semaphore sempCountThread = new Semaphore(0);
	private boolean mCountThreadStutas = false; //判断计数线程是否为等待状态
	private RelativeLayout layout;
	private BudgetDoughnutChart mBudgetDoughnutChart;
	private ImageButton mMenuDistrict;
	private ImageButton mMenuLog;
	private ImageButton mMenuMore;
	private DevListViewAdapter mDevListViewAdapter;
	private int mDeviceTotalNum;
	private int mDeviceAlarmNum;
	private int mDeviceNetBreakNum;
	private int mDeviceNormalNum;
	private int mDeviceOtherNum;
	private String mDeviceDistrictName;
	
	private TextView mTextViewDeviceTotalNum;
	private TextView mTextViewDeviceAlarmNum;
	private TextView mTextViewDeviceNetBreakNum;
	private TextView mTextViewDeviceNormalNum;
	private TextView mTextViewDeviceDistrict;
	private TextView mTextViewPercent;
	private TextView mTextViewNormal;
	private TextView mTextViewRefreshCount;
	private TextView mTextViewdangqian;
	
	private ImageView mImageButtonMainAvtivityRefresh;
	
	
	private ProgressBar mProgressBarMainAvtivity;
	private Dialog noticeDialog;
	private RelativeLayout mRelativeLayoutCoolerNodeGrid;
	private CoolerNodeFragment mTextFragmentOne = null;
	private FragmentManager mFragmentManager = null;
	private boolean mIsOnlyShowAbnormalDeviceStatus = false;
	
	private AreaTreeNode mAreaTreeNode = null;
	class CheckQuitThread extends Thread {
		@Override
		public void run() {
			int count = 2;
			while (count-- > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	class CheckNetConnectThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(2000);
					if(!checkNetwork()){
						Message message=new Message();  
						message.what=HANDLER_NETSET_VISIBLE;  
						mHandler.sendMessage(message);  
						
					}
					else{
						Message message=new Message();  
						message.what=HANDLER_NETSET_INVISIBLE;  
						mHandler.sendMessage(message);  
						
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public class RefreshThread  extends Thread{

		@Override
		public void run(){
			while(appContext.SettingsRefreshFrequency > 0 && visiable)
			{
				HandlerSendMessage(HANDLER_ISSHOW_REFRESH_BUTTON, 0);
				int count = appContext.SettingsRefreshFrequency;
				Log.i("huwei", "SurveilThread-->count = "+count);

				while(count-- > 0)
				{
					try {  
						//显示计数
						HandlerSendMessage(HANDLER_SHOW_COUNT,count+1);
						Thread.sleep(1000);  
					} catch (InterruptedException e) {  
						e.printStackTrace();  
						break;
					}  
				}
				if(visiable){
					//更新设备信息
					HandlerSendMessage(HANDLER_CLOSE_COUNT,-1);
					HandlerSendMessage(HANDLER_DEVUPDATE,-1);
					try {
						mCountThreadStutas = true;
						sempCountThread.acquire();
						mCountThreadStutas = false;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(mRefreshThread != null){
				mRefreshThread.interrupt();
				mRefreshThread = null;
			}
			//线程退出时关闭计数显示
			HandlerSendMessage(HANDLER_CLOSE_COUNT,-1);
			if(mProgressBarMainAvtivity.getVisibility() != View.VISIBLE)
				HandlerSendMessage(HANDLER_ISSHOW_REFRESH_BUTTON, 1);
		}
	}
	
	@SuppressLint("HandlerLeak")
	public Handler mHandler=new Handler()  
	{  
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
			case HANDLER_NETSET_VISIBLE:  
				if(mNotNetWorking.getVisibility() == View.GONE){
					Log.i("huwei", "没有网");
					mNotNetWorking.setVisibility(View.VISIBLE);		
				}

				break;  
			case HANDLER_NETSET_INVISIBLE:  
				if(mNotNetWorking.getVisibility() == View.VISIBLE){
					Log.i("huwei", "有网");
					mNotNetWorking.setVisibility(View.GONE);	
				}

				break;
			case HANDLER_DEVUPDATE:  
				onDevListUpdate();
				break;  
			case HANDLER_SHOW_COUNT:
				if(mTextViewRefreshCount != null){
					mTextViewRefreshCount.setVisibility(View.VISIBLE);
					mTextViewRefreshCount.setText(""+msg.arg1);		
				}
				break;
			case HANDLER_CLOSE_COUNT:
				mTextViewRefreshCount.setVisibility(View.INVISIBLE);
				break;
			case HANDLER_ISSHOW_REFRESH_BUTTON:
				if(msg.arg1 == 0){
					mImageButtonMainAvtivityRefresh.setVisibility(View.INVISIBLE);	
				}else{
					mImageButtonMainAvtivityRefresh.setVisibility(View.VISIBLE);	
				}
				break;
			case HANDLER_UPDATE_DEV_COUNT:
			    List<double[]> values = new ArrayList<double[]>();
			    values.add(new double[] { mDeviceNormalNum, mDeviceNetBreakNum, mDeviceAlarmNum, mDeviceOtherNum});
			    layout.removeAllViews();
			    layout.addView(mBudgetDoughnutChart.execute(MainActivity.this,values), new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			    mTextViewDeviceTotalNum.setText(mDeviceTotalNum + "台");
				mTextViewDeviceAlarmNum.setText(mDeviceAlarmNum + "台");
				mTextViewDeviceNetBreakNum.setText(mDeviceNetBreakNum + "台");
				mTextViewDeviceNormalNum.setText(mDeviceNormalNum + "台");
				mTextViewDeviceDistrict.setText(mDeviceDistrictName);
				if(mDeviceTotalNum != 0){
					mTextViewPercent.setText(mDeviceNormalNum*100/mDeviceTotalNum + "%");
					mTextViewNormal.setText("正常");
				}else{
					mTextViewPercent.setText("");
					mTextViewNormal.setText("无异常");
				}
				if(mCountThreadStutas)
					sempCountThread.release();
				break;
			default:  
				break;            
			}  
			super.handleMessage(msg);  
		}  
	};

	private void HandlerSendMessage(int xWhat,int xArg1){
		Message message=new Message();  
		message.what = xWhat;  
		message.arg1 = xArg1;
		mHandler.sendMessage(message);  	
	}
	private void ShowRefreshing(boolean status){
		if(status){
			mImageButtonMainAvtivityRefresh.setVisibility(View.INVISIBLE);
			mProgressBarMainAvtivity.setVisibility(View.VISIBLE);
		}else{
			if(mRefreshThread == null)
				mImageButtonMainAvtivityRefresh.setVisibility(View.VISIBLE);
			
			mProgressBarMainAvtivity.setVisibility(View.INVISIBLE);
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appContext = (AppContext) getApplication();
		mBudgetDoughnutChart = new BudgetDoughnutChart();
		mDevListView = (ListView)findViewById(R.id.listViewDevice);
		layout = (RelativeLayout)findViewById(R.id.LinearLayoutDoughnutChart);
		mTextViewDeviceTotalNum = (TextView)findViewById(R.id.TextViewDeviceTotalNum);
		mTextViewDeviceAlarmNum = (TextView)findViewById(R.id.TextViewDeviceAlarmNum);
		mTextViewDeviceNetBreakNum = (TextView)findViewById(R.id.TextViewDeviceNetworkBreakNum);
		mTextViewDeviceNormalNum = (TextView)findViewById(R.id.TextViewDeviceNormalNum);
		mTextViewDeviceDistrict =  (TextView)findViewById(R.id.TextViewDeviceDistrict);
		mTextViewPercent = (TextView)findViewById(R.id.TextViewPercent);
		mTextViewNormal = (TextView)findViewById(R.id.TextViewNormal); 
		mTextViewdangqian = (TextView)findViewById(R.id.TextViewdangqian); 
		mSurveilListEmptyShow = findViewById(R.id.SurveilListEmptyShow);
		mMenuDistrict = (ImageButton)findViewById(R.id.menuDistrict);
		mMenuLog = (ImageButton)findViewById(R.id.menuLog);
		mMenuMore = (ImageButton)findViewById(R.id.menuMore);
		mRelativeLayoutCoolerNodeGrid = (RelativeLayout)findViewById(R.id.RelativeLayoutCoolerNodeGrid);
		mTextViewRefreshCount = (TextView)findViewById(R.id.TextViewRefreshCount);
		mImageButtonMainAvtivityRefresh = (ImageView) findViewById(R.id.ImageButtonMainAvtivityRefresh);
		mImageButtonMainAvtivityRefresh.setOnClickListener(this);
		mImageButtonMainAvtivityRefresh.setTag(CLICK_FRESH_BUTTON);
		mProgressBarMainAvtivity = (ProgressBar) findViewById(R.id.ProgressBarMainAvtivity);
		mMenuDistrict.setOnClickListener(this);
		mMenuDistrict.setTag(CLICK_DISTRICT_BUTTON);
		mMenuLog.setOnClickListener(this);
		mMenuLog.setTag(CLICK_LOG_BUTTON);
		mMenuMore.setOnClickListener(this);
		mMenuMore.setTag(CLICK_MORE_BUTTON);
		if(mIsOnlyShowAbnormalDeviceStatus){
			mTextViewdangqian.setText("异常设备统计：");
		}else{
			mTextViewdangqian.setText("全部设备统计：");
		}
		
		mDevListViewAdapter = new DevListViewAdapter(appContext, new ArrayList<AreaTreeNode>(), R.layout.surveil_fragment_gridview);
		mDevListView.setAdapter(mDevListViewAdapter);
		onDevListUpdate();
		//检查更新
		WebClient client = WebClient.getInstance(appContext);
		client.sendMessage(appContext, WebClient.Method_getVersionInfo, null);
		
		mNotNetWorking =  (LinearLayout) findViewById(R.id.notnetworking);
		mNotNetWorking.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
	            startActivity(intent);
			}
		});
		
		checkNetThread = new CheckNetConnectThread();
		checkNetThread.start();

		checkQuitThread = null;

		if (appContext.SettingsNotification) {
			if (appContext.BackgroundServiceIntent == null) {
				appContext.BackgroundServiceIntent = new Intent(appContext,
						BackgroundService.class);
				appContext.startService(appContext.BackgroundServiceIntent);
			}
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETBYAID_DONE);
		filter.addAction(GridAdapter.SHOW_DEVICE_INFO);
		filter.addAction(WebClient.INTERNAL_ACTION_GETVERSIONINFO);
		filter.addAction(GridAdapter.SHOW_HANDLE_DIALOG);
		filter.addAction(WebClient.Method_setHandleStatus);
		appContext.registerReceiver(receiver, filter);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode,  Intent data)  
	{   
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SELECT_DISTRICT)
		{
			if(resultCode == RESULT_OK)
			{
				if(data != null)
				{
					if(data.getStringExtra("AID") != null){
						appContext.AID = data.getStringExtra("AID");
						//2个activity 之间的数据传递可以使用SharedPreferences来共享数据
						SharedPreferences settings = appContext.getSharedPreferences(getString(R.string.settings_filename),Context.MODE_PRIVATE); 
						SharedPreferences.Editor editor = settings.edit();   
						editor.putString(getString(R.string.settings_aid), appContext.AID); 
						editor.commit();
						onDevListUpdate();	
					}
					if(appContext.SettingsNotification)
					{
						if(appContext.BackgroundServiceIntent == null)
						{
							appContext.BackgroundServiceIntent = new Intent(appContext,BackgroundService.class);
							appContext.startService(appContext.BackgroundServiceIntent);
						}
					}
				}
			}
		}
	}
	
	public void onClick(View v)
	{
		int tag = (Integer) v.getTag();
		
		switch (tag)
		{
			
			case CLICK_DISTRICT_BUTTON:
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(this,DistrictListViewActivity.class);
				intent.putExtra("TitleName", R.string.header_tittle_dev_district);
				startActivityForResult(intent, SELECT_DISTRICT);

				if( !(appContext.SettingsNotification && appContext.SettingsRunInBackGround))
				{
					if(appContext.BackgroundServiceIntent != null)
					{
						this.stopService(appContext.BackgroundServiceIntent);
						appContext.BackgroundServiceIntent = null;
					}
				}
			}
			break;
			case CLICK_FRESH_BUTTON:
			{
				onDevListUpdate();
			}
				
			break;
			case CLICK_LOG_BUTTON:
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(this,LogActivity.class);
				startActivityForResult(intent, SELECT_LOG);
			}
			
			break;
			case CLICK_MORE_BUTTON:
			{
				Intent intent = new Intent(this,MoreActivity.class);
				startActivity(intent);
			}
			break;
			default:break;
		}
	}
	 //更新设备列表
	private void onDevListUpdate(){
		ShowRefreshing(true);
		WebClient client = WebClient.getInstance(appContext);
		client.sendMessage(appContext, WebClient.Method_getMonitorInfosForAll, null);
	}
//	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		if(mDevGridView.getItems().isEmpty()) return;
//		showDevDetail((Device)mDevGridView.getItems().get(position));
//	}
	private void isCloseShowDialog(DialogInterface dialog,boolean status) {
		try
		{
		    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
		    field.setAccessible(true);
		     //设置mShowing值，欺骗android系统
		    field.set(dialog, status);
		}catch(Exception e) {
		    e.printStackTrace();
		}
	}
	//显示冷库设备节点
	public void showCoolerDevDetail(String CoolerId)
	{
		mTextFragmentOne = new CoolerNodeFragment();
		mTextFragmentOne.lvDevData.clear();
		if(lvCoolerDevData.size() > 0){
			int i = 0;
			while(i < lvCoolerDevData.size()){
				if(lvCoolerDevData.get(i).getmId().equals(CoolerId)){
					mTextFragmentOne.lvDevData.add(lvCoolerDevData.get(i));
					break;
				}
				i++;
			}
		}
		if (null == mFragmentManager) {
			mFragmentManager = getSupportFragmentManager();
		}
		mRelativeLayoutCoolerNodeGrid.setVisibility(View.VISIBLE);
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.add(R.id.FrameLayoutCoolerNodeGrid, mTextFragmentOne);

		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
	/*
	 * 关闭冷库节点的fragment
	 * 
	 * */
	public void closeCoolerNodeFragment(View view){
		if (null == mFragmentManager) {
			mFragmentManager = getSupportFragmentManager();
		}
		mFragmentManager.popBackStack();
		mRelativeLayoutCoolerNodeGrid.setVisibility(View.GONE);
	}
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		@SuppressLint("SimpleDateFormat") @Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			
//			String resXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><address aid='1' des='深圳' pid='0' rightIndex='48'><temperature macid='19' state='9' low='-50.0' high='-30.0' des='dabingxiang' temperature='00000'/><temperature macid='20' state='9' low='-60.0' high='-90.0' des='测试1' temperature='00000'/><address aid='13' des='深圳1楼' pid='1' rightIndex='60'></address><address aid='15' des='深圳8楼' pid='1' rightIndex='62'><humidity macid='15' state='9' low='-90.0' high='70.0' des='湿度1' humidity='16.00'/><humidity macid='16' state='9' low='-500.0' high='500.0' des='11' humidity='00000'/><temperature macid='1' state='25' low='-95.0' high='-60.0' des='冰箱1' temperature='-29.40'/><temperature macid='2' state='9' low='-90.0' high='-70.0' des='冰箱2' temperature='-65.00'/><temperature macid='3' state='9' low='-90.0' high='-70.0' des='冰箱3' temperature='00000'/><temperature macid='4' state='9' low='-90.0' high='-70.0' des='冰箱4' temperature='-87.60'/><temperature macid='5' state='25' low='-90.0' high='-50.0' des='冰箱5' temperature='51.00'/><temperature macid='6' state='9' low='-90.0' high='-70.0' des='冰箱6' temperature='00000'/><temperature macid='7' state='9' low='-90.0' high='-70.0' des='冰箱7' temperature='-82.50'/><temperature macid='8' state='9' low='-90.0' high='-70.0' des='小探头测试' temperature='-73.70'/><temperature macid='9' state='9' low='-90.0' high='-70.0' des='测试1' temperature='-78.80'/></address><address aid='17' des='test地址33' pid='1' rightIndex='64'></address><address aid='21' des='test' pid='1' rightIndex='69'></address><address aid='29' des='100台测试' pid='1' rightIndex='77'><humidity macid='43' state='10' low='-200.0' high='200.0' des='00124b0003966150' humidity='-0.10'/><humidity macid='44' state='10' low='-200.0' high='200.0' des='00124b0003966234' humidity='-0.10'/><humidity macid='45' state='10' low='-200.0' high='200.0' des='00124b0003966155' humidity='-0.10'/><humidity macid='46' state='10' low='-200.0' high='200.0' des='00124b0003966140' humidity='-0.10'/><humidity macid='48' state='14' low='-200.0' high='200.0' des='00124b000396613e' humidity='-0.10'/><temperature macid='18' state='10' low='-100.0' high='200.0' des='00124b000396603b' temperature='101.00'/><temperature macid='35' state='10' low='-100.0' high='200.0' des='00124b00039679e9' temperature='100.30'/><temperature macid='36' state='10' low='-100.0' high='200.0' des='00124b00039675e6' temperature='101.00'/><temperature macid='37' state='11' low='-100.0' high='200.0' des='00124b0003966146' temperature='-32.30'/><temperature macid='38' state='10' low='-100.0' high='200.0' des='00124b0003966173' temperature='101.00'/><temperature macid='39' state='11' low='-100.0' high='200.0' des='00124b0003966227' temperature='101.00'/><temperature macid='40' state='10' low='-500.0' high='500.0' des='00124b0003967596' temperature='100.30'/><temperature macid='41' state='11' low='-200.0' high='200.0' des='00124b000396617a' temperature='101.00'/><temperature macid='42' state='10' low='-200.0' high='200.0' des='00124b0003966230' temperature='101.00'/><temperature macid='47' state='10' low='-200.0' high='200.0' des='00124b0003965f1f' temperature='101.00'/><temperature macid='49' state='10' low='-200.0' high='200.0' des='00124b00039678f5' temperature='101.00'/><temperature macid='50' state='14' low='-200.0' high='200.0' des='00124b00039679a9' temperature='101.00'/><temperature macid='51' state='10' low='-200.0' high='200.0' des='00124b00039679a8' temperature='101.00'/><temperature macid='52' state='10' low='-200.0' high='200.0' des='00124b0003967999' temperature='47.70'/><temperature macid='53' state='10' low='-200.0' high='200.0' des='00124b00039678aa' temperature='47.60'/><temperature macid='54' state='10' low='-200.0' high='200.0' des='00124b00039675a2' temperature='101.00'/><temperature macid='55' state='10' low='-200.0' high='200.0' des='00124b00039679a6' temperature='101.00'/><temperature macid='56' state='10' low='-200.0' high='200.0' des='00124b00039679c7' temperature='100.30'/><temperature macid='57' state='11' low='-200.0' high='200.0' des='00124b00039675aa' temperature='101.00'/><temperature macid='58' state='10' low='-200.0' high='200.0' des='00124b000396759e' temperature='101.00'/><temperature macid='59' state='10' low='-200.0' high='200.0' des='00124b0003967887' temperature='101.00'/></address></address><address aid='20' des='已删除设备' pid='0' rightIndex='68'></address></root>";
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETBYAID_DONE))
			{
				ShowRefreshing(false);
				String resXml = intent.getStringExtra(WebClient.Param_resXml);
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null"))
					{
						if(mCountThreadStutas)
							sempCountThread.release();
						UIHealper.DisplayToast(appContext,"获取设备信息失败");
						if(mDevListView.getAdapter().getCount() == 0){
							mSurveilListEmptyShow.setVisibility(View.VISIBLE);
						}
					}
					else {
						mSurveilListEmptyShow.setVisibility(View.GONE);
						try{
							//SAXBuilder是一个JDOM解析器 能将路径中的XML文件解析为Document对象
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							
							lvCoolerDevData.clear();
							mAreaTreeNode = new AreaTreeNode(null,"root","0",false, new ArrayList<TreeNode>(), new ArrayList<Device>());
							clearDevData();
							parseGetByAidXml(rootElement, mAreaTreeNode, "");
							
							//接收到设备列表后，进行筛选
							filterDevice(mAreaTreeNode,lvDevData);
//							filterAbnormalDevice(lvDevData,lvAbnormalDevData);
							
							if(lvDevData.size() == 0){
								mSurveilListEmptyShow.setVisibility(View.VISIBLE);
							}
							updateListView(lvDevData);
							HandlerSendMessage(HANDLER_UPDATE_DEV_COUNT,-1);

						}
						catch(Exception e)
						{
							e.printStackTrace();
						}	
					}
					
				}

			}
			else if(intent.getAction().equals(GridAdapter.SHOW_DEVICE_INFO)){
				String mId = intent.getStringExtra(getString(R.string.device_info_id_tittle));
				String environmentType = intent.getStringExtra(getString(R.string.device_environmentType));
				if(Integer.parseInt(environmentType) == Device.Temperature_Cooler){
					showCoolerDevDetail(mId);
				}else{
					Intent i_intent = new Intent(appContext, DevDetailActivity.class);
					String id = getString(R.string.device_info_id_tittle);
					i_intent.putExtra(id, mId);
					startActivity(i_intent);
				}
				

			}//设备处理操作
			else if(intent.getAction().equals(GridAdapter.SHOW_HANDLE_DIALOG)){
				final EditText mEditTextAddressName;
				final String deviceID;
				final boolean handleStatus;
				deviceID = intent.getStringExtra("deviceID");
				handleStatus = intent.getBooleanExtra("handleStatus", false);
				AlertDialog.Builder builder = new Builder(MainActivity.this,R.style.dialog);
				builder.setInverseBackgroundForced(true);
			
				String title;
				if(handleStatus){
					title = "结束处理";
				}else{
					title = "开始处理";
				}
				builder.setTitle(title);
				final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
				View vv = inflater.inflate(R.layout.conserve_dialog, null);
				mEditTextAddressName = (EditText) vv.findViewById(R.id.EditTextAddressName);
				builder.setView(vv);
				builder.setNegativeButton(title, new android.content.DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if(!mEditTextAddressName.getText().toString().equals("")){
							isCloseShowDialog(dialog,true);
						}else{
							UIHealper.DisplayToast(appContext, "没有输入处理描述");
							isCloseShowDialog(dialog,false);
						}
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
						UserInfo mUserInfo = UserInfo.getAppManager();
						WebClient client = WebClient.getInstance(appContext);
						Map<String, String> param = new HashMap<String, String>();
						if(handleStatus){//结束处理
							param.put("macid",deviceID);
							param.put("startUserID","");
							param.put("endUserID",mUserInfo.mUserID);
							param.put("startTime", "" );
							param.put("endTime",df.format(new Date()));
							param.put("startDes", "" );
							param.put("endDes",mEditTextAddressName.getText().toString());
						}else{//开始处理
							param.put("macid",deviceID);
							param.put("startUserID",mUserInfo.mUserID);
							param.put("endUserID","");
							param.put("startTime",df.format(new Date()));
							param.put("endTime","");
							param.put("startDes",mEditTextAddressName.getText().toString());
							param.put("endDes","");
						}

						client.sendMessage(appContext, WebClient.Method_setHandleStatus, param);
					}
				});
				builder.setPositiveButton(R.string.soft_update_cancel, new android.content.DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						isCloseShowDialog(dialog,true);
					}
				});
				if(noticeDialog == null){
					noticeDialog = builder.create();
//					WindowManager.LayoutParams wl = noticeDialog.getWindow().getAttributes();
//					wl.alpha=0.6f;//这句设置了对话框的透明度 
//					noticeDialog.getWindow().setAttributes(wl); 

					noticeDialog.show();
				}else{
					if(!noticeDialog.isShowing()){
						noticeDialog = builder.create();
//						WindowManager.LayoutParams wl = noticeDialog.getWindow().getAttributes();
//						wl.alpha=0.8f;//这句设置了对话框的透明度 
//						noticeDialog.getWindow().setAttributes(wl); 
						noticeDialog.show();
					}
				}
			}
			else if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETVERSIONINFO))
			{
				String resXml = intent.getStringExtra(WebClient.Param_resXml);
				try{
					// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
					ParseXmlService service = new ParseXmlService();
					HashMap<String, String> HashMap = service.parseXml(resXml);
					UpdateManager manager = new UpdateManager(MainActivity.this);
					manager.checkUpdate(HashMap);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}	
			}
			else if(intent.getAction().equals(WebClient.Method_setHandleStatus))
			{
			}
		}

	};
	/*清空设备数据*/
	private void clearDevData() {
		//清理老数据
	
		
		lvDevData.clear();
//		lvAbnormalDevData.clear();
		mDeviceAlarmNum = 0;
		mDeviceNetBreakNum = 0;
		mDeviceNormalNum = 0;
		mDeviceTotalNum = 0;
		mDeviceOtherNum = 0;
	}
	public void parseGetByAidXml(Element element,AreaTreeNode node, String parentLocation){
		Element e = null;
		String location = null;
		if(element.getAttributeValue("des") != null)
		{
			location = parentLocation+element.getAttributeValue("des") + " ";
		}
		else
		{
			location = parentLocation + " ";
		}
		while( (e =element.getChild("humidity")) != null)
		{
			String type = Device.Type_Humidity;
			String id = e.getAttributeValue("macid");
			String status = e.getAttributeValue("state");
			String descprition = e.getAttributeValue("des");
			String low = e.getAttributeValue("low");
			String high = e.getAttributeValue("high");
			//		   String hum = e.getText();
			String hum = e.getAttributeValue("humidity");
			String environmentType = e.getAttributeValue("environmentType");
			//把从XML获取的信息存入湿度对象中
			DevHumidity devHum = new DevHumidity(type, environmentType, id, location, status, descprition,low,high, hum,"");
			//把湿度信息增加到节点
			node.addDevice(devHum);
			element.removeChild("humidity");
		}
		if(element.getChild("temperature") != null){
			if(Integer.parseInt(element.getChild("temperature").getAttributeValue("environmentType"))==(Device.Temperature_Cooler)){
				//把冷库增加到lvCoolerDevData
				AreaTreeNode coolerNode = addCoolerDevDataNode(element,
						location);
				//如果是冷库节点就增加一个冷库设备，然后把节点保存在lvCoolerDevData里
				String type = Device.Type_Frige;
				//冷库id为该房间的地址id
				String id = element.getAttributeValue("aid");
				//状态只有正常和不正常 0为异常 1为正常
				String status = "1";
				float temperatureValue = 0.0f;
				//得出该冷库下是否有异常设备
				for(int i = 0;i< coolerNode.getmDevices().size();i++){
					String deviceStatus = coolerNode.getmDevices().get(i).getmStatus();
					Device device = coolerNode.getmDevices().get(i);
							
					if(Integer.parseInt(deviceStatus) != 0x01){
						status = "0";
					}
					//求各节点的温度值总和
					temperatureValue +=Float.parseFloat(((DevFrige)device).getmTemperature());
				}
				//描述就叫冷库
				String descprition = "冷库";
				//温度值上下限值是节点的上下限值
				String low = coolerNode.getmDevices().get(0).getmLow();
				String high = coolerNode.getmDevices().get(0).getmHigh();
				//冷库的温度为该冷库下所有节点的平均温度
				
				Float F_temValue = temperatureValue/coolerNode.getmDevices().size();
				DecimalFormat dcmFmt = new DecimalFormat("0.00");
				//得出平均值作为冷库的温度值
				String temp = dcmFmt.format(F_temValue).toString();
				//设备类型为冷库设备
				String environmentType = Integer.toString(Device.Temperature_Cooler);
				DevFrige devHum = new DevFrige(type,environmentType, id, location, status, descprition,low,high, temp,"");
				node.addDevice(devHum);
				
			}else{
				while( (e =element.getChild("temperature")) != null)
				{
					String type = Device.Type_Frige;
					String id = e.getAttributeValue("macid");
					String status = e.getAttributeValue("state");
					String descprition = e.getAttributeValue("des");
					String low = e.getAttributeValue("low");
					String high = e.getAttributeValue("high");
					//		   String temp = e.getText();
					String temp = e.getAttributeValue("temperature");
					String environmentType = e.getAttributeValue("environmentType");
					DevFrige devHum = new DevFrige(type,environmentType, id, location, status, descprition,low,high, temp,"");
					node.addDevice(devHum);
					element.removeChild("temperature");
				}
			}
		}

		while( (e =element.getChild("address")) != null)
		{
			String id = e.getAttributeValue("aid");
			String name = e.getAttributeValue("des");
			AreaTreeNode n = new AreaTreeNode(node, id, name, false, new ArrayList<TreeNode>(), new ArrayList<Device>());
			node.getmChildren().add(n);

			parseGetByAidXml(e,n,location);
			element.removeChild("address");
		}
	}
	/* 功能	增加冷库设备
	 * 参数 element 当前冷库元素
	 * 参数 location 当前冷库地址
	 * 
	 * 返回	返回增加的冷库设备节点
	 * */
	private AreaTreeNode addCoolerDevDataNode(Element element, String location) {
		Element e;
		//lvCoolerDevData结构中保存着所有的冷库设备及冷库设备下的节点，冷库的id和描述为该地址的id和地址名
		String addrId = element.getAttributeValue("aid");
		String name = element.getAttributeValue("des");
		//新建一个冷库
		AreaTreeNode coolerNode = new AreaTreeNode(null, addrId, name, false, null, new ArrayList<Device>());
		//把节点加入冷库中
		while( (e =element.getChild("temperature")) != null)
		{
			String type = Device.Type_Frige;
			String id = e.getAttributeValue("macid");
			String status = e.getAttributeValue("state");
			String descprition = e.getAttributeValue("des");
			String low = e.getAttributeValue("low");
			String high = e.getAttributeValue("high");
			//		   String temp = e.getText();
			String temp = e.getAttributeValue("temperature");
			String environmentType = Integer.toString(Device.Temperature_Cooler_node);
			DevFrige devHum = new DevFrige(type,environmentType, id, location, status, descprition,low,high, temp,"");
			coolerNode.addDevice(devHum);
			element.removeChild("temperature");
		}

		lvCoolerDevData.add(coolerNode);
		return coolerNode;
	}
	/* 
	 * 功能：过滤异常设备
	 * 参数 listDevData：当前所有监控设备
	 * 参数  listAbnormalDevData 异常设备
	 * */
	@SuppressWarnings("unused")
	private void filterAbnormalDevice(List<AreaTreeNode> listDevData,
			List<Device> listAbnormalDevData) {
		// TODO Auto-generated method stub
		if(listDevData.size() > 0){
			for(int i = 0;i < listDevData.size();i++){
				for(int j = 0;j < listDevData.get(i).getmDevices().size();j++){
					//判断状态是否异常
					if(!listDevData.get(i).getmDevices().get(j).getmStatus().equals("1")){
						listAbnormalDevData.add(listDevData.get(i).getmDevices().get(j));	
					}

				}
			}
		}
	}
	/*
	 * 功能：遍历设备列表，筛选出需要显示的区域
	 * 参数 node：传入的设备树
	 * 参数 list：筛选出来的设备
	 * */
     public void filterDevice(AreaTreeNode node, List<AreaTreeNode> list){  
 		if(node == null || list == null) return ;
 		
 		if(node.getmChildren() == null){ 
 			return;
 		}
 		else
 		{
 			for(int i = 0 ; i < node.getmChildren().size() ; i++  )
 			{
 				//判断是否和所选择的地址区域的节点相同，如果相同就把树型列表转化为可显示设备列表的形式
 				if(node.getmChildren().get(i).getmId().equals(appContext.AID))
 				{
 					mDeviceDistrictName = node.getmChildren().get(i).getmName();
 					loadListViewData((AreaTreeNode) node.getmChildren().get(i),list);
 					mDeviceTotalNum = mDeviceNormalNum + mDeviceNetBreakNum + mDeviceAlarmNum + mDeviceOtherNum;
 					return ;
 				}
 				filterDevice((AreaTreeNode) node.getmChildren().get(i),list);
 			}
 		}
    }
 	private void loadListViewData(AreaTreeNode node,List<AreaTreeNode> list){
		if(node == null || list == null) return;
		if(node.getmDevices().size() >0)
		{
			List<Device> mDevices = node.getmDevices();
			List<Device> xDevices = new ArrayList<Device>();
			if(mIsOnlyShowAbnormalDeviceStatus){
				for(int i = 0; i< mDevices.size();i++){
					if(Integer.parseInt(mDevices.get(i).getmStatus()) != 0x01){
						xDevices.add(mDevices.get(i));
					}
				}	
			}else{
				xDevices.addAll(mDevices);
			}

			if(xDevices.size() > 0){
				AreaTreeNode DeviceRoom = new AreaTreeNode(null, null, xDevices.get(0).getmLocation(), true, null, xDevices);
				list.add(DeviceRoom);
				for(int i = 0;i < xDevices.size();i++){
					xDevices.get(i).getmStatus();
					//判断是否为冷库设备
					if(Integer.parseInt(xDevices.get(i).getmEnvironmentType()) == Device.Temperature_Cooler){
						
						if(lvCoolerDevData.size() > 0){
							int k = 0;
							//统计该冷库下的设备状态统计
							while(k < lvCoolerDevData.size()){
								if(lvCoolerDevData.get(k).getmId().equals(xDevices.get(i).getmId())){
									for(int j = 0;j < lvCoolerDevData.get(k).getmDevices().size();j++){
										int Istatus = Integer.parseInt(lvCoolerDevData.get(k).getmDevices().get(j).getmStatus());
										if(Istatus == 0x01){
											mDeviceNormalNum ++;
										}
										else if((Istatus & 0x08) == 0x08){
											mDeviceNetBreakNum++;
										}
										else if((Istatus & 0x10) == 0x10){

											mDeviceAlarmNum++;
										}
										else{
											mDeviceOtherNum++;
										}
									}
									break;
								}
								k++;
							}
						}
					}else{
						int Istatus = Integer.parseInt(xDevices.get(i).getmStatus());
						if(Istatus == 0x01){
							mDeviceNormalNum ++;
						}
						else if((Istatus & 0x08) == 0x08){
							mDeviceNetBreakNum++;
						}
						else if((Istatus & 0x10) == 0x10){

							mDeviceAlarmNum++;
						}
						else{
							mDeviceOtherNum++;
						}
					}

				}
			}

		}
		
		if(node.getmChildren() == null){ 
			return;
		}	
		else
		{
			for(int i = 0 ; i < node.getmChildren().size() ; i++  )
			{
				loadListViewData((AreaTreeNode) node.getmChildren().get(i),list);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!(appContext.SettingsNotification && appContext.SettingsRunInBackGround)) {
			if (appContext.BackgroundServiceIntent != null) {
				stopService(appContext.BackgroundServiceIntent);
				appContext.BackgroundServiceIntent = null;
			}
		}

	}
	@Override
	public void onStop(){
		super.onStop();
		visiable = false;
		if(mRefreshThread != null){
			mRefreshThread.interrupt();
			mRefreshThread = null;
		}
	}
	@Override
	public void onStart(){
		super.onStart();
		visiable = true;
		if(mRefreshThread == null){
			mRefreshThread = new RefreshThread();
			try {
				mRefreshThread.start(); 
			} catch (IllegalThreadStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (checkQuitThread == null || !checkQuitThread.isAlive()) {
				UIHealper.DisplayToast(appContext,appContext.getResources().getString(R.string.quitAppHit));
				checkQuitThread = new CheckQuitThread();
				checkQuitThread.start();
			} else {
				Log.e("huwei","程序退出" );
				AppManager.getAppManager().AppExit(this);
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	public void updateListView(List<AreaTreeNode> listItem){
		mDevListViewAdapter.setListItems(listItem);
		mDevListViewAdapter.notifyDataSetChanged();
	}

	private boolean checkNetwork() {
        ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }
	//显示异常设备activity
	public void showAbnormalDeviceActivty(View view){
		Intent intent = new Intent(this,AbnormalDeviceActivity.class);
		startActivity(intent);
	}
	
	public void clickSwitchShowDevice(View view){

		if(mIsOnlyShowAbnormalDeviceStatus){
			mIsOnlyShowAbnormalDeviceStatus = false;
			clearDevData();
			//接收到设备列表后，进行筛选
			filterDevice(mAreaTreeNode,lvDevData);
		}else{
			mIsOnlyShowAbnormalDeviceStatus = true;
			clearDevData();
			//接收到设备列表后，进行筛选
			filterDevice(mAreaTreeNode,lvDevData);
			//检测有没有异常设备
			if((mDeviceTotalNum - mDeviceNormalNum)<=0){
				UIHealper.DisplayToast(appContext, "无异常设备");
				mIsOnlyShowAbnormalDeviceStatus = false;
				clearDevData();
				//接收到设备列表后，进行筛选
				filterDevice(mAreaTreeNode,lvDevData);
				return;
			}
		}


//		filterAbnormalDevice(lvDevData,lvAbnormalDevData);
		
		if(lvDevData.size() == 0){
			mSurveilListEmptyShow.setVisibility(View.VISIBLE);
		}else{
			mSurveilListEmptyShow.setVisibility(View.GONE);
		}
		updateListView(lvDevData);
		HandlerSendMessage(HANDLER_UPDATE_DEV_COUNT,-1);
		if(mIsOnlyShowAbnormalDeviceStatus){
			mTextViewdangqian.setText("异常设备统计：");
		}else{
			mTextViewdangqian.setText("全部设备统计：");
		}
	}
}
