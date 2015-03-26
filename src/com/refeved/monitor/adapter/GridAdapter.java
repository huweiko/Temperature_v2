package com.refeved.monitor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.view.OptimizeGridView.OptimizeGridAdapter;

@SuppressLint("SimpleDateFormat") public class GridAdapter extends BaseAdapter implements OptimizeGridAdapter<Device>{
	
	public static class Item{
		public String text;
		public int resId;
	}
	private Shader shader_alarm;
	private Shader shader_normal;
	private Shader shader_hum;
	private List<Device> mItems = new ArrayList<Device>();
	private Context mContext;
	public static final String SHOW_DEVICE_INFO="com.refeved.monitor.adapter.broadcast.SHOW_DEVICE_INFO";
	public static final String SHOW_HANDLE_DIALOG="com.refeved.monitor.adapter.broadcast.SHOW_HANDLE_DIALOG";
	public GridAdapter(Context context) {
		mContext =  context;
		shader_alarm = new LinearGradient(0, 0, 0, 20, mContext.getResources().getColor(R.color.theme_status_text_redcolor_start), mContext.getResources().getColor(R.color.theme_status_text_redcolor_stop), TileMode.CLAMP);
		shader_normal = new LinearGradient(0, 0, 0, 20, mContext.getResources().getColor(R.color.theme_status_text_greencolor_start), mContext.getResources().getColor(R.color.theme_status_text_greencolor_stop), TileMode.CLAMP);
		shader_hum = new LinearGradient(0, 0, 0, 20, mContext.getResources().getColor(R.color.theme_status_text_humiditycolor), mContext.getResources().getColor(R.color.theme_status_text_humiditycolor), TileMode.CLAMP);
	}
	static class GridListItemView { // 自定义控件集合
		public ImageView image;
		public TextView des;
		public TextView degree;
		public TextView status;
		public ImageView other_status;
		public ImageView device_status;
		public ImageButton handle;
		public FrameLayout device_onclick;
		public ImageView alarm;
	}
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(!mItems.get(position).getmType().equals(Device.Type_Frige) &&
				!mItems.get(position).getmType().equals(Device.Type_Humidity)){
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridListItemView mGridListItemView = null;
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
		
		}
		if(mGridListItemView == null){
			mGridListItemView = new GridListItemView();
		}
//		AbsListView.LayoutParams param = new AbsListView.LayoutParams(160,500); 

//		convertView.setLayoutParams(param); 

		int Istatus = 0;

		mGridListItemView.status = (TextView) convertView.findViewById(R.id.text_status);
		mGridListItemView.image = (ImageView) convertView.findViewById(R.id.icon);
		mGridListItemView.des = (TextView) convertView.findViewById(R.id.text_des);
		mGridListItemView.degree = (TextView) convertView.findViewById(R.id.text_degree);
		mGridListItemView.other_status = (ImageView) convertView.findViewById(R.id.device_other_status);
		mGridListItemView.device_status = (ImageView) convertView.findViewById(R.id.device_status_icon);
		mGridListItemView.device_onclick = (FrameLayout) convertView.findViewById(R.id.FrameLayoutDevice);
		mGridListItemView.handle = (ImageButton) convertView.findViewById(R.id.ImageButtonHandle);
		mGridListItemView.alarm = (ImageView) convertView.findViewById(R.id.alarm);
		final Device item = (Device) getItem(position);
		
		if(item.getmStatus() != null && item.getmStatus() != ""){
//			Istatus = 1;
			Istatus = Integer.parseInt(item.getmStatus());
		}
		final boolean handleStatus = (Istatus & 0x04) == 0x04?true:false;
		if(isNullItem(item)) {
//			convertView.setBackgroundResource(R.drawable.grid_bg);
			mGridListItemView.image.setVisibility(View.INVISIBLE);
			mGridListItemView.des.setVisibility(View.INVISIBLE);
			mGridListItemView.degree.setVisibility(View.INVISIBLE);
			mGridListItemView.status.setVisibility(View.INVISIBLE);
			mGridListItemView.other_status.setVisibility(View.INVISIBLE);
			mGridListItemView.device_status.setVisibility(View.INVISIBLE);
			mGridListItemView.handle.setVisibility(View.INVISIBLE);
			mGridListItemView.alarm.setVisibility(View.INVISIBLE);
			return convertView;
		}
		mGridListItemView.image.setVisibility(View.VISIBLE);
		mGridListItemView.des.setVisibility(View.VISIBLE);
//		convertView.setBackgroundResource(R.drawable.grid_item_border);
		mGridListItemView.handle.setEnabled(true);
		mGridListItemView.handle.setImageResource(R.drawable.handle);
		if(((AppContext)mContext.getApplicationContext()).UserName.equals("guest")){
			mGridListItemView.handle.setVisibility(View.INVISIBLE);
		}else{
			mGridListItemView.handle.setVisibility(View.VISIBLE);
		}
		
