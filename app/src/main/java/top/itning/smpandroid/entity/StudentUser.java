package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author itning
 */
@Data
public class StudentUser implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 性别（true 男； false 女）
     */
    private Boolean sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 学号
     */
    private String studentId;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 政治面貌
     */
    private String politicalStatus;
    /**
     * 民族
     */
    private String ethnic;
    /**
     * 公寓信息
     */
    private Apartment apartment;
    /**
     * 寝室号
     */
    private String roomNum;
    /**
     * 家庭地址
     */
    private String address;
    /**
     * 床号
     */
    private String bedNum;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
