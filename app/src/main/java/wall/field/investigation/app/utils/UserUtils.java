package wall.field.investigation.app.utils;

import android.content.Context;


import wall.field.investigation.mvp.model.entity.User;

/**
 * 当前用户信息
 * Created by wall on 2017/9/18 14:37
 * w_ll@winning.com.cn
 */
public class UserUtils {

    private static UserUtils instance;

    private UserUtils() {
    }

    public static UserUtils getInstance() {
        if (instance == null) {
            instance = new UserUtils();
        }
        return instance;
    }

    public static String getCurrentName(Context context) {
        User user = new StorageUtils<User>(User.class, context).getItem();
        if (user != null && user.name != null) {
            return user.name;
        }
        return "";
    }
    public static String getCurrentPortrait(Context context) {
        User user = new StorageUtils<User>(User.class, context).getItem();
        if (user != null && user.portraitUrl != null) {
            return user.portraitUrl;
        }
        return "";
    }
    public static String getCurrentUid(Context context) {
        User user = new StorageUtils<User>(User.class, context).getItem();
        if (user != null && user.uid != null) {
            return user.uid;
        }
        return "";
    }

    public static String getCurrentToken(Context context) {
        User user = new StorageUtils<User>(User.class, context).getItem();
        if (user != null && user.token != null) {
            return user.token;
        }
        return "";

    }

    public static String getCurrentRole(Context context) {
        User user = new StorageUtils<User>(User.class, context).getItem();
        if (user != null && user.role != null) {
            return user.role;
        }
        return "";

    }

    public static void updatePortrait(String data, Context context) {
        User user = new StorageUtils<User>(User.class, context).getItem();
        user.portraitUrl = data;
        new StorageUtils<User>(User.class, context).save(user);
    }


}