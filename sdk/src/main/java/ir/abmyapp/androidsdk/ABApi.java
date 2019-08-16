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

    ABApi(Context context, String domain, boolean debug, TaskManager taskManager, String backgroundSection) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        String user = prefs.getString(USER, null);

        if (user == null) {
            user = UUID.randomUUID().toString();

            prefs.edit().putString(USER, user).apply();
        }

        mGetResourcesApi = new GetResourcesApi(context, user, domain, debug, taskManager, backgroundSection);
        mSendEventsApi = new SendEventsApi(context, user, domain, taskManager, backgroundSection);
    }

    void getResources(final JSONObject profile, final OnGetResourcesResultCallback callback) {
        mGetResourcesApi.getResources(profile, callback);
    }

    void sendEvents(Map<String, List<String>> events, OnSendEventsResultCallback callback) {
        mSendEventsApi.sendEvents(events, callback);
    }

}
