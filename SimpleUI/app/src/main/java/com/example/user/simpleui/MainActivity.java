package com.example.user.simpleui;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView textView2; //variable name for text
    EditText editText2; // the variable name for edit
    RadioGroup radioGroup; // capture RadioGroup, before create the radio, need to create the RadioGroup first.
    ArrayList<order> orders;
    String drinkName = "black tea"; //set default sex
    String note=""; // empty string for text field
    CheckBox checkBox; // capture checkbox

    ListView listView; //capture listview
    Spinner spinner;

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
        listView = (ListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.spinner);

        orders = new ArrayList<>(); //4/25: capture order list

        // 4/20:These code for real keyboard, detect the action from "Enter"
        // 4/20:onKeyListener only can detect the "Enter" on the Keyboard, it cannot detect the virtual keyboard on the phone or pad (virtual device)
        editText2.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
        });


        // For RadioGroup, select "sex"
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkId) {
                // find view by ID
                RadioButton radioButton = (RadioButton) findViewById(checkId);
                drinkName = radioButton.getText().toString();
            }
        });

        //2016.04.28
        // click listView and show Toast.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                order order = (order) parent.getAdapter().getItem(position);
                //2016.04.28: Snackbar and Toast do the same thing.
                //          Snackbar provide more application for Toast.
                //2016.04.28: Toast.makeText(MainActivity.this, order.note, Toast.LENGTH_LONG).show();//2016.0428: show the feedback (order.note) to user
                //Snackbar.make(view, order.note, Snackbar.LENGTH_LONG).setAction().show();
                Snackbar.make(view, order.note, Snackbar.LENGTH_LONG).show();

            }
        });
        setupListView();
        setupSpinner();
    }

    //4/25: delete check box
    //4/25: set the text to list
    void setupListView(){
        OrderAdapter adapter = new OrderAdapter(this, orders);
        listView. setAdapter(adapter);
    }

    void setupSpinner(){
        String[] data = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item,data);

        spinner.setAdapter(adapter);
    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        note = editText2.getText().toString(); // get the string from if "editText" and translate the text to string format and assign to variable "note".

        String text = note;
        //changeTextView(); // check the checkbox
        textView2.setText(text);


        order order = new order();
        order.drinkName = drinkName;
        order.note = note; //
        order.storeInfo = (String) spinner.getSelectedItem(); // get & store the store information

        orders.add(order);

        editText2.setText(""); // clear the text at edit line
        setupListView();
    }


}
