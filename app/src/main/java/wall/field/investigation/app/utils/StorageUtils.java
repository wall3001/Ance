package wall.field.investigation.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import timber.log.Timber;

/**
 * 保存各种bean到SharedPreferences工具类
 **/
public class StorageUtils<T> {

    public final static String FILE_PRE_NAME = "FAST";
    private final static String TAG = "StorageUtils";
    private SharedPreferences sharedPreferences;
    private String saveTag;
    private JsonHelp<T> jsonhelp;


    public static String getFileName(String calssName) {
        return FILE_PRE_NAME + "_" + calssName;
    }

    //每个bean一个文件
    public StorageUtils(Class<T> entityClass, Context context) {
        //init tool
        saveTag = entityClass.getName();
        String fileName = getFileName(saveTag);
        Timber.d(TAG, "saveTag=" + saveTag + " fileName=" + fileName);
        sharedPreferences = context.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
        jsonhelp = new JsonHelp<T>(entityClass);
    }

    public void add(T t) {
        List<T> list = getItems();
        if (!list.contains(t)) {
            list.add(t);
        }
        save(list);
    }

    public List<T> getItems() {
        String saveArr = sharedPreferences.getString(saveTag, "");
        List<T> list = jsonhelp.getItemList(saveArr);
        Timber.d(TAG, "get tag=" + saveTag + " size=" + list.size());
        return list;
    }

    public void save(List<T> list) {
        Timber.d(TAG, "save tag=" + saveTag + " size=" + list.size());
        String listStr = jsonhelp.list2Json(list);
        sharedPreferences.edit().putString(saveTag, listStr).commit();
    }



    public void save(T t) {
        saveByTag(t, "");
    }

    public void saveByTag(T t, String otherTag) {
        Timber.d(TAG, "save tag=" + saveTag + otherTag);
        String item = jsonhelp.item2Json(t);
        sharedPreferences.edit().putString(saveTag + otherTag, item).commit();
    }

    public void saveItemsByTag(List<T> list, String otherTag) {
        Timber.d(TAG, "save tag=" + saveTag + " size=" + list.size());
        String listStr = jsonhelp.list2Json(list);
        sharedPreferences.edit().putString(saveTag+otherTag, listStr).commit();
    }


    public T getItem() {
        return getItemByTag("");
    }

    public T getItemByTag(String otherTag) {
        String saveArr = sharedPreferences.getString(saveTag + otherTag, "");
        Timber.d(TAG, "getItemByTag saveArr=" + saveArr);
        if (!"".equals(saveArr)) {
            return jsonhelp.getItem(saveArr);
        }
        return null;
    }


    public List<T> getItemsByTag(String otherTag) {
        String saveArr = sharedPreferences.getString(saveTag+otherTag, "");
        List<T> list = jsonhelp.getItemList(saveArr);
        Timber.d(TAG, "get tag=" + saveTag + " size=" + list.size());
        return list;
    }



    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

}
