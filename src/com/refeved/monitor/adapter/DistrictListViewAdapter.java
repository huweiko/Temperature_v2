package com.refeved.monitor.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.TreeNode;
import com.refeved.monitor.ui.DistrictListViewActivity;
import com.refeved.monitor.ui.LineBreakLayout;
import com.refeved.monitor.ui.RepeatingImageButton;

public class DistrictListViewAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private List<TreeNode> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	
	public DistrictListViewAdapter(Context context,List<TreeNode> data) {
		this.context = context;		
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.listItems = data;
	}
	
	public void setListItems(List<TreeNode> data){
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		List<TreeNode> items = listItems.get(position).getmChildren();

		LinearLayout itemMainLayout = new LinearLayout(context);
		itemMainLayout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout itemButtonLayout = new LinearLayout(context);
		itemButtonLayout.setPadding(10, 5, 10, 5);
		
		if(items.size() <= 0) return itemMainLayout;
		
		RelativeLayout tagView = (RelativeLayout) listContainer.inflate(R.layout.district_title, null);
		TextView tagText = (TextView) tagView.findViewById(R.id.TextViewDistrictTitle);
		tagText.setText(listItems.get(position).getmName());
		itemMainLayout.addView(tagView);
		itemMainLayout.addView(itemButtonLayout);
		
		LineBreakLayout buttonLayout = new LineBreakLayout(context);
		
		int count = 0;
		while(count < items.size()){
			//Button button = new Button(context);
			RepeatingImageButton button = new RepeatingImageButton(context);
			button.setOnClickListener((DistrictListViewActivity)context);
			button.setRepeatListener((DistrictListViewActivity)context, 1000);
			button.setBackgroundResource(R.drawable.color_button);
			button.setText(items.get(count).getmName());
			button.setTextColor(R.color.theme_gray_text_color);
			button.setSingleLine();
			button.setWidth(400);
			button.setGravity(Gravity.CENTER_HORIZONTAL);
			if(items.get(count).ismIsOpen()){
				if(items.get(count).getmChildren().size()>0)//对没有子房间的节点不变化背景色
					button.setBackgroundResource(R.drawable.district_button_on);
			}
			button.setTag(items.get(count));
			count++;
			button.setTextSize(20);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			button.setLayoutParams(params);
			buttonLayout.addView(button);
		}
		itemButtonLayout.addView(buttonLayout);
		
		return itemMainLayout;
	}

}