package com.android.sergeyfitis.geektalksdemo.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.helpers.Prefs;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.rl_root_login)
    RelativeLayout rlRootLogin;
    private CallbackManager callbackManager;

    @Bind(R.id.iv_login_logo)
    ImageView ivLoginLogo;
    @Bind(R.id.fab_login)
    FloatingActionButton fabLogin;
    @Bind(R.id.cpb_login)
    CircularProgressBar cpbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Before use place you FB app id to <string name="facebook_app_id">
        if (TextUtils.isEmpty(Prefs.getFbAccessToken())) {
            registerLoginCallbacks();
        } else {
            openMainActivity();
        }
    }

    private void registerLoginCallbacks() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Utils.saveFbAuth(accessToken);
                openMainActivity();
            }

            @Override
            public void onCancel() {
                showMessage(getString(R.string.login_cancelled));
            }

            @Override
            public void onError(FacebookException error) {
                showMessage(error.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.fab_login)
    void login() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                "user_likes",
                "user_managed_groups",
                "user_about_me",
                "user_actions.news",
                "public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(rlRootLogin, message, Snackbar.LENGTH_SHORT).show();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

