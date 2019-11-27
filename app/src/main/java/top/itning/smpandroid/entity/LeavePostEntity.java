package top.itning.smpandroid.entity;

import lombok.Data;

/**
 * @author itning
 */
@Data
public class LeavePostEntity {
    private String startTime;
    private String endTime;
    private int leaveType;
    private String reason;
}
