package top.itning.smpandroid.client.http;

import java.io.Serializable;

import lombok.Data;

/**
 * Rest 返回消息
 *
 * @author itning
 */
@Data
public class RestModel<T> implements Serializable {
    /**
     * 服务代码
     */
    private int code;
    /**
     * 消息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private T data;
}