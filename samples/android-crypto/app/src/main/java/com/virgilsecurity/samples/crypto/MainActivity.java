package com.virgilsecurity.samples.crypto;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;
import com.virgilsecurity.sdk.crypto.Password;
import com.virgilsecurity.sdk.crypto.PrivateKey;
import com.virgilsecurity.sdk.crypto.PublicKey;
import com.virgilsecurity.sdk.crypto.Recipient;
import com.virgilsecurity.sdk.crypto.StreamCipher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PUBLIC_KEY = "public_key";
    private static final String PRIVATE_KEY = "private_key";
    private static final String RECIPIENT_ID = "recipient_id";

    private static final int FILE_SELECT_CODE = 1;

    private Uri mFileUri = null;

    private String mRecipientId = null;
    private PublicKey mPublicKey = null;
    private PrivateKey mPrivateKey = null;

    private TextView mFileNameView;
    private EditText mPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileNameView = (TextView) findViewById(R.id.file_name);
        mPasswordET = (EditText) findViewById(R.id.password);

        Button selectFileBtn = (Button) findViewById(R.id.select_file);
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button encodeWithKeyBtn = (Button) findViewById(R.id.encode);
        encodeWithKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileUri == null) {
                    Toast.makeText(MainActivity.this, R.string.file_not_selected, Toast.LENGTH_SHORT).show();
                    return;
                }

                new EncodeWithKeyAsynkTask().execute(mFileUri);

            }
        });

        Button decodeWithKeyBtn = (Button) findViewById(R.id.decode);
        decodeWithKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileUri == null) {
                    Toast.makeText(MainActivity.this, R.string.file_not_selected, Toast.LENGTH_SHORT).show();
                    return;
                }

                new DecodeWithKeyAsynkTask().execute(mFileUri);
            }
        });

        Button encodeWithPwdBtn = (Button) findViewById(R.id.encode_pwd);
        encodeWithPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileUri == null) {
                    Toast.makeText(MainActivity.this, R.string.file_not_selected, Toast.LENGTH_SHORT).show();
                    return;
                }

                new EncodeWithPwdAsynkTask(mPasswordET.getText().toString()).execute(mFileUri);
            }
        });

        Button decodeWithPwdBtn = (Button) findViewById(R.id.decode_pwd);
        decodeWithPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileUri == null) {
                    Toast.makeText(MainActivity.this, R.string.file_not_selected, Toast.LENGTH_SHORT).show();
                    return;
                }

                new DecodeWithPwdAsynkTask(mPasswordET.getText().toString()).execute(mFileUri);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains(PUBLIC_KEY)) {
            // Load key pair from shared preferences
            mRecipientId = prefs.getString(RECIPIENT_ID, "12345");
            mPublicKey = new PublicKey(prefs.getString(PUBLIC_KEY, ""));
            mPrivateKey = new PrivateKey(prefs.getString(PRIVATE_KEY, ""));
        } else {
            // Generate key pair
            KeyPair keyPair = KeyPairGenerator.generate();
            mRecipientId = UUID.randomUUID().toString();
            mPublicKey = keyPair.getPublic();
            mPrivateKey = keyPair.getPrivate();
            prefs.edit()
                    .putString(RECIPIENT_ID, mRecipientId)
                    .putString(PUBLIC_KEY, mPublicKey.getAsString())
                    .putString(PRIVATE_KEY, mPrivateKey.getAsString())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == -1) {
            mFileUri = data.getData();
            mFileNameView.setText(mFileUri.toString());
        }
    }

    private class EncodeWithKeyAsynkTask extends CryptoAsynkTask {

        @Override
        protected String doInBackground(Uri... params) {
            final File encodedFile = new File(getProcessedFilesStorageDir(), getFileName(params[0]) + ".encoded");
            Log.d(TAG, "Encrypt to file " + encodedFile.toString());
            try (StreamCipher cipher = new StreamCipher();
                 InputStream in = getContentResolver().openInputStream(mFileUri);
                 OutputStream out = new FileOutputStream(encodedFile)) {

                cipher.addKeyRecipient(mRecipientId, mPublicKey);
                cipher.encrypt(in, out, true);

                return "Encrypted data saved to file " + encodedFile.toString();
            } catch (Exception e) {
                Log.e(TAG, "File encryption error", e);
            }
            return "Encryption failed";
        }
    }

    private class EncodeWithPwdAsynkTask extends CryptoAsynkTask {

        private String mPassword;

        public EncodeWithPwdAsynkTask(String password) {
            mPassword = password;
        }

        @Override
        protected String doInBackground(Uri... params) {
            final File encodedFile = new File(getProcessedFilesStorageDir(), getFileName(params[0]) + ".encoded");
            Log.d(TAG, "Encrypt to file " + encodedFile.toString());
            try (StreamCipher cipher = new StreamCipher();
                 InputStream in = getContentResolver().openInputStream(mFileUri);
                 OutputStream out = new FileOutputStream(encodedFile)) {

                cipher.addPasswordRecipient(mPassword);
                cipher.encrypt(in, out, true);

                return "Encrypted data saved to file " + encodedFile.toString();
            } catch (Exception e) {
                Log.e(TAG, "File encryption error", e);
            }
            return "Encryption failed";
        }
    }

    private class DecodeWithKeyAsynkTask extends CryptoAsynkTask {

        @Override
        protected String doInBackground(Uri... params) {
            String fileName = getFileName(params[0]);
            if (fileName.endsWith(".encoded")) {
                fileName = fileName.replace(".encoded", "");
            }

            final File decodedFile = new File(getProcessedFilesStorageDir(), fileName);
            Log.d(TAG, "Decrypt to file " + decodedFile.toString());

            try (StreamCipher cipher = new StreamCipher();
                 InputStream in = getContentResolver().openInputStream(mFileUri);
                 OutputStream out = new FileOutputStream(decodedFile)) {
                cipher.decryptWithKey(in, out, new Recipient(mRecipientId), mPrivateKey);

                return "Decrypted data saved to file " + decodedFile.toString();
            } catch (Exception e) {
                Log.e(TAG, "File decryption error", e);
            }
            return "Decryption failed";
        }
    }

    private class DecodeWithPwdAsynkTask extends CryptoAsynkTask {

        private String mPassword;

        public DecodeWithPwdAsynkTask(String password) {
            mPassword = password;
        }

        @Override
        protected String doInBackground(Uri... params) {
            String fileName = getFileName(params[0]);
            if (fileName.endsWith(".encoded")) {
                fileName = fileName.replace(".encoded", "");
            }

            final File decodedFile = new File(getProcessedFilesStorageDir(), fileName);
            Log.d(TAG, "Decrypt to file " + decodedFile.toString());

            try (StreamCipher cipher = new StreamCipher();
                 InputStream in = getContentResolver().openInputStream(mFileUri);
                 OutputStream out = new FileOutputStream(decodedFile)) {
                cipher.decryptWithPassword(in, out, new Password(mPassword));

                return "Decrypted data saved to file " + decodedFile.toString();
            } catch (Exception e) {
                Log.e(TAG, "File decryption error", e);
            }
            return "Decryption failed";
        }
    }

    private abstract class CryptoAsynkTask extends  AsyncTask<Uri, Void, String> {

        private ProgressDialog spinnerDialog = null;

        @Override
        protected void onPreExecute() {
            // Show progress dialog
            spinnerDialog = ProgressDialog.show(MainActivity.this, "", getBaseContext().getString(R.string.please_wait), true, true,
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            spinnerDialog = null;
                        }
                    });
        }

        @Override
        protected void onPostExecute(String s) {
            if (spinnerDialog != null && spinnerDialog.isShowing()) {
                spinnerDialog.dismiss();
                spinnerDialog = null;
            }
            Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getProcessedFilesStorageDir() {
        // Get the directory for the app's private pictures directory.
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Processed");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
        }
        return file;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
