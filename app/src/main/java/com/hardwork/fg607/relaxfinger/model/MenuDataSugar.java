package com.hardwork.fg607.relaxfinger.model;

import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

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
}
