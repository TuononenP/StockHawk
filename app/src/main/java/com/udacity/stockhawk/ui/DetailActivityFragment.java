package com.udacity.stockhawk.ui;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockLineChart;
import com.udacity.stockhawk.data.YahooFinanceHelper;

import java.util.ArrayList;

/**
 * Fragment containing stock line chart.
 */
public class DetailActivityFragment extends Fragment {

    private static final String CLICKED_SYMBOL = "clicked_symbol";
    private LineChart mChart;
    private StockLineChart mStockLineChart;
    private String mSymbol;
    private Cursor mCursor;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(CLICKED_SYMBOL)) {
            String symbol = intent.getStringExtra(CLICKED_SYMBOL);
            if (symbol.isEmpty() == false) {
                mSymbol = symbol;
                Cursor cursor = getActivity().getContentResolver()
                        .query(Contract.Quote.makeUriForStock(mSymbol), null, null, null, null);
                mCursor = cursor;
                if (mCursor != null) {
                    if (mCursor != null && mCursor.moveToFirst()) {
                        String history = mCursor.getString(Contract.Quote.POSITION_HISTORY);
                        ArrayList<Entry> entries = YahooFinanceHelper.parseHistory(history);
                        mChart = (LineChart) getActivity().findViewById(R.id.stock_line_chart);
                        mStockLineChart = new StockLineChart(getActivity(), mChart,
                                entries, mSymbol);
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail,
                container, false);

        return view;
    }

}
