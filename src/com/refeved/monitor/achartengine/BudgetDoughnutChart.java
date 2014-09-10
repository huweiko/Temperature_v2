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

import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;

import android.content.Context;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class BudgetDoughnutChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Budget chart for several years";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The budget per project for several years (doughnut chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public GraphicalView execute(Context context,List<double[]> values) {
//    List<double[]> values = new ArrayList<double[]>();
//    values.add(new double[] { 12, 14, 11, 10, 19 });
//    values.add(new double[] { 10, 9, 14, 20, 11 });
    List<String[]> titles = new ArrayList<String[]>();
//    titles.add(new String[] { "P1", "P2", "P3", "P4", "P5" });
    titles.add(new String[] { "Normal", "NetworkBreak", "Alarm", "Other"});
    int NormalColor = context.getResources().getColor(R.color.theme_doughnut_normal_color);
    int NetworkBreakColor = context.getResources().getColor(R.color.theme_doughnut_networkbreak_color);
    int AlarmColor = context.getResources().getColor(R.color.theme_doughnut_alarm_color);
    int[] colors = new int[] { NormalColor, NetworkBreakColor, AlarmColor,  Color.GRAY};
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setApplyBackgroundColor(true);
    renderer.setZoomEnabled(false);//不允许缩放
    renderer.setShowLabels(false);//不显示标题
    renderer.setPanEnabled(false);//不允许移动
    renderer.setScale(1.4f);
    renderer.setLegendHeight(1);
    renderer.setBackgroundColor(context.getResources().getColor(R.color.theme_blue_color));
    return ChartFactory.getDoughnutChartView(context,
        buildMultipleCategoryDataset("Project budget",titles , values), renderer);
  }

@Override
public GraphicalView execute(Context context, int[] color) {
	// TODO Auto-generated method stub
	return null;
}


}
