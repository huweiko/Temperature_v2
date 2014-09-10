package com.refeved.monitor.ui;
 
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
 
public class CustomImageView extends ImageView {
 
    private int co;
    private int borderwidth;
    public CustomImageView(Context context) {
        super(context);
    }
    public CustomImageView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //������ɫ
    public void setColour(int color){
        co = color;
    }
    //���ñ߿���
    public void setBorderWidth(int width){
         
        borderwidth = width;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // ���߿�
        Rect rec = canvas.getClipBounds();
        rec.bottom--;
        rec.right--;
        Paint paint = new Paint();
        //���ñ߿���ɫ
        paint.setColor(co);
        paint.setStyle(Paint.Style.STROKE);
        //���ñ߿���
        paint.setStrokeWidth(borderwidth);
        canvas.drawRect(rec, paint);
    }
}