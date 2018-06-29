package wall.field.investigation.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.app.utils.UserUtils;
import wall.field.investigation.mvp.contract.UserCenterContract;
import wall.field.investigation.mvp.model.api.Api;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.User;


@ActivityScope
public class UserCenterModel extends BaseModel implements UserCenterContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public UserCenterModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public void logout() {
        new StorageUtils<User>(User.class, mApplication).clear();
    }

    @Override
    public Observable<BaseJson<String>> changePortrait(String path) {
        LinkedHashMap<String, RequestBody> bodyMap = new LinkedHashMap<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", UserUtils.getCurrentUid(mApplication));
            jsonObject.put("token", UserUtils.getCurrentToken(mApplication));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        bodyMap.put("json", body);
        File uf = new File(path);
        bodyMap.put("img", RequestBody.create(MediaType.parse("image/*"), uf));
        return mRepositoryManager.obtainRetrofitService(Api.class).changePortrait(bodyMap);
    }

    @Override
    public void updatePortrait(String data) {
        UserUtils.updatePortrait(data, mApplication);
    }
}