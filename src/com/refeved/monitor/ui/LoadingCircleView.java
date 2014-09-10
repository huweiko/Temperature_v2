/*
 * �ļ�����MainActivity.java
 * ���ܣ���½��������棬�������
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-17
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Բ�μ��ؽ�����
 * 
 * @author way
 * 
 */
public class LoadingCircleView extends View {

	private final Paint paint;
	private final Context context;
	private Resources res;
	private int max = 100;
	private int progress = 0;
	private int ringWidth;
	// Բ������ɫ
	private int ringColor;
	// ��������ɫ
	private int progressColor;
	// ������ɫ
	private int textColor;
	// �ֵĴ�С
	private int textSize;

	private String textProgress;

	public LoadingCircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.paint = new Paint();
		this.res = context.getResources();
		this.paint.setAntiAlias(true); // �������
		this.ringWidth = dip2px(context, 3); // ����Բ�����
		this.ringColor = Color.BLACK;// ��ɫ����������
		this.progressColor = Color.WHITE;// ��ɫ������
		this.textColor = Color.BLACK;// ��ɫ������ʾ����;
		this.textSize = 15;// Ĭ�������С
	}

	public LoadingCircleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadingCircleView(Context context) {
		this(context, null);
	}

	/**
	 * ���ý��������ֵ
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0)
			max = 0;
		if (progress > max)
			progress = max;
		this.max = max;
	}

	/**
	 * ��ȡ���������ֵ
	 * 
	 * @return
	 */
	public synchronized int getMax() {
		return max;
	}

	/**
	 * ���ü��ؽ��ȣ�ȡֵ��Χ��0~֮��
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if (progress >= 0 && progress <= max) {
			this.progress = progress;
			invalidate();//����������
		}
	}

	/**
	 * ��ȡ��ǰ����ֵ
	 * 
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * ����Բ������ɫ
	 * 
	 * @param ringColor
	 */
	public void setRingColor(int ringColor) {
		this.ringColor = res.getColor(ringColor);
	}

	/**
	 * ���ý�������ɫ
	 * 
	 * @param progressColor
	 */
	public void setProgressColor(int progressColor) {
		this.progressColor = res.getColor(progressColor);
	}

	/**
	 * ����������ɫ
	 * 
	 * @param textColor
	 */
	public void setTextColor(int textColor) {
		this.textColor = res.getColor(textColor);
	}

	/**
	 * ���������С
	 * 
	 * @param textSize
	 */
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	/**
	 * ����Բ���뾶
	 * 
	 * @param ringWidth
	 */
	public void setRingWidthDip(int ringWidth) {
		this.ringWidth = dip2px(context, ringWidth);
	}

	/**
	 * ͨ�����ϻ����ķ�ʽ���½��棬ʵ�ֽ�������
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		int center = getWidth() / 2;
		int radios = center - ringWidth / 2;

		// ����Բ��
		this.paint.setStyle(Paint.Style.STROKE); // ���ƿ���Բ
		this.paint.setColor(ringColor);
		this.paint.setStrokeWidth(ringWidth);
		canvas.drawCircle(center, center, radios, this.paint);
		RectF oval = new RectF(center - radios, center - radios, center
				+ radios, center + radios);
		this.paint.setColor(progressColor);
		canvas.drawArc(oval, 90, 360 * progress / max, false, paint);
		canvas.drawArc(oval, 90, 360 * progress / max, false, paint);
		
		this.paint.setStyle(Paint.Style.FILL);
		this.paint.setColor(textColor);
		this.paint.setStrokeWidth(0);
		this.paint.setTextSize(textSize);
		this.paint.setTypeface(Typeface.DEFAULT_BOLD);
		textProgress = (int) (1000 * (progress / (10.0 * max))) + "%";
		float textWidth = paint.measureText(textProgress);
		canvas.drawText(textProgress, center - textWidth / 2, center + textSize
				/ 2, paint);

		super.onDraw(canvas);
	}

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
