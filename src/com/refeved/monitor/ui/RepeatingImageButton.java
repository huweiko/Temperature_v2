package com.refeved.monitor.ui;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class RepeatingImageButton extends Button {
    private long mStartTime; //��¼������ʼ
    private int mRepeatCount; //�ظ���������
    private RepeatListener mListener;
    private long mInterval = 500; //Timer�����������ÿ0.5����һ�ΰ���
   
    public RepeatingImageButton(Context context) {
        this(context, null);
    }
    public RepeatingImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.imageButtonStyle);
    }
    public RepeatingImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true); //�����ý���
        setLongClickable(true); //���ó����¼�
    }
   
    public void setRepeatListener(RepeatListener l, long interval) { //ʵ���ظ������¼�listener
        mListener = l;
        mInterval = interval;
    }
 
    @Override
    public boolean performLongClick() {
        mStartTime = SystemClock.elapsedRealtime();
        mRepeatCount = 0;
        post(mRepeater);
        return true;
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {  //  ������ԭ��ͬonKeyUp��һ�������ﴦ����Ļ�¼��������onKeyUp����Android�ֻ��ϵ��������¼�
              removeCallbacks(mRepeater);
            if (mStartTime != 0) {
                doRepeat(true);
                mStartTime = 0;
            }
        }
        return super.onTouchEvent(event);
    }
  //���������¼����м���켣�����¼�
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                super.onKeyDown(keyCode, event);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
  //����������֪ͨ��������
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
         
            removeCallbacks(mRepeater); //ȡ���ظ�listener����
            if (mStartTime != 0) {
                doRepeat(true); //��������¼��ۼ�ʱ�䲻Ϊ0��˵��������
                mStartTime = 0; //���ó�����ʱ��
            }
        }
        return super.onKeyUp(keyCode, event);
    }
 
    private Runnable mRepeater = new Runnable() {  //���߳����ж��ظ�
        public void run() {
            doRepeat(false);
            if (isPressed()) {
                postDelayed(this, mInterval); //���㳤�����ӳ���һ���ۼ�
            }
        }
    };
    private  void doRepeat(boolean last) {
        long now = SystemClock.elapsedRealtime();
        if (mListener != null) {
            mListener.onRepeat(this, now - mStartTime, last ? -1 : mRepeatCount++);
        }
    }
    
    public interface RepeatListener {
            void onRepeat(View v, long duration, int repeatcount); //����һΪ�û������Button���󣬲�����Ϊ�ӳٵĺ�����������λ�ظ������ص���
    }
}