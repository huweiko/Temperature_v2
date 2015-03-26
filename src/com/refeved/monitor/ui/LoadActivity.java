/*
 * 文件名：LoadActivity.java
 * 功能：应用程序启动时的加载界面，程序入口
 * 作者：huwei
 * 创建时间：2013-10-22
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import java.lang.Thread;
import android.content.Intent;

public class LoadActivity extends BaseActivity 
{
	private AppContext appContext;
	private TextView mTextviewVersion;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        appContext = (AppContext) getApplication();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        //Android设备display默认是采用16-bits color palette来表示所有颜色，因此对于带alpha值的32位png图片会出现显示失真。
//      getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.load_activity);
        mTextviewVersion = (TextView)findViewById(R.id.load_VersionInfo);
        mTextviewVersion.setText("V"+appContext.AppVesion);
        
        showDensity();
        
        new Thread()
        { 
			 public void run()
			 {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//登录时检查网络状态
//				WebClient.checknetwork(appContext.InwardServerIP,appContext.ExternalServerIP);
				login_activity();
			}
        }.start(); 
    }

	private void showDensity() {
		/*（hdpi: 240 , ldpi: 120 , mdpi: 160 , xhdpi: 320）”*/
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240） 
        String StrDensityDpi = null;
        switch(densityDpi){
        case 120:
        	StrDensityDpi = "ldpi";
        	break;
        case 160:
        	StrDensityDpi = "mdpi";
        	break;
        case 240:
        	StrDensityDpi = "hdpi";
        	break;
        case 320:
        	StrDensityDpi = "xhdpi";
        	break;
        	default:
        		StrDensityDpi = "null";
        		break;
        }
        Log.i("huwei","此设备屏幕分辨率为："+height+"*"+width);
        Log.i("huwei","屏幕密度为："+"("+density+"_"+densityDpi+")"+":"+StrDensityDpi);
	}

    /*
     * 功能：启动登陆界面
     * 参数：无
     * 返回值： 无
     * 
     * */
	public void login_activity()
	{
		/* 新建一个Intent对象 */
		Intent intent = new Intent();
		/* 指定intent要启动的类 */
		intent.setClass(LoadActivity.this, LoginActivity.class);
		byte LoginStatus = 0;
		intent.putExtra("LoginStatus", LoginStatus);
		/* 启动一个新的Activity */
		startActivity(intent);
		/* 关闭当前的Activity */
		LoadActivity.this.finish();	
	}
	protected void onDestroy() 
	{
		super.onDestroy();
	}
}