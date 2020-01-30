package ir.abmyapp.androidsdk.sample;

import android.app.Application;
import android.util.Log;

import ir.abmyapp.androidsdk.ABConfig;
import ir.abmyapp.androidsdk.ABResources;
import ir.abmyapp.androidsdk.IABResources;

public class SampleApplications extends Application {

    private static final String API_TOKEN = "19ede3f2a810417882ecd8e71da86c61";

    @Override
    public void onCreate() {
        super.onCreate();

        ABConfig abConfig = new ABConfig.Builder()
                .setDebug(true)
                .build();

        ABResources.init(this, API_TOKEN, abConfig);

        ABResources.get().fetch(new IABResources.OnFetchResultCallback() {

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
