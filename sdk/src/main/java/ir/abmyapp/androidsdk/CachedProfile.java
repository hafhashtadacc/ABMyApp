package ir.abmyapp.androidsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CachedProfile {

    private static final String PREFERENCES = "ir.abmyapp.androidsdk.CachedProfile";

    private static final String OS = "os";
    private static final String VERSION = "version";

    private static final String OS_ANDROID = "android";

    private SharedPreferences mPrefs;

    CachedProfile(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        fillDefaults(context);
    }

    private void fillDefaults(Context context) {
        if (mPrefs.getString(OS, null) != null) {
            return;
        }

        mPrefs.edit()
                .putString(OS, OS_ANDROID)
                .putString(VERSION, getVersion(context))
                .apply();
    }

    private String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) { }

        return null;
    }

    void addTag(String key, String value) {
        if (value != null) {
            mPrefs.edit().putString(key, value).apply();
        }
        else {
            mPrefs.edit().remove(key).apply();
        }
    }

    JSONObject getTags() {
        JSONObject tags = new JSONObject();

        for (Map.Entry<String, ?> entry: mPrefs.getAll().entrySet()) {
            try {
                tags.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) { }
        }

        return tags;
    }

}
