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

    private Map<String, KeyInfo> mData = new HashMap<>();

    ResourceData() {

    }

    void add(String key, String value) {
        mData.put(key, new KeyInfo(value));
    }

    void add(String key, JSONObject rawValue) throws JSONException {
        String value = rawValue.isNull(KEY_VALUE) ? null : rawValue.getString(KEY_VALUE);

        add(key, value);
    }

    String getValue(String key) {
        KeyInfo keyInfo = mData.get(key);

        return keyInfo == null ? null : keyInfo.value;
    }

    Set<Map.Entry<String, JSONObject>> entrySet() {
        HashSet<Map.Entry<String, JSONObject>> entrySet = new HashSet<>(mData.size());

        for (Map.Entry<String, KeyInfo> entry: mData.entrySet()) {
            JSONObject json = new JSONObject();

            KeyInfo keyInfo = entry.getValue();

            try {
                json.put(KEY_VALUE, keyInfo == null ? JSONObject.NULL : keyInfo.value);
            } catch (JSONException e) { }

            entrySet.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), json));
        }

        return entrySet;
    }

    private static class KeyInfo {

        String value;

        private KeyInfo(String value) {
            this.value = value;
        }

    }

}
