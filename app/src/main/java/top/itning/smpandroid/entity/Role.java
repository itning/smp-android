package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 角色
 *
 * @author itning
 */
@Data
public class Role implements Serializable {
    public static final String STUDENT_ROLE_ID = "1";

    /**
     * 角色ID
     */
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
