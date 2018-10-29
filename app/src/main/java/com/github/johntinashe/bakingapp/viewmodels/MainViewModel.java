package com.github.johntinashe.bakingapp.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.johntinashe.bakingapp.model.RecipesResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainViewModel extends ViewModel {

    private MutableLiveData<ArrayList<RecipesResponse>> recipeLists = null;
    private Call<ArrayList<RecipesResponse>> call;

    public MutableLiveData<ArrayList<RecipesResponse>> getMovies() {
        if (recipeLists == null) {
            recipeLists = new MutableLiveData<>();
            loadRecipes();
        }
        return recipeLists;
    }


    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void loadRecipes() {

        call = getCall();

        call.enqueue(new Callback<ArrayList<RecipesResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<RecipesResponse>> call, @NonNull Response<ArrayList<RecipesResponse>> response) {
                if (recipeLists != null){
                    recipeLists.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<RecipesResponse>> call, @NonNull Throwable t) {
                Log.d("errorNa",t.getMessage());
            }
        });
    }


    private Call<ArrayList<RecipesResponse>> getCall() {
        Api api = getRetrofit().create(Api.class);
        call = null;
        return call = api.getRecipes();
    }

}
