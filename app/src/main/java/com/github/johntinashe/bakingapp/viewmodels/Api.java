package com.github.johntinashe.bakingapp.viewmodels;

import com.github.johntinashe.bakingapp.model.RecipesResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    String BASE_URL = "http://go.udacity.com/";

    @GET("android-baking-app-json")
    Call<ArrayList<RecipesResponse>> getRecipes();
}
