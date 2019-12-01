package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 学生课堂签到
 * 每个班级的签到信息
 *
 * @author itning
 */
@Data
public class StudentClassCheck implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 学生信息
     */
    private User user;
    /**
     * 所属班级
     */
    private StudentClass studentClass;
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
     * 本次签到所对应的元数据
     */
    private StudentClassCheckMetaData studentClassCheckMetaData;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