		if(item.getmType().equals(Device.Type_Frige))
		{
			
			mGridListItemView.des.setText(item.getmDescription());
			mGridListItemView.status.setVisibility(View.VISIBLE);
			mGridListItemView.status.setText(UIHealper.parseStatus(item.getmStatus()));
			mGridListItemView.degree.setVisibility(View.VISIBLE);
			mGridListItemView.degree.setText(((DevFrige)item).getmTemperature()+"℃");
			mGridListItemView.other_status.setVisibility(View.VISIBLE);
			mGridListItemView.device_status.setVisibility(View.VISIBLE);
			mGridListItemView.alarm.setVisibility(View.VISIBLE);
			mGridListItemView.alarm.setImageResource(R.drawable.device_alarm_padding);
			mGridListItemView.other_status.setImageResource(R.drawable.other_none_status);
			int EnvironmentType = Integer.parseInt(item.getmEnvironmentType());
			switch(EnvironmentType){
			case Device.Temperature_Frige:
				showFrigeDeviceContent(mGridListItemView, Istatus);
				break;
			case Device.Temperature_NitrogenCanister:
				showNitrogenCanisterDeviceContent(mGridListItemView, Istatus);
				break;
			case Device.Temperature_Room:
				showRoomDeviceContent(mGridListItemView, Istatus);
				break;
			case Device.Temperature_Cooler:
				showCoolerDeviceContent(mGridListItemView, Istatus);
				break;
			case Device.Temperature_Cooler_node:
				showCoolerNodeDeviceContent(mGridListItemView, Istatus);
				break;
				default:break;
			}
//			if(Istatus == 0x01){
//				mGridListItemView.alarm.setBackgroundResource(R.color.transparent);
//			}else{
//				mGridListItemView.alarm.setBackgroundResource(R.color.alarm_red_color);
//			}
		}
		else if(item.getmType().equals(Device.Type_Humidity))
		{
			mGridListItemView.des.setText(item.getmDescription());
			mGridListItemView.status.setVisibility(View.VISIBLE);
			mGridListItemView.status.setText(UIHealper.parseStatus(item.getmStatus()));
			mGridListItemView.degree.setVisibility(View.VISIBLE);
			mGridListItemView.degree.setText(((DevHumidity)item).getmHumidity()+"%");
			
			mGridListItemView.other_status.setVisibility(View.VISIBLE);
			mGridListItemView.device_status.setVisibility(View.VISIBLE);
			mGridListItemView.alarm.setVisibility(View.VISIBLE);
			mGridListItemView.other_status.setImageResource(R.drawable.other_none_status);
			mGridListItemView.degree.getPaint().setShader(shader_hum);
			mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
			mGridListItemView.alarm.setImageResource(R.drawable.device_alarm_padding);
			if(Istatus == 0x01){//设备正常
				mGridListItemView.image.setImageResource(R.drawable.icon_humidity);
				mGridListItemView.handle.setImageResource(R.color.transparent);
				mGridListItemView.handle.setEnabled(false);
			}else{
				if((Istatus & 0x08) == 0x08){//断网
					mGridListItemView.image.setImageResource(R.drawable.humidity_netbreak);
					mGridListItemView.degree.setText("");
				}else{
					if((Istatus & 0x10) == 0x10){//设备报警
						mGridListItemView.degree.getPaint().setShader(shader_alarm);
						mGridListItemView.image.setImageResource(R.drawable.icon_humidity);
					}else{
						mGridListItemView.image.setImageResource(R.drawable.icon_humidity);	
					}
					if((Istatus & 0x04) == 0x04){//人工处理
						mGridListItemView.handle.setImageResource(R.drawable.handling);
					}
					if((Istatus & 0x01) == 0){//正在充电
						mGridListItemView.other_status.setImageResource(R.drawable.battery);
					}
				}

			}
//			if(Istatus == 0x01){
//				mGridListItemView.alarm.setBackgroundResource(R.color.transparent);
//			}else{
//				mGridListItemView.alarm.setBackgroundResource(R.color.alarm_red_color);
//			}

		}
		mGridListItemView.image.setOnClickListener(new OnClickListener() { 
				@Override 
				public void onClick(View v) { 
					showDevDetail(item);
				} 
			}); 
		mGridListItemView.handle.setOnClickListener(new OnClickListener() { 
			@Override 
			public void onClick(View v) { 
				showHandleDialog(item,handleStatus);
			} 
		}); 
		
