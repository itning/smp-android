package top.itning.smpandroid.client;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.client.http.RestModel;
import top.itning.smpandroid.entity.StudentClassCheck;
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
    Observable<RestModel<Page<StudentClassUser>>> getAllStudentGroup(@Query("page") Integer page, @Query("size") Integer size);

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
     * @param longitude      经度
     * @param latitude       纬度
     * @param studentClassId 课堂ID
     * @return 学生课堂签到
     */
    @FormUrlEncoded
    @POST("/class/check")
    Observable<RestModel<StudentClassCheck>> check(@Field("longitude") double longitude, @Field("latitude") double latitude, @Field("studentClassId") String studentClassId);

    /**
     * 加入班级
     *
     * @param classNum 班号
     * @return 加入的班级
     */
    @FormUrlEncoded
    @POST("/class/join_class")
    Observable<RestModel<StudentClassUser>> joinClass(@Field("classNum") String classNum);
}
