package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author itning
 */
@Data
public class Leave implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 学生
     */
    private StudentUser studentUser;
    /**
     * 请假开始时间
     */
    private Date startTime;
    /**
     * 请假结束时间
     */
    private Date endTime;
    /**
     * 请假类型
     */
    private LeaveType leaveType;
    /**
     * 请假原因
     */
    private String reason;
    /**
     * 审核状态（true 通过；false 未通过）
     */
    private Boolean status;
    /**
     * 评论
     */
    private List<LeaveReason> leaveReasonList;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
