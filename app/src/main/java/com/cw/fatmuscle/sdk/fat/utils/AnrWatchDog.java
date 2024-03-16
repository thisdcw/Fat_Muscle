package com.cw.fatmuscle.sdk.fat.utils;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import android.os.Process;

import java.lang.reflect.Method;

public class AnrWatchDog extends Thread{
    private static final String TAG = "good";
    private AnrChecker anrChecker;
    private AnrListener anrListener;
    private Object cpuTrackerObj;
    private boolean ignoreDebugger;
    private Handler mainHandler;
    private int timeout;
    private Method updateMethod;

    /* loaded from: classes.dex */
    public interface AnrListener {
        void onAnrHappened(String str);
    }

    /* loaded from: classes.dex */
    private class AnrChecker implements Runnable {
        private long executeTime;
        private boolean mCompleted;
        private long mStartTime;

        private AnrChecker() {
            this.executeTime = SystemClock.uptimeMillis();
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (AnrWatchDog.this) {
                this.mCompleted = true;
                this.executeTime = SystemClock.uptimeMillis();
            }
        }

        void schedule() {
            this.mCompleted = false;
            this.mStartTime = SystemClock.uptimeMillis();
            AnrWatchDog.this.mainHandler.postAtFrontOfQueue(this);
        }

        boolean isBlocked() {
            return !this.mCompleted || this.executeTime - this.mStartTime >= 5000;
        }
    }

    private AnrWatchDog(Builder builder) {
        super("ANR-WatchDog-Thread");
        this.timeout = 5000;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.anrChecker = new AnrChecker();
        this.timeout = builder.timeout;
        this.ignoreDebugger = builder.ignoreDebugger;
        this.anrListener = builder.anrListener;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Process.setThreadPriority(10);
        while (!isInterrupted()) {
            synchronized (this) {
                this.anrChecker.schedule();
                long j = this.timeout;
                long uptimeMillis = SystemClock.uptimeMillis();
                while (j > 0) {
                    try {
                        wait(j);
                    } catch (InterruptedException e) {
                        Log.w(TAG, e.toString());
                    }
                    j = this.timeout - (SystemClock.uptimeMillis() - uptimeMillis);
                }
                if (this.anrChecker.isBlocked()) {
                    if (this.ignoreDebugger || !Debug.isDebuggerConnected()) {
                        String cpuInfo = getCpuInfo();
                        String stackTraceInfo = getStackTraceInfo();
                        Log.i(TAG, "haha: " + stackTraceInfo);
                        Log.i(TAG, "haha: " + cpuInfo);
                        AnrListener anrListener = this.anrListener;
                        if (anrListener != null) {
                            anrListener.onAnrHappened(stackTraceInfo);
                        }
                    }
                }
            }
        }
    }

    private String getCpuInfo() {
        try {
            Object obj = this.cpuTrackerObj;
            if (obj == null) {
                Object newInstance = Class.forName("com.android.internal.os.ProcessCpuTracker").getConstructor(Boolean.TYPE).newInstance(false);
                this.cpuTrackerObj = newInstance;
                newInstance.getClass().getMethod("init", new Class[0]).invoke(this.cpuTrackerObj, new Object[0]);
                this.updateMethod = this.cpuTrackerObj.getClass().getMethod("update", new Class[0]);
            } else {
                this.updateMethod.invoke(obj, new Object[0]);
            }
            synchronized (this.cpuTrackerObj) {
                this.cpuTrackerObj.wait(500L);
            }
            this.updateMethod.invoke(this.cpuTrackerObj, new Object[0]);
            return (String) this.cpuTrackerObj.getClass().getMethod("printCurrentState", Long.TYPE).invoke(this.cpuTrackerObj, Long.valueOf(SystemClock.uptimeMillis()));
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return "";
        }
    }

    private String getStackTraceInfo() {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackTraceElement : Looper.getMainLooper().getThread().getStackTrace()) {
            sb.append(stackTraceElement.toString()).append("\r\n");
        }
        return sb.toString();
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private AnrListener anrListener;
        private boolean ignoreDebugger;
        private int timeout;

        public Builder timeout(int i) {
            this.timeout = i;
            return this;
        }

        public Builder ignoreDebugger(boolean z) {
            this.ignoreDebugger = z;
            return this;
        }

        public Builder anrListener(AnrListener anrListener) {
            this.anrListener = anrListener;
            return this;
        }

        public AnrWatchDog build() {
            return new AnrWatchDog(this);
        }
    }

}
