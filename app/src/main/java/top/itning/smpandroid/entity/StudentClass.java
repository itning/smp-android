package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 学生班级，由老师创建
 *
 * @author itning
 */
@Data
public class StudentClass implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 班级名
     */
    private String name;
    /**
     * 班号（创建时教师指定，唯一；学生加入用此）
     */
    private String classNum;
    /**
     * 所属教师
     */
    private User user;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
