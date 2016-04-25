package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView2; //variable name for text
    EditText editText2; // the variable name for edit
    RadioGroup radioGroup; // capture RadioGroup, before creat the radio, need to creat the RadioGroup first.

    String drinkName = "black tea"; //set default sex
    String note=""; // empty string for text field
    CheckBox checkBox; // capture checkbox

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // default code
        setContentView(R.layout.activity_main); // default code

        /* The "textView" of "R.id.textView" map to the id "textView" (at activity_main.xml)
         * The code try to get the id name "textView"
         */
        textView2 = (TextView)findViewById(R.id.textView); 
        
        // This code try to get the id name "editText"
        editText2 = (EditText)findViewById(R.id.editText); 
        
        // capture the change of RadioGroup for Male & Female
        radioGroup = (RadioGroup)findViewById(R.id.RadioGroup);
        
        // capture the change for "hide" enable/disable
        checkBox = (CheckBox)findViewById(R.id.HideCheckbox); 

        // These code for real keyboard, detect the action from "Enter"
        // onKeyListener only can detect the "Enter" on the Keyboard, it cannot detect the virtual keyboard on the phone or pad (virtual device)
        editText2.setOnKeyListener(new View.OnKeyListener() 
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                // need to check the ACTION_DOWN for finish the key type
                // Also need to check the KEYCODE_ENTER to detect the "Enter"
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) //"KEYCODE_ENTER" for detect the "Enter", "ACTION_DONE" for detect the press "key"(down then up)
                {
                    // call "click()" function, to get the string, sex and display the string we type.
                    click(v);
                    
                    // For capture the text we sent and not move to next line.
                    // If not set "true", the text will send out and move to next line.
                    return true;  
                }
                return false;
            }
        });

        // These code for virtual keyboard, need enable "singleLine" at Properties of textEdit.
        // And the Genymotion need to enable the virtual keyboard support at tools.
        editText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                
                /* For virtual keyboard, it only needs ot check the ACTION_DONE
                 * For keep the line in single line, it needs to enable "singleLine", at the properties.
                 * It will set the line to single when type "Enter", and it  will become "send out" text only, not move to next line.
                 */
                                                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                                                        // call "click()" function, to get the string, sex and display the string we type.
                                                        click(v);
                                                        return true; //
                                                    }
                                                    return false;
                                                }
                                            }
        );


        // For RadioGroup, select "sex"
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkId) {
                // find view by ID
                RadioButton radioButton = (RadioButton) findViewById(checkId);
                drinkName = radioButton.getText().toString();
            }
        });



    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        note = editText2.getText().toString(); // get the string from if "editText" and translate the text to string format and assign to variable "note".

        String text = note;
        //changeTextView(); // check the checkbox

        editText2.setText(""); // clear the text at edit line
    }


}
