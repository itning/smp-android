package top.itning.smpandroid.client;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.client.http.RestModel;
import top.itning.smpandroid.entity.Leave;
import top.itning.smpandroid.entity.LeaveReason;

/**
 * @author itning
 */
public interface LeaveClient {
    /**
     * 获取学生寝室签到信息
     *
     * @param page 页码
     * @param size 数量
     * @return Observable
     */
    @GET("/leave/studentLeaves")
    Observable<RestModel<Page<Leave>>> getStudentLeaves(@Query("page") Integer page, @Query("size") Integer size);

    /**
     * 评论
     *
     * @param leaveId 请假ID
     * @param comment 评论
     * @return ResponseEntity
     */
    @FormUrlEncoded
    @POST("/leave/leave/comment")
    Observable<RestModel<LeaveReason>> newComment(@Field("leaveId") String leaveId, @Field("comment") String comment);


    /**
     * 新增请假信息
     *
     * @param startTime 请假开始时间
     * @param endTime   请假结束时间
     * @param reason    原因
     * @param leaveType 请假类型
     * @return 新增的请假信息
     */
    @FormUrlEncoded
    @POST("/leave/leave")
    Observable<RestModel<Leave>> newLeave(@Field("startTime") String startTime,
                                          @Field("endTime") String endTime,
                                          @Field("reason") String reason,
                                          @Field("leaveType") String leaveType);
}
