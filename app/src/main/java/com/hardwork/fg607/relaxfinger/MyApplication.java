package com.hardwork.fg607.relaxfinger;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by fg607 on 16-1-23.
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;
    /** 主线程ID */
    private static int mMainTheadId;
    /** 主线程ID */
    private static Thread mMainThread;
    /** 主线程Handler */
    private static Handler mMainThreadHandler;
    /** 主线程Looper */
    private static Looper mMainLooper;

    @Override
    public void onCreate() {

        super.onCreate();
        mMainTheadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();
        mMainLooper = getMainLooper();
        mInstance = this;



        //mMainThread.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
    }

    public static MyApplication getApplication() {

        return mInstance;
    }

    /** 获取主线程ID */
    public static int getMainThreadId() {
        return mMainTheadId;
    }

    /** 获取主线程 */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /** 获取主线程的handler */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /** 获取主线程的looper */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            android.os.Process.killProcess(mMainTheadId);

        }
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }
}
