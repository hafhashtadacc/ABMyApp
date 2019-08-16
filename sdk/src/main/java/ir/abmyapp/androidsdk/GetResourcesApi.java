package ir.abmyapp.androidsdk;

import android.content.Context;
import android.util.JsonReader;

import com.yashoid.office.AsyncOperation;
import com.yashoid.office.task.TaskManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GetResourcesApi implements BaseABApi {

    private static final String PAGE_DEFAULT = "default";
    private static final String PAGE_DEBUG = "debug";

    private static final String RESOURCES_URL = "api/v1/assignments/applications/{app}/pages/{page}/users/{user}";

    private static final String PROFILE = "profile";
    private static final String ASSIGNMENTS = "assignments";
    private static final String ASSIGNMENT = "assignment";
    private static final String PAYLOAD = "payload";
    private static final String EXPERIMENT_LABEL = "experimentLabel";
    private static final String STATUS = "status";

    private static final String STATUS_NEW_ASSIGNMENT = "NEW_ASSIGNMENT";
    private static final String STATUS_EXISTING_ASSIGNMENT = "EXISTING_ASSIGNMENT";

    private TaskManager mTaskManager;
    private String mBackgroundSection;

    private String mDomain;
    private String mAppName;
    private String mPageName;
    private String mUserName;

    GetResourcesApi(Context context, String user, String domain, boolean debug, TaskManager taskManager, String backgroundSection) {
        mTaskManager = taskManager;
        mBackgroundSection = backgroundSection;

        mDomain = domain;
        mAppName = context.getPackageName().replaceAll("\\.", "-");
        mPageName = debug ? PAGE_DEBUG : PAGE_DEFAULT;
        mUserName = user;
    }

    void getResources(final JSONObject profile, final OnGetResourcesResultCallback callback) {
        AsyncOperation operation = new AsyncOperation(mTaskManager, TaskManager.MAIN, mBackgroundSection) {

            private boolean mSuccessful;
            private ResourceData mResourceData = null;
            private Exception mException = null;

            @Override
            protected void doInBackground() {
                String urlString = mDomain + RESOURCES_URL
                        .replaceAll("\\{app\\}", mAppName)
                        .replaceAll("\\{page\\}", mPageName)
                        .replaceAll("\\{user\\}", mUserName);

                URL url;

                try {
                    url = new URL(urlString);
                } catch (MalformedURLException e) {
                    mSuccessful = false;
                    mException = e;
                    return;
                }

                HttpURLConnection connection = null;

                try {
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setDoOutput(true);
                    connection.addRequestProperty(CONTENT_TYPE, CONTENT_TYPE_JSON);

                    connection.getOutputStream().write(getGetResourcesBody(profile).getBytes("UTF-8"));

                    int responseCode = connection.getResponseCode();

                    if (responseCode < 200 || responseCode >= 300) {
                        mSuccessful = false;
                        return;
                    }

                    ResourceData resourceData = new ResourceData();

                    JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));

                    readResourceData(reader, resourceData);

                    mSuccessful = true;
                    mResourceData = resourceData;
                } catch (IOException e) {
                    mSuccessful = false;
                    mException = e;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute() {
                callback.onGetResourcesResult(new GetResourcesResult() {

                    @Override
                    public boolean isSuccessful() {
                        return mSuccessful;
                    }

                    @Override
                    public ResourceData getData() {
                        return mResourceData;
                    }

                });
            }

        };

        operation.execute();
    }

    private String getGetResourcesBody(JSONObject profile) {
        JSONObject body = new JSONObject();

        try {
            body.put(PROFILE, profile);
        } catch (JSONException e) { }

        return body.toString();
    }

    private void readResourceData(JsonReader reader, ResourceData resourceData) throws IOException {
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();

            if (ASSIGNMENTS.equals(name)) {
                reader.beginArray();

                while (reader.hasNext()) {
                    readAssignment(reader, resourceData);
                }

                reader.endArray();
            }
            else {
                reader.skipValue();
            }
        }

        reader.endObject();
    }

    private void readAssignment(JsonReader reader, ResourceData resourceData) throws IOException {
        Map<String, String> values = new HashMap<>();
        String experiment = null;
        boolean experimentActive = false;

        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();

            if (PAYLOAD.equals(name)) {
                readValues(reader.nextString(), values);
            }
            else if (EXPERIMENT_LABEL.equals(name)) {
                experiment = reader.nextString();
            }
            else if (STATUS.equals(name)) {
                String status = reader.nextString();

                experimentActive = STATUS_NEW_ASSIGNMENT.equals(status) || STATUS_EXISTING_ASSIGNMENT.equals(status);
            }
            else {
                reader.skipValue();
            }
        }

        reader.endObject();

        if (experimentActive) {
            for (Map.Entry<String, String> entry: values.entrySet()) {
                resourceData.add(entry.getKey(), entry.getValue(), experiment);
            }
        }
    }

    private void readValues(String jsonString, Map<String, String> values) throws IOException {
        try {
            JSONObject json = new JSONObject(jsonString);

            Iterator<String> keyIterator = json.keys();

            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String value = json.isNull(key) ? null : json.getString(key);

                values.put(key, value);
            }
        } catch (JSONException e) {
            throw new IOException("Experiment payload is not in JSON format.", e);
        }
    }

}
