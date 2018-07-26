package wall.field.investigation.mvp.presenter;

import android.Manifest;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.PermissionUtil;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import wall.field.investigation.R;
import wall.field.investigation.app.utils.RxUtils;
import wall.field.investigation.mvp.contract.ScoreItemDetailContract;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.Deduct;
import wall.field.investigation.mvp.model.entity.LocalImage;
import wall.field.investigation.mvp.model.entity.ScoreDetail;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.Standard;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.ui.adapter.ImageAdapter;


@ActivityScope
public class ScoreItemDetailPresenter extends BasePresenter<ScoreItemDetailContract.Model, ScoreItemDetailContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    ImageAdapter imageAdapter;

    @Inject
    public ScoreItemDetailPresenter(ScoreItemDetailContract.Model model, ScoreItemDetailContract.View rootView) {
        super(model, rootView);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
        if (this.imageAdapter != null) {
            this.imageAdapter.onRelease();
            this.imageAdapter = null;
        }
    }

    public void getTaskBaseInfo(String taskId) {
        mModel.getTaskBaseInfo(taskId).compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<TaskBaseInfo>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<TaskBaseInfo> taskBaseInfoBaseJson) {
                        if (taskBaseInfoBaseJson.isSuccess()) {
                            mRootView.updateTaskBaseInfo(taskBaseInfoBaseJson.getData());
                        }
                    }
                });

    }

    //删除评分记录项
    public void deleteScore(String taskId, String scoreId) {
        mModel.deleteScore(taskId, scoreId).compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Object> objectBaseJson) {
                        if (objectBaseJson.isSuccess()) {
                            mRootView.showMessage("删除成功");
                            mRootView.killMyself();
                        } else {
                            mRootView.showMessage("删除失败");
                        }
                    }
                });
    }

    public void addImageList(List<LocalImage> selectList) {
        imageAdapter.addData(selectList);
    }


    public void deleteImage(String taskId, String scoreId, LocalImage image, int position) {
        mModel.deleteImage(taskId, scoreId, image.imgId)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Object> objectBaseJson) {
                        if (objectBaseJson.isSuccess()) {
                            mRootView.showMessage(mApplication.getString(R.string.delete_success));
                            //删除成功后删除本地图片
                            imageAdapter.remove(position);
                            mRootView.onSaveStateListener();
                        } else {
                            mRootView.showMessage(mApplication.getString(R.string.delete_fail));
                        }
                    }
                });

    }

    public void startLocation() {
        PermissionUtil.requestPermission(new PermissionUtil.RequestPermission() {
                                             @Override
                                             public void onRequestPermissionSuccess() {
                                                 mRootView.startLocation();
                                             }

                                             @Override
                                             public void onRequestPermissionFailure(List<String> permissions) {
                                                 mRootView.showMessage("请开始定位权限，读取系统状态权限");
                                             }

                                             @Override
                                             public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                                                 mRootView.showMessage("请开始定位权限，读取系统状态权限");
                                             }
                                         }, mRootView.getRxPermissions(), mErrorHandler, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }

    public void requestPermission() {
        PermissionUtil.requestPermission(new PermissionUtil.RequestPermission() {
                                             @Override
                                             public void onRequestPermissionSuccess() {
                                                 mRootView.startAddImage();
                                             }

                                             @Override
                                             public void onRequestPermissionFailure(List<String> permissions) {
                                                 mRootView.showMessage("请开启相机权限，读取内存权限");
                                             }

                                             @Override
                                             public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                                                 mRootView.showMessage("请开启相机权限，读取内存权限");
                                             }
                                         }, mRootView.getRxPermissions(), mErrorHandler, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);

    }


    public void getScoreDetail(String taskId, String scoreId) {
        mModel.getScoreDetail(taskId, scoreId)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<ScoreDetail>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<ScoreDetail> scoreItemBaseJson) {
                        if (scoreItemBaseJson.isSuccess()) {
                            mRootView.updateScoreDetail(scoreItemBaseJson.getData());
                        }
                    }
                });
    }

    public void getTemplateDetail(String templateId) {

        mModel.getTemplateDetail(templateId)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<List<TemplateDetail>>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<List<TemplateDetail>> listBaseJson) {
                        if (listBaseJson.isSuccess() && listBaseJson.getData() != null && listBaseJson.getData().size() > 0) {
                            mModel.saveTemplate(listBaseJson.getData());
                        }
                    }
                });
        //createTemplate();
    }

    public void getTemplateDetailFromClickDeduct(String templateId) {

        mModel.getTemplateDetail(templateId)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<List<TemplateDetail>>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<List<TemplateDetail>> listBaseJson) {
                        if (listBaseJson.isSuccess() && listBaseJson.getData() != null && listBaseJson.getData().size() > 0) {
                            mModel.saveTemplate(listBaseJson.getData());
                            mRootView.showDeduct();
                        }
                    }
                });
        //createTemplate();
    }

    private void createTemplate() {

        List<TemplateDetail> details = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            TemplateDetail detail = new TemplateDetail();
            detail.itemId = i + "";
            detail.itemName = "考核项目" + i;
            detail.standardList = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                Standard standard = new Standard();
                standard.standardId = j + "";
                standard.standardName = "考核标准" + j;
                standard.percent = j + "";
                standard.scoreLimit = j * 1.1 + "";
                detail.standardList.add(standard);
                standard.deductList = new ArrayList<>();
                for (int k = 0; k < j; k++) {
                    Deduct deduct = new Deduct();
                    deduct.deductId = k + "";
                    deduct.deductName = "扣分标准" + k;
                    deduct.score = k * 0.2 + "";
                    standard.deductList.add(deduct);
                }
            }
            details.add(detail);
        }
        mModel.saveTemplate(details);

    }

    //新增
    public void submitScore(String taskId, String itemId, String standardId, String deductId, String deductNum, List<LocalImage> data, String address,String location) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        mModel.submitScore(taskId,itemId,standardId,deductId,deductNum,data,address,location)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Object> objectBaseJson) {
                        if(objectBaseJson.isSuccess()){
                            mRootView.showMessage(mApplication.getString(R.string.save_success));
                            mRootView.killMyself();
                          }else{
                            mRootView.showMessage(objectBaseJson.getMsg());
                        }
                    }
                });
    }

    //修改
    public void updateScoreDetail(String taskId, String scoreId, String itemId, String standardId, String deductId, String deductNum, List<LocalImage> data,String address, String location) {
        if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(scoreId)) {
            return;
        }
        mModel.updateScoreDetail(taskId,scoreId,itemId,standardId,deductId,deductNum,data,address,location)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Object> objectBaseJson) {
                        if(objectBaseJson.isSuccess()){
                            mRootView.showMessage(mApplication.getString(R.string.save_success));
                            mRootView.killMyself();
                        }else{
                            mRootView.showMessage(objectBaseJson.getMsg());
                        }
                    }
                });


    }
}
