package com.gibbo.salvatore;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by federico.scaltriti on 24/02/2018.
 */

public class Salvatore extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
