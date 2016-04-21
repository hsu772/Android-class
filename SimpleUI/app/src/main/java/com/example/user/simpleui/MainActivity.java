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
    RadioGroup radioGroup; // capture RadioGroup
    String sex=""; // set default sex
    String selectedSex = "Male";
    String name=""; // empty string for text field
    CheckBox checkBox; // capture checkbox

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = (TextView)findViewById(R.id.textView); //The "textView" of "R.id.textView" map to the id "textView" (at activity_main.xml)
                                                          // The code try to get the id name "textView"
        editText2 = (EditText)findViewById(R.id.editText); // This code try to get the id name "editText"

        radioGroup = (RadioGroup)findViewById(R.id.RadioGroup);// capture the change of RadioGroup
        checkBox = (CheckBox)findViewById(R.id.HideCheckbox);

        // These code for real keyboard, detect the action from "Enter"
        editText2.setOnKeyListener(new View.OnKeyListener() // onKeyListener only can detect the "Enter" on the Keyboard, it cannot detect the virtual keyboard on the phone or pad
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) //"KEYCODE_ENTER" for detect the "Enter", "ACTION_DONE" for detect the press "key"(down then up)
                {
                    click(v);

                    return true;  //capture the text, if not set "true", the text will move to next line.
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


        // For RadioGroup, select "sex"
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged (RadioGroup group, int checkId){
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
                /*if (isChecked){
                    String text1 = name;
                    textView2.setText(text1); // replace the text
                }
                else{
                    String text1 = name + " sex:" + sex;
                    textView2.setText(text1); // replace the text
                }
                */

                // the changeTextView replace above code for more check.
                if (name != ""){ // if text field is not empty, call "changeTextView()" for check the string
                    changeTextView();
                }

            }

        });

    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        name = editText2.getText().toString(); // get the string from if "editText" and translate the text to string and assign to variable "name".
        //1/String text1 = editText2.getText().toString(); //".toString()" will translate the text to string.
        //2/textView2.setText("Android Class2"); // When click the button, it will change the text "Android Class2" to the id:textView
        //2/text1 = text1 + "sex:" + sex;
        //3/String text1 = name + " sex:" + sex;
        //3/textView2.setText(text1); // replace the text
        sex = selectedSex; // assign the sex to String "sex", avoid the sex been changed directly.
        changeTextView(); // check the checkbox

        editText2.setText(""); // clear the text at edit line
    }

    // This function for check the text view and checkbox
    public void changeTextView(){
        if (checkBox.isChecked()){ // if enable "checkbox", hide "sex"
            String text1 = name;
            textView2.setText(text1); // replace the text
        }
        else{  // not enable "checkbox", show "sex"
            String text1 = name + " sex:" + sex;
            textView2.setText(text1); // replace the text
        }
    }
}
