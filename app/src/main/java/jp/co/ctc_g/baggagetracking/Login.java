package jp.co.ctc_g.baggagetracking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int RC_SIGN_IN = 1;
    private static final String CLIENT_ID = "431885225297-mk0i7mvke68euoam5tcjoqqqt0ur8sd1.apps.googleusercontent.com";
    private static final String GOOGLE_PROVIER = "accounts.google.com";
    private TextView textView;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager cognitoMng;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-1:57da617c-7e78-41e1-9a19-164bb1b0929a",
                Regions.AP_NORTHEAST_1);

        textView = (TextView) findViewById(R.id.cognito_id);
    }

    public void getId(View view) {
        new DatasetSyncTask().execute();
    }

    private class DatasetSyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... v) {
            try {
                String id = credentialsProvider.getIdentityId();
                System.out.println("IdentityID: " + id);
                return id;
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                cognitoMng = new CognitoSyncManager(
                        getApplicationContext(),
                        Regions.AP_NORTHEAST_1,
                        credentialsProvider);
                Dataset dataset = cognitoMng.openOrCreateDataset("tmpdataset");
                dataset.put("myKey", "myValue2");
                dataset.synchronize(new DefaultSyncCallback());
                textView.setText(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Map<String, String> logins = new HashMap<>();
            logins.put(GOOGLE_PROVIER, account.getIdToken());
            credentialsProvider.setLogins(logins);
        } else {
            System.out.println("Error" + result.getStatus().getStatusCode());
        }
    }
}
