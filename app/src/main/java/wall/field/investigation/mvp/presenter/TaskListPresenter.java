package wall.field.investigation.mvp.presenter;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.PermissionUtil;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import wall.field.investigation.app.utils.RxUtils;
import wall.field.investigation.mvp.contract.TaskListContract;
import wall.field.investigation.mvp.model.entity.BaseJson;
import wall.field.investigation.mvp.model.entity.Task;
import wall.field.investigation.mvp.ui.adapter.TaskListAdapter;


@ActivityScope
public class TaskListPresenter extends BasePresenter<TaskListContract.Model, TaskListContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    @Inject
    List<Task> taskList;
    @Inject
    TaskListAdapter mAdapter;
    private final int pageNum = 40;
    private int page = 1;
    /**
     * 0 未开始 1 进行中 2 已完成 3 全部 默认1
     */
    private int state = 1;


    /**
     * 搜索名
     */
    private String taskName = null;

    /**
     * 0 由近及远 1 由远及近 默认 0
     */
    private int distance = 0;

    @Inject
    public TaskListPresenter(TaskListContract.Model model, TaskListContract.View rootView) {
        super(model, rootView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        requestTaskList(true);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(() -> {
            requestTaskList(false);
        }, mRootView.getRecylerView());
    }


    private void createData() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Task task = new Task();
            task.taskId = i + "";
            task.complete = i / 4 + "";
            task.timeStamp = System.currentTimeMillis() + "";
            task.address = "合肥市长江西路130号";
            task.name = "周芷若";
            task.scoreNum = i * 2 + "";
            task.totalScore = i * 50 + 1 + "";
            tasks.add(task);
        }
        mAdapter.setNewData(tasks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
        this.mAdapter = null;
        this.taskList = null;
    }


    public void copyTaskAndMain(String taskId, String copyRemark, String copyLocation) {

        mModel.copyTaskAndMain(taskId, copyRemark, copyLocation)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<BaseJson<Object>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<Object> objectBaseJson) {

                        if (objectBaseJson.isSuccess()) {
                            mRootView.disMissCopyCpw();
                            requestTaskList(true);
                        }
                        mRootView.showMessage(objectBaseJson.getMsg());
                    }
                });
    }


    public void setParams(String taskName, int state, int distance) {
        this.taskName = taskName;
        this.state = state;
        this.distance = distance;
    }


    public void requestTaskList(boolean pullToRefresh) {

        //  createData();
        //请求外部存储权限用于适配android6.0的权限管理机制
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                //request permission success, do something.
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                mRootView.showMessage("Request permissions failure");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                mRootView.showMessage("Need to go to the settings");
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        if (pullToRefresh) {
            page = 1;
            mAdapter.setEnableLoadMore(false);
        }
        mModel.requestTaskList(pullToRefresh, pageNum, page, state, distance, taskName)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(3, 2))
                //遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe(disposable -> {
                    if (pullToRefresh) {
                        mRootView.showLoading();//显示下拉刷新的进度条
                    } else {
                        mRootView.startLoadMore();//显示上拉加载更多的进度条
                    }

                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    if (pullToRefresh) {
                        mRootView.hideLoading();//隐藏下拉刷新的进度条
                    } else {
                        mRootView.endLoadMore();//隐藏上拉加载更多的进度条
                    }
                })
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                //使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<BaseJson<List<Task>>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseJson<List<Task>> taskList) {
                        if (taskList.isSuccess()) {
                            if (pullToRefresh) {
                                mAdapter.setNewData(taskList.getData());
                            } else {
                                mAdapter.addData(taskList.getData());
                            }
                            if (taskList.getData().size() < pageNum) {
                                //加载结束
                                mAdapter.setEnableLoadMore(false);
                                mAdapter.loadMoreEnd();
                            }
                            page++;
                        }
                    }
                });
    }
}
