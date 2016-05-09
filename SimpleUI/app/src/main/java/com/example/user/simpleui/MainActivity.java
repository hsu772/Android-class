package com.example.user.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0; //代表這個 activity, 必須是常數

    TextView textView2; //variable name for text
    EditText editText2; // the variable name for edit
    RadioGroup radioGroup; // capture RadioGroup, before create the radio, need to create the RadioGroup first.
    List<Order> orders;
    String  drinkName; //set default sex
    String note=""; // empty string for text field
    CheckBox checkBox; // capture checkbox
    int pos;

    ListView listView; //capture listview
    Spinner spinner;
    String menuResults=""; //2016.0502

    ProgressBar progressBar;

    //S:2016.0428: share prefernce to store UI status, use to store the information of user, there is a size limitation.
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    //E:2016.0428: share prefernce to store UI status

    //E:2016.0428: realm
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // default code
        setContentView(R.layout.activity_main); // default code
        Log.d("debug", "Main Activity OnCreate"); // 2016.05.02:create debug

        //S: 2016.05.05:Parse server test
/*
        ParseObject testObject = new ParseObject("HomeworkParse"); // "TestObject" is class name
        testObject.put("sid", "許漢裕"); // "foo" is field name, "bar" is content
        testObject.put("email", "hy772@live.com"); // "foo" is field name, "bar" is content
        testObject.saveInBackground(new SaveCallback() { //
            @Override
            public void done(ParseException e) {

                if (e != null){
                    //Toast.makeText(MainActivity.this, "home: save fall", Toast.LENGTH_LONG).show(); // print error when connect server fail
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show(); // print error when connect server fail
                }
                else {
                    Toast.makeText(MainActivity.this, "home: save success", Toast.LENGTH_LONG).show(); // print OK when connect server success
                }
            }
        });
*/
        //E: 2016.05.05:Parse server

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        orders = new ArrayList<>(); //4/25: capture order list

        //S:2016.0428: share prefernce to store UI status, use to store the information of user, there is a size limitation.
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);     //"setting" is the name of dictionary, MODE_PRIVATE: support R/W
        editor = sp.edit(); //like the pencil to write the content to the dictionary of "setting".

        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();

        Realm.setDefaultConfiguration(realmConfig);//2016.0505, to get instance
        // Get a Realm instance for this thread
        //realm = Realm.getInstance(realmConfig); //2016.0505, comment
        realm = Realm.getDefaultInstance(); //2016.0505, get default instance



        editText2.setText(sp.getString("editText", "")); // find the key (id) "editText", it will response content "world".
        //E:2016.0428: share prefernce to store UI status



        // 4/20:These code for real keyboard, detect the action from "Enter"
        // 4/20:onKeyListener only can detect the "Enter" on the Keyboard, it cannot detect the virtual keyboard on the phone or pad (virtual device)
        editText2.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //S:2016.0428: share prefernce to store UI status
                String text = editText2.getText().toString();
                editor.putString("editText", text);  // put the text to id "editText"
                editor.apply(); // need "apply()" for write the content.
                //E:2016.0428: share prefernce to store UI status

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

        //2016.0502: common
        //S:2016.0428: share prefernce to store UI status, store the radio button.
