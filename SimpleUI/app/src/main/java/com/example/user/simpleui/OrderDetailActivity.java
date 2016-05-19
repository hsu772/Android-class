package com.example.user.simpleui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
//import android.location.LocationListener;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.jar.Manifest;

//2016.0512, to receive intent
public class OrderDetailActivity extends AppCompatActivity implements GeocodingTaskResponse, RoutingListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView note;
    TextView storeInfo;
    TextView menuResults;
    ImageView photo;
    ImageView mapImageView; // 2016.0516, for show map of address
    String storeName; // 2016.0516, for show map of address
    String address;// 2016.0516, for show map of address

    MapFragment mapFragment; //2016.0516, for map fragment
    private GoogleMap googleMap;//2016.0519
    private ArrayList<Polyline> polylines;
    private LatLng storeLocation; // store storelocation
    private GoogleApiClient mGoogleApiClient;

    ScrollView scrollView;

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
        address = info[1];

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


        //S: 2016.0516, need use getFragmentManager to get the ID, it's not same as view.
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() { //get back the Google map
            @Override
            public void onMapReady(GoogleMap map) {
                //(new GeoCodingTask(googleMap)).execute(address);//2016.0516, to get the address and get the map
                (new GeoCodingTask(OrderDetailActivity.this)).execute(address);//2016.0516, to get the address and get the map
                googleMap = map;

            }
        });
        //E: 2016.0516, need use getFragmentManager to get the ID, it's not same as view.

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



        //S:2016.0519, scroll view
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        ImageView transparentImageView = (ImageView) findViewById(R.id.imageView2);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Debug", "Touch it");
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
        //E:2016.0519

    }

    public void responseWithGeocodingResults(LatLng location) {

        //S:2016.0519, check does allow show
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {

                //S: 2016.0516, show map on top view
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17)); //LatLng(latlng[0], latlng[1])是經緯度
                googleMap.addMarker(new MarkerOptions().position(location));//2016.0516, add marker at google map

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                return;
            }
        }
        //E201600519

        storeLocation = location;
        googleMap.setMyLocationEnabled(true);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();// connect
        }




    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int inex) {
        if (polylines != null) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < routes.size(); i++) {

            //In case of more than 5 alternative routes

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLUE); // set color
            polyOptions.width(10 + i * 3); // set width
            polyOptions.addAll(routes.get(i).getPoints()); // get the location of point of line
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline); //

//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ routes.get(i).getDistanceValue()+": duration - "+ routes.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);// get current location
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, LocationRequest.create(), this);

        LatLng start = new LatLng(25.0186348, 121.538379);// assign start location. //2016.0516, start address for test

        if (location != null){ // check does the location is null
            start = new LatLng(location.getLatitude(), location.getLongitude());

            CameraUpdate center = CameraUpdateFactory.newLatLng(start);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);//auto move to user's camera
        }

        Routing routing = new Routing.Builder() //2016.0516, from awesome git, route the user to the marker (target)
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .waypoints(start, storeLocation) //.waypoints(start, location)
                .withListener(this).build();

        routing.execute(); // execute the draw the line on the map

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    // stop connection
    protected void onStop() {
        if(mGoogleApiClient != null) mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
         LatLng latLng= new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);//auto move to user's camera
    }

    //@Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }

    //@Override
//    public void onProviderEnabled(String provider) {
//
//    }

    //@Override
//    public void onProviderDisabled(String provider) {
//
//    }

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

    //private static class GeoCodingTask extends AsyncTask<String, Void, Bitmap>{


        private static class GeoCodingTask extends AsyncTask<String, Void, double[]>{
        // GoogleMap googleMap;
        private  final WeakReference<GeocodingTaskResponse> geocodingTaskResponseWeakReference;


        //ImageView imageView;
        //doInBackground -- 實際要執行的程式碼就是寫在這裡
        protected double[] doInBackground(String... params){
            String address = params[0];
            double[] latlng = Utils.addressToLatLng(address);

            //return Utils.getStaticMap(latlng);
            return  latlng; //2016.0516
        }

        //onPostExecute -- 執行完的結果 - Result 會傳入這裡
        protected void onPostExecute(double[] latlng){//(Bitmap bitmap){

            //2016.0519
            if (latlng != null && geocodingTaskResponseWeakReference.get() != null){

                LatLng storeLocation = new LatLng(latlng[0], latlng[1]);
                GeocodingTaskResponse response = geocodingTaskResponseWeakReference.get();
                response.responseWithGeocodingResults(storeLocation);
            }

            //E: 2016.0516
/* //2016.0516, mark
            super.onPostExecute(bitmap);
            if(bitmap != null){ // this bitmap come from above function
                googleMap.setImageBitmap(bitmap);
            }
*/
        }

        //public GeoCodingTask(ImageView imageView){this.imageView = imageView;} //2016.0516
        //public GeoCodingTask(GoogleMap googleMap){this.googleMap = googleMap;} // 2016.0516, apply google map to the Imageview
        // when activity gone, this weekreference also gone
        public GeoCodingTask(GeocodingTaskResponse response){

            //this.googleMap = googleMap;
            this.geocodingTaskResponseWeakReference = new WeakReference<GeocodingTaskResponse>(response);//2016.0519
        } // 2016.0516, apply google map to the Imageview
    }

    private static class  ImageLoadingTask extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;
        GoogleMap googleMap;

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

// create a activit

//S:2016.0519, response the current location
interface GeocodingTaskResponse {
    void responseWithGeocodingResults(LatLng location);


}




