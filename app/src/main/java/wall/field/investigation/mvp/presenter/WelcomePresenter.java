package wall.field.investigation.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.ArmsUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import wall.field.investigation.app.utils.RxUtils;
import wall.field.investigation.mvp.contract.WelcomeContract;
import wall.field.investigation.mvp.ui.activity.LoginActivity;
import wall.field.investigation.mvp.ui.activity.ScoreItemDetailActivity;
import wall.field.investigation.mvp.ui.activity.ScoreListActivity;
import wall.field.investigation.mvp.ui.activity.TaskListActivity;


@ActivityScope
public class WelcomePresenter extends BasePresenter<WelcomeContract.Model, WelcomeContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public WelcomePresenter(WelcomeContract.Model model, WelcomeContract.View rootView) {
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

    public void toLogin() {
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(new ErrorHandleSubscriber<Long>(mErrorHandler) {
                    @Override
                    public void onNext(Long aLong) {
                        ArmsUtils.startActivity(LoginActivity.class);
                        mRootView.killMyself();
                    }
                });
    }
}
