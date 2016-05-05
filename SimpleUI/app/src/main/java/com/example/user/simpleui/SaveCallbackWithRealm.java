package com.example.user.simpleui;

import com.parse.ParseException;
import com.parse.SaveCallback;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by user on 2016/5/5.
 */
public class SaveCallbackWithRealm implements SaveCallback {

    RealmObject realmObject;
    SaveCallback saveCallback;

    public SaveCallbackWithRealm(RealmObject realmObject, SaveCallback callback){
        this.realmObject = realmObject;
        this.saveCallback = callback;
    }

    @Override
    public void done(ParseException e) {

        if (e == null) { // null stand for no error
            Realm realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            realm.copyToRealm(realmObject);
            realm.commitTransaction();
            realm.close(); //2016.0505
        }
        saveCallback.done(e);
    }
}
