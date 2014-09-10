package com.refeved.monitor.adapter;

import java.util.List;
import com.refeved.monitor.R;
import com.refeved.monitor.struct.AreaTreeNode;
import com.refeved.monitor.view.GridViewBase;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DevListViewAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private List<AreaTreeNode> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 

	static class ListItemView{				//自定义控件集合  
	    public TextView DeviceDistrict;
	    public GridViewBase DeviceGridView;
	}  
	
	public DevListViewAdapter(Context context, List<AreaTreeNode> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}

	public void setListItems(List<AreaTreeNode> data){
		listItems = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.DeviceDistrict = (TextView)convertView.findViewById(R.id.TextViewDeviceTitleContent);
			listItemView.DeviceGridView= (GridViewBase)convertView.findViewById(R.id.grid);
//			listItemView.DeviceGridView.setNumColumns(3);

			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		listItemView.DeviceDistrict.setText(listItems.get(position).getmName());
		if (listItemView.DeviceGridView != null){
			listItemView.DeviceGridView.updateListView(listItems.get(position).getmDevices());
		} 

		return convertView;
	}

}
