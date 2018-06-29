package wall.field.investigation.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.utils.DeviceUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.mvp.contract.LoginContract;
import wall.field.investigation.mvp.model.api.Api;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.User;
import wall.field.investigation.mvp.model.entity.Version;


@ActivityScope
public class LoginModel extends BaseModel implements LoginContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public LoginModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseJson<User>> login(String name, String password) {
        return mRepositoryManager.obtainRetrofitService(Api.class).login(name, password);
    }

    @Override
    public void save(User user) {
        new StorageUtils<User>(User.class, mApplication).save(user);
    }

    @Override
    public Observable<BaseJson<Version>> checkNewVersion() {
        return mRepositoryManager.obtainRetrofitService(Api.class).checkVersion(getCurrentVersion());
    }

    private String getCurrentVersion() {
        return DeviceUtils.getVersionName(mApplication);
    }
}