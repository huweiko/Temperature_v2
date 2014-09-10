package com.refeved.monitor.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.refeved.monitor.AppManager;

public class BaseActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//���Activity����ջ
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//����Activity&�Ӷ�ջ���Ƴ�
		AppManager.getAppManager().removeActivity(this);
	}
}
