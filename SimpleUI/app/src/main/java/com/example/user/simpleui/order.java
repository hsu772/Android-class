package com.example.user.simpleui;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.lang.reflect.ParameterizedType;

import io.realm.RealmObject;

/**
 * Created by user on 2016/4/25.
 */
/*
public class order {
    String note;
    String drinkName;
    String storeInfo;
}
*/
public class Order extends RealmObject{
    private String note;
    //private String drinkName;
    private String menuResults="";
    private String storeInfo;

    byte[] photo = null; // don't need to store at Realm

    String photoURL=""; //2016.0512, to get the photo's URL

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMenuResults() {
        return menuResults;
    }

    public void setMenuResults(String menuResults) {
        this.menuResults = menuResults;
    }

    public String getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(String storeInfo) {
        this.storeInfo = storeInfo;
    }

    //S: 2016.0505
    // create new parse object and update to server
    public void saveToRemote(SaveCallback saveCallback){
        ParseObject parseObject = new ParseObject("Order");
        parseObject.put("note", note);
        parseObject.put("storeInfo", storeInfo);
        parseObject.put("menuResults", menuResults);

        if (photo != null){ // if photo is not null.
            ParseFile file = new ParseFile("photo.png", photo); // use ParseFile to pars large file
            parseObject.put("photo", file); // upload photo
        }
        parseObject.saveInBackground(saveCallback); // update to Parse server
    }
    //E: 2016.0505


}

