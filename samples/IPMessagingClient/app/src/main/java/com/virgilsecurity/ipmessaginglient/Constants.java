package com.virgilsecurity.ipmessaginglient;

/**
 * Created by Andrii Iakovenko.
 */
public interface Constants {

    interface PREFERENCES {
        String ACCESS_TOKEN = "pref_access_token";
        String IDENTITY = "pref_identity";
        String IDENTITY_TOKEN = "pref_identity_token";
        String CONFIRMATION_CODE = "pref_confirmationCode";

        String PUBLIC_KEY = "pref_public_key";
        String PRIVATE_KEY = "pref_private_key";
        String VIRGIL_CARD_ID = "pref_virgil_card_id";

        String MESSAGING_SERVICE_ADDRESS = "pref_ipmessaging_service_address";
    }

    interface HEADERS {
        String IDENTITY_TOKEN = "X-IDENTITY-TOKEN";
    }

    interface REQUEST_CODE {
        int SETTINGS = 1;
        int CHANNELS = 2;
    }

    interface EXTRA {
        String CHANNEL_NAME = "CHANNEL_NAME";
        String IDENTITY_TOKEN = "IDENTITY_TOKEN";
    }

}
