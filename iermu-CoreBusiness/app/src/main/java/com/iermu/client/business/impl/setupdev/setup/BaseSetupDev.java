package com.iermu.client.business.impl.setupdev.setup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wcy on 15/8/10.
 */
public abstract class BaseSetupDev implements ISetupDevStep {

    private List<SetupStatusListener> statusListener;
    private List<SetupProgressListener> progressListener;

    @Override
    public synchronized void addSetupStatusListener(SetupStatusListener listener) {
        getStatusListener().add(listener);
    }

    @Override
    public synchronized void addSetupProgressListener(SetupProgressListener listener) {
        getProgressListener().add(listener);
    }

    @Override
    public synchronized void stop() {
        getStatusListener().clear();
        getProgressListener().clear();
    }

    protected synchronized void onSetupChanged(SetupStatus status) {
        List<SetupStatusListener> list = getStatusListener();
        if(list == null || list.size() <= 0) return;
        for(int i=0; i<list.size(); i++) {
            SetupStatusListener listener = list.get(i);
            listener.onSetupStatusChange(status);
        }
    }

    protected synchronized void onSetupProgress(int progress) {
        List<SetupProgressListener> list = getProgressListener();
        if(list == null || list.size() <= 0) return;
        for(int i=0; i<list.size(); i++) {
            SetupProgressListener listener = list.get(i);
            listener.onProgress(progress);
        }
    }

    private List<SetupProgressListener> getProgressListener() {
        if(progressListener == null) {
            progressListener = new ArrayList<SetupProgressListener>();
        }
        return progressListener;
    }
    private List<SetupStatusListener> getStatusListener() {
        if(statusListener == null) {
            statusListener = new ArrayList<SetupStatusListener>();
        }
        return statusListener;
    }
}
