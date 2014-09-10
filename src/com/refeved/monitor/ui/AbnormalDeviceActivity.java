package com.refeved.monitor.ui;

import java.util.List;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.adapter.GridAbnormalDeviceAdapter;
import com.refeved.monitor.struct.Device;
import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class AbnormalDeviceActivity extends Activity{
	private AppContext appContext;
	private GridView mDevGridView;
	private GridAbnormalDeviceAdapter mGridAbnormalDeviceAdapter;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        appContext = (AppContext) getApplication();
        setContentView(R.layout.activity_abnormal_device);
        mDevGridView = (GridView)findViewById(R.id.GridViewAbnormalDevice);
        mGridAbnormalDeviceAdapter = new GridAbnormalDeviceAdapter(appContext, R.layout.item_abnormal);
        mDevGridView.setAdapter(mGridAbnormalDeviceAdapter);
//		updateListView(MainActivity.lvAbnormalDevData);
    }

	public void updateListView(List<Device> listItem){
		mGridAbnormalDeviceAdapter.setListItems(listItem);
		mGridAbnormalDeviceAdapter.notifyDataSetChanged();
	}
	protected void onDestroy() 
	{
		super.onDestroy();
	}
}