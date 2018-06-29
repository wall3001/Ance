package wall.field.investigation.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import io.reactivex.annotations.NonNull;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import wall.field.investigation.R;
import wall.field.investigation.app.utils.RxUtils;
import wall.field.investigation.mvp.contract.ChangePasswordContract;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.ui.activity.LoginActivity;


@ActivityScope
public class ChangePasswordPresenter extends BasePresenter<ChangePasswordContract.Model, ChangePasswordContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public ChangePasswordPresenter(ChangePasswordContract.Model model, ChangePasswordContract.View rootView) {
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

    public void changePassword(String oldPwd, String newPwd) {
        mModel.changePassword(oldPwd, newPwd).compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(@NonNull BaseJson<Object> jsonObjectBaseJson) {
                        if (jsonObjectBaseJson.isSuccess()) {
                            mRootView.showMessage(ArmsUtils.getString(mApplication, R.string.save_success));
                            ArmsUtils.startActivity(LoginActivity.class);
                            mRootView.killMyself();
                        }else{
                            mRootView.showMessage(jsonObjectBaseJson.getMsg());
                        }
                    }
                });

    }


}
