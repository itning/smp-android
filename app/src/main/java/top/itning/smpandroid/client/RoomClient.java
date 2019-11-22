package top.itning.smpandroid.client;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
}
