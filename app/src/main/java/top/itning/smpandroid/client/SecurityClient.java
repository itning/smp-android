package top.itning.smpandroid.client;

import io.reactivex.Observable;
import retrofit2.Response;
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
     * 修改密码
     *
     * @param newPassword 新密码
     * @return no content
     */
    @FormUrlEncoded
    @POST("/security/change/password")
    Observable<Response<Object>> changePassword(@Field("newPassword") String newPassword);

    /**
     * PING
     *
     * @return PONG
     */
    @GET("/security/ping")
    Observable<RestModel<String>> ping();
}
