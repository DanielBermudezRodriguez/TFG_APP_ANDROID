package org.udg.pds.todoandroid.util;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.udg.pds.todoandroid.service.ApiRest;

import java.net.CookieHandler;
import java.net.CookieManager;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InitRetrofit {


    private ApiRest apiRest;

    private static InitRetrofit retrofit = null;

    // Inicialización del objeto Retrofit (Patrón Singleton)
    private InitRetrofit() {

        if (retrofit == null){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            CookieHandler cookieHandler = new CookieManager();

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(interceptor)
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
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
    }

    public static InitRetrofit getInstance() {
        if (retrofit == null) {
            retrofit = new InitRetrofit();
        }
        return retrofit;
    }

    public ApiRest getApiRest(){
        return apiRest;
    }
}





