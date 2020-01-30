package ir.abmyapp.androidsdk;

import java.util.List;
import java.util.Map;

public interface BaseABApi {

    String CONTENT_TYPE = "Content-Type";
    String CONTENT_TYPE_JSON = "application/json";

    interface GetResourcesResult {

        boolean isSuccessful();

        ResourceData getData();

    }

    interface OnGetResourcesResultCallback {

        void onGetResourcesResult(GetResourcesResult result);

    }

    interface SendEventsResult {

        boolean isSuccessful();

    }

    interface OnSendEventsResultCallback {

        void onSendEventsResult(SendEventsResult result);

    }

}
