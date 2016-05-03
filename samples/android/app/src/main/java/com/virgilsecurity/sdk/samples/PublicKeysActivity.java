package com.virgilsecurity.sdk.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.http.ResponseCallback;
import com.virgilsecurity.sdk.client.http.VoidResponseCallback;
import com.virgilsecurity.sdk.client.model.APIError;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.SearchCriteria;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.util.ArrayList;
import java.util.List;

public class PublicKeysActivity extends AppCompatActivity {

    private static final String IDENTITY_TYPE = "EmailAddress";

    private Password password;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String email;
    private String accessToken;

    private ClientFactory clientFactory;

    private TextView m_accessToken;
    private TextView m_email;
    private TextView m_vcID;
    private EditText m_emailET;
    private EditText m_appIDET;
    private ListView m_virgilCards;

    VirgilCardListAdapter virgilCardsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_keys_main);

        Intent intent = getIntent();
        accessToken = intent.getStringExtra(Constants.ACCESS_TOKEN);
        email = intent.getStringExtra(Constants.EMAIL);

        if (intent.hasExtra(Constants.PASSWORD)) {
            password = new Password(intent.getStringExtra(Constants.PASSWORD));
        }
        publicKey = (PublicKey) intent.getSerializableExtra(Constants.PUBLIC_KEY);
        privateKey = (PrivateKey) intent.getSerializableExtra(Constants.PRIVATE_KEY);

        clientFactory = new ClientFactory(accessToken);

        m_accessToken = (TextView) findViewById(R.id.accessToken);
        m_accessToken.setText(accessToken);

        m_email = (TextView) findViewById(R.id.email);
        m_email.setText(email);

        m_vcID = (TextView) findViewById(R.id.vcID);
        m_emailET = (EditText) findViewById(R.id.emailET);
        m_appIDET = (EditText) findViewById(R.id.appIDET);

        m_emailET.setText(email);

        m_virgilCards = (ListView) findViewById(R.id.virgilCardsLV);

        virgilCardsAdapter = new VirgilCardListAdapter(PublicKeysActivity.this, new ArrayList<VirgilCard>());
        m_virgilCards.setAdapter(virgilCardsAdapter);
    }

    public void createVC(View view) {
        String accessToken = m_accessToken.getText().toString();

        ValidatedIdentity identity = new ValidatedIdentity();
        identity.setType(IDENTITY_TYPE);
        identity.setValue(email);

        VirgilCardTemplate.Builder vcBuilder = new VirgilCardTemplate.Builder().setIdentity(identity)
                .setPublicKey(publicKey);

        ResponseCallback<VirgilCard> callback = new ResponseCallback<VirgilCard>() {
            @Override
            public void onSuccess(VirgilCard virgilCard) {
                m_vcID.setText(virgilCard.getId());
            }

            @Override
            public void onFailure(APIError apiError) {
                Toast toast = Toast.makeText(getApplicationContext(), "Virgil Card not created: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        clientFactory.getPublicKeyClient().createCard(vcBuilder.build(), privateKey, password, callback);
    }

    public void revokeVC(View view) {
        final String cardId = m_vcID.getText().toString();

        if (cardId.trim().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please, generate Virgil Card first", Toast.LENGTH_SHORT);
            toast.show();
        }

        ValidatedIdentity identity = new ValidatedIdentity();
        identity.setType(IDENTITY_TYPE);
        identity.setValue(email);

        clientFactory.getPublicKeyClient().deleteCard(identity, cardId, privateKey, password, new VoidResponseCallback() {
            @Override
            public void onSuccess(boolean b) {
                if (b) {
                    m_vcID.setText("");
                }
            }

            @Override
            public void onFailure(APIError apiError) {
                Toast toast = Toast.makeText(getApplicationContext(), "Virgil Card is not deleted: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void search(View view) {
        ResponseCallback<List<VirgilCard>> callback = new ResponseCallback<List<VirgilCard>>() {
            @Override
            public void onSuccess(List<VirgilCard> virgilCards) {
                virgilCardsAdapter.clear();
                virgilCardsAdapter.addAll(virgilCards);
                virgilCardsAdapter.notifyDataSetChanged();

                Log.d("PublicKeysActivity", "Found " + virgilCards.size() + " Virgil Cards");
            }

            @Override
            public void onFailure(APIError apiError) {
                Toast toast = Toast.makeText(getApplicationContext(), "Search error: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        if (view.getId() == R.id.search) {

            String email = m_emailET.getText().toString();

            SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder().setType(IDENTITY_TYPE).setValue(email).setIncludeUnauthorized(true);
            clientFactory.getPublicKeyClient().search(criteriaBuilder.build(), callback);
        } else {
            String appId = m_appIDET.getText().toString();

            SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder().setType(IdentityType.APPLICATION).setValue(appId);
            clientFactory.getPublicKeyClient().search(criteriaBuilder.build(), callback);
        }
    }

}
