package top.itning.smpandroid.client.http;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import ikidou.reflect.TypeBuilder;
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
import top.itning.smpandroid.util.Tuple2;

/**
 * @author itning
 */
public final class HttpHelper {
    /**
     * TOKEN在SharedPreferences中的KEY
     */
    public static final String TOKEN_KEY = "token";
    /**
     * BASE_URL在SharedPreferences中的KEY
     */
    public static final String BASE_URL_KEY = "base_url";
    /**
     * LOGIN_USER_NAME在SharedPreferences中的KEY
     */
    public static final String LOGIN_USER_NAME_KEY = "loginUser_name";
    /**
     * TOKEN在请求头中的权限字段
     */
    private static final String AUTHORIZATION = "Authorization";
    /**
     * Accept Header
     */
    private static final String ACCEPT = "Accept";
    /**
     * JSON Type
     */
    private static final String APPLICATION_JSON_VALUE = "application/json";
    /**
     * Un Authorized Code
     */
    public static final int UNAUTHORIZED = 401;

    private static Retrofit RETROFIT;

    /**
     * 初始化Retrofit<br>
     * 调用时会通过<code>App.smpDataSharedPreferences</code>中的<code>BASE_URL_KEY</code>设置的URL<br>
     * 去设置baseUrl
     */
    public static void initRetrofit() {
        String baseUrl = "http://localhost/";
        if (App.smpDataSharedPreferences != null) {
            baseUrl = App.smpDataSharedPreferences.getString(BASE_URL_KEY, "http://localhost/");
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 设置超时时间
                .connectTimeout(15L, TimeUnit.SECONDS)
                // 设置读写时间
                .readTimeout(15L, TimeUnit.SECONDS)
                .addInterceptor(new AuthorizationHeaderInterceptor())
                .build();
        RETROFIT = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    /**
     * 获取RETROFIT实例
     *
     * @param service 服务名
     * @param <T>     返回类型
     * @return 代理的实例
     */
    public static <T> T get(@NonNull final Class<T> service) {
        return RETROFIT.create(service);
    }

    /**
     * 从HttpException解析服务器数据
     *
     * @param httpException HttpException
     * @return 解析服务器数据可能为<code>null</code>
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static RestModel<String> getRestModelFromHttpException(@NonNull HttpException httpException) {
        try {
            Type type = TypeBuilder
                    .newInstance(RestModel.class)
                    .addTypeParam(String.class)
                    .build();
            return new Gson().fromJson(httpException.response().errorBody().string(), type);
        } catch (Exception e) {
            Log.w("HttpHelper", "parse error response exception", e);
            return null;
        }
    }

    /**
     * 错误处理类
     */
    public static class ErrorInvoke implements Consumer<Throwable> {
        /**
         * 获取错误处理实例
         *
         * @param activity Activity
         * @return ErrorInvoke
         */
        public static ErrorInvoke get(@NonNull Activity activity) {
            return new ErrorInvoke(activity);
        }

        private final Activity activity;
        @Nullable
        private java.util.function.Consumer<Throwable> throwableConsumer;
        @Nullable
        private java.util.function.Consumer<Throwable> andConsumer;
        @Nullable
        private java.util.function.Consumer<Tuple2<HttpException, RestModel<String>>> elseCodeConsumer;

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
                RestModel<String> restModel = getRestModelFromHttpException(httpException);
                if (httpException.code() == UNAUTHORIZED) {
                    if (restModel != null) {
                        Toast.makeText(activity, restModel.getMsg(), Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                } else {
                    if (elseCodeConsumer != null) {
                        elseCodeConsumer.accept(new Tuple2<>(httpException, restModel));
                    } else {
                        final String msg = restModel == null ? httpException.code() + "" : restModel.getMsg();
                        if (Looper.myLooper() != Looper.getMainLooper()) {
                            activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } else {
                if (throwableConsumer != null) {
                    throwableConsumer.accept(throwable);
                }
            }
        }

        /**
         * 如果抛出的异常是{@link HttpException}实例但是状态码非401则会执行该方法
         * 不调用该方法默认会使用{@link android.widget.Toast}将msg消息弹出
         *
         * @param elseCodeConsumer 执行方法
         * @return ErrorInvoke实例
         */
        public ErrorInvoke orElseCode(@NonNull java.util.function.Consumer<Tuple2<HttpException, RestModel<String>>> elseCodeConsumer) {
            this.elseCodeConsumer = elseCodeConsumer;
            return this;
        }

        /**
         * 如果抛出的异常不是{@link HttpException}则会执行该方法
         *
         * @param throwableConsumer 执行方法
         * @return ErrorInvoke实例
         */
        public ErrorInvoke orElseException(@NonNull java.util.function.Consumer<Throwable> throwableConsumer) {
            this.throwableConsumer = throwableConsumer;
            return this;
        }

        /**
         * 该方法会在所有异常处理之前执行
         *
         * @param andConsumer 执行方法
         * @return ErrorInvoke实例
         */
        public ErrorInvoke before(@NonNull java.util.function.Consumer<Throwable> andConsumer) {
            this.andConsumer = andConsumer;
            return this;
        }
    }

    /**
     * Authorization请求头拦截器
     */
    static class AuthorizationHeaderInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            String token = "";
            if (App.smpDataSharedPreferences != null) {
                token = App.smpDataSharedPreferences.getString(HttpHelper.TOKEN_KEY, "");
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
