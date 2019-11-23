package top.itning.smpandroid.client.http;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import top.itning.smpandroid.ui.activity.App;
import top.itning.smpandroid.ui.activity.LoginActivity;

/**
 * @author itning
 */
public final class HttpHelper {
    public static final String TOKEN = "token";
    private static final String AUTHORIZATION = "Authorization";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON_VALUE = "application/json";
    private static final int UNAUTHORIZED = 401;
    /**
     * TODO 临时 BASE URL
     */
    private static final String BASE_URL = "http://192.168.123.217:8888/";

    private static final Retrofit RETROFIT;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 设置超时时间
                .connectTimeout(15L, TimeUnit.SECONDS)
                // 设置读写时间
                .readTimeout(15L, TimeUnit.SECONDS)
                .addInterceptor(new AuthorizationHeaderInterceptor())
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

    public static class ErrorInvoke implements Consumer<Throwable> {


        public static ErrorInvoke get(@NonNull Activity activity) {
            return new ErrorInvoke(activity);
        }

        private final Activity activity;
        @Nullable
        private java.util.function.Consumer<Throwable> throwableConsumer;
        @Nullable
        private java.util.function.Consumer<Throwable> andConsumer;

        private ErrorInvoke(@NonNull Activity activity) {
            this.activity = activity;
        }

        @Override
        public void accept(Throwable throwable) {
            if (andConsumer != null) {
                andConsumer.accept(throwable);
            }
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                if (httpException.code() == UNAUTHORIZED) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }
            } else {
                if (throwableConsumer != null) {
                    throwableConsumer.accept(throwable);
                }
            }
        }

        public ErrorInvoke orElse(@NonNull java.util.function.Consumer<Throwable> throwableConsumer) {
            this.throwableConsumer = throwableConsumer;
            return this;
        }

        public ErrorInvoke before(@NonNull java.util.function.Consumer<Throwable> andConsumer) {
            this.andConsumer = andConsumer;
            return this;
        }
    }

    static class AuthorizationHeaderInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            String token = "";
            if (App.smpDataSharedPreferences != null) {
                token = App.smpDataSharedPreferences.getString(HttpHelper.TOKEN, "");
            }
            Request request = chain.request();
            Request newRequest = request
                    .newBuilder()
                    .addHeader(ACCEPT, APPLICATION_JSON_VALUE)
                    .addHeader(AUTHORIZATION, token)
                    .build();
            return chain.proceed(newRequest);
        }
    }
}
