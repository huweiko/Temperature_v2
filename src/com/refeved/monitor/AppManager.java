package com.refeved.monitor;


import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AppManager {
	
	private static Stack<Activity> activityStack;
	private static AppManager instance;
	private AppManager(){}
	/**
	 * 鍗曚竴瀹炰緥
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			instance=new AppManager();
		}
		return instance;
	}
	/**
	 * 娣诲姞Activity鍒板爢鏍�
	 */
	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	/**
	 * 鑾峰彇褰撳墠Activity锛堝爢鏍堜腑鏈�鍚庝竴涓帇鍏ョ殑锛�
	 */
	public Activity currentActivity(){
		Activity activity=activityStack.lastElement();
		return activity;
	}
	
	/**
	 * 鍒犻櫎褰撳墠Activity锛堝爢鏍堜腑鏈�鍚庝竴涓帇鍏ョ殑锛�
	 */
	public void removeActivity(){
		Activity activity=activityStack.lastElement();
		removeActivity(activity);
	}
	/**
	 * 缁撴潫鎸囧畾鐨凙ctivity
	 */
	public void removeActivity(Activity activity){
		if(activity!=null){
			activityStack.remove(activity);
			activity=null;
		}
	}

	/**
	 * 缁撴潫褰撳墠Activity锛堝爢鏍堜腑鏈�鍚庝竴涓帇鍏ョ殑锛�
	 */
	public void finishActivity(){
		Activity activity=activityStack.lastElement();
		finishActivity(activity);
	}
	/**
	 * 缁撴潫鎸囧畾鐨凙ctivity
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * 缁撴潫鎸囧畾绫诲悕鐨凙ctivity
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	/**
	 * 缁撴潫鎵�鏈堿ctivity
	 */
	public void finishAllActivity(){
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	activityStack.get(i).finish();
            }
	    }
		activityStack.clear();
	}
	/**
	 * 閫�鍑哄簲鐢ㄧ▼搴�
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}
	}

	public void AppLogout(Context context) {
		finishAllActivity();
	}
}