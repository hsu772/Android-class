package com.example.user.simpleui;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//2016.0512, to receive intent
public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView menuResults;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView) findViewById(R.id.note);
        storeInfo = (TextView)findViewById(R.id.storeInfo);
        menuResults =(TextView)findViewById(R.id.menuResults);
        photo = (ImageView)findViewById(R.id.photoImageView);

        Intent intent = getIntent(); //get intent
        note.setText(intent.getStringExtra("note"));
        storeInfo.setText(intent.getStringExtra("storeInfo"));

        //translate the menuResult to JasonArray
        String results = intent.getStringExtra("menuResults");
        String text="";
        try {
            JSONArray jsonArray = new JSONArray(results);
            for (int i=0;i< jsonArray.length();i++){
                JSONObject object =jsonArray.getJSONObject(i);
                text += object.getString("name")+":大杯"+object.getString("l")+"杯 中杯"+object.getString("m")+"杯"+"\n";
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        menuResults.setText(text); // show result

        String url = intent.getStringExtra("photoURL");
        if (!url.equals("")) {// if image exist, load picture
            Picasso.with(this).load(url).into(photo);
        }



    }
}
