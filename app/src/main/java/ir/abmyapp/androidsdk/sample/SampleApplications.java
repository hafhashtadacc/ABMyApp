package ir.abmyapp.androidsdk.sample;

import android.app.Application;
import android.util.Log;

import ir.abmyapp.androidsdk.ABConfig;
import ir.abmyapp.androidsdk.ABResources;
import ir.abmyapp.androidsdk.IABResources;

public class SampleApplications extends Application {

    private static final String DOMAIN = "http://95.216.119.76:8080/";

    @Override
    public void onCreate() {
        super.onCreate();

        ABResources.setConfiguration(
                new ABConfig.Builder()
                        .setDomain(DOMAIN)
                        .setDebug(true)
                        .build()
        );

        ABResources.get(this).fetch(new IABResources.OnFetchResultCallback() {

            @Override
            public void onFetchResult(IABResources.FetchResult result) {
                if (result.isSuccessful()) {
                    Log.i(MainActivity.TAG, "Fetch successful.");

                    result.activateNow();
                }
                else {
                    Log.i(MainActivity.TAG, "Fetch failed.");
                }
            }

        });
    }

}
