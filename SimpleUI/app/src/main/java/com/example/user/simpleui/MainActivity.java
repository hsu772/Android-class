package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView2; //variable name,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = (TextView)findViewById(R.id.textView); //The "textView" of "R.id.textView" map to the id "textView" (at activity_main.xml)
                                                          // The code try to get the id name "textView"

    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        textView2.setText("Android Class2"); // When click the button, it will change the text "Android Class2" to the id:textView
    }
}
