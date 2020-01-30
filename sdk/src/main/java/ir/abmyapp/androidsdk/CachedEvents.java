package ir.abmyapp.androidsdk;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CachedEvents {

    private static final String PREFERENCES = "ir.abmyapp.androidsdk.CachedEvents";

    private static final String KEY_EVENT_UPLOAD_TIME = "__event_upload_time";
    private static final String KEY_EVENTS = "events";

    private SharedPreferences mPrefs;

    private List<String> mEvents;

    CachedEvents(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        loadEvents();
    }

    void loadEvents() {
        String sEvents = mPrefs.getString(KEY_EVENTS, "[]");

        try {
            JSONArray jEvents = new JSONArray(sEvents);

            mEvents = new ArrayList<>(jEvents.length());

            for (int i = 0; i < jEvents.length(); i++) {
                mEvents.add(jEvents.getString(i));
            }
        } catch (JSONException e) { }
    }

    void recordEvent(String event) {
        mEvents.add(event);

        saveEventsInPreferences();
    }

    int getPendingEventCount() {
        return mEvents.size();
    }

    long getLastEventUploadTime() {
        return mPrefs.getLong(KEY_EVENT_UPLOAD_TIME, 0);
    }

    List<String> getEvents() {
        return mEvents;
    }

    void onEventsPushed(List<String> events) {
        mEvents.removeAll(events);

        mPrefs.edit().putLong(KEY_EVENT_UPLOAD_TIME, System.currentTimeMillis()).apply();

        saveEventsInPreferences();
    }

    private void saveEventsInPreferences() {
        SharedPreferences.Editor editor = mPrefs.edit();

        JSONArray events = new JSONArray();

        for (String event: mEvents) {
            events.put(event);
        }

        editor.putString(KEY_EVENTS, events.toString());

        editor.apply();
    }

}
