package com.example.user.simpleui;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;

/**
 * Created by user on 2016/5/5.
 */
public class SimpleUIApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initial parse api

        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId("oWpbagHetb8cx8ulj17xbomr4E6iGNQ5qJBV7KRu") //2016/5/5, APP ID for identify which app, copy from Parse, "App Id" of Parse
                        .clientKey("Tj0IIzdyDaytUouiU0yLYSNsOcHdy6nyAetOhboJ") //2016/5/5, for identify who is, "Client Key" of Parse
//                        .applicationId("1IIbdKr6rgMlclbPtrTcibogTq4wst4GVJC2dOX2") //2016/5/5, 老師的, APP ID for identify which app, copy from Parse, "App Id" of Parse
//                        .clientKey("lYpFFD6Bz8mendveOW91UvjypoGruuaaQPc4EUyR") //2016/5/5, 老師的, for identify who is, "Client Key" of Parse
                        .server("https://parseapi.back4app.com/") // 2016/5/5, server location , "Parse API Address" of Parse


                        .build()
        );
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }
}
