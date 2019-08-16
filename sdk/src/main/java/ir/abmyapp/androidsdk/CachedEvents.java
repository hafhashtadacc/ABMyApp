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

    private static final String EVENT_IMPRESSION = "IMPRESSION";

    private SharedPreferences mPrefs;

    private Map<String, List<String>> mEvents;

    CachedEvents(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        loadEvents();
    }

    void loadEvents() {
        Map<String, ?> rawData = mPrefs.getAll();

        mEvents = new HashMap<>();

        for (Map.Entry<String, ?> entry: rawData.entrySet()) {
            String experiment = entry.getKey();

            if (KEY_EVENT_UPLOAD_TIME.equals(experiment)) {
                continue;
            }

            String sEvents = (String) entry.getValue();

            try {
                JSONArray events = new JSONArray(sEvents);

                List<String> eventList = getEventList(experiment);

                for (int i = 0; i < events.length(); i++) {
                    eventList.add(events.getString(i));
                }
            } catch (JSONException e) { }
        }
    }

    private List<String> getEventList(String experiment) {
        List<String> eventList = mEvents.get(experiment);

        if (eventList == null) {
            eventList = new ArrayList<>();

            mEvents.put(experiment, eventList);
        }

        return eventList;
    }

    void recordImpression(String experiment) {
        List<String> events = getEventList(experiment);

        events.add(EVENT_IMPRESSION);

        saveEventsInPreferences();
    }

    void recordEvent(String event) {
        for (Map.Entry<String, List<String>> entry: mEvents.entrySet()) {
            entry.getValue().add(event);
        }

        saveEventsInPreferences();
    }

    int getPendingEventCount() {
        int pendingEventCount = 0;

        for (Map.Entry<String, List<String>> entry: mEvents.entrySet()) {
            pendingEventCount += entry.getValue().size();
        }

        return pendingEventCount;
    }

    long getLastEventUploadTime() {
        return mPrefs.getLong(KEY_EVENT_UPLOAD_TIME, 0);
    }

    Map<String, List<String>> getEvents() {
        return mEvents;
    }

    void onEventsPushed(Map<String, List<String>> events) {
        for (Map.Entry<String, List<String>> eventEntry: events.entrySet()) {
            List<String> nativeEvents = mEvents.get(eventEntry.getKey());

            if (nativeEvents != null) {
                nativeEvents.removeAll(eventEntry.getValue());

                if (nativeEvents.isEmpty()) {
                    mEvents.remove(eventEntry.getKey());
                }
            }
        }

        mPrefs.edit().putLong(KEY_EVENT_UPLOAD_TIME, System.currentTimeMillis()).apply();

        saveEventsInPreferences();
    }

    private void saveEventsInPreferences() {
        SharedPreferences.Editor editor = mPrefs.edit();

        for (String key: mPrefs.getAll().keySet()) {
            if (KEY_EVENT_UPLOAD_TIME.equals(key)) {
                continue;
            }

            editor.remove(key);
        }

        for (Map.Entry<String, List<String>> eventEntry: mEvents.entrySet()) {
            JSONArray events = new JSONArray();

            List<String> eventList = eventEntry.getValue();

            if (eventEntry != null) {
                for (String event: eventList) {
                    events.put(event);
                }
            }

            editor.putString(eventEntry.getKey(), events.toString());
        }

        editor.apply();
    }

}
