package wall.field.investigation.mvp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import butterknife.OnClick;
import wall.field.investigation.R;
import wall.field.investigation.di.component.DaggerChangePasswordComponent;
import wall.field.investigation.di.module.ChangePasswordModule;
import wall.field.investigation.mvp.contract.ChangePasswordContract;
import wall.field.investigation.mvp.presenter.ChangePasswordPresenter;
import wall.field.investigation.mvp.ui.view.LoadingDialog;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ChangePasswordActivity extends BaseActivity<ChangePasswordPresenter> implements ChangePasswordContract.View {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.edit_oldPwd)
    EditText editOldPwd;
    @BindView(R.id.edit_newPwd)
    EditText editNewPwd;
    @BindView(R.id.edit_newPwdAgain)
    EditText editNewPwdAgain;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerChangePasswordComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .changePasswordModule(new ChangePasswordModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_change_password; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        tvTitle.setText(R.string.modify_password);
    }

    private Dialog loadingDialog;
    @Override
    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.createLoadingDialog(this, "加载中");
        }
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
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

    @OnClick(R.id.btn_save)
    public void onClick() {
        if (!TextUtils.isEmpty(editOldPwd.getText().toString())) {
            if (editOldPwd.getText().toString().equals(editNewPwd.getText().toString())) {
                showMessage(getResources().getString(R.string.old_new_pwd_same));
            }else{
                if (!TextUtils.isEmpty(editNewPwd.getText().toString()) && editNewPwd.getText().toString().equals(editNewPwdAgain.getText().toString())) {
                    mPresenter.changePassword(editOldPwd.getText().toString(), editNewPwd.getText().toString());
                } else {
                    if(TextUtils.isEmpty(editNewPwd.getText().toString())&&TextUtils.isEmpty(editNewPwdAgain.getText().toString())){
                        showMessage(getResources().getString(R.string.new_pwd_not_null));
                    }else{
                        showMessage(getResources().getString(R.string.pwd_not_same));
                    }
                }
            }
        } else {
            showMessage(getResources().getString(R.string.old_pwd_not_null));
        }
    }
}
