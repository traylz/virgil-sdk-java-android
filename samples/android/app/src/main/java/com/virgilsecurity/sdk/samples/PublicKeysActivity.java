package com.virgilsecurity.sdk.samples;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.virgilsecurity.sdk.client.model.publickey.SignResponse;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PublicKeysActivity extends AppCompatActivity {

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

        m_virgilCards = (ListView) findViewById(R.id.virgilCardsLV);

        virgilCardsAdapter = new VirgilCardListAdapter(PublicKeysActivity.this, new ArrayList<VirgilCard>());
        m_virgilCards.setAdapter(virgilCardsAdapter);
        m_virgilCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PublicKeysActivity.this);
                builder.setTitle("Virgil Card signing")
                        .setMessage("Would you like to process this Virgil Card?")
                        .setPositiveButton("Sing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sign(virgilCardsAdapter.getItem(position));
                            }
                        })
                        .setNegativeButton("Unsing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                unsign(virgilCardsAdapter.getItem(position));
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.create().show();
            }
        });
    }

    public void createVC(View view) {
        String accessToken = m_accessToken.getText().toString();

        ValidatedIdentity identity = new ValidatedIdentity();
        identity.setType(IdentityType.EMAIL);
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
        identity.setType(IdentityType.EMAIL);
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

            SearchCriteria.Builder criteriaBuilder = new SearchCriteria.Builder().setValue(email).setIncludeUnconfirmed(true);
            clientFactory.getPublicKeyClient().search(criteriaBuilder.build(), privateKey, password, callback);
        } else {
            String appId = m_appIDET.getText().toString();

            SearchCriteria criteria = new SearchCriteria();
            criteria.setValue(appId);
            clientFactory.getPublicKeyClient().searchApp(criteria, privateKey, password, callback);
        }
    }

    private void sign(VirgilCard virgilCard) {
        final String signerCardId = m_vcID.getText().toString();

        if (signerCardId.trim().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please, generate Virgil Card first", Toast.LENGTH_SHORT);
            toast.show();
        }
        clientFactory.getPublicKeyClient().signCard(virgilCard.getId(), virgilCard.getHash(), signerCardId, privateKey, new ResponseCallback<SignResponse>() {
            @Override
            public void onSuccess(SignResponse signResponse) {
                Toast toast = Toast.makeText(getApplicationContext(), "Signed", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(APIError apiError) {
                Toast toast = Toast.makeText(getApplicationContext(), "Sign error: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void unsign(VirgilCard virgilCard) {
        final String signerCardId = m_vcID.getText().toString();

        if (signerCardId.trim().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please, generate Virgil Card first", Toast.LENGTH_SHORT);
            toast.show();
        }
        clientFactory.getPublicKeyClient().unsignCard(virgilCard.getId(), signerCardId, privateKey, new VoidResponseCallback() {
            @Override
            public void onSuccess(boolean b) {
                String text = "Virgil Card unsigned";
                if (!b) {
                    text = "Not unsigned";
                }
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(APIError apiError) {
                Toast toast = Toast.makeText(getApplicationContext(), "Sign error: " + apiError.getErrorCode(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void openPrivateKeys(View view) {

//        try {
//            Intent intent = new Intent(this, PublicKeysActivity.class);
//
//            intent.putExtra(Constants.ACCESS_TOKEN, m_accessToken.getText().toString());
//            intent.putExtra(Constants.EMAIL, m_email.getText().toString());
//
//            if (password != null) {
//                intent.putExtra(Constants.PASSWORD, password.toString());
//            }
//            intent.putExtra(Constants.PUBLIC_KEY, publicKey);
//            intent.putExtra(Constants.PRIVATE_KEY, privateKey);
//
//            startActivity(intent);
//        }
//        catch (Exception e) {
//            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
//            toast.show();
//        }
    }

}
