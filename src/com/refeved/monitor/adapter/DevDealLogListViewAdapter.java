package com.refeved.monitor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.struct.DeviceAbnormalLog;
import com.refeved.monitor.struct.DeviceDealLog;

public class DevDealLogListViewAdapter extends BaseAdapter {

	private Context context;// ����������
	private List<DeviceDealLog> listItemsDeal;// ������־���ݼ���
	private List<DeviceAbnormalLog> listItemsAbnormal;// �쳣��־���ݼ���

	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;// �Զ�������ͼԴ
	private int hiteStatus;

	static class DealListItemView { // �Զ���ؼ�����
		public TextView mDescription;
		public TextView mStarttime;
		public TextView mStartUserName;
		public TextView mStartDes;
		public TextView mEndUserName;
		public TextView mEndtime;
		public TextView mEndDes;
	}

	static class AbnormalListItemView { // �Զ���ؼ�����
		public TextView mDescription;
		public TextView mExceptionType;
		public TextView mStarttime;
		public TextView mEndtime;
	}

	public DevDealLogListViewAdapter(Context context, List<DeviceDealLog> data,
			ArrayList<DeviceAbnormalLog> data2, int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.itemViewResource = resource;
		this.listItemsDeal = data;
		this.listItemsAbnormal = data2;
	}

	public void setListItems(List<DeviceDealLog> data,
			List<DeviceAbnormalLog> data2, int status) {
		listItemsDeal = data;
		listItemsAbnormal = data2;
		hiteStatus = status;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (hiteStatus == AppContext.ABNORMAL) {
			return listItemsAbnormal.size();
		} else {
			return listItemsDeal.size();
		}
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

	public View DealGetView(int position, View convertView, ViewGroup parent) {
		DealListItemView listItemView = null;
		try{
			// �������ֺ�ͼƬ
			DeviceDealLog deviceLog = listItemsDeal.get(position);
			// if (convertView == null) {
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
	
			listItemView = new DealListItemView();
			// ��ȡ�ؼ�����
			listItemView.mDescription = (TextView) convertView
					.findViewById(R.id.device_deallog_description);
			listItemView.mStarttime = (TextView) convertView
					.findViewById(R.id.device_deallog_starttime);
			listItemView.mStartUserName = (TextView) convertView
					.findViewById(R.id.device_deallog_startuser);
			listItemView.mStartDes = (TextView) convertView
					.findViewById(R.id.device_deallog_startdes);
			listItemView.mEndUserName = (TextView) convertView
					.findViewById(R.id.device_deallog_enduser);
			listItemView.mEndtime = (TextView) convertView
					.findViewById(R.id.device_deallog_endtime);
			listItemView.mEndDes = (TextView) convertView
					.findViewById(R.id.device_deallog_enddes);
			convertView.findViewById(R.id.bd_show_detail_log).setVisibility(
					View.GONE);
			convertView.findViewById(R.id.bd_show_deal_log).setVisibility(
					View.VISIBLE);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
			// }else {
			// listItemView = (ListItemView)convertView.getTag();
			// }
	
			listItemView.mDescription.setText(deviceLog.getmDescription());
			listItemView.mStarttime.setText(deviceLog.getmStarttime());
			listItemView.mStartUserName.setText(deviceLog.getmStartUserName());
			listItemView.mStartDes.setText(deviceLog.getmStartDes());
			listItemView.mEndUserName.setText(deviceLog.getmEndUserName());
			listItemView.mEndtime.setText(deviceLog.getmEndtime());
			listItemView.mEndDes.setText(deviceLog.getmEndDes());

		}catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public View AbnormalGetView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AbnormalListItemView listItemView = null;
		try{
			// �������ֺ�ͼƬ
			DeviceAbnormalLog DeviceLog = listItemsAbnormal.get(position);
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
	
			listItemView = new AbnormalListItemView();
			// ��ȡ�ؼ�����
			listItemView.mDescription = (TextView) convertView
					.findViewById(R.id.device_abnormallog_description);
			listItemView.mStarttime = (TextView) convertView
					.findViewById(R.id.device_abnormallog_starttime);
			listItemView.mEndtime = (TextView) convertView
					.findViewById(R.id.device_abnormallog_endtime);
			listItemView.mExceptionType = (TextView) convertView
					.findViewById(R.id.device_abnormallog_status);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
			convertView.findViewById(R.id.bd_show_deal_log)
					.setVisibility(View.GONE);
			convertView.findViewById(R.id.bd_show_detail_log).setVisibility(
					View.VISIBLE);

			listItemView.mDescription.setText(DeviceLog.getmDescription());
			listItemView.mStarttime.setText(DeviceLog.getmStarttime());
			listItemView.mEndtime.setText(DeviceLog.getmEndtime());
			listItemView.mExceptionType.setText(DeviceLog.getmExceptionType());

		}catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (hiteStatus == AppContext.ABNORMAL) {
			return AbnormalGetView(position, convertView, parent);
		} else {
			return DealGetView(position, convertView, parent);
		}
	}

}
