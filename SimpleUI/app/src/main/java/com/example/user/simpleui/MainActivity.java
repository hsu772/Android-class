package com.example.user.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView2; //variable name for text
    EditText editText2; // the variable name for edit
    RadioGroup radioGroup; // capture RadioGroup, before creat the radio, need to creat the RadioGroup first.
    String sex=""; // empty the sex field
    String selectedSex = "Male"; //set default sex
    String name=""; // empty string for text field
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
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                
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
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged (RadioGroup group, int checkId){
                
                // Use "selectedSex" to record the sex that user select
                // It can resolve the sex infromation been set to the textView2 directly.
                if (checkId == R.id.MaleradioButton){ // if "check" the "MaleradioButton", set "Male"
                    selectedSex = "Male";
                }
                else if (checkId == R.id.FemaleradioButton){ // if "check" the "FemaleradioButton", set "Female"
                    selectedSex = "Female";
                }
            }
        });


        // For check box
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                /* //version1: display the name & sex at textView2
                if (isChecked){
                    String text1 = name;
                    textView2.setText(text1); // replace the text
                }
                else{
                    String text1 = name + " sex:" + sex;
                    textView2.setText(text1); // replace the text
                }
                */

                /* version2: impeove version1, resolve the textView2 show the string if we don't type any thing at editText2 
                 *           and click the "checkBox" at the same time.
                 * Check the string cannot empty first.
                 * Then, call changeTexeView() for change the text at textView2.
                 */
                if (name != ""){ // if text field is not empty, call "changeTextView()" for check the string
                    changeTextView();
                }

            }

        });

    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        name = editText2.getText().toString(); // get the string from if "editText" and translate the text to string format and assign to variable "name".
        //v1/String text1 = editText2.getText().toString(); //".toString()" will translate the text to string.
        //v2/textView2.setText("Android Class2"); // When click the button, it will change the text "Android Class2" to the id:textView
        //v2/text1 = text1 + "sex:" + sex;
        //v3/String text1 = name + " sex:" + sex;
        //v3/textView2.setText(text1); // replace the text
        sex = selectedSex; // assign the sex to String "sex", avoid the sex been changed directly.
        changeTextView(); // check the checkbox

        editText2.setText(""); // clear the text at edit line
    }

    // This function for check the text view and checkbox
    public void changeTextView(){
        if (checkBox.isChecked()){ // if enable "checkbox", it will hide "sex", only display the "name"
            String text1 = name;
            textView2.setText(text1); // replace the text
        }
        else{  // not enable "checkbox", show "sex" and "name"
            String text1 = name + " sex:" + sex;
            textView2.setText(text1); // replace the text
        }
    }
}
