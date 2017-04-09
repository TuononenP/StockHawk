package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.data.YahooFinanceHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private static YahooFinanceHelper _yahooFinanceHelper;

    private QuoteSyncJob(Context context) {
        _yahooFinanceHelper = new YahooFinanceHelper(context);
    }

    static void addQuote(Context context, String symbol) {
        Stock stock = _yahooFinanceHelper.GetSingleQuote(symbol);
        if (stock != null) {
            ContentValues values = _yahooFinanceHelper.GetStockContentValues(stock);
            context.getContentResolver()
                    .insert(Contract.Quote.URI, values);
        }
        else {
            Timber.d(context.getString(R.string.log_quote_not_added));
        }
    }

    static void addQuotes(Context context) {
        Timber.d(context.getString(R.string.log_running_sync_job));

        Set<String> stockPref = PrefUtils.getStocks(context);
        Set<String> stockCopy = new HashSet<>();
        stockCopy.addAll(stockPref);
        String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

        Timber.d(stockCopy.toString());

        if (stockArray.length == 0) {
            return;
        }

        ArrayList<ContentValues> quoteCVs = new ArrayList<>();
        Map<String, Stock> quotes = _yahooFinanceHelper.GetMultipleQuotes(stockArray);
        if (null != quotes) {
            for (Map.Entry<String, Stock> entry : quotes.entrySet()) {
                quoteCVs.add(_yahooFinanceHelper.GetStockContentValues(entry.getValue()));
            }
        }

        context.getContentResolver()
                .bulkInsert(
                        Contract.Quote.URI,
                        quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
    }

    private static void schedulePeriodic(Context context) {
        Timber.d(context.getString(R.string.log_scheduling_periodic_task));

        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }

    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
        syncImmediately(context);
    }

    public static synchronized void syncImmediately(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());
        }
    }

}
