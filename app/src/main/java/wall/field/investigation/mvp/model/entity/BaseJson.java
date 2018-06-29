package wall.field.investigation.mvp.model.entity;


import java.io.Serializable;

import wall.field.investigation.mvp.model.api.Api;

/**
 * 请求数据返回的基类
 * Created by wall on 2017/9/14 13:26
 * w_ll@winning.com.cn
 */

public class BaseJson<T> implements Serializable {

    private T data;
    private String code;
    private String msg;

    public T getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 请求是否成功
     * @return ture 成功 false 失败
     */
    public boolean isSuccess() {
        if (code.equals(Api.RequestSuccess)) {
            return true;
        } else {
            return false;
        }
    }

}
