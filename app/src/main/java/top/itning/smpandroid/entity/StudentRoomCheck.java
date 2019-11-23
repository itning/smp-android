package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 学生寝室签到表
 *
 * @author itning
 */
@Data
public class StudentRoomCheck implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 学生
     */
    private User user;
    /**
     * 签到时间
     */
    private Date checkTime;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
