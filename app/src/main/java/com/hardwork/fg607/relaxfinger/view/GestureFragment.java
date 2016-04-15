package com.hardwork.fg607.relaxfinger.view;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

import net.grandcentrix.tray.TrayAppPreferences;


public class GestureFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private Preference mClick;
    private Preference mDoubleClick;
    private Preference mLongPress;
    private Preference mSwipeUp;
    private Preference mSwipeDown;
    private Preference mSwipeLeft;
    private Preference mSwipeRight;
    private TrayAppPreferences mPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_gesture);
        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        initPreferences();

        checkSetting();
    }

    private void checkSetting() {

        mClick.setSummary(mPreferences.getString("click", "返回键"));
        mDoubleClick.setSummary(mPreferences.getString("doubleClick", "最近任务键"));
        mLongPress.setSummary(mPreferences.getString("longPress","移动(固定)悬浮球"));
        mSwipeUp.setSummary(mPreferences.getString("swipeUp", "通知栏"));
        mSwipeDown.setSummary(mPreferences.getString("swipeDown", "Home键"));
        mSwipeLeft.setSummary(mPreferences.getString("swipeLeft", "快捷应用"));
        mSwipeRight.setSummary(mPreferences.getString("swipeRight", "快速设置"));
    }

    private void initPreferences() {


        mClick = (Preference) findPreference("click");
        mClick.setOnPreferenceClickListener(this);
        mDoubleClick = (Preference) findPreference("doubleClick");
        mDoubleClick.setOnPreferenceClickListener(this);
        mLongPress = (Preference) findPreference("longPress");
        mLongPress.setOnPreferenceClickListener(this);
        mSwipeUp = (Preference) findPreference("swipeUp");
        mSwipeUp.setOnPreferenceClickListener(this);
        mSwipeDown = (Preference) findPreference("swipeDown");
        mSwipeDown.setOnPreferenceClickListener(this);
        mSwipeLeft = (Preference) findPreference("swipeLeft");
        mSwipeLeft.setOnPreferenceClickListener(this);
        mSwipeRight = (Preference) findPreference("swipeRight");
        mSwipeRight.setOnPreferenceClickListener(this);
    }

    public  void sendMsg(int what,String name,boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(getActivity(), FloatingBallService.class);
        getActivity().startService(intent);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        showFunctionDialog(preference);

        return true;
    }

    public void showFunctionDialog(final Preference preference){


        final String summary = preference.getSummary().toString();

        Resources res =getResources();
        String[] function=res.getStringArray(R.array.array_function);

        int pos =  -1;

        for(int i =0;i<function.length;i++){

            if(function[i].equals(summary)){
                pos = i;
                break;
            }
        }


        new MaterialDialog.Builder(getActivity())
                .title(R.string.functionChooseTitle)
                .items(R.array.array_function)
                .itemsCallbackSingleChoice(pos, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (preference.getKey()) {

                            case "click":
                                mClick.setSummary(text);
                                mPreferences.put("click", text.toString());
                                break;
                            case "doubleClick":
                                mDoubleClick.setSummary(text);
                                mPreferences.put("doubleClick", text.toString());
                                break;
                            case "longPress":
                                mLongPress.setSummary(text);
                                mPreferences.put("longPress", text.toString());
                                break;
                            case "swipeUp":
                                mSwipeUp.setSummary((String) text);
                                mPreferences.put("swipeUp", text.toString());
                                break;
                            case "swipeDown":
                                mSwipeDown.setSummary((String) text);
                                mPreferences.put("swipeDown", text.toString());
                                break;
                            case "swipeLeft":
                                mSwipeLeft.setSummary((String) text);
                                mPreferences.put("swipeLeft", text.toString());
                                break;
                            case "swipeRight":
                                mSwipeRight.setSummary((String) text);
                                mPreferences.put("swipeRight", text.toString());
                                break;
                            default:
                                break;

                        }

                        sendMsg(Config.GESTURE_FUNCTION, "loadfunction", true);

                        if (text.equals("休眠(需要开启锁屏功能)")) {


                        } else if (text.equals("后台应用")) {

                            sendMsg(Config.START_DETECT, "startDetect", true);
                        }

                        if(summary.equals("后台应用") && !text.equals("后台应用")){

                            sendMsg(Config.STOP_DETECT, "stopDetect", true);
                        }

                        return true;
                    }
                })
                .negativeText("取消")
                .show();
    }
}
