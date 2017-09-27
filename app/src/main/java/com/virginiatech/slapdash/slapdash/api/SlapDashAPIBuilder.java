package com.virginiatech.slapdash.slapdash.api;

import java.io.IOException;

import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Matt on 10/6/2016.
 */

public class SlapDashAPIBuilder {

    @Getter
    private static final String SLAPDASH_BASE_URL = "https://nimayz.click/slapdash/api/v1/";

    public static SlapDashAPI getAPI() {

       new OkHttpClient.Builder()
               .addInterceptor(new Interceptor() {
                   @Override
                   public Response intercept(Chain chain) throws IOException {
                       Request authed = chain.request()
                               .newBuilder()
                               .addHeader("Content-Type", "application/json")
                               .addHeader("Accept", "application/json")
                               .build();

                       return chain.proceed(authed);
                   }
               }).build();

        return new Retrofit.Builder()
                .baseUrl(SLAPDASH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SlapDashAPI.class);
    }
}
