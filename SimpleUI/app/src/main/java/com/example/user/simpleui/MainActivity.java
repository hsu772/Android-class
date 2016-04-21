package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView2; //variable name for text
    EditText editText2; // the variable name for edit
    RadioGroup radioGroup; // capture RadioGroup
    String sex="Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = (TextView)findViewById(R.id.textView); //The "textView" of "R.id.textView" map to the id "textView" (at activity_main.xml)
                                                          // The code try to get the id name "textView"
        editText2 = (EditText)findViewById(R.id.editText); // This code try to get the id name "editText"

        radioGroup = (RadioGroup)findViewById(R.id.RadioGroup);// capture the change of RadioGoup

        // These code for real keyboard, detect the action from "Enter"
        editText2.setOnKeyListener(new View.OnKeyListener() // onKeyListener only can detect the "Enter" on the Keyboard, it cannot detect the virtual keyboard on the phone or pad
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) //"KEYCODE_ENTER" for detect the "Enter", "ACTION_DONE" for detect the press "key"(down then up)
                {
                    click(v);

                    return true;  //capture the text, if not set "tru", the text will move to next line.
                }
                return false;
            }
        });

        // These code for virtual keyboard, need enable "singleLine" at Properties of textEdit.
        editText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_DONE) { //due to it enable "singleLine", the "Enter" will become "sent" text
                    click(v);
                    return true; //
                }
                return false;
            }
        }
        );


        //
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged (RadioGroup group, int checkId){
                if (checkId == R.id.MaleradioButton){
                    sex = "Male";
                }
                else if (checkId == R.id.FemaleradioButton){
                    sex = "Female";
                }
            }
        });

    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        String text1 = editText2.getText().toString(); //".toString()" will tranlate the text to string.
        //textView2.setText("Android Class2"); // When click the button, it will change the text "Android Class2" to the id:textView

        textView2.setText(text1+":"+sex); // replace the text
        editText2.setText(""); // clear the text at edit line
    }
}
