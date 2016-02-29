package com.virgilsecurity.sdk.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.virgilsecurity.sdk.crypto.CryptoHelper;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;

public class CryptoActivity extends AppCompatActivity {

    private static final String RECIPIENT_ID = "069c9f8e-0852-4dd2-a43a-85d40449f1c6";

    private KeyPair m_keyPair;

    private EditText m_password;
    private EditText m_publicKey;
    private EditText m_privateKey;
    private EditText m_text;
    private EditText m_result;
    private EditText m_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crypto_main);

        m_password = (EditText) findViewById(R.id.passwordET);
        m_publicKey = (EditText) findViewById(R.id.publicKeyET);
        m_privateKey = (EditText) findViewById(R.id.privateKeyET);
        m_text = (EditText) findViewById(R.id.enteredTextET);
        m_result = (EditText) findViewById(R.id.resultTextET);
        m_sign = (EditText) findViewById(R.id.signET);
    }

    public void generateKeyPair(View view) {


        String password = m_password.getText().toString();

        if (password.trim().length() > 0) {
            m_keyPair = KeyPairGenerator.generate(password);
        } else {
            m_keyPair = KeyPairGenerator.generate();
        }

        m_publicKey.setText(m_keyPair.getPublic().getAsString());
        m_privateKey.setText(m_keyPair.getPrivate().getAsString());
    }


    public void encode(View view) {
        PublicKey publicKey = new PublicKey(m_publicKey.getText().toString());
        String text = m_text.getText().toString();

        String processedText = "";

        try {
            processedText = CryptoHelper.encrypt(text, RECIPIENT_ID, publicKey);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

        m_result.setText(processedText);
    }

    public void decode(View view) {
        String password = m_password.getText().toString();
        PrivateKey privateKey = new PrivateKey(m_privateKey.getText().toString());
        String text = m_text.getText().toString();

        String processedText = "";

        try {
            if (password.trim().length() == 0) {
                processedText = CryptoHelper.decrypt(text, RECIPIENT_ID, privateKey);
            } else {
                processedText = CryptoHelper.decrypt(text, RECIPIENT_ID, privateKey, new Password(password));
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }

        m_result.setText(processedText);
    }

    public void sign(View view) {
        String password = m_password.getText().toString();
        PrivateKey privateKey = new PrivateKey(m_privateKey.getText().toString());
        String text = m_text.getText().toString();

        String sign = "";
        try {
            if (password.trim().length() == 0) {
                sign = CryptoHelper.sign(text, privateKey);
            } else {
                sign = CryptoHelper.sign(text, privateKey, new Password(password));
            }
            m_sign.setText(sign);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void verifySign(View view) {
        PublicKey publicKey = new PublicKey(m_publicKey.getText().toString());
        String text = m_text.getText().toString();
        String sign = m_sign.getText().toString();

        String toastText = "";
        try {
            if (CryptoHelper.verify(text, sign, publicKey)) {
                toastText = "Signature verified";
            } else {
                toastText = "Signature NOT verified";
            }
        } catch (Exception e) {
            toastText = e.getMessage();
        }
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void openIdentity(View view) {
        String password = m_password.getText().toString();
        String publicKey = m_publicKey.getText().toString();
        String privateKey = m_privateKey.getText().toString();

        try {
            if (publicKey.trim().length() == 0) {
                throw new IllegalArgumentException("Public key should be defined");
            }
            if (privateKey.trim().length() == 0) {
                throw new IllegalArgumentException("Private key should be defined");
            }

            Intent intent = new Intent(this, IdentityActivity.class);
            intent.putExtra(Constants.PASSWORD, password);
            intent.putExtra(Constants.PUBLIC_KEY, publicKey);
            intent.putExtra(Constants.PRIVATE_KEY, privateKey);
            startActivity(intent);
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
