package com.github.johntinashe.bakingapp.data;

import com.github.johntinashe.bakingapp.viewmodels.Api;

import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitClient {

    public static retrofit2.Retrofit getRetrofit() {
        return new retrofit2.Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

