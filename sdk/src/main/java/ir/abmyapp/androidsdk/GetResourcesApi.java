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

public class GetResourcesApi implements BaseABApi {

    private static final String TYPE_DEFAULT = "default";
    private static final String TYPE_DEBUG = "debug";

    private static final String RESOURCES_URL = "api/v1/assignments/{token}/{type}/user_id/{user}";

    private static final String PROFILE = "profile";

    private TaskManager mTaskManager;
    private String mBackgroundSection;

    private String mDomain;
    private String mApiToken;
    private String mType;
    private String mUserName;

    GetResourcesApi(String domain, String apiToken, boolean debug, String user,
                    TaskManager taskManager, String backgroundSection) {
        mTaskManager = taskManager;
        mBackgroundSection = backgroundSection;

        mDomain = domain;
        mApiToken = apiToken;
        mType = debug ? TYPE_DEBUG : TYPE_DEFAULT;
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
                        .replaceAll("\\{token\\}", mApiToken)
                        .replaceAll("\\{type\\}", mType)
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
            String value = reader.nextString();

            resourceData.add(name, value);
        }

        reader.endObject();
    }

}
