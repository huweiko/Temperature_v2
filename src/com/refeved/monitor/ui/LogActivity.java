package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.adapter.DevDealLogListViewAdapter;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.DeviceAbnormalLog;
import com.refeved.monitor.struct.DeviceDealLog;
import com.refeved.monitor.util.LogPrint;
import com.refeved.monitor.view.LogListView;
import com.refeved.monitor.xlist.XListView;
import com.refeved.monitor.xlist.XListView.IXListViewListener;

/**
 * @author huwei
 *
 * 2014年7月18日
 */
@SuppressLint({ "HandlerLeak", "ResourceAsColor" })
public class LogActivity extends BaseActivity implements
		OnClickListener,IXListViewListener{
	XListView mListView;
	private onRefreshClass mOnRefreshClass;
	private AppContext appContext;
	private ImageButton mImageButtonBack;
	private ImageButton mImageButtonRefresh;
	private ImageButton mImageButtonLogPicture;
	private ProgressBar mProgressBarLogActivity;
	private Spinner mSpinnerLog;
	private boolean mDealLogInitStatus = false;
	private boolean mAbnormalLogInitStatus = false;
	LogListView mLogDealListView;
	Boolean visiable = false;
	ListView mListViewLogs; 
	DevDealLogListViewAdapter mDealLogListViewAdapter; 
	int count = 5;
	private List<DeviceAbnormalLog> lvAbnormalLogData = new ArrayList<DeviceAbnormalLog>();
	private List<DeviceDealLog> lvDealLogData = new ArrayList<DeviceDealLog>();
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	private static final int CLICK_BACK = 0;
	private static final int CLICK_LOGREFRESH = 1; 
	private static final int EVERY_PAGE_NUM = 20; //每次加载的个数

	private final static int HANDLER_WEB = 0; 
	private final static int HANDLER_REFRESH = 1; 
	
	private final static int XLIST_REFRESH = 1; //下拉刷新
	private final static int XLIST_LOAD_MORE = 2; //加载更多

	RefreshThread mRefreshThread;
	LogDate c_dealDate = new LogDate();
	LogDate c_abnormalDate = new LogDate();
	private boolean mHasMore = false;
	final Semaphore sempDeal = new Semaphore(0);
	final Semaphore sempAbnormal = new Semaphore(0);
	 
	public class LogDate {
		int pageNO;
		int pageTotal;
		int LogTotal;
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_WEB: {
				Log.d("huwei---"+LogPrint.ML(), "handler web to get data!");
				if (appContext.SwitchLog == AppContext.ABNORMAL) {
					onDevLogListupdate(WebClient.Method_getExceptionLogs,
							c_abnormalDate.pageNO);
				} else {
					onDevLogListupdate(WebClient.Method_getMachineHandleLogs,
							c_dealDate.pageNO);
				}
			}
				break;
			case HANDLER_REFRESH: {
				Log.d("huwei---"+LogPrint.ML(), "handler web to refresh data!");
				afterDataLoad(msg.obj);
				refreshPull();
				refreshLoad();
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void parseGetByAidXml_Abnormal(Element element,
			List<DeviceAbnormalLog> node) {
		Element e = null;
		if ((e = element.getChild("total")) != null) {
			c_abnormalDate.LogTotal = Integer.parseInt(e.getValue());
			c_abnormalDate.pageTotal = (c_abnormalDate.LogTotal % EVERY_PAGE_NUM) != 0 ? (c_abnormalDate.LogTotal / EVERY_PAGE_NUM) + 1
					: (c_abnormalDate.LogTotal % EVERY_PAGE_NUM);
			element.removeChild("total");
		}
		while ((e = element.getChild("exceptionLog")) != null) {
			String mAcid = e.getChild("maid") != null ? e.getChild("maid")
					.getValue() : "null";
			String mDescription = e.getChild("macDes") != null ? e.getChild(
					"macDes").getValue() : "null";
			String mExceptionType = e.getChild("exceptionDes") != null ? e
					.getChild("exceptionDes").getValue() : "null";
			String mStarttime = e.getChild("starttime") != null ? e.getChild(
					"starttime").getValue() : "null";
			String mEndtime = e.getChild("endtime") != null ? e.getChild(
					"endtime").getValue() : "null";
			DeviceAbnormalLog n = new DeviceAbnormalLog(mAcid, mDescription,
					mExceptionType, mStarttime, mEndtime);
			node.add(n);
			element.removeChild("exceptionLog");
		}
		if ((e = element.getChild("list")) != null) {
			parseGetByAidXml_Abnormal(e, node);
			element.removeChild("list");
		}
	}

	public void parseGetByAidXml_Deal(Element element, List<DeviceDealLog> node) {
		Element e = null;
		if ((e = element.getChild("total")) != null) {
			c_dealDate.LogTotal = Integer.parseInt(e.getValue());
			c_dealDate.pageTotal = (c_dealDate.LogTotal % EVERY_PAGE_NUM) != 0 ? (c_dealDate.LogTotal / EVERY_PAGE_NUM) + 1
					: (c_dealDate.LogTotal % EVERY_PAGE_NUM);
			element.removeChild("total");
		}
		while ((e = element.getChild("machine")) != null) {
			String mAcid = e.getChild("macid") != null ? e.getChild("macid")
					.getValue() : "null";
			String mDescription = e.getChild("macdes") != null ? e.getChild(
					"macdes").getValue() : "null";
			String mStarttime = e.getChild("startTime") != null ? e.getChild(
					"startTime").getValue() : "null";
			String mStartUserName = e.getChild("startUserName") != null ? e
					.getChild("startUserName").getValue() : "null";
			String mStartDes = e.getChild("startDes") != null ? e.getChild(
					"startDes").getValue() : "null";
			String mEndUserName = e.getChild("endUserName") != null ? e
					.getChild("endUserName").getValue() : "null";
			String mEndtime = e.getChild("endTime") != null ? e.getChild(
					"endTime").getValue() : "null";
			String mEndDes = e.getChild("endDes") != null ? e
					.getChild("endDes").getValue() : "null";
			DeviceDealLog n = new DeviceDealLog(mAcid, mDescription,
					mStarttime, mStartUserName, mStartDes, mEndUserName,
					mEndtime, mEndDes);
			node.add(n);
			element.removeChild("machine");
		}
		if ((e = element.getChild("list")) != null) {
			parseGetByAidXml_Deal(e, node);
			element.removeChild("list");
		}

	}

	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			if (intent.getAction().equals(
					WebClient.INTERNAL_ACTION_GETABNORMALLOGS)) {
				if(!mAbnormalLogInitStatus)
					mAbnormalLogInitStatus = true;
				ShowRefreshing(false);				
				List<DeviceAbnormalLog> list = new ArrayList<DeviceAbnormalLog>();

				if (resXml.equals("error") || resXml.equals("null")) {
				
					Log.w("huwei", "abnormal log fail");
					list = null;
					mOnRefreshClass.onError(0, resXml);
				} else {
					try {
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);
						InputSource is = new InputSource(sr);
						Document Doc = builder.build(is);
						Element rootElement = Doc.getRootElement();
						parseGetByAidXml_Abnormal(rootElement, list);
						mOnRefreshClass.onSuccess(list);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (intent.getAction().equals(
					WebClient.INTERNAL_ACTION_GETDEALLOGS)) {
				if(!mDealLogInitStatus)
					mDealLogInitStatus = true;
				ShowRefreshing(false);
				List<DeviceDealLog> list = new ArrayList<DeviceDealLog>();
				if (resXml.equals("error")) {
					Log.w("huwei", "deal log fail");
					list = null;
					mOnRefreshClass.onError(0, resXml);
				} else {
					try {
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);
						InputSource is = new InputSource(sr);
						Document Doc = builder.build(is);
						Element rootElement = Doc.getRootElement();
						parseGetByAidXml_Deal(rootElement, list);
						lvDealLogData.addAll(list);
						mOnRefreshClass.onSuccess(list);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public class RefreshThread extends Thread {

		@Override
		public void run() {
			while (appContext.SettingsRefreshFrequency > 0 && visiable) {
				int count = appContext.SettingsRefreshFrequency * 100;
				while (count-- > 0 && visiable) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (visiable) {
					Message message = new Message();
					message.what = HANDLER_WEB;
					mHandler.sendMessage(message);
				}
			}
		}
	}

	private void ShowRefreshing(boolean status){
		if(status){
			mImageButtonRefresh.setVisibility(View.INVISIBLE);
			mProgressBarLogActivity.setVisibility(View.VISIBLE);
		}else{
			mImageButtonRefresh.setVisibility(View.VISIBLE);
			mProgressBarLogActivity.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		InitLogActivity();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETABNORMALLOGS);
		filter.addAction(WebClient.INTERNAL_ACTION_GETDEALLOGS);
		appContext.registerReceiver(receiver, filter);
	}
	
    //使用XML形式操作 
    class SpinnerXMLSelectedListener implements OnItemSelectedListener{ 
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, 
                long arg3) { 
			if (arg2 == 1) {
				mImageButtonLogPicture.setImageResource(R.drawable.deal_log_pic);
				appContext.SwitchLog = AppContext.DEAL;
				if(mDealLogInitStatus){
					mDealLogListViewAdapter.setListItems(lvDealLogData, null,
							AppContext.DEAL);
					mDealLogListViewAdapter.notifyDataSetChanged();
					mListView.setPullLoadEnable(true);
				}else{
					loadDate(XLIST_REFRESH);
				}

			} else {
				mImageButtonLogPicture.setImageResource(R.drawable.abnormal_log_pic);
				appContext.SwitchLog = AppContext.ABNORMAL;
				if(mAbnormalLogInitStatus){
					mDealLogListViewAdapter.setListItems(null, lvAbnormalLogData,
							AppContext.ABNORMAL);
					mDealLogListViewAdapter.notifyDataSetChanged();
					mListView.setPullLoadEnable(true);
				}else{
					loadDate(XLIST_REFRESH);
				}

			}

			SharedPreferences settings = appContext
					.getSharedPreferences(
							getString(R.string.settings_filename),
							Context.MODE_PRIVATE);
			Editor editor = settings.edit();
			
			editor.putInt(getString(R.string.settings_log_button_status),
					appContext.SwitchLog);
			
			editor.commit();
        } 

        public void onNothingSelected(AdapterView<?> arg0) { 

        } 
    } 
	private void initXListView(Context context) {
		mListView = (XListView) findViewById(R.id.mListView);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		//
		mListView.pullRefreshing();
		mDealLogListViewAdapter = new DevDealLogListViewAdapter(context,
				new ArrayList<DeviceDealLog>(),
				new ArrayList<DeviceAbnormalLog>(),
				R.layout.dev_deal_log_listitem);
		mListView.setAdapter(mDealLogListViewAdapter);
	}
	private void InitLogActivity(){
		appContext = (AppContext)getApplication();
		c_dealDate.pageNO = 1;
		c_abnormalDate.pageNO = 1;
		
		mImageButtonBack = (ImageButton) findViewById(R.id.log_activity_back);
		mImageButtonLogPicture = (ImageButton) findViewById(R.id.ImageButtonLogPicture);
		mImageButtonRefresh = (ImageButton) findViewById(R.id.ImageButtonLogActivityRefresh);
		mSpinnerLog = (Spinner) findViewById(R.id.SpinnerLog);
		mProgressBarLogActivity = (ProgressBar) findViewById(R.id.ProgressBarLogActivity);
        //将可选内容与ArrayAdapter连接起来 
		String[] mItems = getResources().getStringArray(R.array.log_select);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_checked_text,mItems);


        //设置下拉列表的风格  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        //将adapter2 添加到spinner中 
        mSpinnerLog.setAdapter(adapter); 
        //添加事件Spinner事件监听   
        mSpinnerLog.setOnItemSelectedListener(new SpinnerXMLSelectedListener()); 
        //设置默认值 
        mSpinnerLog.setVisibility(View.VISIBLE);
		if (appContext.SwitchLog == AppContext.ABNORMAL) {
			mSpinnerLog.setSelection(0);
			mImageButtonLogPicture.setImageResource(R.drawable.abnormal_log_pic);
		} else {
			mSpinnerLog.setSelection(1);
			mImageButtonLogPicture.setImageResource(R.drawable.deal_log_pic);
		}
		mImageButtonBack.setOnClickListener(this);
		mImageButtonBack.setTag(CLICK_BACK);
		mImageButtonRefresh.setOnClickListener(this);
		mImageButtonRefresh.setTag(CLICK_LOGREFRESH);
		initXListView(appContext);
	}
	@SuppressWarnings("unchecked")
	private void afterDataLoad(final Object xLogList) {
		if(this != null){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					List<DeviceAbnormalLog> AbnormalList = null;
					List<DeviceDealLog> DealList = null;
					if (appContext.SwitchLog == AppContext.ABNORMAL) {
						AbnormalList = (List<DeviceAbnormalLog>) xLogList;
						if(AbnormalList.size()<EVERY_PAGE_NUM){
							mListView.setPullLoadEnable(false);
						}else{
							mListView.setPullLoadEnable(true);
						}
						mHasMore = (AbnormalList != null)
								&& (AbnormalList.size() > 0);
					} else {
						DealList = (List<DeviceDealLog>) xLogList;
						if(DealList.size()<EVERY_PAGE_NUM){
							mListView.setPullLoadEnable(false);
						}else{
							mListView.setPullLoadEnable(true);
						}
						mHasMore = (DealList != null) && (DealList.size() > 0);
					}

					if (!mHasMore) {
						Log.i("huwei", "afterDataLoad-->not Data");
						if (appContext.SwitchLog == AppContext.ABNORMAL) {
							c_abnormalDate.pageNO--;
						} else {
							c_dealDate.pageNO--;
						}
					}else {
						Log.i("huwei", "afterDataLoad-->have Data");

						if (appContext.SwitchLog == AppContext.ABNORMAL) {
							lvAbnormalLogData.addAll(AbnormalList);
							mDealLogListViewAdapter.setListItems(null,
									lvAbnormalLogData, AppContext.ABNORMAL);
							mDealLogListViewAdapter.notifyDataSetChanged();
						} else {
							lvDealLogData.addAll(DealList);
							mDealLogListViewAdapter.setListItems(lvDealLogData,
									null, AppContext.DEAL);
							mDealLogListViewAdapter.notifyDataSetChanged();
						}

					}
				}
			});
		}

	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();

		switch (tag) {
		
		case CLICK_BACK:
			finish();
			break;
		
		case CLICK_LOGREFRESH: {
			loadDate(XLIST_REFRESH);
		}
			break;
		default:
			break;
		}
	}
	@Override
	public void onStop() {
		super.onStop();
		visiable = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		appContext.unregisterReceiver(receiver);
	}

	private void onDevLogListupdate(String logMethod, int pageNO) {
		ShowRefreshing(true);
		WebClient client = WebClient.getInstance(appContext);
		Map<String, String> param = new HashMap<String, String>();
		param.put("pageNO", "" + pageNO);
		param.put("pageSIZE", "" + EVERY_PAGE_NUM);
		client.sendMessage(appContext, logMethod, param);
	}

	@Override
	public void onRefresh() {
		loadDate(XLIST_REFRESH);
	}

	@Override
	public void onLoadMore() {
		loadDate(XLIST_LOAD_MORE);
	}
	/*
	 * 
	 * 设置回调
	 * 参数 sendMode 1、代表下拉  2、代表加载更多（也即是上拉）
	 */
	private void loadDate(int sendMode) {
		sendData(sendMode,new onRefreshClass() {
			
			@Override
			public void onSuccess(Object list) {
				// TODO Auto-generated method stub
				afterDataLoad(list);
				refreshPull();
				refreshLoad();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void refreshLoad(){
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}
	
	private void refreshPull(){
		if (mListView.getPullRefreshing()) {
			mListView.stopRefresh();
		}
	}
	
	public interface onRefreshClass{
		public void onSuccess(Object list);
		public void onError(int arg0, String arg1);
	}
	public void sendData(int sendMode,onRefreshClass OnRefreshClass){
		mOnRefreshClass = OnRefreshClass;
		if(sendMode == XLIST_REFRESH){
			if (appContext.SwitchLog == AppContext.ABNORMAL) {
				c_abnormalDate.pageNO = 1;
				lvAbnormalLogData.clear();
				onDevLogListupdate(WebClient.Method_getExceptionLogs,
						c_abnormalDate.pageNO);
			} else {
				c_dealDate.pageNO = 1;
				lvDealLogData.clear();
				onDevLogListupdate(WebClient.Method_getMachineHandleLogs,
						c_dealDate.pageNO);
			}
		}else if(sendMode == XLIST_LOAD_MORE){
			if (appContext.SwitchLog == AppContext.ABNORMAL) {
				c_abnormalDate.pageNO++;
				onDevLogListupdate(WebClient.Method_getExceptionLogs,
						c_abnormalDate.pageNO);
			} else {
				c_dealDate.pageNO++;
				onDevLogListupdate(WebClient.Method_getMachineHandleLogs,
						c_dealDate.pageNO);
			}
		}
	}
}