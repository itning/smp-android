package top.itning.smpandroid.client;

import java.io.Serializable;

import lombok.Data;

/**
 * Rest 返回消息
 *
 * @author itning
 */
@Data
public class RestModel<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
}
