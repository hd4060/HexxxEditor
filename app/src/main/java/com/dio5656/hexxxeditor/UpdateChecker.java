package com.dio5656.hexxxeditor;

import static com.dio5656.hexxxeditor.MainActivity.fragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

class UpdateChecker extends AsyncTask<Void, Void, String> {
     Context context;
     Activity activity;
     String latestversion ="";
     public  UpdateChecker (Context context, Activity activity) {
         this.context=context;
         this.activity=activity;

     }


    @Override
    protected String doInBackground(Void... voids) {

        try {
            //get the HTML document
            Document document = Jsoup.connect("https://github.com/dio5656/HexxxEditor/blob/master/README.md").get();
            Element element = document.select("#user-content-version").first();
            System.out.println("element"+element);
            if (element != null) {
                latestversion = element.text();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return latestversion;
    }




     public String getLatestversion() {
         return latestversion;
     }

     protected void onPostExecute(String result) {
       //  System.out.println("result="+result);
         if (!result.isEmpty()) {
            latestversion =result;
             System.out.println("Version="+ latestversion.split(": v")[1]);
             //get latest version
             latestversion = latestversion.split(": v")[1];
             //get installed version
             String installedversion="";
             PackageManager packageManager = activity.getPackageManager();
             String packageName = activity.getPackageName();

             try{
                 PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                 installedversion = packageInfo.versionName;
                 System.out.println("iiiiiiiiiii="+installedversion);
             } catch (PackageManager.NameNotFoundException e) {
                 e.printStackTrace();
             }

             if (isNewVersion(installedversion,latestversion)) {
                 UpdateDialog dialogFragment = new UpdateDialog(latestversion);
                 dialogFragment.show(fragmentManager, "MyDialogFragment");
             }

        }
     }
     public boolean isNewVersion (String installed, String latest) {
         String  [] installedParts = installed.split("\\.");
         System.out.println("installedParts"+installedParts.length);
         String  [] latestParts = latest.split("\\.");
         System.out.println("latestParts"+latestParts.length);
         int versionlenght = Math.min(installedParts.length,latestParts.length);
         System.out.println("versionlenght="+versionlenght);
         for (int i=0;i<versionlenght;i++) {
             if (Integer.valueOf(latestParts[i])> Integer.valueOf(installedParts[i]))
                 return true;

         }
         return false;
     }
 }