//        int checkId = sp.getInt("RadioGroup", R.id.blackTeaRadioButton); // get the id "RadioGroup"
//        radioGroup.check(checkId);
//        //E:2016.0428: share prefernce to store UI status
//        RadioButton radioButton = (RadioButton) findViewById(checkId);
//        drinkName = radioButton.getText().toString();
//
//        // For RadioGroup, select "sex"
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkId) {
//                //S:2016.0428: share prefernce to store UI status
//                editor.putInt("RadioGroup", checkId); // put the value to id "RadioGroup"
//                editor.apply(); // apply the value
//                //E:2016.0428: share prefernce to store UI status
//
//
//                // find view by ID
//                RadioButton radioButton = (RadioButton) findViewById(checkId);
//                drinkName = radioButton.getText().toString();
//            }
//        });

        //2016.04.28
        // click listView and show Toast.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order) parent.getAdapter().getItem(position);
                //2016.04.28: Snackbar and Toast do the same thing.
                //          Snackbar provide more application for Toast.
                //2016.04.28: Toast.makeText(MainActivity.this, order.note, Toast.LENGTH_LONG).show();//2016.0428: show the feedback (order.note) to user
                //Snackbar.make(view, order.note, Snackbar.LENGTH_LONG).setAction().show();
                Snackbar.make(view, order.getNote(), Snackbar.LENGTH_LONG).show();

            }
        });
        setupSpinner();
        setupListView();

        //test...
        //S:2016.04.29: homework-1
        int spinId = sp.getInt("spinner", 0);//R.id.spinner); // get the id "spinner"
        Log.d("debug","spinId is "+ spinId);
        spinner.setSelection(spinId);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // editor.putInt ("spinner", spinId);  // put the text to id "editText"
               // editor.apply(); // need "apply()" for write the content.


                //pos = parent.getSelectedItemPosition();
                editor.putInt("spinner",position);
                Log.d("debug","postion is "+ position);
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        //E:2016.04.29: homework-1
       // Log.d("debug","postion 2 is "+ pos);

        //spinner.setSelection();



    }

    //4/25: delete check box
    //4/25: set the text to list
    void setupListView(){
/*        //2016.0505: comment
        RealmResults results = realm.allObjects(Order.class); //list order

        OrderAdapter adapter = new OrderAdapter(this, results.subList(0, results.size()));
        listView. setAdapter(adapter);
*/
        progressBar.setVisibility(View.VISIBLE);//2016.0509

        //S:2016.0509, list local information
        //Realm realm = Realm.getDefaultInstance(); //2016.0509, mark because this instance can get from onCreate().
        // get back the original data
        final RealmResults results = realm.allObjects(Order.class); //list order, need add "final"

        OrderAdapter adapter = new OrderAdapter(MainActivity.this, results.subList(0, results.size()));
        listView. setAdapter(adapter); // list local information

       //realm.close();//2016.0509, mark for not need here
        //E:2016.0509

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order"); // define how to get
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null){ // check does get the data ok or fail
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE); //2016.0509, disable progress bar when finish

                    //
                    return;// return and do nothing if fail
                }

                //S:2016.0505, use objects to get order
                List<Order> orders = new ArrayList<Order>(); // get order

                Realm realm = Realm.getDefaultInstance(); // get realm

                for (int i=0; i<objects.size(); i++){ // to get back the order
                    Order order = new Order();
                    order.setNote(objects.get(i).getString("note"));// get the data from "note" field
                    order.setStoreInfo(objects.get(i).getString("storeInfo"));// get the data from "storeInfo" field
                    order.setMenuResults(objects.get(i).getString("menuResults"));// get the data from "menuResults" field
                    orders.add(order);// save to order to orders.

                    //compare local & remote data
                    if (results.size() <= i) // results is for local data, i
                    {
                        realm.beginTransaction();
                        realm.copyToRealm(order);
                        realm.commitTransaction();
                    }

                }
                realm.close(); // close realm

                progressBar.setVisibility(View.GONE); //2016.0509, disable progress bar if not exception

                OrderAdapter adapter = new OrderAdapter(MainActivity.this, orders);
                listView.setAdapter(adapter);
            }
        });
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


        Order order = new Order();
        order.setMenuResults(menuResults);// = drinkName;
        order.setNote(note);// = note;
        order.setStoreInfo((String) spinner.getSelectedItem());


//        realm.beginTransaction();
//        realm.copyToRealm(order);
//        realm.commitTransaction();
//        realm.close(); //2016.0505



        //orders.add(order);// not need for realm.
//2016.0505, replace below call back function
        SaveCallbackWithRealm callbackWithRealm = new SaveCallbackWithRealm(order, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Toast.makeText(MainActivity.this, "Save Fail", Toast.LENGTH_LONG).show();
                }

                editText2.setText("");
                menuResults = ""; //
                setupListView();
            }
        });
        order.saveToRemote(callbackWithRealm);
//E:2016.0505, replace below call back function
  /*
        //S:2016.0505, create a save call back function
        order.saveToRemote(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e != null){
                    Toast.makeText(MainActivity.this, "Save Fail", Toast.LENGTH_LONG).show();
                }
                //else {
                    //S: 2016.04.28
                    editText2.setText(""); // clear the text at edit line

                    menuResults = ""; //
                //S:2016.0505, move to saveToRemote
  /*              Realm realm = Realm.getDefaultInstance(); // get default instance
                // 2016.0428: Persist your data easily
                realm.beginTransaction();
                realm.copyToRealm(order);
                realm.commitTransaction();
                realm.close(); //2016.0505
                //E:2016.0505, move to saveToRemote
*/
                //setupListView();
                    //E:2016.04.28
                //}
 //           }
  //      }); */
        //E:2016.0505, create a save call back function

        //S:2016.0428: write/Read file
        //Utils.writeFile(this, "notes", order.note+'\n'); //write file name:"ontes" to order.note
        //E:2016.0428: write/Read file


    }

    //S:2016.05.02, show onStart()/onResume()/ onPause()

    //S:2016.05.02, Create button function "goToMenu"
    public void goToMenu(View view){
        Intent intent = new Intent(); // intent: the bridge between active and activity
        intent.setClass(this, DrinkMenuActivity.class);

        //標註記號給哪個 activity, REQUEST_CODE_MENU_ACTIVITY = 0
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY); //startActivity will help to call other activity that define at "intent"
                
    }

    @Override
    //requestCode 對應到 REQUEST_CODE_MENU_ACTIVITY
    // data: 對應到之前 activity 留下來的內容
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MENU_ACTIVITY){

            if (resultCode == RESULT_OK) {
                menuResults = data.getStringExtra("result"); // 從 "result" 取回 data, 放到 menuResults


            }
        }
    }

    //E:2016.05.02, Create button function "goToMenu"
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","Main Activity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "Main Activity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","Main Activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "Main Activity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // 2016.0505, close instance
        Log.d("debug", "Main Activity onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "Main Activity onRestart");
    }

    //E:2016.05.02, show onStart()/onResume()/ onPause()
}
