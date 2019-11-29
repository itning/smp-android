package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author itning
 */
@Data
public class StudentClassCheckMetaData implements Serializable {
    /**
     * 元数据ID
     */
    private String id;
    /**
     * 签到开始时间
     */
    private Date startTime;
    /**
     * 签到结束时间
     */
    private Date endTime;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 签到者距离教师经纬度最大距离
     */
    private float m;
    /**
     * 元数据所对应的班级
     */
    private StudentClass studentClass;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
