package com.hardwork.fg607.relaxfinger.model;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.view.BlankActivity;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;
import java.net.URISyntaxException;

/**
 * Created by fg607 on 16-6-18.
 */
public class MenuDataSugar extends SugarRecord{

    String whichMenu;
    String name;
    int type;
    String action;

    public MenuDataSugar(){}

    public MenuDataSugar(String whichMenu, String name, int type, String action) {
        this.whichMenu = whichMenu;
        this.name = name;
        this.type = type;
        this.action = action;
    }

    public String getWhichMenu() {
        return whichMenu;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

    public void click() throws URISyntaxException,ActivityNotFoundException{

        switch (type){

            case 0:

           /*     Intent intent = new Intent(MyApplication.getApplication(), BlankActivity.class);
                intent.putExtra("packageName",action);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getApplication().startActivity(intent);*/

                AppUtils.startApplication(action);
                break;
            case 1:
                FloatingBallUtils.switchButton(name);
                break;
            case 2:
               /* Intent intent1 = new Intent(MyApplication.getApplication(), BlankActivity.class);
                intent1.putExtra("intentUri",action);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getApplication().startActivity(intent1);*/
                AppUtils.startActivity(action);
                break;
            default:
                break;
        }
    }
}
