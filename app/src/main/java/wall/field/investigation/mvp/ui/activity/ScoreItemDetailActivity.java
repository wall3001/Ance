package wall.field.investigation.mvp.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import wall.field.investigation.R;
import wall.field.investigation.app.EventBusTags;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.di.component.DaggerScoreItemDetailComponent;
import wall.field.investigation.di.module.ScoreItemDetailModule;
import wall.field.investigation.mvp.contract.ScoreItemDetailContract;
import wall.field.investigation.mvp.model.entity.Address;
import wall.field.investigation.mvp.model.entity.Deduct;
import wall.field.investigation.mvp.model.entity.ImageBean;
import wall.field.investigation.mvp.model.entity.LocalImage;
import wall.field.investigation.mvp.model.entity.ScoreDetail;
import wall.field.investigation.mvp.model.entity.Standard;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.presenter.ScoreItemDetailPresenter;
import wall.field.investigation.mvp.ui.adapter.ImageAdapter;
import wall.field.investigation.mvp.ui.view.LoadingDialog;
import wall.field.investigation.mvp.ui.view.ShowDeduct;
import wall.field.investigation.mvp.ui.view.ShowDelete;
import wall.field.investigation.mvp.ui.view.ShowItem;
import wall.field.investigation.mvp.ui.view.ShowStandard;

import static com.jess.arms.utils.Preconditions.checkNotNull;


/**
 * 新增保存修改都共用此页面
 */
