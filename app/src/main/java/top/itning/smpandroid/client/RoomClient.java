package top.itning.smpandroid.client;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.client.http.RestModel;
import top.itning.smpandroid.entity.StudentRoomCheck;

/**
 * @author itning
 */
public interface RoomClient {
    /**
     * 获取学生寝室签到信息
     *
     * @param page 页码
     * @param size 数量
     * @return Observable
     */
    @GET("/room/checks")
    Observable<RestModel<Page<StudentRoomCheck>>> getStudentCheckInfo(@Query("page") Integer page, @Query("size") Integer size);

    /**
     * 允许打卡
     *
     * @return Observable
     */
    @GET("/room/allow_check")
    Observable<RestModel<Boolean>> allowCheck();

    /**
     * 寝室打卡
     *
     * @param file      图像
     * @param longitude 经度
     * @param latitude  纬度
     * @return Observable
     */
    @Multipart
    @POST("/room/check")
    Observable<RestModel<StudentRoomCheck>> check(@Part MultipartBody.Part file, @Part("longitude") double longitude, @Part("latitude") double latitude);
}