		return convertView;
	}
//房间设备
	private void showRoomDeviceContent(GridListItemView mGridListItemView,
			int istatus) {
		mGridListItemView.degree.getPaint().setShader(shader_normal);
		// TODO Auto-generated method stub
		mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
		//设备正常
		if(istatus == 0x01){
			mGridListItemView.image.setImageResource(R.drawable.icon_room_tem);
			mGridListItemView.handle.setImageResource(R.color.transparent);
			mGridListItemView.handle.setEnabled(false);
		}else{
			//断网
			if((istatus & 0x08) == 0x08){
				mGridListItemView.image.setImageResource(R.drawable.room_status_netbreak_pic);
				mGridListItemView.degree.setText("");
			}else{
				//报警
				if((istatus & 0x10) == 0x10){
					mGridListItemView.image.setImageResource(R.drawable.icon_room_tem);
					mGridListItemView.degree.getPaint().setShader(shader_alarm);
				}
				//人工处理
				if((istatus & 0x04) == 0x04){
					mGridListItemView.handle.setImageResource(R.drawable.handling);
					mGridListItemView.image.setImageResource(R.drawable.icon_room_tem);
				}
				//充电
				if((istatus & 0x01) == 0){
					mGridListItemView.other_status.setImageResource(R.drawable.battery);
					mGridListItemView.image.setImageResource(R.drawable.icon_room_tem);
				}
			}

		}
		
	}
