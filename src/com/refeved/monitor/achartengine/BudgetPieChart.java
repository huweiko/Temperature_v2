/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.refeved.monitor.achartengine;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class BudgetPieChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Budget chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The budget per project for this year (pie chart)";
  }

@Override
public GraphicalView execute(Context context,int[] colors) {
//    int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN };
    double[] values = new double[] {0,0,0,0,0};
    for(int i = 0; i<5;i++){
    	if(colors[i] != 0){
    		values[i] = 1;
    	}
    }
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setChartTitleTextSize(20);
    renderer.setZoomEnabled(false);//����������
    renderer.setShowLabels(false);//����ʾ����
    renderer.setPanEnabled(false);//�������ƶ�
    return ChartFactory.getPieChartView(context, buildCategoryDataset("Project budget", values),
        renderer);
}

@Override
public GraphicalView execute(Context context, List<double[]> values) {
	// TODO Auto-generated method stub
	return null;
}

}
