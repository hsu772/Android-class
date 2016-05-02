package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
        Log.d("debug", "DrinkMenuActivity OnCreate");
    }
    //E:2016.05.02, Create button function "goToMenu"

    //S:2016.05.02, Create button function "add", to count the number
    public void add (View view){ // button 就是一個view, 所以 view 可以直接帶用
        Button button = (Button) view; //view 做轉型

        String text = button.getText().toString();

        int count = Integer.parseInt(text); // Integer 可以把東西轉成 int
        count++; //count ++

        button.setText(String.valueOf(count));//count 轉成 string.
    }
    //E:2016.05.02, Create button function "add", to count the number

    //S:2016.05.02, Create button function "cancel"
    public void cancel (View view){
        finish(); // 直接 destroy 這個 activity, 並回到上一頁
    }
    //E:2016.05.02, Create button function "cancel"

    //S:2016.05.02, Create button function "done"
    public void done (View view){

    }

    public JSONArray getData (){ //收集 data for done(), JSON like sharePerference,
        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.root); //取得 linearLayout (id=root), 再取得 button
        JSONArray jsonArray = new JSONArray(); // 在JSONArray 中包 JSON Object.

        for (int i=1; i < 4; i++) {
            LinearLayout linearLayout = rootLinearLayout.getChildAt(i); //getChildAt 取得 child

            TextView textView = (TextView) LinearLayout.getChildAt(0);
            Button mButton = (Button) LinearLayout.getChildAt(1);
            Button lButton = (Button) LinearLayout.getChildAt(2);

            String drinkName = textView.getText().toString(); //取得 textView 的名字
            int m = Integer. parseInt(mButton.getText().toString()); //取得 mButton 的字串, 並轉成 int
            int l = Integer. parseInt(lButton.getText().toString());

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put ("name", drinkName); // format:(key,value), "name" is key,// "name":"black tea", "mNumber":5
                jsonObject.put ("m", m); // format:(key,value), "name" is key,// "name":"black tea", "mNumber":5
                jsonObject.put ("l", l); // format:(key,value), "name" is key,// "name":"black tea", "mNumber":5

                jsonArray.put(jsonObject); // 把 JsonObject  放到 JsonArray
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return jsonArray; // return jsonArray
    }
    //E:2016.05.02, Create button function "done"

        @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","DrinkMenuActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "DrinkMenuActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","DrinkMenuActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "DrinkMenuActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "DrinkMenuActivity onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "DrinkMenuActivity onRestart");
    }

    //E:2016.05.02, show onStart()/onResume()/ onPause()
}
