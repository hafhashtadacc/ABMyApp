package ir.abmyapp.androidsdk;

import android.content.Context;

public class ABResources {

    private static IABResources mABResources = null;

    public static void init(Context context, String apiToken, ABConfig config) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null.");
        }

        if (apiToken == null) {
            throw new IllegalArgumentException("API token is required.");
        }

        if (config == null) {
            config = new ABConfig.Builder().build();
        }

        mABResources = new ABResourcesImp(context, apiToken, config);
    }

    public static IABResources get() {
        if (mABResources == null) {
            throw new IllegalStateException("ABResources is not initialized.");
        }

        return mABResources;
    }

}
