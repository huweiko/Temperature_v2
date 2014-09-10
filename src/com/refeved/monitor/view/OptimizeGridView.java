package com.refeved.monitor.view;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.refeved.monitor.adapter.GridAdapter;

public class OptimizeGridView extends GridViewBase {
    /**
     * 要用能包含重复元素的集合
     *
     * @param <T>
     */
    public interface OptimizeGridAdapter<T> {
        List<T> getItems();
        /**
         * Should notify the listView data is changed
         *
         * @param items
         */
        void setItems(List<T> items);
        void setColumn(int column);
        T getNullItem();
        boolean isNullItem(T item);
    }
    
    GridAdapter mAdapter;

    public OptimizeGridView(Context context) {
        super(context);
        
        mAdapter = new GridAdapter(context);
        this.setAdapter(mAdapter);
    }
    
    public OptimizeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mAdapter = new GridAdapter(context);
        this.setAdapter(mAdapter);
    }

    public OptimizeGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mAdapter = new GridAdapter(context);
        this.setAdapter(mAdapter);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
    		    MeasureSpec.AT_MOST);

    	super.onMeasure(widthMeasureSpec, expandSpec);
    }
    

	@SuppressWarnings("unchecked")
	@Override
    public void updateListView(@SuppressWarnings("rawtypes") List listItems) {
		// TODO Auto-generated method stub
    	mAdapter.setItems(listItems);
    	mAdapter.notifyDataSetChanged();
	}
    
    @SuppressWarnings("rawtypes")
	@Override
	public List getItems() {
		// TODO Auto-generated method stub
		return mAdapter.getItems();
	}

}
