package top.itning.smpandroid.client;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import top.itning.smpandroid.client.http.RestModel;

/**
 * @author itning
 */
public interface SecurityClient {
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return Observable
     */
    @FormUrlEncoded
    @POST("/security/login")
    Observable<RestModel<String>> login(@Field("username") String username, @Field("password") String password);

    /**
     * PING
     *
     * @return PONG
     */
    @GET("/security/ping")
    Observable<RestModel<String>> ping();
}
