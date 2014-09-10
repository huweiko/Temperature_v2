package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.adapter.DevLogListViewAdapter;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.DeviceLog;

public class DevDetailHistoryListFragment extends Fragment{
	
	protected static final int HANDLER_DOMBUILD_XML = 0;
	private ListView mListViewLogs;
	private DevLogListViewAdapter mDevLogListViewAdapter;
	private AppContext appContext;
	private List<DeviceLog> lvLogData = new ArrayList<DeviceLog>();
	
	    @SuppressLint("HandlerLeak")
		public Handler mHandler = new Handler(){
	    	@Override
			public void handleMessage(Message msg)  
			{  
				switch(msg.what)  
				{  
					case HANDLER_DOMBUILD_XML:
					{
						String resXml = (String) msg.obj;
						try{
							//SAXBuilder��һ��JDOM������ �ܽ�·���е�XML�ļ�����ΪDocument����
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							parseGetByAidXml(rootElement, lvLogData);
							List<DeviceLog> mvLogData = new ArrayList<DeviceLog>();
							//�ѻ�ȡ����־��Ϣ����
							for(int i = lvLogData.size()-1;i >= 0;i--){
								mvLogData.add(lvLogData.get(i));
							}
							mDevLogListViewAdapter.setListItems(mvLogData);
							mDevLogListViewAdapter.notifyDataSetChanged();
						}catch(Exception e)
						{
							e.printStackTrace();
						}	

					}
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
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
//			Log.i("huwei", LogPrint.CML() + resXml);
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_FINDTODAYALLBYMACID))
			{
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null"))
					{
						UIHealper.DisplayToast(appContext,"��ȡ�豸��Ϣʧ��");
					}
					else {
				        Message msg = new Message();
				        msg.what = HANDLER_DOMBUILD_XML;
				        msg.obj = resXml;
				        mHandler.sendMessage(msg);
					}
					
				}
			}
		}
	};
	private void parseGetByAidXml(Element element,List<DeviceLog> node){
		Element e = null;
		while ((e = element.getChild("machine")) != null) {
			String value = e.getChild("value") != null ? e.getChild("value")
					.getValue() : "";
			String time = e.getChild("time") != null ? e.getChild(
					"time").getValue() : "";
		
			DeviceLog n = new DeviceLog(value, time);
			node.add(n);
			element.removeChild("machine");
		}
		if ((e = element.getChild("list")) != null) {
			parseGetByAidXml(e, node);
			element.removeChild("list");
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//��onCreate()�����е���setHasOptionsMenu()������ϵͳ�������Fragment��onCreateOptionsMenu()����
		setHasOptionsMenu(true);
		appContext = (AppContext) getActivity().getApplication();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_FINDTODAYALLBYMACID);
		appContext.registerReceiver(receiver, filter);
	}
	//onCreateView�Ǵ�����fragment��Ӧ����ͼ
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mTemLogFragmentView = inflater.inflate(R.layout.dev_detail_listview_fragment, container,false);
		mListViewLogs = (ListView) mTemLogFragmentView.findViewById(R.id.dev_detail_listview);
		mDevLogListViewAdapter = new DevLogListViewAdapter(this.getActivity(), new ArrayList<DeviceLog>(), R.layout.dev_detail_log_listitem);
		mListViewLogs.setAdapter(mDevLogListViewAdapter);
		onDevLogListupdate(WebClient.Method_findTodayAllByMACID);
		return mTemLogFragmentView;
	}
	
	//��activity��onCreate()��������ʱ���á�
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

	}
	private void onDevLogListupdate(String logMethod) {
		WebClient client = WebClient.getInstance(appContext);
		Map<String, String> param = new HashMap<String, String>();
		param.put("MACID", DevDetailActivity.DevDetailMacID);
		client.sendMessage(appContext, logMethod, param);
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}

}