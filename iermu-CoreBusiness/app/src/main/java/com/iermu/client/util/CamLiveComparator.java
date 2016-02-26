package com.iermu.client.util;

import com.iermu.client.model.CamLive;

import java.util.Comparator;

/**
 * Created by zhoushaopei on 15/11/20.
 */
public class CamLiveComparator<T> implements Comparator<CamLive> {

    @Override
    public int compare(CamLive lhs, CamLive rhs) {
        boolean offline = lhs.isOffline();
        boolean offline1= rhs.isOffline();
        if(offline && offline1) {
            String description = lhs.getDescription();
            String description1 = rhs.getDescription();
            return description.compareTo(description1);
        }
        return new Boolean(offline).compareTo(new Boolean(offline1));
    }

}