public class ScoreItemDetailActivity extends BaseActivity<ScoreItemDetailPresenter> implements ScoreItemDetailContract.View {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_score_name)
    TextView tvScoreName;
    @BindView(R.id.tv_item_content)
    TextView tvItemContent;
    @BindView(R.id.tv_standard_content)
    TextView tvStandardContent;
    @BindView(R.id.tv_deduct_content)
    TextView tvDeductContent;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_total_score)
    TextView tvTotalScore;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.rv_location)
    RelativeLayout rvLocation;
    @BindView(R.id.tv_right_top)
    TextView tvRightTop;
    @BindView(R.id.tv_location_content)
    EditText tvLocationContent;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.img_state)
    ImageView imgState;
    @BindView(R.id.tv_score_state)
    TextView tvScoreState;
    @BindView(R.id.rv_deduct_score)
    RelativeLayout rvDeductScore;
    private String taskId;
    private String scoreId;
    private String templateId;
    private boolean isAdd; //是否是新增

    private boolean hasChangeImg = false; //是否改了图片；
    @Inject
    List<TemplateDetail> templateDetailList;

    @Inject
    List<Standard> standardList;

    @Inject
    List<Deduct> deductList;

    @Inject
    RxPermissions rxPermissions;

    @Inject
    ImageAdapter imageAdapter;

    private ScoreDetail mScoreDetail;

    private ScoreDetail oldScoreDetail;

    private TaskBaseInfo mTaskBaseInfo;

    private String scoreName;

    private int deductNum = 0;

    private double maxScore;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。aMapLocation.getProvince() + aMapLocation.getCity() +
                    tvLocationContent.setText(aMapLocation.getDistrict() + aMapLocation.getStreet());
                    if (mScoreDetail == null) {
                        mScoreDetail = new ScoreDetail();
                    }
                    mScoreDetail.address = tvLocationContent.getText().toString();
                    mScoreDetail.location = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() + aMapLocation.getStreet();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                }
            }
        }
    };


    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerScoreItemDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .scoreItemDetailModule(new ScoreItemDetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_score_item_detail;
        //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        isAdd = getIntent().getBooleanExtra(EventBusTags.ISADD, false);
        taskId = getIntent().getStringExtra(EventBusTags.TASKID);
        scoreId = getIntent().getStringExtra(EventBusTags.SCOREID);
        templateId = getIntent().getStringExtra(EventBusTags.TEMPLATEID);
        scoreName = getIntent().getStringExtra(EventBusTags.SCORERECORDITEMNAME);
        //   tvTitle.setText(TextUtils.isEmpty(address) ? getString(R.string.score_record_item) : address);
        tvLocationContent.setFocusableInTouchMode(false);
        tvLocationContent.setOnClickListener(v -> {
            tvLocationContent.setFocusableInTouchMode(true);
        });
        if (isAdd) {
            imgState.setImageResource(R.drawable.ic_new);
            tvTitle.setText(R.string.add_score_item);
            //从上级带过来地址
            //    String address = getIntent().getStringExtra(EventBusTags.ADDRESS);
            //不用上级地址，用历史地址
            Address address = new StorageUtils<Address>(Address.class, this).getItem();
            if (address != null) {
                tvLocationContent.setText(address.address);
            }
            btnSave.setText(R.string.save);
        } else {
            imgState.setImageResource(R.drawable.ic_complete);
            if (!TextUtils.isEmpty(scoreName)) {
                tvScoreName.setText(scoreName);
            }
            tvTitle.setText(R.string.edit_score_item);
            tvRightTop.setText(R.string.delete);
            btnSave.setText(R.string.save_modify);
        }
        //配置图片查看
        mRecyclerView.setAdapter(imageAdapter);
        //删除图片
        imageAdapter.setOnItemClickListener((adapter, view, position) -> {
            LocalImage image = (LocalImage) adapter.getItem(position);
            if (image != null) {
                if (image.showDelete) {
                    //删除照片
                    deleteImage(image, position);
                } else {
                    //预览图片
                    previewPicture(adapter.getData(), position);
                }
            }
        });

        addTextWatchListeners();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationClient.setLocationOption(option);

        rvLocation.setOnLongClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, AmapActivity.class);
            ArmsUtils.startActivity(this, intent);
            return true;
        });

    }

    //预览图片
    private void previewPicture(List<LocalImage> data, int position) {

        List<LocalMedia> medias = new ArrayList<>();
        int j = data.size();
        for (int i = 0; i < j; i++) {
            LocalImage file = data.get(i);
            if (file != null) {
                LocalMedia media = new LocalMedia();
                media.setPath(file.imgUrl);
                media.setPosition(i);
                media.setPictureType("image");
                medias.add(media);
            }
        }
        // PictureSelectionConfig.getInstance().themeStyleId
        PictureSelector.create(getActivity()).themeStyle(R.style.picture).openExternalPreview(position, medias);

    }

    //删除照片
    private void deleteImage(LocalImage image, int position) {
        ShowDelete.getInstance().ShowDelete(getActivity(), mRecyclerView, "确定删除照片？", () -> {
            if (image != null) {
                if (!isAdd && image.isDownLoad) {
                    //删除服务端的图片
                    if (mPresenter != null) {
                        mPresenter.deleteImage(taskId, scoreId, image, position);
                        hasChangeImg = true;
                    }
                } else {
                    //删除本地图片
                    imageAdapter.remove(position);
                    if (oldScoreDetail != null && oldScoreDetail.imgList.size() == imageAdapter.getData().size()) {
                        hasChangeImg = false;
                    }
                    onSaveStateListener();

                }
            }
        });
    }

    //监听文本状态
    private void addTextWatchListeners() {
        addTextWatchListener(tvItemContent);
        addTextWatchListener(tvStandardContent);
        addTextWatchListener(tvDeductContent);
        addTextWatchListener(tvNum);
        addTextWatchListener(tvLocationContent);
    }

    //文本监听
    private void addTextWatchListener(TextView tv) {
        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onSaveStateListener();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    //监听保存按钮的状态
    @Override
    public void onSaveStateListener() {
        if (mScoreDetail != null) {
            if (!TextUtils.isEmpty(mScoreDetail.itemName) && !TextUtils.isEmpty(mScoreDetail.standardName) &&
                    !TextUtils.isEmpty(mScoreDetail.deductName) && !TextUtils.isEmpty(tvLocationContent.getText().toString())
                    && imageAdapter != null && imageAdapter.getData().size() > 0 && !TextUtils.isEmpty(tvNum.getText().toString())
                    ) {
                btnSave.setEnabled(true);
                if (isAdd) {
                    //新增
                    imgState.setImageResource(R.drawable.ic_not_save);
                } else {
                    //历史
                    if ("2".equals(mScoreDetail.scoreState)) {
                        //审核通过
                        if (checkChange()) {
                            imgState.setImageResource(R.drawable.ic_not_save);
                        } else {
                            imgState.setImageResource(R.drawable.ic_passed);
                        }
                    } else if ("3".equals(mScoreDetail.scoreState)) {
                        if (checkChange()) {
                            imgState.setImageResource(R.drawable.ic_not_save);
                        } else {
                            imgState.setImageResource(R.drawable.ic_error);
                        }
                    } else {
                        if (checkChange()) {
                            imgState.setImageResource(R.drawable.ic_not_save);
                        } else {
                            //待审核
                            imgState.setImageResource(R.drawable.ic_complete);
                        }
                    }
                }
            } else {
                if (isAdd) {
                    //新增
                    imgState.setImageResource(R.drawable.ic_new);
                } else {
                    //历史
                    if ("2".equals(mScoreDetail.scoreState)) {
                        //审核通过
                        imgState.setImageResource(R.drawable.ic_passed);
                    } else if ("3".equals(mScoreDetail.scoreState)) {
                        imgState.setImageResource(R.drawable.ic_error);
                    }
                }
                btnSave.setEnabled(false);
            }
        } else {
            btnSave.setEnabled(false);
        }
    }

    private boolean checkChange() {
        if (mScoreDetail == null) {
            return false;
        }
        if (oldScoreDetail == null) {
            return false;
        }

        if (!oldScoreDetail.itemId.equals(mScoreDetail.itemId)
                ) {
            return true;
        }
        if (!oldScoreDetail.itemName.equals(mScoreDetail.itemName)
                ) {
            return true;
        }

        if (!oldScoreDetail.standardId.equals(mScoreDetail.standardId)
                ) {
            return true;
        }
        if (!oldScoreDetail.standardName.equals(mScoreDetail.standardName)
                ) {
            return true;
        }

        if (!oldScoreDetail.deductId.equals(mScoreDetail.deductId)
                ) {
            return true;
        }
        if (!oldScoreDetail.deductName.equals(mScoreDetail.deductName)
                ) {
            return true;
        }

        if (!oldScoreDetail.deductNum.equals(mScoreDetail.deductNum)
                ) {
            return true;
        }
        if (!oldScoreDetail.deductValue.equals(mScoreDetail.deductValue)
                ) {
            return true;
        }
        if (!oldScoreDetail.deductOnce.equals(mScoreDetail.deductOnce)
                ) {
            return true;
        }

        if (oldScoreDetail.imgList != null && imageAdapter != null && oldScoreDetail.imgList.size() != imageAdapter.getItemCount()
                ) {
            return true;
        } else {
            if (hasChangeImg) {
                return true;
            }
        }

        if (oldScoreDetail.location != null && mScoreDetail.location != null && !oldScoreDetail.location.equals(mScoreDetail.location)
                ) {
            return true;
        }
        if (!TextUtils.isEmpty(oldScoreDetail.address) && !oldScoreDetail.address.equals(mScoreDetail.address)
                ) {
            return true;
        }
        //最后比较图片新增再删除未变的情况
        List<ImageBean> imgs1 = oldScoreDetail.imgList;
        if (imageAdapter == null) {
            return false;
        }
        List<LocalImage> imgs2 = imageAdapter.getData();
        if (imgs1.size() > 0 && imgs1.size() == imgs2.size()) {
            int j = imgs1.size();
            for (int i = 0; i < j; i++) {
                if (!imgs1.get(i).imgUrl.equals(imgs2.get(i).imgUrl)) {
                    return true;
                }
            }
        }

        return false;


    }

    @Override
    public void showDeduct() {
        //显示扣分标准
        rvDeductScore.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTaskBaseInfo == null) {
            //加载主体信息；
            if (mPresenter != null) {
                mPresenter.getTaskBaseInfo(taskId);
            }
        }
        if (mScoreDetail == null && !isAdd) {
            if (mPresenter != null) {
                mPresenter.getScoreDetail(taskId, scoreId);
            }
        }
        if (templateDetailList.size() == 0) {
            if (mPresenter != null) {
                mPresenter.getTemplateDetail(templateId);
            }
        }

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
        this.mLocationListener = null;
        this.mLocationClient = null;
        if (this.imageAdapter != null) {
            this.imageAdapter.onRelease();
            this.imageAdapter = null;
        }
        this.rxPermissions = null;
        ShowItem.getInstance().release();
        ShowStandard.getInstance().release();
        ShowDeduct.getInstance().release();
        ShowDelete.getInstance().release();
        templateDetailList.clear();
        standardList.clear();
        deductList.clear();
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
        setResult(101);
        finish();
    }

    @OnClick({R.id.img_add_img, R.id.img_right_location, R.id.tv_right_top, R.id.rv_item, R.id.rv_standard, R.id.rv_deduct_score, R.id.img_minus, R.id.img_add, R.id.rv_add_img, R.id.rv_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right_top:
                if (!isAdd) {
                    //删除评分项记录
                    ShowDelete.getInstance().ShowDelete(this, tvAddress, getResources().getString(R.string.delete_score), () -> {
                        if (mPresenter != null) {
                            mPresenter.deleteScore(taskId, scoreId);
                        }
                    });
                }
                break;
            case R.id.rv_item:
                //添加考核项目
                if (templateDetailList.size() > 0) {
                    ShowItem.getInstance().ShowItem(getActivity(), templateDetailList, tvTitle, templateDetail -> {
                        if (templateDetail == null) {
                            showMessage("请选择一条考核项目");
                            return;
                        }
                        //新增的时候可能为空
                        if (mScoreDetail == null) {
                            mScoreDetail = new ScoreDetail();
                        }
                        boolean hasChanged = true;

                        if (!TextUtils.isEmpty(mScoreDetail.itemId) && !TextUtils.isEmpty(templateDetail.itemId) && mScoreDetail.itemId.equals(templateDetail.itemId)
                                && !TextUtils.isEmpty(mScoreDetail.itemName) && mScoreDetail.itemName.equals(templateDetail.itemName)) {
                            hasChanged = false;
                        }
                        mScoreDetail.itemId = templateDetail.itemId;
                        mScoreDetail.itemName = templateDetail.itemName;
                        tvItemContent.setText(mScoreDetail.itemName);
                        if (templateDetail.standardList.size() == 1) {
                            //刚好只有一个考核标准的时候
                            updateStandard(templateDetail.standardList.get(0), true);
                        } else {
                            //改变的时候清除原有的
                            if (hasChanged) {
                                clear23Level();
                            }
                        }

                    });
                } else {
                    if (mPresenter != null) {
                        mPresenter.getTemplateDetail(templateId);
                    }
                }

                break;
            case R.id.rv_standard:
                //添加考核标准,通过考核项目的id找到考核标准
                if (mScoreDetail != null && !TextUtils.isEmpty(mScoreDetail.itemId)) {
                    if (templateDetailList.size() > 0) {
                        if (standardList.size() == 0) {
                            //去找考核标准
                            lookupStandard();
                        }
                        //更新一次
                        if (standardList.size() == 0) {
                            if (mPresenter != null) {
                                mPresenter.getTemplateDetail(templateId);
                            }
                        }

                        if (standardList.size() > 0) {
                            //选择考核标准
                            ShowStandard.getInstance().ShowStandard(getActivity(), standardList, tvTitle, standard -> {
                                if (mScoreDetail != null) {
                                    if (standard == null) {
                                        showMessage("请选择一条考核标准");
                                        return;
                                    }
                                    boolean hasChanged = true;
                                    if (!TextUtils.isEmpty(mScoreDetail.standardId) && !TextUtils.isEmpty(standard.standardId) && mScoreDetail.standardId.equals(standard.standardId) &&
                                            !TextUtils.isEmpty(mScoreDetail.standardName) && mScoreDetail.standardName.equals(standard.standardName)) {
                                        hasChanged = false;
                                    }
                                    updateStandard(standard, hasChanged);
                                } else {
                                    showMessage(getString(R.string.error_select_1));
                                }
                            });
                        }
                    } else {
                        if (mPresenter != null) {
                            mPresenter.getTemplateDetail(templateId);
                        }
                    }
                } else {
                    showMessage(getString(R.string.error_select_1));
                }
                break;
            case R.id.rv_deduct_score:
                //添加扣分标准
                //添加考核标准,通过考核标准的id找到扣分标准
                if (mScoreDetail != null && !TextUtils.isEmpty(mScoreDetail.standardId)) {
                    if (templateDetailList.size() > 0) {
                        if (deductList.size() == 0) {
                            //去找扣分标准
                            lookupDeductList();
                        }
                        //更新一次
                        if (deductList.size() == 0) {
                            if (mPresenter != null) {
                                mPresenter.getTemplateDetailFromClickDeduct(templateId);
                            }
                        }
                        if (deductList.size() > 0) {
                            //选择考核标准
                            ShowDeduct.getInstance().ShowDeduct(getActivity(), deductList, tvTitle, deduct -> {
                                if (mScoreDetail != null) {
                                    updateDeduct(deduct);
                                } else {
                                    showMessage(getString(R.string.error_select_1));
                                }
                            });
                        }
                    } else {
                        if (mPresenter != null) {
                            mPresenter.getTemplateDetailFromClickDeduct(templateId);
                        }
                    }
                } else {
                    showMessage(getString(R.string.error_select_2));
                }
                break;
            case R.id.img_minus:
                //扣分处减
                if (mScoreDetail == null) {
                    return;
                }
                if (TextUtils.isEmpty(mScoreDetail.deductOnce)) {
                    return;
                }
                if (deductNum == 0) {
                    showMessage("不能再少了");
                } else {
                    deductNum--;
                    double value = deductNum * Double.valueOf(mScoreDetail.deductOnce);
                    if (value >= maxScore) {
                        showMessage("已达到扣分上限");
                        value = maxScore;
                    }
                    mScoreDetail.deductNum = String.valueOf(deductNum);
                    mScoreDetail.deductValue = String.valueOf(value);
                    tvNum.setText(String.valueOf(deductNum));
                    tvTotalScore.setText(String.format("-%.1f", value));
                }
                break;
            case R.id.img_add:
                //扣分处增
                if (mScoreDetail == null) {
                    return;
                }
                if (TextUtils.isEmpty(mScoreDetail.deductOnce)) {
                    return;
                }
                deductNum++;
                double value = deductNum * Double.valueOf(mScoreDetail.deductOnce);
                if (value >= maxScore) {
                    showMessage("已达到扣分上限");
                    value = maxScore;
                }
                mScoreDetail.deductNum = String.valueOf(deductNum);
                mScoreDetail.deductValue = String.valueOf(value);
                tvNum.setText(String.valueOf(deductNum));
                tvTotalScore.setText(String.format("-%.1f", value));
                break;
            case R.id.img_add_img:
                //添加实地图片
                if (mPresenter != null) {
                    mPresenter.requestPermission();
                }
                break;
            case R.id.img_right_location:
                if (mPresenter != null) {
                    mPresenter.startLocation();
                    mPresenter.startLocation();
                }
                break;
            default:
                break;
        }
    }


    private void lookupDeductList() {
        int j = standardList.size();
        if (standardList.size() == 0) {
            lookupStandard();
        }
        deductList.clear();
        for (int i = 0; i < j; i++) {
            Standard detail = standardList.get(i);
            if (detail.standardId.equals(mScoreDetail.standardId)) {
                //找到标准,要全克隆
                int l = detail.deductList.size();
                for (int k = 0; k < l; k++) {
                    deductList.add((Deduct) detail.deductList.get(k).clone());
                }
                return;
            }
        }
    }

    //改变考核标准
    private void updateStandard(Standard standard, boolean hasChanged) {
        if (standard == null) {
            showMessage("请选择一条考核标准");
            return;
        }
        if (mScoreDetail == null) {
            return;
        }
        mScoreDetail.standardId = standard.standardId;
        mScoreDetail.standardName = standard.standardName;
        mScoreDetail.scoreLimit = standard.scoreLimit;
        mScoreDetail.percent = standard.percent;
        setMaxScore(mScoreDetail);
        tvStandardContent.setText(mScoreDetail.standardName);
        //刚好只有一个扣分标准的时候
        if (standard.deductList.size() == 1) {
            lookupDeductList();
            updateDeduct(standard.deductList.get(0));
        } else {
            if (hasChanged) {
                clear3Level();
            }
        }
    }

    //更新扣分标准
    private void updateDeduct(Deduct deduct) {
        if (deduct == null) {
            showMessage("请选择一条扣分标准");
            return;
        }

        if (mScoreDetail == null) {
            return;
        }
        if (!TextUtils.isEmpty(mScoreDetail.deductId) && !TextUtils.isEmpty(mScoreDetail.deductName)) {
            if (!TextUtils.isEmpty(deduct.deductId) && mScoreDetail.deductId.equals(deduct.deductId) && mScoreDetail.deductName.equals(deduct.deductName)) {
                //没有改变不改变
                return;
            }
        }
        deductNum = 0;
        mScoreDetail.deductNum = null;
        mScoreDetail.deductOnce = null;
        tvTotalScore.setText(null);
        tvNum.setText(null);
        mScoreDetail.deductId = deduct.deductId;
        mScoreDetail.deductName = deduct.deductName;
        mScoreDetail.deductOnce = deduct.score;
        tvDeductContent.setText(mScoreDetail.deductName);
    }

    //设置最大扣分值
    private void setMaxScore(ScoreDetail mScoreDetail) {
        double scoreLimit = Double.valueOf(mScoreDetail.scoreLimit);
        double percent = Double.valueOf(mScoreDetail.percent);
        maxScore = scoreLimit * percent / 100 + scoreLimit;
    }


    private void lookupStandard() {
        int j = templateDetailList.size();
        standardList.clear();
        for (int i = 0; i < j; i++) {
            TemplateDetail detail = templateDetailList.get(i);
            if (detail.itemId.equals(mScoreDetail.itemId)) {
                //找到标准，此时要全克隆
                int l = detail.standardList.size();
                for (int k = 0; k < l; k++) {
                    standardList.add((Standard) detail.standardList.get(k).clone());
                }
                return;
            }
        }
    }

    //清除2,3级控制
    private void clear23Level() {
        standardList.clear();
        if (mScoreDetail != null) {
            tvStandardContent.setText(null);
            mScoreDetail.standardId = null;
            mScoreDetail.standardName = null;
        }
        clear3Level();
    }

    //清除3级的
    private void clear3Level() {
        deductList.clear();
        deductNum = 0;
        mScoreDetail.deductNum = null;
        mScoreDetail.deductOnce = null;
        tvTotalScore.setText(null);
        tvNum.setText(null);
        if (mScoreDetail != null) {
            tvDeductContent.setText(null);
            mScoreDetail.deductId = null;
            mScoreDetail.deductName = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    if (!mRecyclerView.isShown()) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalImage 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    List<LocalImage> imgList = new ArrayList<>();
                    for (int i = 0; i < selectList.size(); i++) {
                        LocalImage localImage = new LocalImage();
                        if (selectList.get(i).isCompressed()) {
                            localImage.imgUrl = selectList.get(i).getCompressPath();
                        } else {
                            localImage.imgUrl = selectList.get(i).getPath();
                        }
                        localImage.showDelete = false;
                        localImage.isDownLoad = false;
                        imgList.add(localImage);
                    }
                    if (mPresenter != null) {
                        mPresenter.addImageList(imgList);
                        onSaveStateListener();
                    }
                    if (imgList.size() > 0) {
                        hasChangeImg = true;
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void updateTaskBaseInfo(TaskBaseInfo info) {
        this.mTaskBaseInfo = info;
        // tvTitle.setText(info.address);
        tvAddress.setText(info.address);
        tvScore.setText(String.format("-%.1f", Double.valueOf(info.totalScore)));
        tvState.setText(getState(info.complete));
    }

    @Override
    public void startLocation() {
        mLocationClient.startLocation();
    }

    @Override
    public RxPermissions getRxPermissions() {
        return rxPermissions;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void startAddImage() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageSpanCount(3)
                .compress(true)
                .minimumCompressSize(2048) //小于2M的不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public void updateScoreDetail(ScoreDetail data) {
        this.mScoreDetail = data;
        this.oldScoreDetail = (ScoreDetail) data.clone();
        tvItemContent.setText(data.itemName);
        tvStandardContent.setText(data.standardName);
        tvDeductContent.setText(data.deductName);
        tvNum.setText(data.deductNum);
        deductNum = Integer.valueOf(data.deductNum);
        if (!TextUtils.isEmpty(data.scoreState)) {
            tvScoreState.setText(getItemState(data.scoreState));
            tvScoreState.setTextColor(getColor(data.scoreState));
        }
        tvTotalScore.setText(String.format("-%.1f", Double.valueOf(data.deductValue)));
        setMaxScore(mScoreDetail);
        //添加图片
        if (data.imgList != null && data.imgList.size() > 0) {
            if (!mRecyclerView.isShown()) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            int j = data.imgList.size();
            List<LocalImage> imageList = new ArrayList<>();
            for (int i = 0; i < j; i++) {
                LocalImage localImage = new LocalImage();
                localImage.imgUrl = data.imgList.get(i).imgUrl;
                localImage.imgId = data.imgList.get(i).imgId;
                localImage.isDownLoad = true;
                localImage.showDelete = false;
                imageList.add(localImage);
            }
            imageAdapter.setNewData(imageList);
        }
        //地理位置
        if (TextUtils.isEmpty(data.address)) {
            tvLocationContent.setText(data.location);
        } else {
            tvLocationContent.setText(data.address);
        }


    }

    private String getState(String complete) {
        String state = "";
        switch (complete) {
            case "0":
                state = "未开始";
                break;
            case "1":
                state = "进行中";
                break;
            case "2":
                state = "已完成";
                break;
            default:
                break;
        }
        return state;
    }


    //保存
    @OnClick(R.id.btn_save)
    public void onClick() {

        if (isAdd) {
            //新增
            if (mScoreDetail == null) {
                return;
            }
            if (mPresenter != null) {
                mPresenter.submitScore(taskId, mScoreDetail.itemId, mScoreDetail.standardId,
                        mScoreDetail.deductId, tvNum.getText().toString(), imageAdapter.getData(), tvLocationContent.getText().toString(), mScoreDetail.location);
            }
        } else {
            //修改
            if (mScoreDetail == null) {
                return;
            }
            if (mPresenter != null) {
                mPresenter.updateScoreDetail(taskId, scoreId, mScoreDetail.itemId, mScoreDetail.standardId,
                        mScoreDetail.deductId, tvNum.getText().toString(), imageAdapter.getData(), tvLocationContent.getText().toString(), mScoreDetail.location);
            }
        }
    }


    private int getColor(String scoreState) {
        int color = R.color.white;
        switch (scoreState) {
            case "0":
                color = R.color.txt_sub;
                break;
            case "1":
                color = R.color.txt_sub;
                break;
            case "2":
                color = R.color.green_past;
                break;
            case "3":
                color = R.color.red_btn_bg;
                break;
            default:
                break;
        }
        return color;
    }

    private String getItemState(String scoreState) {
        String state;
        switch (scoreState) {
            case "0":
                state = "未提交";
                break;
            case "1":
                state = "待审核";
                break;
            case "2":
                state = "通过";
                break;
            case "3":
                state = "需修改";
                break;
            default:
                state = "";
                break;
        }
        return state;

    }

}
