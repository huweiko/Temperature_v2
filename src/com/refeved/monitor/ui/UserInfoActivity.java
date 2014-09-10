package com.refeved.monitor.ui;

import com.refeved.monitor.AppManager;
import com.refeved.monitor.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class UserInfoActivity extends BaseActivity implements OnClickListener{
	private ImageButton mImageButtonUserInfoActivityBack;
	private Button mButtonLogout;
	private final int CLICK_BACK = 0;
	private final int CLICK_LOGOUT = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		mImageButtonUserInfoActivityBack = (ImageButton) findViewById(R.id.ImageButtonUserInfoActivityBack);
		mButtonLogout = (Button) findViewById(R.id.ButtonLogout);
		mImageButtonUserInfoActivityBack.setOnClickListener(this);
		mImageButtonUserInfoActivityBack.setTag(CLICK_BACK);
		mButtonLogout.setOnClickListener(this);
		mButtonLogout.setTag(CLICK_LOGOUT);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		int tag = (Integer) v.getTag();
		
		switch (tag)
		{
			case CLICK_BACK:
			{
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			}
			break;
			case CLICK_LOGOUT:
			{
				AlertDialog.Builder builder = new Builder(UserInfoActivity.this);
				builder.setTitle(R.string.is_Logout);
				
				builder.setPositiveButton(R.string.soft_update_confirm, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
//						AppManager.getAppManager().AppLogout(UserInfoActivity.this);
						// TODO Auto-generated method stub
						Intent intent = new Intent(UserInfoActivity.this,LoginActivity.class);
						byte LoginStatus = 1;
						intent.putExtra("LoginStatus", LoginStatus);
						startActivity(intent);
					}
				});
				builder.setNegativeButton(R.string.soft_update_cancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				Dialog noticeDialog = builder.create();
				noticeDialog.show();

			}
			break;
			default:break;
		}
	
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}