package com.grp16.itsmap.smapexam.network;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//https://firebase.google.com/docs/auth/android/start/
public class Authentication {

    private FirebaseAuth auth;

    public Authentication() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void logIn(String username, String password, final AuthenticationCallBack callBack) {
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && auth.getCurrentUser() != null) {
                            callBack.onSuccess();
                        } else {
                            callBack.onFailed(task.getException().getMessage().toString());
                        }
                    }
                });
    }

    public void logOut() {
        auth.signOut();
    }

    public void createAccount(String username, String password, final AuthenticationCallBack callBack) {
        auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && auth.getCurrentUser() != null) {
                            callBack.onSuccess();

                        } else {
                            callBack.onFailed(task.getException().getMessage().toString());
                        }
                    }
                });
    }

}
