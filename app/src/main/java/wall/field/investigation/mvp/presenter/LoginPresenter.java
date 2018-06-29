package wall.field.investigation.mvp.presenter;

import android.app.Application;
import android.text.TextUtils;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import wall.field.investigation.app.utils.RxUtils;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.mvp.contract.LoginContract;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.User;
import wall.field.investigation.mvp.model.entity.Version;


@ActivityScope
public class LoginPresenter extends BasePresenter<LoginContract.Model, LoginContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public LoginPresenter(LoginContract.Model model, LoginContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void login(String name, String password) {
        mModel.login(name,password).compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<User>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<User> userBaseJson) {
                        if(userBaseJson.isSuccess()){
                            User user = userBaseJson.getData();
                            if(user!=null){
                                mModel.save(user);
                                mRootView.toTaskListActivity(user);
                            }
                        }else{
                            mRootView.showMessage(userBaseJson.getMsg());
                        }
                    }
                });
    }


    public void checkNewVersion() {
        mModel.checkNewVersion().compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Version>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Version> versionBaseJson) {
                        if(versionBaseJson.isSuccess()){
                            Version version = versionBaseJson.getData();
                            if(version!=null&& !TextUtils.isEmpty(version.url)){
                                mRootView.showNewVersion(version);
                            }
                        }
                    }
                });
    }
}
