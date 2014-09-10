package com.refeved.monitor.ui;

import java.util.ArrayList;
import java.util.List;

import com.refeved.monitor.R;
import com.refeved.monitor.adapter.DevListViewAdapter;
import com.refeved.monitor.struct.AreaTreeNode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ListView;


public class CoolerNodeFragment extends Fragment {

	ListView mDevListView;
	private DevListViewAdapter mDevListViewAdapter;
	public List<AreaTreeNode> lvDevData = new ArrayList<AreaTreeNode>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.myfragment_layout, container,
				false);
		mDevListView = (ListView)view.findViewById(R.id.listViewCoolerNode);
		mDevListViewAdapter = new DevListViewAdapter(getActivity(), new ArrayList<AreaTreeNode>(), R.layout.surveil_fragment_gridview);
		mDevListView.setAdapter(mDevListViewAdapter);
		updateListView(lvDevData);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		return super.onCreateAnimation(transit, enter, nextAnim);
	}
	public void updateListView(List<AreaTreeNode> listItem){
		mDevListViewAdapter.setListItems(listItem);
		mDevListViewAdapter.notifyDataSetChanged();
	}
}
