package ir.abmyapp.androidsdk;

import android.content.Context;

public class ABResources {

    private static ABConfig mConfig = null;

    private static IABResources mABResources = null;

    public static void setConfiguration(ABConfig config) {
        mConfig = config;

        mABResources = null;
    }

    public static IABResources get(Context context) {
        if (mABResources == null) {
            if (mConfig == null) {
                mConfig = new ABConfig.Builder().build();
            }

            mABResources = new ABResourcesImp(context, mConfig);
        }

        return mABResources;
    }

}
