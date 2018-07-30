package wall.field.investigation.mvp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import wall.field.investigation.R;
import wall.field.investigation.app.utils.UserUtils;
import wall.field.investigation.di.component.DaggerUserCenterComponent;
import wall.field.investigation.di.module.UserCenterModule;
import wall.field.investigation.mvp.contract.UserCenterContract;
import wall.field.investigation.mvp.presenter.UserCenterPresenter;
import wall.field.investigation.mvp.ui.view.GlideCircleTransform;
import wall.field.investigation.mvp.ui.view.LoadingDialog;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class UserCenterActivity extends BaseActivity<UserCenterPresenter> implements UserCenterContract.View {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_cur_user)
    TextView tvCurUser;
    @BindView(R.id.img_portrait)
    ImageView imgPortrait;
    @BindView(R.id.rv_change_portrait)
    RelativeLayout rvChangePortrait;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUserCenterComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .userCenterModule(new UserCenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_user_center;
        //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        tvTitle.setText(R.string.user_center);
        tvCurUser.setText(UserUtils.getCurrentName(getBaseContext()));
        Glide.with(this).load(UserUtils.getCurrentPortrait(getBaseContext())).apply(RequestOptions.bitmapTransform(new GlideCircleTransform(getBaseContext()))).into(imgPortrait);
        tvVersion.setText(String.valueOf(DeviceUtils.getVersionName(this)));

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
    protected void onDestroy() {
        super.onDestroy();
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

    @OnClick({R.id.rv_change_portrait, R.id.rv_change_pwd, R.id.btn_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rv_change_portrait:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .imageSpanCount(3)
                        .compress(true)
                        .minimumCompressSize(2048) //小于2M的不压缩
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.rv_change_pwd:
                ArmsUtils.startActivity(ChangePasswordActivity.class);
                break;
            case R.id.btn_logout:
                if (mPresenter != null) {
                    mPresenter.logout();
                }
                ArmsUtils.killAll();
                ArmsUtils.startActivity(LoginActivity.class);
                killMyself();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalImage 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    if (selectList.get(0).isCompressed()) {
                        Glide.with(this).load(selectList.get(0).getCompressPath()).apply(RequestOptions.bitmapTransform(new GlideCircleTransform(getBaseContext()))).into(imgPortrait);
                    } else {
                        Glide.with(this).load(selectList.get(0).getPath()).apply(RequestOptions.bitmapTransform(new GlideCircleTransform(getBaseContext()))).into(imgPortrait);
                    }
                    if (mPresenter != null) {
                        mPresenter.changePortrait(selectList.get(0).getPath());
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
