package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView2; //variable name for text
    EditText editText2; // the variable name for edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = (TextView)findViewById(R.id.textView); //The "textView" of "R.id.textView" map to the id "textView" (at activity_main.xml)
                                                          // The code try to get the id name "textView"
        editText2 = (EditText)findViewById(R.id.editText); // This code try to get the id name "editText"

        // These code for real keyboard, detect the action from "Enter"
        editText2.setOnKeyListener(new View.OnKeyListener() // onKeyListener only can detect the "Enter" on the Keyboard, it cannot detect the virtual keyboard on the phone or pad
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) //"KEYCODE_ENTER" for detect the "Enter", "ACTION_DONE" for
                {
                    click(v);

                    return true;  //capture the text
                }
                return false;
            }
        });

        // These code for virtual keyboard, need enable "singleLine" at Properties of textEdit.
        editText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                                    click(v);
                                                    return true;
                                                }
                                                return false;
                                             }
                                            }
        );
    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        String text1 = editText2.getText().toString(); //".toString()" will tranlate the text to string.
        //textView2.setText("Android Class2"); // When click the button, it will change the text "Android Class2" to the id:textView
        textView2.setText(text1); // replace the text
        editText2.setText(""); // clear the text at edit line
    }
}
