package top.itning.smpandroid.client;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.client.http.RestModel;
import top.itning.smpandroid.entity.StudentClassCheck;
import top.itning.smpandroid.entity.StudentClassCheckDTO;
import top.itning.smpandroid.entity.StudentClassUser;

/**
 * @author itning
 */
public interface ClassClient {
    /**
     * 获取所有学生班级
     *
     * @param page 分页
     * @param size 页面大小
     * @return 学生班级
     */
    @GET("/class/student_class_users")
    Observable<RestModel<Page<StudentClassUser>>> getAllStudentClassUser(@Query("page") Integer page, @Query("size") Integer size);

    /**
     * 获取学生签到信息
     *
     * @param studentClassId 学生课堂ID
     * @param page           分页
     * @param size           数量
     * @return 学生签到信息
     */
    @GET("/class/checks/{studentClassId}")
    Observable<RestModel<Page<StudentClassCheck>>> getAllChecks(@Path("studentClassId") String studentClassId, @Query("page") Integer page, @Query("size") Integer size);

    /**
     * 检查是否可以签到
     *
     * @param studentClassId 学生班级ID
     * @return 可以签到返回<code>true</code>
     */
    @GET("/class/can_check/{studentClassId}")
    Observable<RestModel<Boolean>> canCheck(@Path("studentClassId") String studentClassId);

    /**
     * 学生课堂签到
     *
     * @param file           文件
     * @param longitude      经度
     * @param latitude       纬度
     * @param studentClassId 课堂ID
     * @return 学生课堂签到
     */
    @Multipart
    @POST("/class/check")
    Observable<RestModel<StudentClassCheck>> check(@Part MultipartBody.Part file,
                                                   @Part("longitude") double longitude,
                                                   @Part("latitude") double latitude,
                                                   @Part("studentClassId") RequestBody studentClassId);

    /**
     * 加入班级
     *
     * @param classNum 班号
     * @return 加入的班级
     */
    @FormUrlEncoded
    @POST("/class/join_class")
    Observable<RestModel<StudentClassUser>> joinClass(@Field("classNum") String classNum);

    /**
     * 退出班级
     *
     * @param studentClassId 班级ID
     * @return void
     */
    @FormUrlEncoded
    @POST("/class/quit_class")
    Observable<Response<Object>> quitClass(@Field("studentClassId") String studentClassId);

    /**
     * 获取签到信息
     *
     * @param studentUserName 学生用户名
     * @param studentClassId  班级ID
     * @return 签到信息
     */
    @GET("/class/user_check_detail")
    Observable<RestModel<List<StudentClassCheckDTO>>> getUserCheckDetail(@Query("studentUserName") String studentUserName, @Query("studentClassId") String studentClassId);
}
