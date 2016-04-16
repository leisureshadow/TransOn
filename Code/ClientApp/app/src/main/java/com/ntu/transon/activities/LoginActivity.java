package com.ntu.transon.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Message;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;
import com.ntu.transon.AndroidApplication;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.R;
import com.ntu.transon.User;
import com.ntu.transon.connection.Connector;
import com.ntu.transon.connection.Listener;
import com.ntu.transon.connection.LoginHandler;
import com.ntu.transon.connection.ServiceHandler;

import java.io.IOException;


/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends PlusBaseActivity {
    private View mProgressView;
    private SignInButton mPlusSignInButton;
    private View mSignOutButtons;
    private View mLoginFormView;
    private Handler signInHandler = new SignInHandler();
    private String accessToken = null;
    private boolean isTokenValid = false;

    private void gotoLobby(){
        finish();
        Intent intent = new Intent(LoginActivity.this,LobbyActivity.class);
//        Intent intent = new Intent(LoginActivity.this,MeetingRoomActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) findViewById(R.id.plus_sign_in_button);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play
            // Services.
            mPlusSignInButton.setVisibility(View.GONE);
            // TODO: show error message
            Log.e("GooglePlay", "Google Play services not supported.");
            return;
        }


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSignOutButtons = findViewById(R.id.plus_sign_out_buttons);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void requestAccessToken() {
        // get access token
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (!isTokenValid && accessToken != null)
                        GoogleAuthUtil.clearToken(LoginActivity.this, accessToken);
                    accessToken = GoogleAuthUtil.getToken(LoginActivity.this,
                                    Plus.AccountApi.getAccountName(getGoogleApiClient()),
                                    "oauth2:" +
                                    " https://www.googleapis.com/auth/plus.login" +
                                    " https://www.googleapis.com/auth/plus.profile.emails.read");
                    isTokenValid = true;
                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.
                    // TODO: handle this
                    throw new RuntimeException(transientEx);
                } catch (UserRecoverableAuthException e) {
                    // Recover
                    accessToken = null;
                    signOut();
                    return null;
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                    // TODO: handle this
                    throw new RuntimeException(authEx);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (accessToken != null) {
                    Log.i(TAG, "Access token retrieved:" + accessToken);
                    Packet loginPacket = new Packet("Login");
                    loginPacket.addItems("accessToken", accessToken);
                    final LoginHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
                    loginHandler.setInitPck(loginPacket);

                    loginHandler.addListener("Login", new Listener() {
                        @Override
                        public void handle(Packet p) {
                            loginHandler.removeListener("Login");
                            User user = (User) p.getItem("user", User.class);
                            // set User
                            AndroidApplication androidApp = AndroidApplication.getInstance();
                            androidApp.setUser(user);
                            Log.v(TAG, "init " + user.toString());
                            // login success, goto next step
                            signInHandler.sendEmptyMessage(0);
                        }
                    });
                    sendTokenToServer();
                }
            }
        };
        showProgress(true);
        task.execute();
    }

    private void sendTokenToServer(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // connect to server
                try {
                    ServiceHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
                    Connector.connect(loginHandler, 1126);
                    AndroidApplication.getInstance().startReactor();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

//            @Override
//            protected void onPostExecute(Void aVoid) {
                // send token and listen for the returned User information
//                super.onPostExecute(aVoid);
//                final ServiceHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
//                Packet loginPacket = new Packet("Login");
//                loginPacket.addItems("accessToken", accessToken);

                /*
                loginHandler.addListener("Login", new Listener() {
                    @Override
                    public void handle(Packet p) {
                        loginHandler.removeListener("Login");
                        User user = (User) p.getItem("user", User.class);
                        // set User
                        AndroidApplication androidApp = AndroidApplication.getInstance();
                        androidApp.setUser(user);
                        Log.v(TAG, "init " + user.toString());
                        // login success, goto next step
                        signInHandler.sendEmptyMessage(0);
                    }
                });
                */
//                if (Looper.myLooper() == Looper.getMainLooper())
//                    Log.e("I'm in","main");
//                try {
//                    loginHandler.write(loginPacket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        };
        task.execute();
    }

    @Override
    protected void onPlusClientSignIn() {
        requestAccessToken();
    }

    private class SignInHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showProgress(false);
                    gotoLobby();

                    //Set up sign out and disconnect buttons.
                    Button signOutButton = (Button) findViewById(R.id.plus_sign_out_button);
                    signOutButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signOut();
                        }
                    });
                    Button disconnectButton = (Button) findViewById(R.id.plus_disconnect_button);
                    disconnectButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isTokenValid = false;
                            revokeAccess();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    protected void onPlusClientBlockingUI(boolean show) {
        showProgress(show);
    }

    @Override
    protected void updateConnectButtonState() {
        boolean connected = getGoogleApiClient().isConnected();
        mSignOutButtons.setVisibility(connected ? View.VISIBLE : View.GONE);
        mPlusSignInButton.setVisibility(connected ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPlusClientRevokeAccess() {
        // TODO: Access to the user's G+ account has been revoked.  Per the developer terms, delete
        // any stored user data here.
    }

    @Override
    protected void onPlusClientSignOut() {};


    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS;
    }
}



