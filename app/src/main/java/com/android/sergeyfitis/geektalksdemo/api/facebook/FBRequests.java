package com.android.sergeyfitis.geektalksdemo.api.facebook;

import com.android.sergeyfitis.geektalksdemo.api.C;
import com.android.sergeyfitis.geektalksdemo.helpers.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class FBRequests {
    private static ApiCalls apiCalls;

    public static ApiCalls getDefault() {
        if (apiCalls == null) {
            OkHttpClient client = new OkHttpClient();
            client.interceptors().add(
                    new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            HttpUrl url = chain.request().httpUrl()
                                    .newBuilder()
                                    .addEncodedQueryParameter("access_token", Prefs.getFbAccessToken())
                                    .build();

                            Request request = chain.request()
                                    .newBuilder()
                                    .url(url)
                                    .build();
                            return chain.proceed(request);
                        }
                    }
            );
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(C.FB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client)
                    .build();
            apiCalls = retrofit
                    .create(ApiCalls.class);
        }
        return apiCalls;
    }
}
