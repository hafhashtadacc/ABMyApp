package ir.abmyapp.androidsdk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

class ABResourcesImp implements IABResources {

    private static final FetchResult NOT_FETCH_TIME_RESULT = new FetchResult() {

        @Override
        public boolean isSuccessful() {
            return true;
        }

        @Override
        public void activateNow() { }

    };

    private static final FetchResult UNSUCCESSFUL_FETCH_RESULT = new FetchResult() {

        @Override
        public boolean isSuccessful() {
            return false;
        }

        @Override
        public void activateNow() { }

    };

    private Context mContext;
    private Resources mResources;

    private Handler mHandler;

    private ABConfig mConfig;

    private CachedResources mCachedResources;
    private CachedEvents mCachedEvents;
    private CachedProfile mCachedProfile;

    private ABApi mApi;

    private boolean mIsGettingResources = false;
    private boolean mIsUploadingEvents = false;

    ABResourcesImp(Context context, ABConfig config) {
        mContext = context.getApplicationContext();
        mResources = mContext.getResources();

        mHandler = new Handler();

        mConfig = config;

        mCachedResources = new CachedResources(mContext);
        mCachedEvents = new CachedEvents(mContext);
        mCachedProfile = new CachedProfile(mContext);

        mApi = new ABApi(mContext, mConfig.getDomain(), mConfig.isDebug(), mConfig.getTaskManager(), mConfig.getBackgroundOfficeSection());
    }

    @Override
    public void fetch(final OnFetchResultCallback callback) {
        long lastFetchTime = mCachedResources.getLastUpdateTime();

        if (!mIsGettingResources && mConfig.canFetch(lastFetchTime)) {
            mIsGettingResources = true;

            mApi.getResources(mCachedProfile.getTags(), new ABApi.OnGetResourcesResultCallback() {

                @Override
                public void onGetResourcesResult(ABApi.GetResourcesResult result) {
                    mIsGettingResources = false;

                    if (result.isSuccessful()) {
                        mCachedResources.storeNewData(result.getData());

                        callback.onFetchResult(new FetchResult() {

                            @Override
                            public boolean isSuccessful() {
                                return true;
                            }

                            @Override
                            public void activateNow() {
                                mCachedResources.reloadData();
                            }

                        });
                    }
                    else {
                        callback.onFetchResult(UNSUCCESSFUL_FETCH_RESULT);
                    }
                }

            });
        }
        else {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    callback.onFetchResult(NOT_FETCH_TIME_RESULT);
                }

            });
        }
    }

    @Override
    public void fetchAndActivate() {
        fetch(new OnFetchResultCallback() {

            @Override
            public void onFetchResult(FetchResult result) {
                if (result.isSuccessful()) {
                    result.activateNow();
                }
            }

        });
    }

    @Override
    public void setTag(String tag, String value) {
        mCachedProfile.addTag(tag, value);
    }

    @Override
    public String getString(String key) {
        String value = mCachedResources.get(key);

        if (value != null) {
            mCachedEvents.recordImpression(mCachedResources.getExperiment(key));

            checkForEventUpload();
        }

        return value;
    }

    @Override
    public Integer getInt(String key) {
        String value = getString(key);

        return value == null ? null : Integer.parseInt(value);
    }

    @Override
    public Integer getColor(String key) {
        String value = getString(key);

        return value == null ? null : Color.parseColor(value);
    }

    @Override
    public Float getFloat(String key) {
        String value = getString(key);

        return value == null ? null : Float.parseFloat(value);
    }

    @Override
    public Boolean getBoolean(String key) {
        String value = getString(key);

        return value == null ? null : Boolean.parseBoolean(value);
    }

    @Override
    public Float getDimen(String key) {
        String value = getString(key);

        return value == null ? null : DimensionConverter.stringToDimension(value, mResources.getDisplayMetrics());
    }

    @Override
    public Integer getDimenPixelSize(String key) {
        String value = getString(key);

        return value == null ? null : DimensionConverter.stringToDimensionPixelSize(value, mResources.getDisplayMetrics());
    }

    @Override
    public Drawable getDrawable(String key) {
        return null; // TODO
    }

    private String getResourceName(int resId) {
        return mResources.getResourceTypeName(resId) + "/" + mResources.getResourceEntryName(resId);
    }

    @Override
    public String getString(int resId) {
        String string = getString(getResourceName(resId));

        if (string == null) {
            string = mResources.getString(resId);
        }

        return string;
    }

    @Override
    public int getInt(int resId) {
        Integer value = getInt(getResourceName(resId));

        if (value == null) {
            value = mResources.getInteger(resId);
        }

        return value;
    }

    @Override
    public int getColor(int resId) {
        Integer value = getColor(getResourceName(resId));

        if (value == null) {
            value = ContextCompat.getColor(mContext, resId);
        }

        return value;
    }

    @Override
    public float getFloat(int resId) {
        Float value = getFloat(getResourceName(resId));

        if (value == null) {
            TypedValue outValue = new TypedValue();
            mResources.getValue(resId, outValue, true);
            value = outValue.getFloat();
        }

        return value;
    }

    @Override
    public boolean getBoolean(int resId) {
        Boolean value = getBoolean(getResourceName(resId));

        if (value == null) {
            value = mResources.getBoolean(resId);
        }

        return value;
    }

    @Override
    public float getDimen(int resId) {
        Float value = getDimen(getResourceName(resId));

        if (value == null) {
            value = mResources.getDimension(resId);
        }

        return value;
    }

    @Override
    public int getDimenPixelSize(int resId) {
        Integer value = getDimenPixelSize(getResourceName(resId));

        if (value == null) {
            value = mResources.getDimensionPixelSize(resId);
        }

        return value;
    }

    @Override
    public Drawable getDrawable(int resId) {
        Drawable value = getDrawable(getResourceName(resId));

        if (value == null) {
            value = ResourcesCompat.getDrawable(mResources, resId, null);
        }

        return value;
    }

    @Override
    public void recordEvent(String event) {
        mCachedEvents.recordEvent(event);

        checkForEventUpload();
    }

    @Override
    public void recordEvent(String key, String event) {
        String experiment = mCachedResources.getExperiment(key);

        if (experiment != null) {
            mCachedEvents.recordEvent(experiment, event);
        }
    }

    private void checkForEventUpload() {
        if (mConfig.canUploadEvents(mCachedEvents.getPendingEventCount(), mCachedEvents.getLastEventUploadTime())) {
            if (mIsUploadingEvents) {
                return;
            }

            mIsUploadingEvents = true;

            mApi.sendEvents(mCachedEvents.getEvents(), new ABApi.OnSendEventsResultCallback() {

                @Override
                public void onSendEventsResult(ABApi.SendEventsResult result) {
                    mIsUploadingEvents = false;

                    if (result.isSuccessful()) {
                        mCachedEvents.onEventsPushed(result.getEvents());

                        checkForEventUpload();
                    }
                }

            });
        }
    }

}
