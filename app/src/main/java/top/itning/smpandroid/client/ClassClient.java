package top.itning.smpandroid.client;

import io.reactivex.Observable;
import retrofit2.http.GET;
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
}
