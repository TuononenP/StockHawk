package com.udacity.stockhawk.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.udacity.stockhawk.R;

import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by Petri Tuononen on 09-Apr-17.
 */

public class YahooFinanceHelper {

    private static final int YEARS_OF_HISTORY = 2;

    private static Context mContext;

    public YahooFinanceHelper(Context context) {
        mContext = context;
    }

    @Nullable
    public static Stock getSingleQuote(String symbol) {
        Stock stock = null;
        try {
            stock = YahooFinance.get(symbol);
            Timber.d(stock.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // check if ticker does not exists
        if (null == stock || stock.isValid() == false) {
            return null;
        }
        else {
            return stock;
        }
    }

    @Nullable
    public static Map<String, Stock> getMultipleQuotes(String[] symbols) {
        if (symbols.length == 0) {
            return null;
        }
        try {
            Map<String, Stock> quotes = YahooFinance.get(symbols);
            Timber.d(quotes.toString());
            return quotes;
        } catch (IOException e) {
            e.printStackTrace();
            Timber.d(e.getMessage());
        }
        return null;
    }

    public static ContentValues getStockContentValues(Stock stock) {
        if (stock.isValid() == false) {
            return null;
        }
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        StockQuote quote = stock.getQuote();

        float price = quote.getPrice() != null ? quote.getPrice().floatValue() : 0;
        float change = quote.getChange() != null ? quote.getChange().floatValue() : 0;
        float percentChange = quote.getChangeInPercent() != null ?
                quote.getChangeInPercent().floatValue() : 0;

        // WARNING! Don't request historical data for a stock that doesn't exist!
        // The request will hang forever X_x
        List<HistoricalQuote> history = new ArrayList<HistoricalQuote>();
        try {
            history = stock.getHistory(from, to, Interval.WEEKLY);
        } catch (IOException e) {
            e.printStackTrace();
            Timber.d(e.getMessage());
        }

        StringBuilder historyBuilder = new StringBuilder();

        for (HistoricalQuote it : history) {
            historyBuilder.append(it.getDate().getTimeInMillis());
            historyBuilder.append(", ");
            historyBuilder.append(it.getClose());
            historyBuilder.append("\n");
        }

        ContentValues quoteCV = new ContentValues();
        quoteCV.put(Contract.Quote.COLUMN_SYMBOL, stock.getSymbol());
        quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
        quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
        quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
        quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());

        return quoteCV;
    }

    public static ArrayList<com.github.mikephil.charting.data.Entry> parseHistory(String history) {
        if (history == null || history == "") {
            return null;
        }

        ArrayList<Entry> entries = new ArrayList<>();
        String[] array = history.split("\n");
        for (String item : array) {
            String[] timeAndClose = item.split(", ");
            if (timeAndClose.length >= 2) {
                float timeInMillis = 0;
                try {
                    timeInMillis = Float.parseFloat(timeAndClose[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Timber.d("Could not convert time in millis to float.");
                }
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(timeInMillis);
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                String dateString = String.format("%1$d/%2$d/%3$d", month, day, year);
                float closePrice = 0;
                try {
                    closePrice = Float.parseFloat(timeAndClose[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Timber.d("Could not convert close price to float.");
                }
                entries.add(new Entry(timeInMillis, closePrice));
            }
        }
        return entries;
    }

    public static class CheckIfQuoteExistsAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... symbol) {
            try {
                Stock quote = (Stock) YahooFinance.get(symbol);
                if (quote.isValid()) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                String errorMessage = mContext.getString(R.string.symbol_not_found_in_yahoo, (Object) symbol);
                Timber.e(errorMessage);
            }
            return false;
        }
    }
}
