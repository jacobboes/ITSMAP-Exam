package com.grp16.itsmap.smapexam.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.network.Authentication;
import com.grp16.itsmap.smapexam.network.AuthenticationCallBack;

public class LoginActivity extends AppCompatActivity {

    private static final String USER_PREFERENCES = "AR.app.preferences";

    private static final String USERNAME_KEY = "username.key";
    private static final String PASSWORD_KEY = "password.key";

    private EditText username;
    private EditText password;
    private Button loginBtn;

    private Authentication authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        authentication = new Authentication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authentication.isLoggedIn()) {
            startMainActivity();
        }
    }

    private void initializeViews() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    private void logIn() {
        authentication.logIn(getUsername(), getPassword(), new AuthenticationCallBack() {
            @Override
            public void onSuccess() {
                startMainActivity();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(LoginActivity.this, "Failed log in. " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @NonNull
    private String getPassword() {
        return password != null ? password.getText().toString() : "";
    }

    @NonNull
    private String getUsername() {
        return username != null ? username.getText().toString() : "";
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void savePreferences() {
        SharedPreferences settings = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USERNAME_KEY, getUsername());
        editor.putString(PASSWORD_KEY, getPassword());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences settings = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        username.setText(settings.getString(USERNAME_KEY, ""));
        password.setText(settings.getString(PASSWORD_KEY, ""));
    }
}
