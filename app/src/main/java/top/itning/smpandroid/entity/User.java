package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户
 *
 * @author itning
 */
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    private String id;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 电话
     */
    private String tel;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 角色
     */
    private Role role;
    /**
     * 所对应的学生
     */
    private StudentUser studentUser;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
