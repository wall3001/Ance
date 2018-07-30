package wall.field.investigation.mvp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.utils.PermissionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import timber.log.Timber;
import wall.field.investigation.R;
import wall.field.investigation.app.EventBusTags;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.di.component.DaggerLoginComponent;
import wall.field.investigation.di.module.LoginModule;
import wall.field.investigation.mvp.contract.LoginContract;
import wall.field.investigation.mvp.model.entity.Name;
import wall.field.investigation.mvp.model.entity.User;
import wall.field.investigation.mvp.model.entity.Version;
import wall.field.investigation.mvp.presenter.LoginPresenter;
import wall.field.investigation.mvp.ui.view.LoadingDialog;
import wall.field.investigation.mvp.ui.view.ShowNewVersion;
import winnings.update.Updater;
import winnings.update.UpdaterConfig;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 登录页
 *
 * @author wall
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.img_show)
    ImageView imgShow;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_copyright)
    TextView tvCopyright;
    @BindView(R.id.img_delete_name)
    ImageView imgDeleteName;
    @BindView(R.id.img_delete_pwd)
    ImageView imgDeletePwd;

    private RxPermissions rxPermissions;
    @Inject
    RxErrorHandler mErrorHandler;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .loginModule(new LoginModule(this))
                .build()
                .inject(this);
        rxPermissions = new RxPermissions(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        //版权时间
        tvCopyright.setText(String.format(getResources().getString(R.string.format_txt_copyright), new SimpleDateFormat("yyyy").format(System.currentTimeMillis())));

        //检测最新版本
        if (mPresenter != null) {
            mPresenter.checkNewVersion();
        }

        Name name = new StorageUtils<Name>(Name.class, getApplicationContext()).getItem();
        if (name != null) {
            editName.setText(name.name);
            editPassword.setText(name.password);
            if (!TextUtils.isEmpty(name.name)) {
                editName.setSelection(name.name.length());
                imgDeleteName.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(name.password)) {
                editPassword.setSelection(name.password.length());
                imgDeletePwd.setVisibility(View.VISIBLE);
            }
        } else {
            //   imgDeleteName.setVisibility(View.GONE);
            //  imgDeletePwd.setVisibility(View.GONE);
        }

        addTextChangedListener(editName, imgDeleteName);
        addTextChangedListener(editPassword, imgDeletePwd);
    }

    private void addTextChangedListener(EditText edit, ImageView img) {
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(edit.getText().toString())) {
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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


    private boolean show = false;

    @OnClick({R.id.img_show, R.id.btn_login, R.id.img_delete_name, R.id.img_delete_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_show:
                if (show) {
                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editPassword.setSelection(editPassword.getText().length());
                    imgShow.setImageResource(R.drawable.ic_eye_unable);
                } else {
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editPassword.setSelection(editPassword.getText().length());
                    imgShow.setImageResource(R.drawable.ic_eye_able);
                }
                show = !show;
                break;
            case R.id.btn_login:
                if (!TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editPassword.getText().toString())) {
                    login();
                } else {
                    showMessage("请输入用户名和密码");
                }
                break;
            case R.id.img_delete_name:
                editName.setText(null);
                break;
            case R.id.img_delete_pwd:
                editPassword.setText(null);
                break;
            default:
                break;
        }
    }

    private void login() {

        if (mPresenter != null) {
            mPresenter.login(editName.getText().toString(), editPassword.getText().toString());
        }
    }

    @Override
    public void toTaskListActivity(User user) {
        Intent intent = new Intent();
        intent.setClass(this, TaskListActivity.class);
        ArmsUtils.startActivity(this, intent);
        killMyself();
    }

    @Override
    public void showNewVersion(Version version) {
        DeviceUtils.hideSoftKeyboard(this, editName);
        ShowNewVersion.getInstance().ShowNewVersion(this, version, editName, () -> {
            if (!TextUtils.isEmpty(version.url)) {
                PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        if (version.url.startsWith("http") | version.url.startsWith("Http") | version.url.startsWith("HTTP")) {
                            UpdaterConfig config = new UpdaterConfig.Builder(getBaseContext())
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setDescription(version.version)
                                    .setFileUrl(version.url.replaceAll("\\\\", "/"))
                                    .setCanMediaScanner(true)
                                    .build();
                            Updater.get().showLog(true).download(config);
                            String s = version.url.replaceAll("\\\\", "/");
                            Timber.e("s==" + s);
                        }
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        showMessage("请开启读取存取权限");
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {

                    }
                }, rxPermissions, mErrorHandler);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShowNewVersion.getInstance().release();
        rxPermissions = null;
        mErrorHandler = null;

    }


}
