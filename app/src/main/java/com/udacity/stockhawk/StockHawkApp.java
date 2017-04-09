package com.udacity.stockhawk;

import android.app.Application;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class StockHawkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(getBaseContext())
        );

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        // To view DB, insert chrome://inspect to Chrome Url bar and open Web SQL tab
        Stetho.initialize(initializer);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }
}
