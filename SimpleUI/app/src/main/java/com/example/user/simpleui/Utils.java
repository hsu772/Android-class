package com.example.user.simpleui;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

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
}











