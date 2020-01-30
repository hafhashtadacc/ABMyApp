package ir.abmyapp.androidsdk;

import android.content.Context;

import com.yashoid.office.AsyncOperation;
import com.yashoid.office.task.TaskManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendEventsApi implements BaseABApi {

    private static final String SEND_EVENTS_URL = "api/v1/events/apiToken/{token}/userId/{user}";

    private static final String EVENTS = "events";
    private static final String NAME = "name";

    private TaskManager mTaskManager;
    private String mBackgroundSection;

    private String mDomain;
    private String mApiToken;
    private String mUserName;

    SendEventsApi(String domain, String apiToken, String user, TaskManager taskManager, String backgroundSection) {
        mTaskManager = taskManager;
        mBackgroundSection = backgroundSection;

        mDomain = domain;
        mApiToken = apiToken;
        mUserName = user;
    }

    void sendEvents(final List<String> events, final OnSendEventsResultCallback callback) {
        AsyncOperation operation = new AsyncOperation(mTaskManager, TaskManager.MAIN, mBackgroundSection) {

            private boolean mSuccessful = false;

            @Override
            protected void doInBackground() {
                if (events == null || events.isEmpty()) {
                    mSuccessful = true;
                    return;
                }

                mSuccessful = sendEvents(events);
            }

            @Override
            protected void onPostExecute() {
                callback.onSendEventsResult(new SendEventsResult() {

                    @Override
                    public boolean isSuccessful() {
                        return mSuccessful;
                    }

                });
            }

        };

        operation.execute();
    }

    private boolean sendEvents(List<String> events) {
        String urlString = mDomain + SEND_EVENTS_URL
                .replaceAll("\\{token\\}", mApiToken)
                .replaceAll("\\{user\\}", mUserName);

        URL url;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            return false;
        }

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.addRequestProperty(CONTENT_TYPE, CONTENT_TYPE_JSON);

            connection.getOutputStream().write(getEventListBody(events).getBytes("UTF-8"));

            int responseCode = connection.getResponseCode();

            return responseCode < 200 || responseCode >= 300;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getEventListBody(List<String> eventList) {
        JSONObject body = new JSONObject();

        try {
            JSONArray events = new JSONArray();

            for (String event: eventList) {
                events.put(event);
            }

            body.put(EVENTS, events);
        } catch (JSONException e) { }

        return body.toString();
    }

}
