package com.bignerdranch.android.maplbsline.Tools;

import android.content.Intent;

import java.util.List;

/**
 * Created by glossimar on 2017/8/20.
 */

public interface SetNameListener {
    void onFinish(String name);
    void onFinish(List<FriendsInfo> friendsInfoList);
    void onFinish(Intent intent);
    void onFinish(boolean result);
    void onLocationGetFinish(List<Double> doubleList);
}
