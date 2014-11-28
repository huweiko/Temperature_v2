package com.refeved.monitor.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.refeved.monitor.R;
import com.refeved.monitor.struct.SpecimenInfo;

public class DevSpecimenListViewAdapter extends BaseAdapter {
	
	private Context 					context;//����������
	private List<SpecimenInfo> 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 

	static class ListItemView{				//�Զ���ؼ�����  
		public TextView textViewlistCount;
		public TextView textViewSpecimenDes;
	    public TextView textViewSpecimenNum;
	}  
	
	public DevSpecimenListViewAdapter(Context context, List<SpecimenInfo> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	public void setListItems(List<SpecimenInfo> data){
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
			listItemView.textViewlistCount = (TextView)convertView.findViewById(R.id.TextView_listitem_specimen_count);
			listItemView.textViewSpecimenDes = (TextView)convertView.findViewById(R.id.TextView_listitem_specimen_des);
			listItemView.textViewSpecimenNum= (TextView)convertView.findViewById(R.id.TextView_listitem_specimen_number);
			
			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		SpecimenInfo specimenInfo = listItems.get(position);
		listItemView.textViewlistCount.setText((position+1)+".");
		listItemView.textViewSpecimenDes.setText(specimenInfo.getmSpecimenDes());
		listItemView.textViewSpecimenNum.setText(specimenInfo.getmSpecimenNum());
	
		return convertView;
	}

}
