package com.example.user.simpleui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import io.realm.internal.Util;

/**
 * Created by user on 2016/4/28.
 *  for read/write tool
 */
public class Utils {
    public static void writeFile(Context context, String fileName, String content){

        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND); //MODE_APPEND : for append the content.
            fos.write(content.getBytes());
            fos.close(); //need to close when done, avoid some one cannot write.
        } catch (FileNotFoundException e) { // use try, catch to avoid the the content damage when system wrong

            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // read file, need know the context and file name.
    public static String readFile(Context context, String fileName){
        try {

            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            fis.read(buffer, 0, buffer.length);
            fis.close();
            return new String(buffer); //

        } catch (FileNotFoundException e){
            e. printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return ""; // return "", if something wrong.
    }


    //create URI for camera, and save picture to "picture"
    /*Web上可用的每種資源 - HTML文檔、圖像、視頻片段、程式等 - 由一個通過通用資源標誌符（Universal Resource Identifier, 簡稱"URI"）進行定位。
            1.URI一般由三部分組成：
            2.訪問資源的命名機制。
            3.存放資源的主機名稱。
            4.資源自身的名稱，由路徑表示。
    */
    public static Uri getPhotoURI(){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // get the folder where store the picture


        if(dir.exists() == false){// check does the folder exist or not.
            dir.mkdir();
        }
        File file = new File(dir,"simpleUI_photo.png"); // folder name
        return Uri.fromFile(file); //translate file to URI

    }

    // 2016.0509, use context to read/write file
    public static byte[] uriToBytes (Context context, Uri uri){

        try{
           InputStream inputStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024]; // create buffer
            int len = 0; // record the length of photo
            while ((len = inputStream.read(buffer)) != -1) {// use while to read the photo, "-1" for end of read
                byteArrayOutputStream.write(buffer);
            }
            return byteArrayOutputStream.toByteArray();// from 2 dimetion to 1 dimention
        }
    catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;


    }

    //S: 2016.0512,
    public static byte[] urlToBytes(String urlString){
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len=0;

            while ((len=inputStream.read(buffer)) != -1){// read data stream

                byteArrayOutputStream.write(buffer, 0, len);
            }

            return byteArrayOutputStream.toByteArray();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //E: 2016.0512

    //S: 2016.0512. get the location from google map

    public static String getGeoCodingUrl(String address){
        try {
            address = URLEncoder.encode(address, "utf-8");//translate
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://maps.google.com/maps/api/geocode/json?address=taipei"+ address;
        return  url;
    }

    public static double[] addressToLatLng(String address){

        String url = Utils.getGeoCodingUrl(address);
        byte[] bytes =Utils.urlToBytes(url);

        if(bytes != null){
            String result = new String(bytes);

            try {
                JSONObject object = new JSONObject(result);

                if (object.getString("status").equals("OK")){ // check "status" is "OK" or not?

                    JSONObject location = object.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    return new double []{lat,lng}; // double array

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Bitmap getStaticMap(double[] latlng){

        String center = latlng[0] +"," + latlng[1];

        String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?center="+ center + "&zoom=17&size=640x400";
        byte[] bytes = Utils.urlToBytes(staticMapUrl);
        if(bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
            return bitmap;
        }
        return null;
    }
    //E: 2016.0512. get the location of earth
}











