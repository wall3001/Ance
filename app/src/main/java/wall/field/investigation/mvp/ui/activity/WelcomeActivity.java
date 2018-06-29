package wall.field.investigation.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import wall.field.investigation.di.component.DaggerWelcomeComponent;
import wall.field.investigation.di.module.WelcomeModule;
import wall.field.investigation.mvp.contract.WelcomeContract;
import wall.field.investigation.mvp.presenter.WelcomePresenter;

import wall.field.investigation.R;


import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 启动欢迎页面
 * @author wall
 */
public class WelcomeActivity extends BaseActivity<WelcomePresenter> implements WelcomeContract.View {

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerWelcomeComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .welcomeModule(new WelcomeModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_welcome;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (mPresenter != null) {
            mPresenter.toLogin();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }
}
