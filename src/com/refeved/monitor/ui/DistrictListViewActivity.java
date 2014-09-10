package com.refeved.monitor.ui;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.adapter.DistrictListViewAdapter;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.TreeNode;

@SuppressLint("NewApi")
public class DistrictListViewActivity extends BaseActivity implements
		RepeatingImageButton.RepeatListener, View.OnClickListener {
	private AppContext appContext;
	private TreeNode rootTreeNode;
	private TextView mdistrict_activity_actionbar_title_Textview;
	private ImageButton mDistrict_activity_back;
	private ImageButton mImageButtonDistrictActivityRefresh;
	private ProgressBar mProgressBarDistrictActivity;
	private ListView mDistrictListView;
	private View mDistrictEmptyLoading;
	private View mDistrictListEmptyOnClick;
	private DistrictListViewAdapter mDistrictListViewAdapter;
	private List<TreeNode> lvTreeNodeData = new ArrayList<TreeNode>();
	private void ShowRefreshing(boolean status){
		if(status){
			mImageButtonDistrictActivityRefresh.setVisibility(View.INVISIBLE);
			mProgressBarDistrictActivity.setVisibility(View.VISIBLE);
		}else{
			mImageButtonDistrictActivityRefresh.setVisibility(View.VISIBLE);
			mProgressBarDistrictActivity.setVisibility(View.INVISIBLE);
		}
		
	}
	public void parseGetAreaMapXml(Element element,TreeNode node, String parentLocation){
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
		   
		   while( (e =element.getChild("address")) != null)
		   {
			   String id = e.getAttributeValue("aid");
			   String name = e.getAttributeValue("des");
			   TreeNode n = new TreeNode(node, id, name, false, new ArrayList<TreeNode>());
			   node.getmChildren().add(n);
			   
			   parseGetAreaMapXml(e,n,location);
			   element.removeChild("address");
		   }
	   }
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
		
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETAREAMAPDONE))
			{
				ShowRefreshing(false); 
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null")){
						mDistrictEmptyLoading.setVisibility(View.GONE);
						if(mDistrictListView.getAdapter().getCount() == 0){
							mDistrictListEmptyOnClick.setVisibility(View.VISIBLE);
						}
						UIHealper.DisplayToast(appContext,"获取地址信息失败！");
					}
					else {
						if((mDistrictEmptyLoading != null) && (mDistrictListEmptyOnClick != null)){
							mDistrictEmptyLoading.setVisibility(View.GONE);
							mDistrictListEmptyOnClick.setVisibility(View.GONE);
						}
						try{
							SAXBuilder builder = new SAXBuilder();
							StringReader sr = new StringReader(resXml);   
							InputSource is = new InputSource(sr); 
							Document Doc = builder.build(is);
							Element rootElement = (Element) Doc.getRootElement();
							rootTreeNode = new TreeNode(null,"root","",false, new ArrayList<TreeNode>());
							parseGetAreaMapXml(rootElement, rootTreeNode, "");
							
							lvTreeNodeData.clear();
							lvTreeNodeData.add(rootTreeNode);
							if(lvTreeNodeData.size() == 0){
								mDistrictListEmptyOnClick.setVisibility(View.VISIBLE);
							}
							mDistrictListViewAdapter.notifyDataSetChanged();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_district_list_view);
		appContext = (AppContext) getApplication();

		//获取intent附加值
		Bundle extras = getIntent().getExtras();
		if(extras != null){
//			mHeaderTittle.setText(extras.getInt("TitleName"));	
		}
		rootTreeNode = new TreeNode(null, "root", "Node_0", false,
				new ArrayList<TreeNode>());
		//initTree();
		//lvTreeNodeData.add(rootTreeNode);

//		mHeaderHome.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				UIHealper.showHome(DistrictListViewActivity.this);
//			}
//
//		});

		mDistrictListViewAdapter = new DistrictListViewAdapter(this,
				lvTreeNodeData);
		mDistrictListView = (ListView) findViewById(R.id.district_listview);
		mDistrict_activity_back = (ImageButton)findViewById(R.id.district_activity_back);
		mImageButtonDistrictActivityRefresh = (ImageButton)findViewById(R.id.ImageButtonDistrictActivityRefresh);
		mProgressBarDistrictActivity = (ProgressBar)findViewById(R.id.ProgressBarDistrictActivity);
		mdistrict_activity_actionbar_title_Textview = (TextView)findViewById(R.id.district_activity_actionbar_title_Textview);
		mdistrict_activity_actionbar_title_Textview.setText(appContext.UserName);
		//正在加载布局
		mDistrictEmptyLoading = findViewById(R.id.DistrictListEmpty);
//		mDistrictListView.setEmptyView(mDistrictEmptyLoading);
		//空白列表点击加载
		mDistrictListEmptyOnClick = findViewById(R.id.DistrictListOnClick);
		mDistrictListEmptyOnClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDistrictEmptyLoading.setVisibility(View.VISIBLE);
				mDistrictListEmptyOnClick.setVisibility(View.GONE);
				WebClient client = WebClient.getInstance(appContext);
				client.sendMessage(appContext, WebClient.Method_getAddressTree, null);
			}

		});
		mImageButtonDistrictActivityRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDevListUpdate();
			}

		});
		mDistrict_activity_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent intent = new Intent();
                DistrictListViewActivity.this.setResult(RESULT_OK, intent);
                DistrictListViewActivity.this.finish();
			}
		});
		
		mDistrictListView.setAdapter(mDistrictListViewAdapter);
		
		IntentFilter filter = new IntentFilter();
	    filter.addAction(WebClient.INTERNAL_ACTION_GETAREAMAPDONE);
	    appContext.registerReceiver(receiver, filter);
	    onDevListUpdate();
