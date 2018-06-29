package wall.field.investigation.app.utils;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by michael--yao on 2017/8/17 10:19.
 */

public class JsonHelp<T> {

    private final static String TAG = "JsonHelp";
    private final Gson gson;
    private final Class<T> clazz;

    public JsonHelp(Class<T> clazz) {
        gson = new Gson();
        this.clazz = clazz;
    }

    // json转对象
    @SuppressLint("TimberArgCount")
    public T getItem(String jsonStr) {
        if (jsonStr != null && !("").equals(jsonStr)) {
            try {
                T t = gson.fromJson(jsonStr, clazz);
                if (t != null) {
                    Timber.w(TAG, "getItem=" + t.toString());
                }
                return t;
            } catch (Exception e) {
                Timber.e("json转对象解析错误" + e.getMessage());
            }
        }
        return null;
    }

    // json转对象列表
    public List<T> getItemList(String jsonListStr) {
        List<T> list = new ArrayList<T>();
        if (jsonListStr != null && !("").equals(jsonListStr)) {
            try {
                JSONArray jarry = new JSONArray(jsonListStr);
                int size = jarry.length();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        String item = jarry.getString(i);
                        list.add(getItem(item));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Timber.d(TAG, "getItemList size=" + list.size());
        return list;
    }

    // 对象转json
    public String item2Json(T t) {
        try {
            String jsonStr = gson.toJson(t);
            Timber.d(TAG, "item2Json return String=(" + jsonStr + ")");
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // 对象列表转json
    public String list2Json(List<T> list) {
        try {
            String jsonStr = gson.toJson(list);
            Timber.d(TAG, "list2Json return String=(" + jsonStr + ")");
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    // 对象转json对象
    public JSONObject item2JsonObject(T t) {
        try {
            String jsonStr = gson.toJson(t);
            JSONObject js = new JSONObject(jsonStr);
            Timber.d(TAG, "item2JsonObject return js=(" + js + ")");
            return js;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 对象列表转json数组
    public JSONArray list2JsonArray(List<T> list) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (T t : list) {
                String jsonStr = gson.toJson(t);
                JSONObject js = new JSONObject(jsonStr);
                jsonArray.put(js);
            }
            Timber.d(TAG, "list2JsonArray return jsonArray=(" + jsonArray + ")");
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
}



















