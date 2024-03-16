package com.cw.fatmuscle.common.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Stack;

public class ActivityStackManager {
    private static Stack<Activity> activityStack;

    private ActivityStackManager() {
    }

    private static final class InstanceHolder {
        static final ActivityStackManager instance = new ActivityStackManager();
    }

    public static ActivityStackManager getInstance() {
        //这里加锁来保证单例的线程安全性,避免在出现多个线程访问时创建多个实例,违反单例模式的设计原则
        return InstanceHolder.instance;
    }

    /**
     * 结束当前页面并跳转
     *
     * @param currentActivity
     * @param targetActivity
     */
    public void startActivity(Activity currentActivity, Class<?> targetActivity) {
        Intent intent = new Intent(currentActivity, targetActivity);
        currentActivity.startActivity(intent);
        finishCurrentActivity(currentActivity);
    }

    /**
     * 不结束当前页面并跳转
     *
     * @param currentActivity
     * @param targetActivity
     */
    public void startActivityNoFinish(Activity currentActivity, Class<?> targetActivity) {
        Intent intent = new Intent(currentActivity, targetActivity);
        currentActivity.startActivity(intent);
    }

    /**
     * 不结束当前页面并携带参数跳转
     *
     * @param currentActivity
     * @param targetActivity
     */
    public void startActivityNoFinish(Activity currentActivity, Class<?> targetActivity,Bundle bundle) {
        Intent intent = new Intent(currentActivity, targetActivity);
        intent.putExtras(bundle);
        currentActivity.startActivity(intent);
    }

    /**
     * 携带参数结束当前页面跳转
     *
     * @param currentActivity
     * @param targetActivity
     * @param bundle
     */
    public void startActivity(Activity currentActivity, Class<?> targetActivity, Bundle bundle) {
        Intent intent = new Intent(currentActivity, targetActivity);
        intent.putExtras(bundle);
        currentActivity.startActivity(intent);
        finishCurrentActivity(currentActivity);
    }

    /**
     * 结束当前页面
     *
     * @param currentActivity
     */
    public void finishCurrentActivity(Activity currentActivity) {
        currentActivity.finish();
    }

    /**
     * 往堆栈中添加activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 从堆栈中移除activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 从堆栈中弹出顶层activity
     *
     */
    public void popActivity() {
        activityStack.pop();
    }

    public void finishAllAndStart(Activity currentActivity, Class<?> cls) {
        finishAll();
        startActivity(currentActivity, cls);
    }

    /**
     * 移除堆栈所有页面
     */
    public void finishAll() {
        for (Activity activity : activityStack) {
            if (activity != null) {
                activity.finish();
            }
        }
        activityStack.clear();
    }
}

