package org.udg.pds.todoandroid.util;


import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.udg.pds.todoandroid.service.ApiRest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitRetrofit {

    private ApiRest apiRest;

    // Inicialización del objeto Retrofit
    public void init() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(Global.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiRest = retrofit.create(ApiRest.class);
    }

    public ApiRest getApiRest(){
        return apiRest;
    }

}
