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
import com.refeved.monitor.adapter.DevSpecimenListViewAdapter;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.SpecimenInfo;

public class DevSpecimenListFragment extends Fragment{
	
	private final int HANDLER_DOMBUILD_XML = 0;
	private ListView mListViewSpecimen;
	private DevSpecimenListViewAdapter mDevSpecimenListViewAdapter;
	private AppContext appContext;
	private List<SpecimenInfo> lvSpecimenData = new ArrayList<SpecimenInfo>();
	private View mRelativeLayoutDevDetailEmptyShow;//无数据时的显示内容
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
							//SAXBuilder是一个JDOM解析器 能将路径中的XML文件解析为Document对象
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							parseGetByAidXml(rootElement, lvSpecimenData);
							if(lvSpecimenData.size() == 0){
								mRelativeLayoutDevDetailEmptyShow.setVisibility(View.VISIBLE);
							}else{
								mRelativeLayoutDevDetailEmptyShow.setVisibility(View.GONE);
							}
							mDevSpecimenListViewAdapter.setListItems(lvSpecimenData);
							mDevSpecimenListViewAdapter.notifyDataSetChanged();
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
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETSPECIMENMACHINEMACDES))
			{
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null"))
					{
						if(mListViewSpecimen.getAdapter().getCount() == 0){
							mRelativeLayoutDevDetailEmptyShow.setVisibility(View.VISIBLE);
						}
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
	private void parseGetByAidXml(Element element,List<SpecimenInfo> node){
		Element e = null;
		while ((e = element.getChild("machine")) != null) {
			String SpecimenDes = e.getChild("project") != null ? e.getChild("project")
					.getValue() : "";
			String SpecimenNum = e.getChild("total") != null ? e.getChild(
					"total").getValue() : "";
		
					SpecimenInfo n = new SpecimenInfo(SpecimenDes, SpecimenNum);
			node.add(n);
			element.removeChild("machine");
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//在onCreate()方法中调用setHasOptionsMenu()，否则系统不会调用Fragment的onCreateOptionsMenu()方法
		setHasOptionsMenu(true);
		appContext = (AppContext) getActivity().getApplication();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETSPECIMENMACHINEMACDES);
		appContext.registerReceiver(receiver, filter);
	}
	//onCreateView是创建该fragment对应的视图
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mTemLogFragmentView = inflater.inflate(R.layout.dev_specimen_listview_fragment, container,false);
		mListViewSpecimen = (ListView) mTemLogFragmentView.findViewById(R.id.dev_specimen_listview);
		mRelativeLayoutDevDetailEmptyShow = mTemLogFragmentView.findViewById(R.id.RelativeLayoutDevDetailEmptyShow);
		mDevSpecimenListViewAdapter = new DevSpecimenListViewAdapter(getActivity(), new ArrayList<SpecimenInfo>(), R.layout.dev_specimen_listitem);
		mListViewSpecimen.setAdapter(mDevSpecimenListViewAdapter);
		onDevLogListupdate(WebClient.Method_getSpecimenMachineMacdes);
		return mTemLogFragmentView;
	}
	
	//当activity的onCreate()方法返回时调用。
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