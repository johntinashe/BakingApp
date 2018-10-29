package com.github.johntinashe.bakingapp.viewmodels;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressWarnings("unused")
public class ViewModel extends android.arch.lifecycle.ViewModel {


    public void getAsyncCall(){

        OkHttpClient httpClient = new OkHttpClient();

        String url = "http://go.udacity.com/android-baking-app-json";
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("errorRR", "error in getting response using async okhttp call");
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    throw new IOException("Error response " + response);
                }
            }
        });
    }
}


