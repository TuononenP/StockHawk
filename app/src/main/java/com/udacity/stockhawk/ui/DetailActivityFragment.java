package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.StockLineChart;

/**
 * Fragment containing stock line chart.
 */
public class DetailActivityFragment extends Fragment {

    private static final String CLICKED_SYMBOL = "clicked_symbol";
    private LineChart mChart;
    private StockLineChart mStockLineChart;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChart = (LineChart) getActivity().findViewById(R.id.stock_line_chart);
        mStockLineChart = new StockLineChart(getActivity(), mChart);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail,
                container, false);

        return view;
    }

}
