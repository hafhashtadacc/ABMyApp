package ir.abmyapp.androidsdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ResourceData {

    private static final String KEY_VALUE = "v";
    private static final String KEY_EXPERIMENT = "e";

    private Map<String, KeyInfo> mData = new HashMap<>();

    ResourceData() {

    }

    void add(String key, String value, String experiment) {
        mData.put(key, new KeyInfo(value, experiment));
    }

    void add(String key, JSONObject rawValue) throws JSONException {
        String value = rawValue.isNull(KEY_VALUE) ? null : rawValue.getString(KEY_VALUE);
        String experiment = rawValue.isNull(KEY_EXPERIMENT) ? null : rawValue.getString(KEY_EXPERIMENT);

        add(key, value, experiment);
    }

    String getValue(String key) {
        KeyInfo keyInfo = mData.get(key);

        return keyInfo == null ? null : keyInfo.value;
    }

    String getExperiment(String key) {
        KeyInfo keyInfo = mData.get(key);

        return keyInfo == null ? null : keyInfo.experiment;
    }

    Set<Map.Entry<String, JSONObject>> entrySet() {
        HashSet<Map.Entry<String, JSONObject>> entrySet = new HashSet<>(mData.size());

        for (Map.Entry<String, KeyInfo> entry: mData.entrySet()) {
            JSONObject json = new JSONObject();

            KeyInfo keyInfo = entry.getValue();

            try {
                json.put(KEY_VALUE, keyInfo == null ? JSONObject.NULL : keyInfo.value);
                json.put(KEY_EXPERIMENT, keyInfo == null ? JSONObject.NULL : keyInfo.experiment);
            } catch (JSONException e) { }

            entrySet.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), json));
        }

        return entrySet;
    }

    private static class KeyInfo {

        String value;
        String experiment;

        private KeyInfo(String value, String experiment) {
            this.value = value;
            this.experiment = experiment;
        }

    }

}
