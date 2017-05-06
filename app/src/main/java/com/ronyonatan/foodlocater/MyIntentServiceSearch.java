package com.ronyonatan.foodlocater;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyIntentServiceSearch extends IntentService {
    // ArrayList<MyPlace> allres;
    String keyWord;
    String result = "no result";
    double lat;
    double lon;
   // Boolean getall;

    public MyIntentServiceSearch() {
        super("MyIntentServiceSearch");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        lat = intent.getDoubleExtra("lat", 0);
        lon = intent.getDoubleExtra("lon", 0);
        keyWord = intent.getStringExtra("keyWord");
       // getall = intent.getBooleanExtra("all", false);

        OkHttpClient client = new OkHttpClient();
        Request request;

        if (keyWord.isEmpty() || keyWord.equals(" ") ) /// give all places
            request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=1000&keyword=&key=AIzaSyDo6e7ZL0HqkwaKN-GwKgqZnW03FhJNivQ")
                    .build();
        else // give by keyword
            request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=1000&keyword=" + keyWord + "&key=AIzaSyDo6e7ZL0HqkwaKN-GwKgqZnW03FhJNivQ")
                    .build();


        try {
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert response != null;
            result = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result.equals("no result") || result == null) {
            Log.e("YonatanError", "no results from site");
            return;
        }

        Intent FinishToCheck = new Intent("com.example.ronyonatan.localbroadcast.FINISHED!");
            FinishToCheck.putExtra("results",result);
        FinishToCheck.putExtra("keyword",keyWord);

        LocalBroadcastManager.getInstance(this).sendBroadcast(FinishToCheck);


    }


}



