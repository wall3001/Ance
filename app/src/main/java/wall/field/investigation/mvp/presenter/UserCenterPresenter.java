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
import wall.field.investigation.mvp.contract.UserCenterContract;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.ui.activity.TaskListActivity;


@ActivityScope
public class UserCenterPresenter extends BasePresenter<UserCenterContract.Model, UserCenterContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public UserCenterPresenter(UserCenterContract.Model model, UserCenterContract.View rootView) {
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

    public void logout() {
        mModel.logout();
        mAppManager.killActivity(TaskListActivity.class);
    }

    public void changePortrait(String path) {

        mModel.changePortrait(path).compose(RxUtils.applySchedulers(mRootView)).subscribe(new ErrorHandleSubscriber<BaseJson<String>>(mErrorHandler) {
            @Override
            public void onNext(BaseJson<String> objectBaseJson) {
                if(objectBaseJson.isSuccess()){
                    mModel.updatePortrait(objectBaseJson.getData());
                }
                if(!TextUtils.isEmpty(objectBaseJson.getMsg())){
                    mRootView.showMessage(objectBaseJson.getMsg());
                }
            }
        });
    }
}
