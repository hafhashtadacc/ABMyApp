package ir.abmyapp.androidsdk;

import android.graphics.drawable.Drawable;

import androidx.annotation.BoolRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FractionRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;

public interface IABResources {

    interface FetchResult {

        boolean isSuccessful();

        void activateNow();

    }

    interface OnFetchResultCallback {

        void onFetchResult(FetchResult result);

    }

    void fetch(OnFetchResultCallback callback);

    void fetchAndActivate();

    void setTag(String tag, String value);

    String getString(String key);

    Integer getInt(String key);

    Integer getColor(String key);

    Float getFloat(String key);

    Boolean getBoolean(String key);

    Float getDimen(String key);

    Integer getDimenPixelSize(String key);

    Drawable getDrawable(String key);

    String getString(@StringRes int resId);

    int getInt(@IntegerRes int resId);

    int getColor(@ColorRes int resId);

    float getFloat(@FractionRes int resId);

    boolean getBoolean(@BoolRes int resId);

    float getDimen(@DimenRes int resId);

    int getDimenPixelSize(@DimenRes int resId);

    Drawable getDrawable(@DrawableRes int resId);

    void recordEvent(String event);

    void recordEvent(String key, String event);

}
