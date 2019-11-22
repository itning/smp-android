package top.itning.smpandroid.client;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author itning
 */
public final class HttpHelper {
    private static final String AUTHORIZATION = "Authorization";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON_VALUE = "application/json";
    public static final int UNAUTHORIZED = 401;
    /**
     * TODO 临时 BASE URL
     */
    private static final String BASE_URL = "http://192.168.123.217:8888/";

    private static final Retrofit RETROFIT;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 设置超时时间
                .connectTimeout(5L, TimeUnit.SECONDS)
                // 设置读写时间
                .readTimeout(5L, TimeUnit.SECONDS)
                .addInterceptor(new AuthorizationHeaderInterceptor())
                .addInterceptor(new UnauthorizedInterceptor())
                .build();
        RETROFIT = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static <T> T get(@NonNull final Class<T> service) {
        return RETROFIT.create(service);
    }

    static class AuthorizationHeaderInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request newRequest = request
                    .newBuilder()
                    .addHeader(ACCEPT, APPLICATION_JSON_VALUE)
                    .addHeader(AUTHORIZATION, "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzQ0MjI1MTcsImxvZ2luVXNlciI6IntcIm5hbWVcIjpcIuiwr-mmqOS4vVwiLFwidXNlcm5hbWVcIjpcImFcIixcInJvbGVcIjp7XCJpZFwiOlwiMVwiLFwibmFtZVwiOlwi5a2m55SfXCIsXCJnbXRDcmVhdGVcIjoxNTczMjE0MTEwMDAwLFwiZ210TW9kaWZpZWRcIjoxNTczMjE0MTEzMDAwfSxcImVtYWlsXCI6XCJvYTdqM2EzQHNvaHUuY29tXCIsXCJ0ZWxcIjpcIjEzNTAyMjg2OTkzXCJ9IiwiaXNzIjoiaXRuaW5nIn0.uEUoTj-dC_E2kq_bg9SmXOVbhSDU3Xqu7JmGXKaN_I8")
                    .build();
            return chain.proceed(newRequest);
        }
    }

    static class UnauthorizedInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            if (response.code() == UNAUTHORIZED) {
                Log.e("AA", "登陆超时");
            }
            return response;
        }
    }
}
