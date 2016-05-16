package com.example.user.simpleui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//2016.0512, to receive intent
public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView menuResults;
    ImageView photo;
    ImageView mapImageView; // 2016.0516, for show map of address
    String storeName; // 2016.0516, for show map of address
    String address;// 2016.0516, for show map of address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView) findViewById(R.id.note);
        storeInfo = (TextView) findViewById(R.id.storeInfo);
        menuResults = (TextView) findViewById(R.id.menuResults);
        photo = (ImageView) findViewById(R.id.photoImageView);
        mapImageView = (ImageView) findViewById(R.id.mapImageView);// 2016.0516

        Intent intent = getIntent(); //get intent
        note.setText(intent.getStringExtra("note"));
        storeInfo.setText(intent.getStringExtra("storeInfo"));

        //S: 2016.0516, saperate the string w/ ","
        String[] info = intent.getStringExtra("storeInfo").split(",");

        storeName = info[0];
        address = info [1];

        //E: 2016.0516,

        //translate the menuResult to JasonArray
        String results = intent.getStringExtra("menuResults");
        String text = "";
        try {
            JSONArray jsonArray = new JSONArray(results);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                text += object.getString("name") + ":大杯" + object.getString("l") + "杯 中杯" + object.getString("m") + "杯" + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menuResults.setText(text); // show result

        String url = intent.getStringExtra("photoURL");
        if (!url.equals("")) {// if image exist, load picture
            //Picasso.with(this).load(url).into(photo);

            (new ImageLoadingTask(photo)).execute(url);
            // (new GeoCodingTask(photo)).execute("台北市羅斯福路四段一號");

//S: not write as this way, it will occupted the memory size
//            for (int i = 0; i < 10; i++) {
//                //Thread use to download file
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            wait(10000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread.start();
//            }
// E not write as this way, it will occupted the memory size
        }

        (new GeoCodingTask(mapImageView)).execute(address);//2016.0516, to get the address and get the map

        //S: 2016.0516, test thread occupt memory, we can see the memory increase
//        for (int i=0;i< 10;i++){
//            Thread t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true){
//                        SystemClock.sleep(1000);
//                    }
//                }
//            });
//        }
        //S: 2016.0516

    }

    // use task to get the lat, lng
    // AsyncTask 用意在簡化背景執行 Thread 程式碼的撰寫。
    // 要使用 AsyncTask，必定要建立一個繼承自 AsyncTask 的子類別,並傳入 3 項資料：
    //    Params -- 要執行 doInBackground() 時傳入的參數，數量可以不止一個
    //    Progress -- doInBackground() 執行過程中回傳給 UI thread 的資料，數量可以不止一個
    //    Rsesult -- 傳回執行結果， 若您沒有參數要傳入，則填入 Void (注意 V 為大寫)。

    //    AsyncTask 的運作有 4 個階段：
    //    onPreExecute -- AsyncTask 執行前的準備工作，例如畫面上顯示進度表，
    //    doInBackground -- 實際要執行的程式碼就是寫在這裡，
    //    onProgressUpdate -- 用來顯示目前的進度，
    //    onPostExecute -- 執行完的結果 - Result 會傳入這裡。
    private static class GeoCodingTask extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;
        //doInBackground -- 實際要執行的程式碼就是寫在這裡
        protected Bitmap doInBackground(String... params){
            String address = params[0];
            double[] latlng = Utils.addressToLatLng(address);

            return Utils.getStaticMap(latlng);
        }

        //onPostExecute -- 執行完的結果 - Result 會傳入這裡
        protected void onPostExecute(Bitmap bitmap){

            super.onPostExecute(bitmap);
            if(bitmap != null){ // this bitmap come from above function
                imageView.setImageBitmap(bitmap);
            }
        }

        public GeoCodingTask(ImageView imageView){this.imageView = imageView;}
    }

    private static class  ImageLoadingTask extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;

        protected Bitmap doInBackground (String... params){

            String url = params[0];
            byte[] bytes = Utils.urlToBytes(url);

            if (bytes!=null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // translate bitmap format
                return bitmap;
            }
            return null;
        }

        protected void onPostExecute(Bitmap bitmap){

            super.onPostExecute(bitmap);
            if(bitmap != null){ // this bitmap come from above function
                imageView.setImageBitmap(bitmap);
            }
        }


        public ImageLoadingTask(ImageView imageView){ //structure
            this.imageView = imageView;
        }
    }
}
