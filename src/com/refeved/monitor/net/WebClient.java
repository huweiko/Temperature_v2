package com.refeved.monitor.net;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.struct.UserInfo;
import com.refeved.monitor.ui.LoginActivity;
import com.refeved.monitor.util.LogPrint;

public class WebClient {
	public static final String INTERNAL_ACTION_WEBMESSAGE = "com.refeved.monitor.action.WebMessage";  
	public static final String INTERNAL_ACTION_GETTEMP_DONE="com.refeved.monitor.net.broadcast.GETTEMPDONE";
	public static final String INTERNAL_ACTION_GETBYMACID="com.refeved.monitor.net.broadcast.GETBYMACID";
	public static final String INTERNAL_ACTION_GETBYAID_DONE="com.refeved.monitor.net.broadcast.GETBYAIDDONE";
	public static final String INTERNAL_ACTION_GETERRORBYAID="com.refeved.monitor.net.broadcast.GETERRORBYAID";
	public static final String INTERNAL_ACTION_GETERRORBYAID_DONE="com.refeved.monitor.net.broadcast.GETERRORBYAIDDONE";
	public static final String INTERNAL_ACTION_GETAREAMAP="com.refeved.monitor.net.broadcast.GETAREAMAP";
	public static final String INTERNAL_ACTION_GETAREAMAPDONE="com.refeved.monitor.net.broadcast.GETAREAMAPDONE";
	public static final String INTERNAL_ACTION_GETDEVINFO="com.refeved.monitor.net.broadcast.GETDEVINFO";
	public static final String INTERNAL_ACTION_GETDEVINFODONE="com.refeved.monitor.net.broadcast.GETDEVINFODONE";
	public static final String INTERNAL_ACTION_FINDBYLOGIN="com.refeved.monitor.net.broadcast.FINDBYLOGIN";
	public static final String INTERNAL_ACTION_UPDATEBYSN="com.refeved.monitor.net.broadcast.UPDATEBYSN";
	public static final String INTERNAL_ACTION_FINDTODAYALLBYMACID="com.refeved.monitor.net.broadcast.FINDTODAYALLBYMACID";
	//��ȡĳһ��ʱ����¶���Ϣ
	public static final String INTERNAL_ACTION_GETAVGVAL="com.refeved.monitor.net.broadcast.GETAVGVAL";
	
	//��ȡ�쳣��־
	public static final String INTERNAL_ACTION_GETABNORMALLOGS="com.refeved.monitor.net.broadcast.GETABNORMALLOGS";
	//��ȡ������־
	public static final String INTERNAL_ACTION_GETDEALLOGS="com.refeved.monitor.net.broadcast.GETDEALLOGS";
	
	//��ȡ�汾��Ϣ
	public static final String INTERNAL_ACTION_GETVERSIONINFO="com.refeved.monitor.net.broadcast.GETVERSIONINFO";
	//���ô�����Ϣ
	public static final String INTERNAL_ACTION_SETHANDLESTATUS="com.refeved.monitor.net.broadcast.SETHANDLESTATUS";
	
	
	private final static WebClient mInstance = new WebClient();
	AppContext appContext;
	/*
	 * �����ռ� 
	 */
	private static final String ServiceNameSpace = "http://api"; 
	
	/*
	 * WebService���ýӿ�
	 */
	//��½�ӿ�
	public static final String Method_findByLogin = "findByLogin";
	//��ȡ�����豸��Ϣ
	public static final String Method_getMonitorInfosForAll = "getMonitorInfosForAllWidthUserID";
	//�����ȡ�ӿ�
	public static final String Method_getAddressTree = "getAddressTreeWidthUserID";
	//�����豸������쳣��Ϣ�ӿ�
	public static final String Method_getMachineError = "getMachineError";
	//��ȡ�쳣�豸��־�ӿ�
	public static final String Method_getExceptionLogs = "getExceptionLogs";
	//��ȡ������־�ӿ�
	public static final String Method_getMachineHandleLogs = "getMachineHandleLogs";
	//��ȡ�����쳣�豸����Ϣ
	public static final String Method_getMonitorInfosForAllExceptionWidthUserID = "getMonitorInfosForAllExceptionWidthUserIDNew";
	//��ά��ɨ�����
	public static final String Method_updateBySN = "updateBySN";
	
	public static final String Method_getByMACID = "getByMACID";
	//��ȡ�������ʪ��ֵ
	public static final String Method_findTodayAllByMACID = "findTodayAllByMACID";
	//��ȡĳһ��ʱ�����ʪ��ƽ��ֵ
	public static final String Method_getAvgVal = "getAvgVal";
	//��ȡ�汾��Ϣ
	public static final String Method_getVersionInfo = "getVersion";
	//���ô�����Ϣ
	public static final String Method_setHandleStatus = "setHandleStatus";
	
