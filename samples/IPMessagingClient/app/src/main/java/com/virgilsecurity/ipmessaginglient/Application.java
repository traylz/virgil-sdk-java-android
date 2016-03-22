package com.virgilsecurity.ipmessaginglient;

import android.content.Context;
import android.preference.PreferenceManager;

import com.virgilsecurity.sdk.client.ClientFactory;

/**
 * Created by Andrii Iakovenko.
 */
public class Application extends android.app.Application {

    private static Application instance;

    private static ClientFactory clientFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static void reset() {
        clientFactory = null;
    }

    public static ClientFactory getClientFactory() {
        if (clientFactory == null) {
            String accessToken = PreferenceManager.getDefaultSharedPreferences(instance).getString(Constants.PREFERENCES.ACCESS_TOKEN, "");

            if (accessToken != null && !accessToken.isEmpty()) {
                clientFactory = new ClientFactory(accessToken);
            }
        }

        return clientFactory;
    }

    public static Context getInstance() {
        return instance;
    }
}
