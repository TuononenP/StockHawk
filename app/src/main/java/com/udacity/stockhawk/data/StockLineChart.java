package com.udacity.stockhawk.data;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;

import java.util.ArrayList;

/**
 * Created by Petri Tuononen on 17-Apr-17.
 */

public class StockLineChart {

    private Context mContext;
    private LineChart mLineChart;

    public StockLineChart(Context context, LineChart lineChart) {
        mContext = context;
        mLineChart = lineChart;
        setData();
        setChartCustomizations();
    }

    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(60, 0));
        yVals.add(new Entry(48, 1));
        yVals.add(new Entry(70.5f, 2));
        yVals.add(new Entry(100, 3));
        yVals.add(new Entry(180.9f, 4));

        return yVals;
    }

    private void setData() {
        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet set1;

        set1 = new LineDataSet(yVals, "DataSet 1");
        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        LineData data = new LineData(set1);

        mLineChart.setData(data);

    }

    private void setChartCustomizations() {
        Description desc = new Description();
        desc.setText("");
        mLineChart.setDescription(desc);
        mLineChart.setNoDataText(mContext.getString(R.string.chart_no_data));

        // enable touch
        mLineChart.setTouchEnabled(true);

        // enable dragging
        mLineChart.setDragEnabled(true);

        // enable scaling
        mLineChart.setScaleEnabled(true);

        // disable grid background
        mLineChart.setDrawGridBackground(false);

        // enable pinch zoom
        mLineChart.setPinchZoom(true);

        // set background
        mLineChart.setBackgroundColor(Color.LTGRAY);
    }
}