	/*
	 * ����URL
	*/ 
	
	private static String URL_USERAPI; 
	private static String URL_MONTITORAPI; 
	private static String URL_LOGAPI; 
	private static String URL_MACHINEAPI; 
	private static String URL_OTHERAPI; 
	// ���ʲ���
	public static final String Param_SendXml = "xml";
	public static final String Param_resXml = "return";


	private static final int timeOut = 60000;
	public static String mCurrentServerIP;
	
	UserInfo mUserInfo = UserInfo.getAppManager();
	private WebClient(){
	}

	public static WebClient getInstance(Context context){
		if(mCurrentServerIP == null){
			// TODO Auto-generated method stub
			SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.settings_filename), Context.MODE_PRIVATE);
			//���ļ��л�ȡ���������
			String mRadioGroupStatus = sharedPreferences.getString("RadioNetStatus", LoginActivity.InwardServerIP);
			if(mRadioGroupStatus.equals(LoginActivity.ExternalServerIP)){
				mCurrentServerIP = sharedPreferences.getString(context.getString(R.string.settings_external_server_IP),"58.64.200.105");
			}else{
				mCurrentServerIP = sharedPreferences.getString(context.getString(R.string.settings_inward_server_IP),"172.16.99.206");
			}
		}
		return mInstance;
	}
	
	public static boolean checknetwork(String InwardServerIP,String ExternalServerIP){
		boolean state = false;
		try {
			InetAddress InwardAd = InetAddress.getByName(InwardServerIP);
			InetAddress ExternalAd = InetAddress.getByName(ExternalServerIP);

			state = InwardAd.isReachable(1000);
			if(state == true)
			{
				WebClient.mCurrentServerIP = InwardServerIP;
			} else {
				state = ExternalAd.isReachable(1000);
				if(state == true){
					WebClient.mCurrentServerIP = ExternalServerIP;
				}
				else{
					WebClient.mCurrentServerIP = InwardServerIP;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//�����Ƿ���Դﵽ�õ�ַ
		Log.d("huwei", "mCurrentServerIP = "+WebClient.mCurrentServerIP);
		return state;
	}
	public class CheckThread  extends Thread{

		@Override
		public void run(){
			while(true)
			{
				try {
					checknetwork(appContext.InwardServerIP,appContext.ExternalServerIP);
					sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}

	CheckThread mCheckThread;
	
	public void sendMessage(Context context , String method ,Map<String,String> param){
		appContext = (AppContext)context.getApplicationContext();
//		if(mCheckThread == null){
//			mCheckThread = new CheckThread();
//    		try {
//    			mCheckThread.start(); 
//			} catch (IllegalThreadStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
		SendThread sendThread = new SendThread(context ,method,param);
		URL_USERAPI = "http://"  + mCurrentServerIP + ":9102/temperature-ws/services/userAPI";
		URL_MONTITORAPI = "http://"  + mCurrentServerIP + ":9102/temperature-ws/services/montitorAPI";
		URL_LOGAPI = "http://"  + mCurrentServerIP + ":9102/temperature-ws/services/logAPI";
		URL_MACHINEAPI = "http://"  + mCurrentServerIP + ":9102/temperature-ws/services/machineAPI";
		URL_OTHERAPI = "http://"  + mCurrentServerIP + ":9102/temperature-ws/services/otherAPI";
		sendThread.start();
	}
	
	
	public class SendThread extends Thread{
		Map<String,String> mParam = null;
    	String mMethod = null;
    	Context mContext = null;
    	
    	public SendThread(Context context ,String method,Map<String,String> param){
    		mParam = param;
    		mContext = context;
    		mMethod = method;
    	}
    	
    	@Override 
    	public void run(){
    		String result = null;
    		try {
				result = CallWebServer(mParam,mMethod);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    		if(result == null){
    			result = "null";
    		}
			String action = null;
			if(mMethod.equals(Method_findByLogin)){
				action = INTERNAL_ACTION_FINDBYLOGIN;
			}
			else if(mMethod.equals(Method_getMonitorInfosForAll)){
				action = INTERNAL_ACTION_GETBYAID_DONE;
			}
			else if(mMethod.equals(Method_getAddressTree))
			{
				action = INTERNAL_ACTION_GETAREAMAPDONE;
			}
			else if(mMethod.equals(Method_getMachineError))
			{
				action = INTERNAL_ACTION_GETDEVINFODONE;
			}
			else if(mMethod.equals(Method_getExceptionLogs))
			{
				action = INTERNAL_ACTION_GETABNORMALLOGS;
			}
			else if(mMethod.equals(Method_getMachineHandleLogs))
			{
				action = INTERNAL_ACTION_GETDEALLOGS;
			}
			else if(mMethod.equals(Method_getMonitorInfosForAllExceptionWidthUserID))
			{
				action = INTERNAL_ACTION_GETERRORBYAID_DONE;
			}
			else if(mMethod.equals(Method_updateBySN))
			{
				action = INTERNAL_ACTION_UPDATEBYSN;
			}
			else if(mMethod.equals(Method_getByMACID)){
				action = INTERNAL_ACTION_GETBYMACID;
			}
			else if(mMethod.equals(Method_findTodayAllByMACID)){
				action = INTERNAL_ACTION_FINDTODAYALLBYMACID;
			}
			else if(mMethod.equals(Method_getAvgVal)){
				action = INTERNAL_ACTION_GETAVGVAL;
			}
			else if(mMethod.equals(Method_getVersionInfo)){
				action = INTERNAL_ACTION_GETVERSIONINFO;
			}
			else if(mMethod.equals(Method_setHandleStatus)){
				action = INTERNAL_ACTION_SETHANDLESTATUS;
			}
			Log.d("huwei---"+LogPrint.CML(), action);
			Intent intent = new Intent(action);
			intent.putExtra(WebClient.Param_resXml, result);
			mContext.sendBroadcast(intent);
    	}

    	@SuppressLint("SimpleDateFormat") private String CallWebServer(Map<String,String> param,String method) throws XmlPullParserException
    	{
			String Url = null;
			String Xml = null;
    		if(method.equals(Method_findByLogin))
    		{	
                if(param != null)
                {
                	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                    while(it.hasNext())
                    {
                    	Entry<String, String> entry = it.next();   
                    	Xml="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><USERNAME>"+entry.getKey()+"</USERNAME><USERPWD>"+entry.getValue()+"</USERPWD></root>";
                    }	
                    Url = URL_USERAPI;
                }
    		}
    		else if(method.equals(Method_getMonitorInfosForAll))
    		{
    			Url = URL_MONTITORAPI;
    			Xml = appContext.UserID;
    		}
    		else if(method.equals(Method_getAddressTree))
    		{
    			Url = URL_MONTITORAPI;
    			Xml = appContext.UserID;
    		}
    		else if(method.equals(Method_getMachineError))
    		{
    			String mAcid = null;
    			String mFrom = null;
    			String mTo = null;
                if(param != null)
                {
                	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                    while(it.hasNext())
                    {
                    	Entry<String, String> entry = it.next();  

                    	if(entry.getKey().equals("MACID"))
                    	{
                    		mAcid = entry.getValue();
                    	}
                    	else if(entry.getKey().equals("from"))
                    	{
                    		mFrom = entry.getValue();
                    	}
                    	else if(entry.getKey().equals("to"))
                    	{
                    		mTo = entry.getValue();
                    	}	
                    }
                    Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <root><macid>"+mAcid+"</macid><from>"+mFrom+"</from><to>"+mTo+"</to></root>";
                    Url = URL_MONTITORAPI;
                }
    		}
    		else if(method.equals(Method_getExceptionLogs))
    		{
    			String pageNO = null;
    			String pageSIZE = null;
    			if(param != null)
                {
                	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                    while(it.hasNext())
                    {
                    	Entry<String, String> entry = it.next();  

                    	if(entry.getKey().equals("pageNO"))
                    	{
                    		pageNO = entry.getValue();
                    	}
                    	else if(entry.getKey().equals("pageSIZE"))
                    	{
                    		pageSIZE = entry.getValue();
                    	}
                    }
                    Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <page><pageNO>"+pageNO+"</pageNO><pageSIZE>"+pageSIZE+"</pageSIZE></page>";
                    Url = URL_LOGAPI;
                 }
    		}
    		else if(method.equals(Method_getMachineHandleLogs)){
    			String pageNO = null;
    			String pageSIZE = null;
    			if(param != null)
                {
                	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                    while(it.hasNext())
                    {
                    	Entry<String, String> entry = it.next();  

                    	if(entry.getKey().equals("pageNO"))
                    	{
                    		pageNO = entry.getValue();
                    	}
                    	else if(entry.getKey().equals("pageSIZE"))
                    	{
                    		pageSIZE = entry.getValue();
                    	}
                    }
                    Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <page><pageNO>"+pageNO+"</pageNO><pageSIZE>"+pageSIZE+"</pageSIZE></page>";
                    Url = URL_LOGAPI;
                 }
    		}
    		else if(method.equals(Method_getMonitorInfosForAllExceptionWidthUserID))
    		{
    			Url = URL_MONTITORAPI;
    			Xml = appContext.UserID;
    		}
    		else if(method.equals(Method_updateBySN))
    		{
    			Url = URL_MACHINEAPI;
    			
    			
            	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                while(it.hasNext())
                {
                	Entry<String, String> entry = it.next();   
                	if(entry.getKey().equals("MachineList"))
                	{
                		Xml = entry.getValue();
                	}
                }
    		}
			else if(method.equals(Method_getByMACID)){
				Url = URL_MACHINEAPI;
            	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                while(it.hasNext())
                {
                	Entry<String, String> entry = it.next();   
                	if(entry.getKey().equals("MACID"))
                	{
                		Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><machine><macid>"+entry.getValue()+"</macid></machine>";
                	}
                }
			}
			else if(method.equals(Method_findTodayAllByMACID)){
				Url = URL_LOGAPI;
            	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                while(it.hasNext())
                {
                	Entry<String, String> entry = it.next();   
                	if(entry.getKey().equals("MACID"))
                	{
                		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                		Log.d("huwei",mSimpleDateFormat.format(new Date()));
                		Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><machine><id>"+entry.getValue()+"</id></machine><startTime>"+mSimpleDateFormat.format(new Date())+"</startTime></root>";
                	}
                }
			}
			else if(method.equals(Method_getAvgVal)){
				Url = URL_MONTITORAPI;
				String macid = null;
				String starttime = null;
				String endtime = null;
            	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                while(it.hasNext())
                {
                	Entry<String, String> entry = it.next();   
                	if(entry.getKey().equals("MACID"))
                	{
                		macid = entry.getValue();
                	}
                	if(entry.getKey().equals("STARTTIME"))
                	{
                		starttime = entry.getValue();
                	}
                	if(entry.getKey().equals("ENDTIME"))
                	{
                		endtime = entry.getValue();
                	}
                }
                Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><macid>"+macid+"</macid><startTime>"+starttime+"</startTime><endTime>"+endtime+"</endTime></root>";
			}
			else if(method.equals(Method_getVersionInfo)){
				Url = URL_OTHERAPI;
				Xml = "";
			}
			else if(method.equals(Method_setHandleStatus)){
    			String macid = "";
    			String startUserID = "";
    			String endUserID = "";
    			String startTime = "";
    			String endTime = "";
    			String startDes = "";
    			String endDes = "";
                if(param != null)
                {
                	Iterator<Entry<String, String>> it= param.entrySet().iterator();
                    while(it.hasNext())
                    {
                    	Entry<String, String> entry = it.next();  

                    	if(entry.getKey().equals("macid"))
                    	{
                    		macid = entry.getValue();
                    	}
                    	else if(entry.getKey().equals("startUserID"))
                    	{
                    		startUserID = entry.getValue();
                    	}
                    	else if(entry.getKey().equals("endUserID"))
                    	{
                    		endUserID = entry.getValue();
                    	}	
                    	else if(entry.getKey().equals("startTime"))
                    	{
                    		startTime = entry.getValue();
                    	}	
                    	else if(entry.getKey().equals("endTime"))
                    	{
                    		endTime = entry.getValue();
                    	}	
                    	else if(entry.getKey().equals("startDes"))
                    	{
                    		startDes = entry.getValue();
                    	}	
                    	else if(entry.getKey().equals("endDes"))
                    	{
                    		endDes = entry.getValue();
                    	}	
                    }
                    Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <root><macid>"+macid+"</macid><startUserID>"+startUserID+"</startUserID><endUserID>"+endUserID+"</endUserID><startTime>"+startTime+"</startTime><endTime>"+endTime+"</endTime><startDes>"+startDes+"</startDes><endDes>"+endDes+"</endDes></root>";
                    Url = URL_MONTITORAPI;
                }
			}
			
    		if(Url != null)
    		{
    			try {
    				return readContentFromWeb(Url,method,Xml);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    		
    		return null;
        }
		private String readContentFromWeb(String Url,String method,String Xml) throws IOException, XmlPullParserException {

    		String resXml = null;
    		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            SoapObject soapReq = new SoapObject(ServiceNameSpace,method);

            soapReq.addProperty(Param_SendXml,Xml);
            soapEnvelope.setOutputSoapObject(soapReq);
            HttpTransportSE httpTransport = new HttpTransportSE(Url,timeOut);
            Log.d("huwei", "Url = "+Url);
            try{
                httpTransport.call(ServiceNameSpace+"/"+method, soapEnvelope);
                Object retObj = soapEnvelope.bodyIn;
                if (retObj instanceof SoapFault){
                    SoapFault fault = (SoapFault)retObj;
                    Exception ex = new Exception(fault.faultstring);
                    ex.printStackTrace();
                }else{
                    SoapObject result=(SoapObject)retObj;
                    if (result.getPropertyCount() > 0){
	                   if (result.hasProperty(Param_resXml))
	                   {
	                	   Object obj = result.getProperty(Param_resXml);
	                       if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
	                    	   resXml =  ((SoapPrimitive) obj).toString();
	                       }else if (obj!= null && obj instanceof String){
	                           resXml = (String) obj;
	                       }
	                       
	                       return resXml;
	                   }
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            
            return null;
    	}
    }
}
