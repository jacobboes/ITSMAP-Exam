package com.grp16.itsmap.smapexam.network;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.Executor;

//https://firebase.google.com/docs/auth/android/start/
public class Authentication{

    private FirebaseAuth auth;

    Authentication(){
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    public boolean isLogedIn(){
        return auth.getCurrentUser() != null;
    }

    public void logIn(String username, String password, final AuthenticationCallBack callBack){
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        callBack.callBack(auth.getCurrentUser());
                    } else {
                        callBack.callBack(null);
                    }
                }
            });
    }

    public void logOut(){
        auth.signOut();
    }

    public void createAccount(String username, String password, final AuthenticationCallBack callBack){
        auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callBack.callBack(auth.getCurrentUser());

                        } else {
                            callBack.callBack(null);
                        }
                    }
                });
    }

}
