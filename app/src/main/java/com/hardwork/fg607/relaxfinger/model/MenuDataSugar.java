package com.hardwork.fg607.relaxfinger.model;

import android.content.ActivityNotFoundException;

import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.orm.SugarRecord;

import java.net.URISyntaxException;

/**
 * Created by fg607 on 16-6-18.
 */
public class MenuDataSugar extends SugarRecord {

    String whichMenu;
    String name;
    int type;
    String action;
    String activity;

    public MenuDataSugar(){}

    public MenuDataSugar(String whichMenu, String name, int type, String action, String activity) {
        this.whichMenu = whichMenu;
        this.name = name;
        this.type = type;
        this.action = action;
        this.activity = activity;
    }

    public String getActivity() {
        return activity;
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
                AppUtils.startApplication(action,activity);
                break;
            case 1:
                FloatingBallUtils.switchButton(name);
                break;
            case 2:
                AppUtils.startActivity(action);
                break;
            default:
                break;
        }
    }
}
