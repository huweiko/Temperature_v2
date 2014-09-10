package com.refeved.monitor.net;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.ErrorDev;
import com.refeved.monitor.ui.MainActivity;

@SuppressLint("HandlerLeak")
public class BackgroundService extends Service{
	AppContext appContext;
	int notificationID = 0;
	boolean ServiceRunning = false;
	private List<ErrorDev> lvDevData = new ArrayList<ErrorDev>();
	
	public Handler mHandler=new Handler()  
    {  
        public void handleMessage(Message msg)  
        {  
            switch(msg.what)  
            {  
            case 1:  
            	WebClient client = WebClient.getInstance(appContext);
    			client.sendMessage(appContext, WebClient.Method_getMonitorInfosForAllExceptionWidthUserID, null);
                break;  
            default:  
                break;            
            }  
            super.handleMessage(msg);  
        }  
    }; 
  public class RefreshThread  extends Thread{
    	
    	@Override
		public void run(){
    		if(ServiceRunning && appContext.SettingsNotification){
    			Message message=new Message();  
    			message.what=1;  
    			mHandler.sendMessage(message);  
    		}
    		Log.i("huwei", "ServiceRunning = "+ServiceRunning);
    		Log.i("huwei", "appContext.SettingsNotification = "+appContext.SettingsNotification);
    		Log.i("huwei", "ServiceRunning = "+ServiceRunning);
    		Log.i("huwei", "appContext.SettingsCheckFrequency = "+appContext.SettingsCheckFrequency);
    		while(ServiceRunning && appContext.SettingsNotification)
        	{
    			while(ServiceRunning && appContext.SettingsCheckFrequency > 0 && appContext.SettingsNotification)
            	{
    				Log.i("huwei", "ServiceRunning");
            		int count = appContext.SettingsCheckFrequency * 100;
            		while(ServiceRunning && count-- > 0)
            		{
                		try {  
                            Thread.sleep(10);  
                        } catch (InterruptedException e) {  
                            e.printStackTrace();  
                        }  
            		}
            		
            		if(ServiceRunning && appContext.SettingsNotification){
            			Message message=new Message();  
            			message.what=1;  
            			mHandler.sendMessage(message);  
            		}
                    
            	}
        	}
    		
    		stopSelf();
    	}
    }
    
    RefreshThread mRefreshThread = null;
    

    
    
   public void parseGetErrorByAidXml(Element element,List<Device> node){
	   Element e = null;
	   while( (e =element.getChild("dev")) != null)
	   {
		   String id = e.getAttributeValue("macid");
		   String status = e.getAttributeValue("state");
		   String descprition = e.getAttributeValue("des");
		   Device mDevice = new Device("", "", id, "", status, descprition, "", "", "");
		   node.add(mDevice);
		   element.removeChild("dev");
	   }
   }
	
 
    
public BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
			//	Log.i("huwei", "INTERNAL_ACTION_GETERRORBYAID_DONE resXml = "+resXml);
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETERRORBYAID_DONE))
			{
				if(resXml != null && !resXml.equals("null"))
				{
					try{
//						resXml = "<root><device macid='16' state='13' des='速度多少发大水'/><device macid='22' state='13' des='少发大水'/></root>";
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);   
						InputSource is = new InputSource(sr); 
						Document Doc = builder.build(is);
						Element rootElement = (Element) Doc.getRootElement();
						List<Device> node = new ArrayList<Device>();
						parseGetErrorByAidXml(rootElement, node);
						
						for(int i = 0 ; i < lvDevData.size() ; i++)
						{
							lvDevData.get(i).setmChecked(false);
						}
						
						CheckErrorList(node,lvDevData);
						//UIHealper.DisplayToast(appContext, "Get Error Devices "+ lvDevData.size());
						
						Iterator<ErrorDev> it = lvDevData.iterator();
						while(it.hasNext())
						{
							if(it.next().getmChecked() == false)
							{
								it.remove();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			}
			
			}		
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		super.onCreate();
	
		appContext = (AppContext) getApplication();
		IntentFilter filter = new IntentFilter();
	    filter.addAction(WebClient.INTERNAL_ACTION_GETERRORBYAID_DONE);
	    appContext.registerReceiver(receiver, filter);
		ServiceRunning = true;
	}

	public int onStartCommand(Intent intent , int flags , int startId){
		ServiceRunning = true;
		
		if(mRefreshThread == null){
    		mRefreshThread = new RefreshThread();
    		try {
    			mRefreshThread.start(); 
			} catch (IllegalThreadStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

		
		return Service.START_STICKY;
	}
	public void onDestroy(){
		super.onDestroy();
		
		ServiceRunning = false;
		appContext.unregisterReceiver(receiver);
		
		if(mRefreshThread != null){
    		try {
				mRefreshThread.join(15);
				mRefreshThread = null;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	private void CheckErrorList(List<Device> node,List<ErrorDev> list){
		Calendar currentCal = Calendar.getInstance();
		
		if(node == null || list == null) return;
		
		List<Device> devices = node;
		for(int i = 0 ; i < devices.size() ; i++)
		{
			Device dev= devices.get(i);
			boolean isMacth = false;
			boolean isNotification = false;
			for(int j = 0 ; j < list.size() ; j ++)
			{
				if(dev.getmId().equals(list.get(j).getmId()))
				{
					isMacth = true;
					list.get(j).setmChecked(true);
					
					long s = currentCal.getTimeInMillis() - list.get(j).getmCal().getTimeInMillis();
					if(s > 3600 * 1000)//一个小时通知一次
					{
						isNotification = true;
						list.get(j).setmCal(currentCal);
					}
					
					break;
				}
			}
			
			if(!isMacth)
			{
				ErrorDev errorDev = new ErrorDev(dev.getmType(), dev.getmEnvironmentType(), dev.getmId(), 
						dev.getmLocation(), dev.getmStatus(), dev.getmDescription(), 
						dev.getmLow(), dev.getmHigh(), currentCal, "");
				list.add(errorDev);
				
				isNotification = true;
			}
			
			if(isNotification)
			{
				showNotification(dev);
			}
				
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showNotification(Device device){
		Intent notificationIntent = new Intent(this, MainActivity.class);
		String id = getString(R.string.device_info_id_tittle);
		notificationIntent.putExtra(id, device.getmId());
		
		//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
		
		Notification notification = new Notification();
		notification.icon = R.drawable.device_info_icon;
		notification.tickerText = getString(R.string.notification_tittle);
		notification.defaults = Notification.DEFAULT_ALL;

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent,PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(this,device.getmDescription(),getString(R.string.device_info_status_tittile) + UIHealper.parseStatus(device.getmStatus()), pendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID++, notification);
	}

	
	/**
	 * 检测是否有活动网络
	 */
	public static boolean contactNet(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况
		if (activeNetInfo != null && activeNetInfo.isConnected()
				&& activeNetInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}
}
