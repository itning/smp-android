package top.itning.smpandroid.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * @author itning
 */
@Data
public class LoginUser implements Serializable {
    private String name;
    private String username;
    private Role role;
    private String email;
    private String tel;
}
