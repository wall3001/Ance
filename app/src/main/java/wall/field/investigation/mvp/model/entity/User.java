package wall.field.investigation.mvp.model.entity;

/**
 * Created by wall on 2018/6/15 15:34
 * w_ll@winning.com.cn
 */
public class User {


    public String uid;
    public String token;
    public String name;
    public String password;
    public String portraitUrl;
    /**
     *  二期新增接口 管理员 = 0,调研员 = 1,调研组长 = 2
     */
    public String role;
}
