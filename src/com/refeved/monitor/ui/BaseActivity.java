package com.refeved.monitor.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.refeved.monitor.AppManager;

public class BaseActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//结束Activity&从堆栈中移除
		AppManager.getAppManager().removeActivity(this);
	}
}
