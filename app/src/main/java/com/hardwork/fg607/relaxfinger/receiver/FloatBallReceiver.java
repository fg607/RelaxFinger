package com.hardwork.fg607.relaxfinger.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.hardwork.fg607.relaxfinger.service.FloatService;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

/**
 * Created by fg607 on 15-8-23.
 */
public class FloatBallReceiver extends BroadcastReceiver {

    private SharedPreferences sp;
    private Context mContext;


    public void onReceive(Context context, Intent intent) {


        mContext = context;

        sp = FloatingBallUtils.getSharedPreferences();



        String action = intent.getAction();

        switch (action){

            case Intent.ACTION_BOOT_COMPLETED:
                if(sp.getBoolean("autoStartSwitch",false) && sp.getBoolean("floatSwitch", false)){

                    sendMsg(Config.FLOAT_SWITCH, "ballstate", true);
                }
                break;
            case Config.ACTION_SHOW_FLOATBALL:
                if(sp.getBoolean("floatSwitch",false)){

                    sendMsg(Config.RECOVER_FLOATBALL,"ballstate",true);
                }
                break;
            default:
                break;

        }
    }

    public  void sendMsg(int what,String name,boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(mContext, FloatService.class);
        mContext.startService(intent);
    }
}