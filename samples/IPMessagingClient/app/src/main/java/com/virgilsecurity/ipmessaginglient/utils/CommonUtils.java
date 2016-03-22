package com.virgilsecurity.ipmessaginglient.utils;

import android.widget.Toast;

import com.virgilsecurity.ipmessaginglient.Application;

/**
 * Created by Andrii Iakovenko.
 */
public class CommonUtils {

    public static void showToast(String text, int duration) {
        Toast.makeText(Application.getInstance(), text, duration).show();
    }

    public static void showToast(int resId, int duration) {
        Toast.makeText(Application.getInstance(), resId, duration).show();
    }

    public static void showToast(String text) {
        Toast.makeText(Application.getInstance(), text, Toast.LENGTH_LONG).show();
    }

    public static void showToast(int resId) {
        showToast(resId, Toast.LENGTH_LONG);
    }
}
