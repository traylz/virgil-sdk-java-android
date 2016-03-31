package com.virgilsecurity.ipmessaginglient;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.virgilsecurity.ipmessaginglient.utils.CommonUtils;
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Action;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.privatekey.PrivateKeyInfo;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.client.utils.StringUtils;
import com.virgilsecurity.sdk.crypto.Base64;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.io.IOException;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private String actionId = null;

        SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            Context hostActivity = getActivity();
            prefs = PreferenceManager.getDefaultSharedPreferences(hostActivity);
            initPreference(Constants.PREFERENCES.ACCESS_TOKEN);
            initPreference(Constants.PREFERENCES.IDENTITY);
            initPreference(Constants.PREFERENCES.CONFIRMATION_CODE);
        }

        private void initPreference(String name) {
            Preference preference = findPreference(name);

            if (preference instanceof EditTextPreference) {
                preference.setOnPreferenceChangeListener(this);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (Constants.PREFERENCES.ACCESS_TOKEN.equals(preference.getKey())) {
                String value = String.valueOf(newValue);
                if (value == null || value.trim().length() == 0) {
                    // Access token should be set
                    return false;
                }
                Application.reset();
            } else if (Constants.PREFERENCES.IDENTITY.equals(preference.getKey())) {
                // Verify identity
                String value = String.valueOf(newValue);
                if (value == null || value.trim().length() == 0) {
                    return false;
                }
                verifyIdentity(value);
            } else if (Constants.PREFERENCES.CONFIRMATION_CODE.equals(preference.getKey())) {
                // Confirm identity
                String value = String.valueOf(newValue);
                if (value == null || value.trim().length() == 0) {
                    return false;
                }
                confirmIdentity(actionId, value);
            }

            preference.setSummary(String.valueOf(newValue));
            return true;
        }

        private void verifyIdentity(final String email) {
            try {
                Application.getClientFactory().getIdentityClient().verify(IdentityType.EMAIL, email, new ResponseCallback<Action>() {
                    @Override
                    public void onSuccess(Action action) {
                        actionId = action.getActionId();

                        // Remove this async task if you want to use confirmed Virgil Cards only
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                registerVirgilCard(new ValidatedIdentity(IdentityType.EMAIL, email));
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                CommonUtils.showToast(R.string.virgil_card_register_success);
                            }
                        }.execute();
                        CommonUtils.showToast(R.string.pref_confirmation_code_description);
                    }

                    @Override
                    public void onFailure(APIError error) {
                        CommonUtils.showToast(R.string.identity_verification_error);
                    }
                });
            } catch (IOException e) {
                CommonUtils.showToast(R.string.identity_verification_error);
            }
        }

        private void confirmIdentity(String actionId, String confirmationCode) {
            try {
                Application.getClientFactory().getIdentityClient().confirm(actionId, confirmationCode, new ResponseCallback<ValidatedIdentity>() {
                    @Override
                    public void onSuccess(final ValidatedIdentity identity) {
                        prefs.edit().putString(Constants.PREFERENCES.IDENTITY_TOKEN, identity.getToken()).commit();
                        CommonUtils.showToast(R.string.identity_confirmation_success);

                        // Uncomment this async task if you want to use confirmed Virgil Cards only
                        /*
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                registerVirgilCard(identity);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                CommonUtils.showToast(R.string.virgil_card_register_success);
                            }
                        }.execute();
                        */
                    }

                    @Override
                    public void onFailure(APIError error) {
                        CommonUtils.showToast(R.string.identity_confirmation_error);
                    }
                });
            } catch (IOException e) {
                CommonUtils.showToast(R.string.identity_confirmation_error);
            }
        }

        private void registerVirgilCard(ValidatedIdentity identity) {
            // Obtain public key for the Private Keys Service retrieved from the
            // Public Keys Service
            SearchCriteria criteria = new SearchCriteria();
            criteria.setValue("com.virgilsecurity.private-keys");

            List<VirgilCard> cards = Application.getClientFactory().getPublicKeyClient().searchApp(criteria);
            VirgilCard serviceCard = cards.get(0);

            // search the card by email identity on Virgil Keys service.
            SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder().setValue(identity.getValue()).setIncludeUnconfirmed(true);
            cards = Application.getClientFactory().getPublicKeyClient().search(criteriaBuilder.build());

            // The app is verifying whether the user really owns the provided email
            // address and getting a temporary token for public key registration
            // (in case that the card is not registered, otherwise this token will be
            // used to retrieve a private key).
            VirgilCard card;
            if (!cards.isEmpty()) {
                card = cards.get(0);
                Log.d(TAG, "Virgil Card ID: " + card.getId());
                Log.d(TAG, "Public key:\n" + Base64.decode(card.getPublicKey().getKey()).toString());

                // Load member's keys
                PrivateKeyInfo privateKeyInfo = Application.getClientFactory().getPrivateKeyClient(serviceCard).get(card.getId(), identity);
                Log.d(TAG, "Private key:\n" + Base64.decode(privateKeyInfo.getKey()).toString());

                prefs.edit()
                        .putString(Constants.PREFERENCES.VIRGIL_CARD_ID, card.getId())
                        .putString(Constants.PREFERENCES.PUBLIC_KEY, card.getPublicKey().getKey())
                        .putString(Constants.PREFERENCES.PRIVATE_KEY, privateKeyInfo.getKey())
                        .commit();
            } else {
                Log.d(TAG, "Register Virgil Card");

                // generate a new public/private key pair.
                KeyPair keyPair = KeyPairGenerator.generate();

                // The app is registering a Virgil Card which includes a
                // public key and an email address identifier. The card will
                // be used for the public key identification and searching
                // for it in the Public Keys Service.
                VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity).setPublicKey(keyPair.getPublic());
                card = Application.getClientFactory().getPublicKeyClient().createCard(vcBuilder.build(), keyPair.getPrivate());

                // Private key can be added to Virgil Security storage if you want to
                // easily synchronise your private key between devices.
                Application.getClientFactory().getPrivateKeyClient(serviceCard).stash(card.getId(), keyPair.getPrivate());

                Log.d(TAG, "Virgil Card ID: " + card.getId());
                Log.d(TAG, "Public key:\n" + keyPair.getPublic().getAsString());
                Log.d(TAG, "Private key:\n" + keyPair.getPrivate().getAsString());

                prefs.edit()
                        .putString(Constants.PREFERENCES.VIRGIL_CARD_ID, card.getId())
                        .putString(Constants.PREFERENCES.PUBLIC_KEY, keyPair.getPublic().getAsString())
                        .putString(Constants.PREFERENCES.PRIVATE_KEY, keyPair.getPrivate().getAsString())
                        .commit();
            }
        }
    }
}
