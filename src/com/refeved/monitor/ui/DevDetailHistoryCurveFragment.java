package com.refeved.monitor.ui;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.net.WebClient;
import com.refeved.monitor.struct.DeviceLog;
import com.refeved.monitor.util.DimenUtils;
public class DevDetailHistoryCurveFragment extends Fragment implements OnClickListener{

	private List<DeviceLog> lvLogDataWeek = new ArrayList<DeviceLog>();
	private List<DeviceLog> lvLogDataMonth = new ArrayList<DeviceLog>();
    private GraphicalView chart;
    private int addY = -1;
	private long addX;
	/**��������*/
    private static final String TAG = "message";
	protected static final int HANDLER_DOMBUILD_XML = 1;
	private static final int CLICK_BUTTONWEEK_CURVE = 0;
	private static final int CLICK_BUTTONMONTH_CURVE = 1;
	private static final int WEEKSTYLE = 1;
	private static final int MONTHSTYLE = 2;
    private TimeSeries series1;
    private XYMultipleSeriesDataset dataset1;
    private Random random=new Random();
    /**ʱ������*/
    Date[] xcache = new Date[20];
	/**����*/
    int[] ycache = new int[20];
    
    
    private int mBackColor;
    private AppContext appContext;
    private LinearLayout layout;
    private Button mButtonWeekCurve;
    private Button mButtonMonthCurve;
    private View mLoadingView;
    private int mCurveStyle;
    private boolean mIsInitMonth = false;
    @SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler(){
    	@Override
		public void handleMessage(Message msg)  
		{  
			switch(msg.what)  
			{  
				case HANDLER_DOMBUILD_XML:
				{
					String resXml = (String) msg.obj;
					try{
						//SAXBuilder��һ��JDOM������ �ܽ�·���е�XML�ļ�����ΪDocument����
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);   
						InputSource is = new InputSource(sr); 
						Document Doc = builder.build(is);
						Element rootElement = (Element) Doc.getRootElement();
						if(mCurveStyle == WEEKSTYLE){
							parseGetByAidXml(rootElement, lvLogDataWeek);
							Double [] date = new Double[2];
							if(getMaxMinValue(lvLogDataWeek,date) == 0){
								chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataWeek), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
							}else{
								Log.i("huwei", "����û������");
							}
						}
						else{
							parseGetByAidXml(rootElement, lvLogDataMonth);	
							Double [] date = new Double[2];
							if(getMaxMinValue(lvLogDataMonth,date) == 0){
								chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataMonth), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
							}else{
								Log.i("huwei", "����û������");
							}
							mIsInitMonth = true;
						}
						layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

					}catch(Exception e)
					{
						e.printStackTrace();
					}	

				}
				break;
				default:  
				break;            
			}  
			super.handleMessage(msg);  
		}  
	}; 
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String resXml = intent.getStringExtra(WebClient.Param_resXml);
//			Log.i("huwei", LogPrint.CML() + resXml);
			if(intent.getAction().equals(WebClient.INTERNAL_ACTION_GETAVGVAL))
			{
				mLoadingView.setVisibility(View.GONE);
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null"))
					{
						UIHealper.DisplayToast(appContext,"��ȡ�豸��Ϣʧ��");
					}
					else {
				        Message msg = new Message();
				        msg.what = HANDLER_DOMBUILD_XML;
				        msg.obj = resXml;
				        mHandler.sendMessage(msg);
					}
					
				}
			}
		}
	};
	//����������¶ȵ����ֵ����Сֵ
	private int getMaxMinValue(List<DeviceLog> lvLogData, Double[] date){
		int k = lvLogData.size();
		int result = 0;
		Double [] logdate = new Double[k];
		int m = 0;
		for(int i=0;i<k;i++){
			if(!lvLogData.get(i).getmTemperature().equals("null")){
				logdate[m++] = Double.parseDouble(lvLogData.get(i).getmTemperature());
			}
		}
		for(int i = 0; i < m; i++){
			for(int j = i;j < m-1; j++){
				if(logdate[i] < logdate[j+1]){
					Double temp = logdate[i];
					logdate[i] = logdate[j+1];
					logdate[j+1] = temp;
				}
			}
		}
		
		if(m-1 > 0){
			date[0] = logdate[0];
			date[1] = logdate[m-1];
		}else{
			result = -1;
		}
		return result;
	}
	private void parseGetByAidXml(Element element,List<DeviceLog> node){
		Element e = null;
		while ((e = element.getChild("node")) != null) {
			String value = e.getChild("val") != null ? e.getChild("val")
					.getValue() : "";
			String time = e.getChild("day") != null ? e.getChild(
					"day").getValue() : "";
		
			DeviceLog n = new DeviceLog(value, time);
			node.add(n);
			element.removeChild("node");
		}
		if ((e = element.getChild("list")) != null) {
			parseGetByAidXml(e, node);
			element.removeChild("list");
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//��onCreate()�����е���setHasOptionsMenu()������ϵͳ�������Fragment��onCreateOptionsMenu()����
		setHasOptionsMenu(true);
		appContext = (AppContext) getActivity().getApplication();
		mBackColor = getResources().getColor(R.color.transparent);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETAVGVAL);
		appContext.registerReceiver(receiver, filter);
	}
	//onCreateView�Ǵ�����fragment��Ӧ����ͼ
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mTemLogFragmentView = inflater.inflate(R.layout.dev_detail_curve_fragment, container,false);
		layout = (LinearLayout)mTemLogFragmentView.findViewById(R.id.linearlayoutCurve);
		mButtonWeekCurve = (Button)mTemLogFragmentView.findViewById(R.id.ButtonWeekCurve);
		mButtonWeekCurve.setOnClickListener(this);
		mButtonWeekCurve.setTag(CLICK_BUTTONWEEK_CURVE);
		mButtonMonthCurve = (Button)mTemLogFragmentView.findViewById(R.id.ButtonMonthCurve);
		mButtonMonthCurve.setOnClickListener(this);
		mButtonMonthCurve.setTag(CLICK_BUTTONMONTH_CURVE);
		mLoadingView = mTemLogFragmentView.findViewById(R.id.dev_detail_curve_loading_linearLayout);
		
		return mTemLogFragmentView;
	}
	//��activity��onCreate()��������ʱ���á�
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		dataset1 = new XYMultipleSeriesDataset();
		series1 = new TimeSeries("Demo series ");
		onDevLogListupdate(WebClient.Method_getAvgVal,GetCurveTimeInterval(WEEKSTYLE));
	}
	@SuppressLint("SimpleDateFormat")
	private void onDevLogListupdate(String logMethod ,Map<String, String> param) {
		mLoadingView.setVisibility(View.VISIBLE);
		WebClient client = WebClient.getInstance(appContext);
		client.sendMessage(appContext, logMethod, param);
	}
	
	@SuppressLint("SimpleDateFormat") 
	private Map<String, String> GetCurveTimeInterval(int TimeStyle){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(new Date());
		mCurveStyle = TimeStyle;
		if(TimeStyle == WEEKSTYLE){
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-6);	
		}
		else if(TimeStyle == MONTHSTYLE){
			calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-29);	
		}
		Date date = calendar.getTime();
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String endTime = mSimpleDateFormat.format(new Date());
		String startTime = mSimpleDateFormat.format(date);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("MACID", DevDetailActivity.DevDetailMacID);
		param.put("STARTTIME", startTime);
		param.put("ENDTIME", endTime);
		return param;
	}
   @SuppressWarnings("unused")
   private void updateChart() {
	    //�趨����Ϊ20
	    int length = series1.getItemCount();
	    if(length>=20) length = 20;
	    addY=random.nextInt()%10;
	    addX=new Date().getTime();
	    
	    //��ǰ��ĵ���뻺��
		for (int i = 0; i < length; i++) {
			xcache[i] =  new Date((long)series1.getX(i));
			ycache[i] = (int) series1.getY(i);
		}
	    
		series1.clear();
		//���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��
		//�����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�
		series1.add(new Date(addX), addY);
		for (int k = 0; k < length; k++) {
    		series1.add(xcache[k], ycache[k]);
    	}
		//�����ݼ�������µĵ㼯
		dataset1.removeSeries(series1);
		dataset1.addSeries(series1);
		//���߸���
		chart.invalidate();
    }
	/**
	 * �趨�����ʽ
	 * @return
	 */
	   private XYMultipleSeriesRenderer getDemoRenderer(double YAxisMax,double YAxisMin) {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setGridColor(getResources().getColor(R.color.theme_blue_option_color)); //���÷�����ɫ
		    renderer.setChartTitleTextSize(DimenUtils.sp2px(appContext, 15));
//		    renderer.setXTitle("ʱ��");    //x��˵��
//		    renderer.setAxisTitleTextSize(16);
		    renderer.setAxesColor(getResources().getColor(R.color.theme_blue_color));
		    renderer.setLabelsTextSize(DimenUtils.sp2px(appContext, 12));    //����̶������С
		    renderer.setLabelsColor(getResources().getColor(R.color.theme_blue_color));
		    renderer.setLegendTextSize(15);    //����˵��
		    renderer.setXLabelsColor(getResources().getColor(R.color.theme_blue_color));
		    renderer.setYLabelsColor(0,getResources().getColor(R.color.theme_blue_color));
		    renderer.setShowLegend(false);
		    renderer.setMargins(new int[] {20, 30, 100, 0});
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.GREEN);
		    r.setChartValuesTextSize(15);
		    r.setChartValuesSpacing(3);
		    r.setPointStyle(PointStyle.CIRCLE);
		    r.setLineWidth(getResources().getDimension(R.dimen.curveLineWidth));
		    r.setFillBelowLine(true);
		    r.setFillBelowLineColor(mBackColor);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    renderer.setMarginsColor(getResources().getColor(R.color.white));
		    renderer.setShowGrid(true);
		    renderer.setYAxisMax(YAxisMax);
		    renderer.setYAxisMin(YAxisMin);
		    renderer.setInScroll(true);  //������С
		    renderer.setZoomButtonsVisible(true);
		    return renderer;
		  }
	   /**
	    * ���ݶ���
	    * @return
	    */
	   @SuppressLint("SimpleDateFormat")
	private XYMultipleSeriesDataset getDateDemoDataset(List<DeviceLog> xDeviceLog) {

			series1.clear();
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			for (int k = 0; k < xDeviceLog.size(); k++) {
				String Tem = xDeviceLog.get(k).getmTemperature();
				if(!Tem.equals("null")){
					Double f = Double.parseDouble(Tem);
					try {
						Date date = mSimpleDateFormat.parse(xDeviceLog.get(k).getmTime());
						series1.add(date,f);
						Log.i("huwei", "date = "+mSimpleDateFormat.format(date) + "tem = "+f);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				Log.i("huwei", k+" = "+Tem);
			}
			dataset1.removeSeries(series1);
			dataset1.addSeries(series1);
			
			Log.i(TAG, dataset1.toString());
			return dataset1;
	  }
	    @Override
	    public void onDestroy() {
	    	super.onDestroy();
	    }
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int tag = (Integer) v.getTag();
			switch (tag) {
			case CLICK_BUTTONWEEK_CURVE:
			{
				layout.removeView(chart);
				mButtonWeekCurve.setTextColor(getResources().getColor(R.color.theme_curve_green_color));
				mButtonWeekCurve.setTextSize(20);
				mButtonMonthCurve.setTextSize(15);
				mButtonMonthCurve.setTextColor(getResources().getColor(R.color.theme_gray_text_color));
				Double [] date = new Double[2];
				if(getMaxMinValue(lvLogDataWeek,date) == 0){
					chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataWeek), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
					layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				}else{
					Log.i("huwei", "����û������");
				}
			}
				break;
			case CLICK_BUTTONMONTH_CURVE:
			{
				layout.removeView(chart);
				mButtonWeekCurve.setTextSize(15);
				mButtonMonthCurve.setTextSize(20);
				mButtonWeekCurve.setTextColor(getResources().getColor(R.color.theme_gray_text_color));
				mButtonMonthCurve.setTextColor(getResources().getColor(R.color.theme_curve_green_color));
				if(!mIsInitMonth){
					onDevLogListupdate(WebClient.Method_getAvgVal,GetCurveTimeInterval(MONTHSTYLE));
				}
				else{
					Double [] date = new Double[2];
					if(getMaxMinValue(lvLogDataMonth,date) == 0){
						chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataMonth), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
						layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));	
					}else{
						Log.i("huwei", "����û������");
					}

				}
			}
				break;
			default:
				break;
			}
		};


}