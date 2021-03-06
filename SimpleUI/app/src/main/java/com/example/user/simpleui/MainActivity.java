package com.example.user.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
//import java.util.jar.Manifest;// 2016.0509, we don't need the Manifest from Java, need Android version
import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_MENU_ACTIVITY = 0; //代表這個 activity, 必須是常數
    private static final int REQUEST_CODE_CAMERA_ACTIVITY = 1; //代表camera activity, 必須是常數

    private boolean hasPhoto = false; // 2016.0509, check doest take the photo by camera, default is "false"

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
    ProgressDialog progressDialog; //2016.0512, show bar when load image
    ImageView photoImageView;

    //S:2016.0428: share prefernce to store UI status, use to store the information of user, there is a size limitation.
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    //E:2016.0428: share prefernce to store UI status

    //E:2016.0428: realm
    Realm realm;
    private CallbackManager callbackManager;

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
        progressBar = (ProgressBar) findViewById(R.id.progressBar); //2016.0509, for progress bar
        photoImageView = (ImageView) findViewById(R.id.imageView); //2016.0509, for image view

        progressDialog = new ProgressDialog(this); //2016.0512,

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

        //S: 2016.0509, for checkbox, image
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    photoImageView.setVisibility(View.GONE);
                }
                else{
                    photoImageView.setVisibility(View.VISIBLE);
                }

            }
        });
        //E: 2016.0509, for checkbox, image

        //2016.04.28
        // click listView and show Toast.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order order = (Order) parent.getAdapter().getItem(position);
                //2016.04.28: Snackbar and Toast do the same thing.
                //          Snackbar provide more application for Toast.
                //2016.04.28: Toast.makeText(MainActivity.this, order.note, Toast.LENGTH_LONG).show();//2016.0428: show the feedback (order.note) to user
                //Snackbar.make(view, order.note, Snackbar.LENGTH_LONG).setAction().show();

                //S:2016.0512
                goToDetailOrder(order);
                //E: 2016.0512
                Snackbar.make(view, order.getNote(), Snackbar.LENGTH_LONG).show();

            }
        });

        setupListView();
        setupSpinner();
        setupFaceBook();

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

    //S: 2016.0519, for facebook
    void setupFaceBook(){
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                GraphRequest request = GraphRequest.newGraphPathRequest(accessToken // each persion is a graphic
                        , "/v2.5/me",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) { // response provide the accesstoken the how much right (birthday or phone or address ...) to access
                                JSONObject object = response.getJSONObject();// use JSONobject to get thd data between different API
                                try {
                                    String name = object.getString("name");
                                    Toast.makeText(MainActivity.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                    textView2.setText("Hello " + name);
                                    Log.d("debug", object.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }
    //E: 2016.0519, for facebook


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
                if (e != null) { // check does get the data ok or fail
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE); //2016.0509, disable progress bar when finish

                    //
                    return;// return and do nothing if fail
                }

                //S:2016.0505, use objects to get order
                List<Order> orders = new ArrayList<Order>(); // get order

                Realm realm = Realm.getDefaultInstance(); // get realm

                for (int i = 0; i < objects.size(); i++) { // to get back the order
                    Order order = new Order();
                    order.setNote(objects.get(i).getString("note"));// get the data from "note" field
                    order.setStoreInfo(objects.get(i).getString("storeInfo"));// get the data from "storeInfo" field
                    order.setMenuResults(objects.get(i).getString("menuResults"));// get the data from "menuResults" field


                    if (objects.get(i).getParseFile("photo") != null) { //2016.0512, check does the photo exist
                        order.photoURL = objects.get(i).getParseFile("photo").getUrl(); //2016.0512, get photo's URL
                    }
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
        //S: Homework #3
/*
        final ArrayList<String> data= new ArrayList<String>();
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("StoreInfo");
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> postList, ParseException e) {
                        if (e == null) {
                            // If there are results, update the list of posts
                            // and notify the adapter
                            for (int i = 0; i < postList.size(); i++) {
                                Order order = new Order();

                                String name = (String) postList.get(i).getString("StoreName");

                                data.add(i, name);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                        spinner.setAdapter(adapter);
                    }

                });
*/
        //E: Homework #3

        //S:2016.0516.parse store info and address at show at spinner

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null || objects == null){
                    return;
                }

                    String[] storeInfo = new String[objects.size()];
                    for (int i=0; i< objects.size();i++){
                        ParseObject object = objects.get(i);
                        storeInfo[i] = object.getString("StoreName")+","+object.getString("Address");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_spinner_dropdown_item,storeInfo);

                    spinner.setAdapter(adapter);

            }
        });

        //E:2016.0516.parse store info and address at show at spinner

        //S: 2016.0516, marked