//	    int taskID = this.getTaskId();
//		UIHealper.DisplayToast(appContext, "taskID = "+taskID);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		appContext.unregisterReceiver(receiver);
	}

	@Override
	public void onRepeat(View v, long duration, int repeatcount) {
		// TODO Auto-generated method stub
		if (repeatcount == 0) {
//			String id = v.getTag(R.id.button_tag_index1).toString();
			TreeNode node = (TreeNode) v.getTag();
//			UIHealper.DisplayToast(appContext, " double click = " + id
//					+ node.getmName());

            Intent intent = new Intent();
            intent.putExtra("AID", node.getmId());
            intent.putExtra("mName", node.getmName());
            this.setResult(RESULT_OK, intent);
            this.finish();
		}

		return;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		TreeNode node = (TreeNode) v.getTag();

		if (node.getmChildren() == null)
			return;

		List<TreeNode> list = new ArrayList<TreeNode>();
		for (int pos = 0; pos < lvTreeNodeData.size(); pos++) {
			TreeNode item = lvTreeNodeData.get(pos);
//			item.setmIsOpen(false);//初始化
			list.add(item);
			if (node.getmParent() == null) {
				break;
			} else if (node.getmParent().getmId().equals(item.getmId())) {
				item.setmIsOpen(true);//点亮被选择的房间的父房间
				for(int i =0;i < item.getmChildren().size();i++){
					if(item.getmChildren().get(i).getmId().equals(node.getmId())){
						item.getmChildren().get(i).setmIsOpen(true);//点亮被选择的房间
					}else{
						item.getmChildren().get(i).setmIsOpen(false);//关闭没被选择的房间
					}
				}
				break;
			}
		}
		for(int i =0;i < node.getmChildren().size();i++){
			node.getmChildren().get(i).setmIsOpen(false);//清除被选中房间下所有的子房间
		}
		lvTreeNodeData = list;
		lvTreeNodeData.add(node);
		mDistrictListViewAdapter.setListItems(lvTreeNodeData);
		mDistrictListViewAdapter.notifyDataSetChanged();
//		UIHealper.DisplayToast(appContext, node.getmName());
	}
	 //更新设备列表
		private void onDevListUpdate(){
			ShowRefreshing(true);
			if((mDistrictEmptyLoading != null) && (mDistrictListEmptyOnClick != null)){
				mDistrictEmptyLoading.setVisibility(View.VISIBLE);
				mDistrictListEmptyOnClick.setVisibility(View.GONE);
			}
			WebClient client = WebClient.getInstance(appContext);
			client.sendMessage(appContext, WebClient.Method_getAddressTree, null);
		}
	
}