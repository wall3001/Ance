package wall.field.investigation.mvp.presenter;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.util.ConnectConsumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import wall.field.investigation.app.EventBusTags;
import wall.field.investigation.app.utils.RxUtils;
import wall.field.investigation.mvp.contract.ScoreListContract;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;
import wall.field.investigation.mvp.ui.adapter.ScoreListAdapter;


@ActivityScope
public class ScoreListPresenter extends BasePresenter<ScoreListContract.Model, ScoreListContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    ScoreListAdapter adapter;

    private int page = 1;
    private final int pageNum = 40;

    @Inject
    public ScoreListPresenter(ScoreListContract.Model model, ScoreListContract.View rootView) {
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onCreate() {

    }

    public void requestScoreList(boolean refresh, String taskId) {
        if (refresh) {
            page = 1;
        }
        mModel.getScoreList(taskId, page, pageNum)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                .doOnSubscribe(disposable -> {
                    if (refresh) {
                        mRootView.showLoading();
                    } else {
                        mRootView.startLoadMore();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (refresh) {
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                    } else {
                        mRootView.endLoadMore();//隐藏上拉加载更多的进度条
                    }
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<List<ScoreItem>>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<List<ScoreItem>> listBaseJson) {
                        if (listBaseJson.isSuccess()) {
                            if (refresh) {
                                adapter.setNewData(listBaseJson.getData());
                            } else {
                                adapter.addData(listBaseJson.getData());
                            }
                            if (listBaseJson.getData().size() < pageNum) {
                                if(adapter.getData().size()<pageNum){
                                    adapter.loadMoreEnd(true);
                                }else{
                                    adapter.loadMoreEnd();
                                }
                            } else {
                                page++;
                            }
                        }
                    }
                });
       // createData();

    }

    private void createData() {
        List<ScoreItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ScoreItem item = new ScoreItem();
            item.scoreId = i + "";
            item.scoreState = i % 3 + "";
            item.scoreSummary = "总结概要来看看dddd" + i;
            item.scoreName = "评分记录项" + i;
            item.scoreValue = i * 3 + 1 + "";
            list.add(item);
        }
        adapter.addData(list);
    }

    public void getTaskBaseInfo(String taskID) {

        mModel.getTaskBaseInfo(taskID).compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<TaskBaseInfo>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<TaskBaseInfo> taskBaseInfoBaseJson) {
                        if (taskBaseInfoBaseJson.isSuccess()) {
                            mRootView.updateBaseInfo(taskBaseInfoBaseJson.getData());
                        }
                    }
                });

    }

    public void setLoadMore(String taskId) {
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(() -> {
            requestScoreList(false, taskId);
        }, mRootView.getRecyclerView());

    }

    public void deleteScoreItem(String taskId, String scoreId) {
        mModel.deleteScoreItem(taskId, scoreId)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Object> objectBaseJson) {
                        if (objectBaseJson.isSuccess()) {
                            mRootView.showMessage("删除成功");
                            requestScoreList(true, taskId);
                            getTaskBaseInfo(taskId);
                        } else {
                            mRootView.showMessage("删除失败");
                        }
                    }
                });
    }
}
