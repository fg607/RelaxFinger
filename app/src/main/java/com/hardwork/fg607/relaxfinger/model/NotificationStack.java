package com.hardwork.fg607.relaxfinger.model;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by fg607 on 17-1-17.
 */

public class NotificationStack {

    StackNode top;

    SparseArray<NotificationInfo> validArray;

    public NotificationStack(){

        top = new StackNode(0,null);
        validArray = new SparseArray<>();
    }

    public NotificationInfo getTop(){

        if(top.next == null){

            return null;
        }

        int id = top.next.notfyId;

        NotificationInfo info = validArray.get(id);

        if(info != null){

            return info;

        }else{

            pop();

            return getTop();
        }
    }

    public void push(NotificationInfo info){

        StackNode newNode = new StackNode(info.id,top.next);

        top.next = newNode;

        validArray.put(info.id,info);
    }

    public NotificationInfo pop(){

        NotificationInfo notify = null;

        if(top.next!= null){

            StackNode node = top.next;

            int id  = node.notfyId;

            top.next = top.next.next;

            notify  = validArray.get(id);

            validArray.remove(id);
        }

        return notify;
    }

    public void invalidNotification(int id){

        validArray.remove(id);
    }

    public boolean isEmpty(){

        return validArray.size()==0?true:false;
    }

    public void clear(){

        while (top.next != null){

            pop();
        }

        validArray.clear();
    }

    public class StackNode{

        int notfyId;
        StackNode next;

        public StackNode(int notfyId, StackNode next) {
            this.notfyId = notfyId;
            this.next = next;
        }
    }
}

