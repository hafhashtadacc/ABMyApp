package ir.abmyapp.androidsdk;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

class CachedResources {

    private static final String PREFERENCES = "ir.abmyapp.androidsdk.CachedResources";

    private static final String KEY_UPDATE_TIME = "__update_time";

    private SharedPreferences mPrefs;

    private ResourceData mData;

    CachedResources(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        reloadData();
    }

    long getLastUpdateTime() {
        return mPrefs.getLong(KEY_UPDATE_TIME, 0);
    }

    void storeNewData(ResourceData data) {
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.clear();

        for (Map.Entry<String, JSONObject> entry: data.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue().toString());
        }

        editor.putLong(KEY_UPDATE_TIME, System.currentTimeMillis());

        editor.apply();
    }

    void reloadData() {
        Map<String, ?> rawData = mPrefs.getAll();

        mData = new ResourceData();

        for (Map.Entry<String, ?> entry: rawData.entrySet()) {
            String key = entry.getKey();

            if (KEY_UPDATE_TIME.equals(key)) {
                continue;
            }

            String value = (String) entry.getValue();

            try {
                mData.add(entry.getKey(), new JSONObject(value));
            } catch (JSONException e) { }
        }
    }

    String get(String key) {
        return mData.getValue(key);
    }

    String getExperiment(String key) {
        return mData.getExperiment(key);
    }

}
