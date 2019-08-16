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

    private static final String SEND_EVENTS_URL = "api/v1/events/applications/{app}/experiments/{experiment}/users/{user}";

    private static final String EVENTS = "events";
    private static final String NAME = "name";

    private TaskManager mTaskManager;
    private String mBackgroundSection;

    private String mDomain;
    private String mAppName;
    private String mUserName;

    SendEventsApi(Context context, String user, String domain, TaskManager taskManager, String backgroundSection) {
        mTaskManager = taskManager;
        mBackgroundSection = backgroundSection;

        mDomain = domain;
        mAppName = context.getPackageName().replaceAll("\\.", "-");
        mUserName = user;
    }

    void sendEvents(final Map<String, List<String>> events, final OnSendEventsResultCallback callback) {
        AsyncOperation operation = new AsyncOperation(mTaskManager, TaskManager.MAIN, mBackgroundSection) {

            private boolean mSuccessful = false;
            private Map<String, List<String>> mEvents;

            @Override
            protected void doInBackground() {
                mEvents = new HashMap<>(events.size());

                for (String experiment: events.keySet()) {
                    List<String> eventList = events.get(experiment);

                    if (eventList == null || eventList.isEmpty()) {
                        continue;
                    }

                    boolean successful = sendEvents(experiment, eventList);

                    if (successful) {
                        mSuccessful = true;
                        mEvents.put(experiment, eventList);
                    }
                }
            }

            @Override
            protected void onPostExecute() {
                callback.onSendEventsResult(new SendEventsResult() {

                    @Override
                    public boolean isSuccessful() {
                        return mSuccessful;
                    }

                    @Override
                    public Map<String, List<String>> getEvents() {
                        return mEvents;
                    }

                });
            }

        };

        operation.execute();
    }

    private boolean sendEvents(String experiment, List<String> eventList) {
        String urlString = mDomain + SEND_EVENTS_URL
                .replaceAll("\\{app\\}", mAppName)
                .replaceAll("\\{experiment\\}", experiment)
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

            connection.getOutputStream().write(getEventListBody(eventList).getBytes("UTF-8"));

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
                events.put(getEventObject(event));
            }

            body.put(EVENTS, events);
        } catch (JSONException e) { }

        return body.toString();
    }

    private JSONObject getEventObject(String event) throws JSONException {
        JSONObject object = new JSONObject();

        object.put(NAME, event);

        return object;
    }

}
