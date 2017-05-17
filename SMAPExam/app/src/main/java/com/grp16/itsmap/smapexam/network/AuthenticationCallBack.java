package com.grp16.itsmap.smapexam.network;


import com.google.firebase.auth.FirebaseUser;

public interface AuthenticationCallBack {
    void callBack(FirebaseUser user);
}