//        String[] data = getResources().getStringArray(R.array.storeInfo);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item,data);
//
//        spinner.setAdapter(adapter);
    }

    public void click(View view) //The name "click" will map to Properties: onClick  (at activity_main.xml)
    {
        progressDialog.setTitle("Loading....");//2016.0512, show text "loading..."
        progressDialog.show(); //2016.0512, show text
        note = editText2.getText().toString(); // get the string from if "editText" and translate the text to string format and assign to variable "note".

        String text = note;
        //changeTextView(); // check the checkbox
        textView2.setText(text);


        Order order = new Order();
        order.setMenuResults(menuResults);// = drinkName;
        order.setNote(note);// = note;
        order.setStoreInfo((String) spinner.getSelectedItem());

        //S: 2016.0509, if get the photo, upload the photo
        // photo like byte array, because we use RGB to store the photo information.
        if(hasPhoto){
            Uri uri = Utils.getPhotoURI();
            byte[] photo = Utils.uriToBytes(this, uri);

            if (photo == null) {
                Log.d("Debug", "Read Photo Fail!");
            }
            else {// write the photo to order.
                order.photo = photo;
            }
        }
        //E: 2016.0509

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
                menuResults = "";
                photoImageView.setImageResource(0); //2016.0509, clear image
                hasPhoto = false; //2016.0509, set hasphoto to false
                progressDialog.dismiss();//2016.0512, disable the text "loading..."

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

    //S:2016.0512, call OrderDetail activity to get detail order.
    public void goToDetailOrder(Order order){
       Intent intent = new Intent(); ///call activity

        intent.setClass(this, OrderDetailActivity.class);

        intent.putExtra("note", order.getNote());
        intent.putExtra("storeInfo", order.getStoreInfo());
        intent.putExtra("menuResults", order.getMenuResults());
        intent.putExtra("photoURL", order.photoURL);
        startActivity(intent);

    }
    //E:2016.0512

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
        //S: 2016.0509,
        else if (requestCode == REQUEST_CODE_CAMERA_ACTIVITY){ // check request code is for camera
            if(resultCode == RESULT_OK){
                photoImageView.setImageURI(Utils.getPhotoURI());
                hasPhoto = true; // record get the photo
            }
        }
        //E: 2016.0509
        callbackManager.onActivityResult(requestCode, resultCode, data); //2016.0519, for facebook to receive the callback from onActivity.
    }

    //E:2016.05.02, Create button function "goToMenu"

    //S: 2016.0509, for photo action
    //onCreateOptionsMenu 是當你按下手機的 Menu 時會觸發的動作
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //onOptionsItemSelected 是當你按下跳出來的 Menu 選項時會觸發的動作
    //先觸發 onCreateOptionsMenu 才有 onOptionsItemselected
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        // show
        if (id==R.id.action_take_photo){
            Toast.makeText(this, "Take Photo", Toast.LENGTH_LONG).show();
            goToCamera();
        }

        return super. onOptionsItemSelected(item);
    }

    // need check the permission w/ user, if user not approvide,
    protected void goToCamera(){

        if (Build.VERSION.SDK_INT >=23) // if version > 23
        {
            // check does user permission
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //request the permssion w/ user
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0); // String{} can request many permission
                return;
            }
        }


        /*
        * 在「Intent」這樣的事件處理觀念裡，Android 試圖將事件解釋為「應用程式的意圖」或是「使用者的意圖」，並試著去解釋該意圖的目的，
        * 若 Android 系統本身能理解應用程式的意圖，便會「自行」去處理該意圖所應執行的工作。
        * Android的做法是，讓每個意圖（Intent）都帶有一個動作（action），並根據不同的動作去行動。
        *
        */
        //call camera's activity
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE); //ACTION_IMAGE_CAPTURE: take one photo and back
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoURI()); // put photo
        startActivityForResult(intent, REQUEST_CODE_CAMERA_ACTIVITY); // need assign request code, REQUEST_CODE_CAMERA_ACTIVITY=1
    }
    //E:2016.0509

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
