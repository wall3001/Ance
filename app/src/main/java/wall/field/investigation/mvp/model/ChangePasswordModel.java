package wall.field.investigation.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import wall.field.investigation.app.utils.UserUtils;
import wall.field.investigation.mvp.contract.ChangePasswordContract;
import wall.field.investigation.mvp.model.api.Api;
import wall.field.investigation.mvp.model.entity.BaseJson;


@ActivityScope
public class ChangePasswordModel extends BaseModel implements ChangePasswordContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public ChangePasswordModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseJson<Object>> changePassword(String oldPwd, String newPwd) {
        return mRepositoryManager.obtainRetrofitService(Api.class).changePassword(UserUtils.getCurrentUid(mApplication), UserUtils.getCurrentToken(mApplication),oldPwd,newPwd);
    }
}