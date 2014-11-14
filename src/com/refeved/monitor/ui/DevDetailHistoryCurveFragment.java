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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.BasicStroke;
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
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.DeviceLog;
import com.refeved.monitor.util.DimenUtils;
public class DevDetailHistoryCurveFragment extends Fragment implements OnClickListener{

	private List<DeviceLog> lvLogDataWeek = new ArrayList<DeviceLog>();
	private List<DeviceLog> lvLogDataMonth = new ArrayList<DeviceLog>();
    private GraphicalView chart;
	/**曲线数量*/
    private static final String TAG = "message";
	protected static final int HANDLER_DOMBUILD_XML = 1;
	private static final int CLICK_BUTTONWEEK_CURVE = 0;
	private static final int CLICK_BUTTONMONTH_CURVE = 1;
	private static final int WEEKSTYLE = 1;
	private static final int MONTHSTYLE = 2;
    /**时间数据*/
    Date[] xcache = new Date[20];
	/**数据*/
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
						//SAXBuilder是一个JDOM解析器 能将路径中的XML文件解析为Document对象
						SAXBuilder builder = new SAXBuilder();
						StringReader sr = new StringReader(resXml);   
						InputSource is = new InputSource(sr); 
						Document Doc = builder.build(is);
						Element rootElement = (Element) Doc.getRootElement();
						if(mCurveStyle == WEEKSTYLE){
							parseGetByAidXml(rootElement, lvLogDataWeek);
							Double [] date = new Double[2];
							if(getMaxMinValue(lvLogDataWeek,date) == 0){
								date[0] = (date[0]>DevDetailActivity.WarningLine[0]?date[0]:DevDetailActivity.WarningLine[0]);
								date[1] = (date[1]<DevDetailActivity.WarningLine[1]?date[1]:DevDetailActivity.WarningLine[1]);
								chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataWeek), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
							
							}else{
								Log.i("huwei", "本周没有数据");
							}
						}
						else{
							parseGetByAidXml(rootElement, lvLogDataMonth);	
							Double [] date = new Double[2];
							
							if(getMaxMinValue(lvLogDataMonth,date) == 0){
								date[0] = (date[0]>DevDetailActivity.WarningLine[0]?date[0]:DevDetailActivity.WarningLine[0]);
								date[1] = (date[1]<DevDetailActivity.WarningLine[1]?date[1]:DevDetailActivity.WarningLine[1]);
								chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataMonth), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
							}else{
								Log.i("huwei", "本月没有数据");
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
				Log.i("huwei", "收到曲线数据");
				mLoadingView.setVisibility(View.GONE);
				if(resXml != null)
				{
					if(resXml.equals("error") || resXml.equals("null"))
					{
						UIHealper.DisplayToast(appContext,"获取设备信息失败");
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
	//求给定队列温度的最大值和最小值
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
		//在onCreate()方法中调用setHasOptionsMenu()，否则系统不会调用Fragment的onCreateOptionsMenu()方法
		setHasOptionsMenu(true);
		appContext = (AppContext) getActivity().getApplication();
		mBackColor = getResources().getColor(R.color.transparent);
		IntentFilter filter = new IntentFilter();
		filter.addAction(WebClient.INTERNAL_ACTION_GETAVGVAL);
		appContext.registerReceiver(receiver, filter);
	}
	//onCreateView是创建该fragment对应的视图
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
	//当activity的onCreate()方法返回时调用。
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

		onDevLogListupdate(WebClient.Method_getAvgVal,GetCurveTimeInterval(WEEKSTYLE));
	}
	@SuppressLint("SimpleDateFormat")
	private void onDevLogListupdate(String logMethod ,Map<String, String> param) {
		Log.i("huwei", "开始获取曲线数据");
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
	/**
	 * 设定如表样式
	 * @return
	 */
	   private XYMultipleSeriesRenderer getDemoRenderer(double YAxisMax,double YAxisMin) {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setXTitle("时间");
		    if(DevDetailActivity.mDeviceType.equals(Device.Type_Frige)){
		    	renderer.setYTitle("温度（℃）");
		    }else if(DevDetailActivity.mDeviceType.equals(Device.Type_Humidity)){
		    	renderer.setYTitle("湿度（%）");
		    }
		    
		    renderer.setGridColor(getResources().getColor(R.color.theme_blue_option_color)); //设置方格颜色
		    renderer.setChartTitleTextSize(DimenUtils.sp2px(appContext, 15));
		    renderer.setAxesColor(getResources().getColor(R.color.theme_blue_color));
		    renderer.setLabelsTextSize(DimenUtils.sp2px(appContext, 12));    //数轴刻度字体大小
		    renderer.setLabelsColor(getResources().getColor(R.color.theme_blue_color));
		    renderer.setLegendTextSize(15);    //曲线说明
		    renderer.setXLabelsColor(getResources().getColor(R.color.theme_blue_color));
		    renderer.setYLabelsColor(0,getResources().getColor(R.color.theme_blue_color));
		    renderer.setShowLegend(true);//开启曲线说明
		    renderer.setZoomEnabled(false);//不允许缩放
		    renderer.setPanEnabled(false);//不允许移动
		    renderer.setClickEnabled(false);
		    renderer.setMargins(new int[] {20, 30, 80, 20});
		    int[] colorValue = {Color.GREEN,Color.RED,Color.RED};
		    float[] FontSize = {3,2,2};
		    BasicStroke[] LineType = {BasicStroke.SOLID,BasicStroke.SOLID,BasicStroke.SOLID};
		    PointStyle[] PointType = {PointStyle.CIRCLE,PointStyle.POINT,PointStyle.POINT};
		    for(int i = 0;i < 3;i++){
			    XYSeriesRenderer r = new XYSeriesRenderer();
			    r.setColor(colorValue[i]);
			    r.setChartValuesTextSize(15);
			    r.setChartValuesSpacing(3);
			    r.setPointStyle(PointType[i]);
			    r.setLineWidth(FontSize[i]);
			    r.setFillBelowLine(true);
			    r.setFillBelowLineColor(mBackColor);
			    r.setFillPoints(true);
			    r.setStroke(LineType[i]);
			    renderer.addSeriesRenderer(r);
		    }

		    renderer.setMarginsColor(getResources().getColor(R.color.white));
		    renderer.setShowGrid(true);
		    renderer.setYAxisMax(YAxisMax);
		    renderer.setYAxisMin(YAxisMin);
		    renderer.setInScroll(false);  //调整大小

		    return renderer;
		  }
	   /**
	    * 数据对象
	    * @return
	    */
	   @SuppressLint("SimpleDateFormat")
	private XYMultipleSeriesDataset getDateDemoDataset(List<DeviceLog> xDeviceLog) {

		    XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
			TimeSeries series1 = new TimeSeries(getString(R.string.Curve_avarageValue));
			TimeSeries SeriesAbove = new TimeSeries(getString(R.string.Curve_cordon_above));
			TimeSeries SeriesBelow= new TimeSeries(getString(R.string.Curve_cordon_below));
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			for (int k = 0; k < xDeviceLog.size(); k++) {
				String Tem = xDeviceLog.get(k).getmTemperature();
				if(!Tem.equals("null")){
					Double f = Double.parseDouble(Tem);
					try {
						Date date = mSimpleDateFormat.parse(xDeviceLog.get(k).getmTime());
						series1.add(date,f);
						SeriesAbove.add(date,DevDetailActivity.WarningLine[0]);
						SeriesBelow.add(date,DevDetailActivity.WarningLine[1]);
						Log.i("huwei", "date = "+mSimpleDateFormat.format(date) + "tem = "+f);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				Log.i("huwei", k+" = "+Tem);
			}
			dataset1.addSeries(series1);
			dataset1.addSeries(SeriesAbove);
			dataset1.addSeries(SeriesBelow);
			
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
					date[0] = (date[0]>DevDetailActivity.WarningLine[0]?date[0]:DevDetailActivity.WarningLine[0]);
					date[1] = (date[1]<DevDetailActivity.WarningLine[1]?date[1]:DevDetailActivity.WarningLine[1]);
					chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataWeek), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
					layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				}else{
					Log.i("huwei", "本周没有数据");
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
						date[0] = (date[0]>DevDetailActivity.WarningLine[0]?date[0]:DevDetailActivity.WarningLine[0]);
						date[1] = (date[1]<DevDetailActivity.WarningLine[1]?date[1]:DevDetailActivity.WarningLine[1]);
						chart = ChartFactory.getTimeChartView(appContext, getDateDemoDataset(lvLogDataMonth), getDemoRenderer(date[0]+10.0d,date[1] - 10.0d), "yyyy-MM-dd");
						layout.addView(chart, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));	
					}else{
						Log.i("huwei", "本月没有数据");
					}

				}
			}
				break;
			default:
				break;
			}
		};


}