//液氮罐
	private void showNitrogenCanisterDeviceContent(
			GridListItemView mGridListItemView, int istatus) {
		mGridListItemView.degree.getPaint().setShader(shader_normal);
		mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
		// TODO Auto-generated method stub
		mGridListItemView.device_status.setBackgroundResource(R.color.transparent);
		//设备正常
		if(istatus == 0x01){
			mGridListItemView.image.setImageResource(R.drawable.icon_nitrogen_canister);
			mGridListItemView.handle.setImageResource(R.color.transparent);
			mGridListItemView.handle.setEnabled(false);
			
		}else{
			//设备断网
			if((istatus & 0x08) == 0x08){
				mGridListItemView.image.setImageResource(R.drawable.nitrogen_canister_netbreak_pic);
				mGridListItemView.degree.setText("");
			}else{
				//设备报警
				if((istatus & 0x10) == 0x10){
					mGridListItemView.image.setImageResource(R.drawable.icon_nitrogen_canister);
					mGridListItemView.degree.getPaint().setShader(shader_alarm);
				}
				//人工处理
				if((istatus & 0x04) == 0x04){
					mGridListItemView.handle.setImageResource(R.drawable.handling);
					mGridListItemView.image.setImageResource(R.drawable.icon_nitrogen_canister);
				}
				//设备充电
				if((istatus & 0x01) == 0){
					mGridListItemView.other_status.setImageResource(R.drawable.battery);
					mGridListItemView.image.setImageResource(R.drawable.icon_nitrogen_canister);
				}
			}
		}
		
	}
	//冰箱
	private void showFrigeDeviceContent(GridListItemView mGridListItemView,
			int istatus) {
		// TODO Auto-generated method stub
		mGridListItemView.degree.getPaint().setShader(shader_normal);
		if(istatus == 0x01){
			mGridListItemView.image.setImageResource(R.drawable.icon_temperature);
			mGridListItemView.device_status.setImageResource(R.drawable.device_status_normal_pic);
			mGridListItemView.handle.setImageResource(R.color.transparent);
			mGridListItemView.handle.setEnabled(false);
		}else{
			//断网
			if((istatus & 0x08) == 0x08){
				mGridListItemView.image.setImageResource(R.drawable.fridge_pic);
				mGridListItemView.device_status.setImageResource(R.drawable.device_status_netbreak_pic);
				mGridListItemView.degree.setText("");
			}else{
				//报警
				if((istatus & 0x10) == 0x10){
					mGridListItemView.image.setImageResource(R.drawable.icon_temperature);
					mGridListItemView.device_status.setImageResource(R.drawable.device_status_alarm_pic);
					mGridListItemView.degree.getPaint().setShader(shader_alarm);
				}else{
					mGridListItemView.image.setImageResource(R.drawable.icon_temperature);
					mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
				}
				//人工处理
				if((istatus & 0x04) == 0x04){
					mGridListItemView.handle.setImageResource(R.drawable.handling);
				}
				//充电
				if((istatus & 0x01) == 0){
					mGridListItemView.other_status.setImageResource(R.drawable.battery);
				}
			}
			
		}
	}
	//冷库
	private void showCoolerDeviceContent(GridListItemView mGridListItemView,
			int istatus) {
		mGridListItemView.degree.getPaint().setShader(shader_normal);
		mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
		// TODO Auto-generated method stub
		//设备正常
		if(istatus == 0x01){
			mGridListItemView.image.setImageResource(R.drawable.icon_cooler_tem);
			mGridListItemView.handle.setImageResource(R.color.transparent);
			mGridListItemView.handle.setEnabled(false);
		}else{
			mGridListItemView.image.setImageResource(R.drawable.icon_cooler_tem);
			mGridListItemView.handle.setImageResource(R.color.transparent);
			mGridListItemView.handle.setEnabled(false);
			mGridListItemView.status.setText("异常");
		}
	}
	//冷库节点
	private void showCoolerNodeDeviceContent(GridListItemView mGridListItemView,
			int istatus) {
		mGridListItemView.degree.getPaint().setShader(shader_normal);
		mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
		// TODO Auto-generated method stub
		mGridListItemView.device_status.setImageResource(R.drawable.device_alarm_padding);
		//设备正常
		if(istatus == 0x01){
			mGridListItemView.image.setImageResource(R.drawable.icon_coolernode_tem);
			mGridListItemView.handle.setImageResource(R.color.transparent);
			mGridListItemView.handle.setEnabled(false);
		}else{
			//设备断网
			if((istatus & 0x08) == 0x08){
				mGridListItemView.image.setImageResource(R.drawable.coolernode_status_netbreak_pic);
				mGridListItemView.degree.setText("");
			}else{
				//设备报警
				if((istatus & 0x10) == 0x10){
					mGridListItemView.image.setImageResource(R.drawable.icon_coolernode_tem);
					mGridListItemView.degree.getPaint().setShader(shader_alarm);
				}
				//人工处理
				if((istatus & 0x04) == 0x04){
					mGridListItemView.handle.setImageResource(R.drawable.handling);
					mGridListItemView.image.setImageResource(R.drawable.icon_coolernode_tem);
				}
				//设备充电
				if((istatus & 0x01) == 0){
					mGridListItemView.other_status.setImageResource(R.drawable.battery);
					mGridListItemView.image.setImageResource(R.drawable.icon_coolernode_tem);
				}
			}
		}
		
	}

	//显示设备细节
	public void showDevDetail(Device device)
	{
		Intent intent = new Intent(SHOW_DEVICE_INFO);
		String id = mContext.getString(R.string.device_info_id_tittle);
		String environmentType = mContext.getString(R.string.device_environmentType);
		intent.putExtra(id, device.getmId());
		intent.putExtra(environmentType, device.getmEnvironmentType());
		mContext.sendBroadcast(intent);
	}
	//显示处理对话框
	public void showHandleDialog(Device device,boolean handleStatus)
	{
		Intent intent = new Intent(SHOW_HANDLE_DIALOG);
		intent.putExtra("deviceID", device.getmId());
		intent.putExtra("handleStatus", handleStatus);
		mContext.sendBroadcast(intent);
	}

	public static Device NULL_ITEM = new Device("", "", "", "", "", "", "", "","");
	@Override
	public List<Device> getItems() {
		return mItems;
	}

	@Override
	public void setItems(List<Device> items) {
		mItems = items;
	}

	@Override
	public Device getNullItem() {
		return NULL_ITEM;
	}

	@Override
	public boolean isNullItem(Device item) {
		return item == NULL_ITEM;
	}

	@Override
	public void setColumn(int column) {
		// TODO Auto-generated method stub
		
	}
}
