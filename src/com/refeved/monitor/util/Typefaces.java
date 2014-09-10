package com.refeved.monitor.util;

import android.app.Activity; 
import android.content.Context; 
import android.graphics.Canvas; 
import android.graphics.Color; 
import android.graphics.Paint; 
import android.graphics.Typeface; 
import android.os.Bundle; 
import android.view.View; 

/**
 * 
*    
* ��Ŀ���ƣ�mip   
* �����ƣ�Typefaces   
* �������� ������ʽ
* �����ˣ�JIE
* ����ʱ�䣺2011-12-29 ����11:51:05   
* �޸��ˣ�Administrator   
* �޸�ʱ�䣺2011-12-29 ����11:51:05   
* �޸ı�ע��   
* @version    
*
 */
public class Typefaces extends Activity 
{ 
	/** Called when the activity is first created. */ 
	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState); 
	
		setContentView(new SampleView(this)); 
	} 
	
	private static class SampleView extends View 
	{ 
		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		private Typeface mFace; 
	
		public SampleView(Context context) 
		{
			super(context); 
			//ʵ�����Զ������� 
			mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/Clockopia.ttf"); 
			//���������С 
			mPaint.setTextSize(18); 
		} 
		
		@Override 
		protected void onDraw(Canvas canvas) 
		{
			canvas.drawColor(Color.WHITE); 
//			//����Ĭ������ 
//			mPaint.setTypeface(null); 
//			canvas.drawText("Default:abcdefg", 10, 100, mPaint); 
			//�����Զ������� 
			mPaint.setTypeface(mFace); 
			canvas.drawText("Custom:abcdefg", 10, 200, mPaint); 
		} 
	}
}