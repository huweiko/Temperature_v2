package com.refeved.monitor.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
	private boolean enabled;
	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = false;
	}

	public void setScanScroll(boolean isCanScroll) {
	}

	@Override
	public void scrollTo(int x, int y) {
			super.scrollTo(x, y);
	}

	//����û�з�Ӧ�Ϳ�����
    @Override
     public boolean onTouchEvent(MotionEvent event) {
         if (this.enabled) {
             return super.onTouchEvent(event);
         }
   
         return false;
     }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentItem(int item) {
		// TODO Auto-generated method stub
		super.setCurrentItem(item);
	}
}