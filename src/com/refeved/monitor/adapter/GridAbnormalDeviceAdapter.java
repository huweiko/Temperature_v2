package com.refeved.monitor.adapter;

import java.util.ArrayList;
import java.util.List;
import com.refeved.monitor.R;
import com.refeved.monitor.achartengine.BudgetDoughnutChart;
import com.refeved.monitor.achartengine.BudgetPieChart;
import com.refeved.monitor.struct.AreaTreeNode;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.ui.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat") public class GridAbnormalDeviceAdapter extends BaseAdapter{
	private List<Device> mItems = new ArrayList<Device>();
	private Context mContext;
	private int mItemViewResource;//�Զ�������ͼԴ 
	private BudgetPieChart mBudgetPieChart = new BudgetPieChart();
	
	int[] DeviceStatuscolors = new int[] { Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN };
	private static final int DEV_ALARM_COLOR = 3;   //����
	private static final int DEV_BREAKNET_COLOR = 2;//����
	private static final int DEV_HANDLE_COLOR = 0;  //���ڴ���
	private static final int DEV_OPENDOOR_COLOR = 4;//������
	private static final int DEV_BATTERY_COLOR = 1; //ʹ�õ��
	
	private class GridListItemView { // �Զ���ؼ�����
		public TextView mTextViewAbnormalDevName;
		public RelativeLayout mRelativeLayoutAlarmStatusChart;
	}
	public GridAbnormalDeviceAdapter(Context context,int resource ) {
		this.mContext = context;
		this.mItemViewResource = resource;
	}
	public void setListItems(List<Device> data){
		mItems = data;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		GridListItemView  gridItemView = null;
		
		if (convertView == null) {
			//��ȡlist_item�����ļ�����ͼ
			convertView = LayoutInflater.from(mContext).inflate(this.mItemViewResource, null);
			
			gridItemView = new GridListItemView();
			//��ȡ�ؼ�����
			gridItemView.mTextViewAbnormalDevName = (TextView)convertView.findViewById(R.id.TextViewAbnormalDevName);
			gridItemView.mRelativeLayoutAlarmStatusChart= (RelativeLayout)convertView.findViewById(R.id.RelativeLayoutAlarmStatusChart);
			
			//���ÿؼ�����convertView
			convertView.setTag(gridItemView);
		}else {
			gridItemView = (GridListItemView)convertView.getTag();
		}	
		
		//�������ֺ�ͼƬ
		Device device = mItems.get(position);
		gridItemView.mTextViewAbnormalDevName.setText(device.getmDescription());
	    int[] color = new int[]{0,0,0,0,0};
		int Istatus = Integer.parseInt(device.getmStatus());
		
	    if(Istatus == 0x01){//�豸����
	    	
		}else{
			if((Istatus & 0x08) == 0x08){//����
				color[DEV_BREAKNET_COLOR] = DeviceStatuscolors[DEV_BREAKNET_COLOR];
			}else{
				if((Istatus & 0x10) == 0x10){//�豸����
					color[DEV_ALARM_COLOR] = DeviceStatuscolors[DEV_ALARM_COLOR];
				}else{
				}
				if((Istatus & 0x04) == 0x04){//�˹�����
					color[DEV_HANDLE_COLOR] = DeviceStatuscolors[DEV_HANDLE_COLOR];
				}
				if((Istatus & 0x02) == 0x02){//������
					color[DEV_OPENDOOR_COLOR] = DeviceStatuscolors[DEV_OPENDOOR_COLOR];
				}
				if(Istatus == 0){
					
				}else{
					if((Istatus & 0x01) == 0){//ʹ�õ��
						color[DEV_BATTERY_COLOR] = DeviceStatuscolors[DEV_BATTERY_COLOR];
					}
				}

			}

		}
	    gridItemView.mRelativeLayoutAlarmStatusChart.removeAllViews();
	    gridItemView.mRelativeLayoutAlarmStatusChart.addView(mBudgetPieChart.execute(mContext,color), new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		return convertView;
	}
}
