package com.virgilsecurity.sdk.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.Action;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.io.IOException;

public class IdentityActivity extends AppCompatActivity {

    private Password password;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private ClientFactory clientFactory;

    private EditText m_accessToken;
    private EditText m_email;
    private TextView m_actionId;
    private EditText m_confirmation;
    private EditText m_validationToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identity_main);

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.PASSWORD)) {
            password = new Password(intent.getStringExtra(Constants.PASSWORD));
        }
        publicKey = new PublicKey(intent.getStringExtra(Constants.PUBLIC_KEY));
        privateKey = new PrivateKey(intent.getStringExtra(Constants.PRIVATE_KEY));

        m_accessToken = (EditText) findViewById(R.id.accessTokenET);
        m_email = (EditText) findViewById(R.id.emailET);
        m_actionId = (TextView) findViewById(R.id.actionIdView);
        m_confirmation = (EditText) findViewById(R.id.confirmationET);
        m_validationToken = (EditText) findViewById(R.id.validationTokenET);
    }

    public void init(View view) {
        String accessToken = m_accessToken.getText().toString();
        clientFactory = new ClientFactory(accessToken);
    }

    public void verify(View view) {
        String email = m_email.getText().toString();

        try {
            getClientFactory().getIdentityClient().verify(IdentityType.EMAIL, email, new ResponseCallback<Action>() {
                @Override
                public void onSuccess(Action action) {
                    m_actionId.setText(action.getActionId());
                }

                @Override
                public void onFailure(APIError apiError) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Verification error: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void confirm(View view) {
        String actionId = m_actionId.getText().toString();
        String confirmationCode = m_confirmation.getText().toString();

        try {
            getClientFactory().getIdentityClient().confirm(actionId, confirmationCode, new ResponseCallback<ValidatedIdentity>() {
                        @Override
                        public void onSuccess(ValidatedIdentity validatedIdentity) {
                            m_validationToken.setText(validatedIdentity.getToken());
                        }

                        @Override
                        public void onFailure(APIError apiError) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Confirmation error: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
            );
        } catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void validate(View view) {
        String actionId = m_actionId.getText().toString();
        String confirmationCode = m_confirmation.getText().toString();


        ValidatedIdentity identity = new ValidatedIdentity();
        identity.setType(IdentityType.EMAIL);
        identity.setValue(m_email.getText().toString());
        identity.setToken(m_validationToken.getText().toString());

        try {
            getClientFactory().getIdentityClient().validate(identity, new VoidResponseCallback() {
                @Override
                public void onSuccess(boolean b) {
                    String text = b ? "Validated" : "Not validated";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onFailure(APIError apiError) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Validation error: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        } catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void openPublicKeys(View view) {

        try {
            Intent intent = new Intent(this, PublicKeysActivity.class);

            intent.putExtra(Constants.ACCESS_TOKEN, m_accessToken.getText().toString());
            intent.putExtra(Constants.EMAIL, m_email.getText().toString());

            if (password != null) {
                intent.putExtra(Constants.PASSWORD, password.toString());
            }
            intent.putExtra(Constants.PUBLIC_KEY, publicKey);
            intent.putExtra(Constants.PRIVATE_KEY, privateKey);

            startActivity(intent);
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private ClientFactory getClientFactory() {
        if (clientFactory == null) {
            String accessToken = m_accessToken.getText().toString();
            clientFactory = new ClientFactory(accessToken);
        }
        return clientFactory;
    }

}
