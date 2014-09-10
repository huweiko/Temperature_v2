package com.refeved.monitor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.DeviceLog;
import com.refeved.monitor.ui.DevDetailActivity;

public class DevLogListViewAdapter extends BaseAdapter {
	
	private Context 					context;//����������
	private List<DeviceLog> 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 

	static class ListItemView{				//�Զ���ؼ�����  
		public TextView textViewlistCount;
	    public TextView time_yymmdd;
	    public TextView time_hhmmss;
	    public TextView Temperature;
	}  
	
	public DevLogListViewAdapter(Context context, List<DeviceLog> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	public void setListItems(List<DeviceLog> data){
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
			//��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//��ȡ�ؼ�����
			listItemView.textViewlistCount = (TextView)convertView.findViewById(R.id.TextView_listitem_count);
			listItemView.Temperature= (TextView)convertView.findViewById(R.id.dev_detail_log_listitem_temperature_textview);
			listItemView.time_yymmdd = (TextView)convertView.findViewById(R.id.dev_detail_log_listitem_time_textview_YY_MM_DD);
			listItemView.time_hhmmss = (TextView)convertView.findViewById(R.id.dev_detail_log_listitem_time_textview_HH_MM_SS);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		DeviceLog deviceLog = listItems.get(position);
		if(DevDetailActivity.mDeviceType != null){
			if(DevDetailActivity.mDeviceType.equals(Device.Type_Frige)){
				listItemView.Temperature.setText(deviceLog.getmTemperature()+"��C");
			}
			else if(DevDetailActivity.mDeviceType.equals(Device.Type_Humidity)){
				listItemView.Temperature.setText(deviceLog.getmTemperature()+"%");
			}
		}

		String strtime = deviceLog.getmTime();
		String [] sp = strtime.split(" ");
		listItemView.time_yymmdd.setText(sp[0]);
		listItemView.time_hhmmss.setText(sp[1]);
		listItemView.textViewlistCount.setText((position+1)+".");
	
		return convertView;
	}

}
