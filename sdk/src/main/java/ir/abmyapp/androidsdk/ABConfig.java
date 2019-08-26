package ir.abmyapp.androidsdk;

import com.yashoid.office.task.DefaultTaskManager;
import com.yashoid.office.task.TaskManager;

import java.util.concurrent.TimeUnit;

public class ABConfig {

    private static final long FETCH_INTERVAL = TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS);
    private static final long EVENT_UPLOAD_INTERVAL = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);
    private static final String DOMAIN = "http://abmyapp.ir:8080/";

    public static class Builder {

        private long fetchInterval = FETCH_INTERVAL;
        private long eventUploadInterval = EVENT_UPLOAD_INTERVAL;
        private TaskManager taskManager = DefaultTaskManager.getInstance();
        private String backgroundOfficeSection = TaskManager.NETWORK;
        private String domain = DOMAIN;
        private boolean debug = false;

        public Builder setFetchInterval(long fetchInterval) {
            this.fetchInterval = fetchInterval;

            return this;
        }

        public Builder setTaskManager(TaskManager taskManager) {
            this.taskManager = taskManager;

            return this;
        }

        public Builder setBackgroundOfficeSection(String section) {
            this.backgroundOfficeSection = section;

            return this;
        }

        public Builder setDomain(String domain) {
            this.domain = domain;

            return this;
        }

        public Builder setDebug(boolean debug) {
            this.debug = debug;

            return this;
        }

        public ABConfig build() {
            ABConfig config = new ABConfig();

            config.mFetchInterval = fetchInterval;
            config.mEventUploadInterval = eventUploadInterval;
            config.mTaskManager = taskManager;
            config.mBackgroundOfficeSection = backgroundOfficeSection;
            config.mDomain = domain;
            config.mDebug = debug;

            return config;
        }

    }

    private long mFetchInterval;
    private long mEventUploadInterval;

    private TaskManager mTaskManager;
    private String mBackgroundOfficeSection;

    private String mDomain;
    private boolean mDebug;

    private ABConfig() {

    }

    boolean canFetch(long lastFetchTime) {
        return mDebug || (System.currentTimeMillis() - lastFetchTime) >= mFetchInterval;
    }

    TaskManager getTaskManager() {
        return mTaskManager;
    }

    String getBackgroundOfficeSection() {
        return mBackgroundOfficeSection;
    }

    String getDomain() {
        return mDomain;
    }

    boolean isDebug() {
        return mDebug;
    }

    boolean canUploadEvents(int pendingEventCount, long lastEventUploadTime) {
        return mDebug || (System.currentTimeMillis() - lastEventUploadTime) >= mEventUploadInterval;
    }

}
