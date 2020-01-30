package ir.abmyapp.androidsdk;

import android.content.Context;
import android.content.SharedPreferences;

import com.yashoid.office.task.TaskManager;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class ABApi implements BaseABApi {

    private static final String PREFERENCES = "ir.abmyapp.androidsdk.ABApi";

    private static final String USER = "user";

    private GetResourcesApi mGetResourcesApi;
    private SendEventsApi mSendEventsApi;

    ABApi(Context context, String domain, String apiToken, boolean debug, TaskManager taskManager, String backgroundSection) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        String user = prefs.getString(USER, null);

        if (user == null) {
            user = UUID.randomUUID().toString();

            prefs.edit().putString(USER, user).apply();
        }

        mGetResourcesApi = new GetResourcesApi(domain, apiToken, debug, user, taskManager, backgroundSection);
        mSendEventsApi = new SendEventsApi(domain, apiToken, user, taskManager, backgroundSection);
    }

    void getResources(final JSONObject profile, final OnGetResourcesResultCallback callback) {
        mGetResourcesApi.getResources(profile, callback);
    }

    void sendEvents(List<String> events, OnSendEventsResultCallback callback) {
        mSendEventsApi.sendEvents(events, callback);
    }

}
