package wall.field.investigation.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.widget.CustomPopupWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import wall.field.investigation.R;
import wall.field.investigation.app.EventBusTags;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.di.component.DaggerTaskListComponent;
import wall.field.investigation.di.module.TaskListModule;
import wall.field.investigation.mvp.contract.TaskListContract;
import wall.field.investigation.mvp.model.entity.Task;
import wall.field.investigation.mvp.model.entity.User;
import wall.field.investigation.mvp.presenter.TaskListPresenter;
import wall.field.investigation.mvp.ui.adapter.TaskListAdapter;
import wall.field.investigation.mvp.ui.view.ShowDelete;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * 任务列表页
 */
public class TaskListActivity extends BaseActivity<TaskListPresenter> implements TaskListContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.img_left_top)
    ImageView imgLeftTop;
    @BindView(R.id.tv_right_top)
    TextView tvRightTop;
    @Inject
    RxPermissions mRxPermissions;
    @Inject
    TaskListAdapter mAdapter;
    @Inject
    RecyclerView.LayoutManager linearLayoutManager;

    CustomPopupWindow popupWindow;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private boolean isLoadingMore;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerTaskListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .taskListModule(new TaskListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_task_list;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
     //   tvRightTop.setText(R.string.filter);
        imgLeftTop.setImageResource(R.drawable.ic_user_center);
        popupWindow = CustomPopupWindow
                .builder()
                .parentView(tvTitle)
                .isWrap(false)
                .isHeightWrap(true)
                .contentView(LayoutInflater.from(getBaseContext()).inflate(R.layout.ppw_filter, null))
                .customListener((contentView, customPopupWindow) -> {
                    View all = contentView.findViewById(R.id.tv_all);
                    View unstart = contentView.findViewById(R.id.tv_unStart);
                    View doing = contentView.findViewById(R.id.tv_doing);
                    View complete = contentView.findViewById(R.id.tv_complete);
                    all.setOnClickListener(v -> {
                        if (mPresenter != null) {
                            mPresenter.setState(3);
                            mPresenter.requestTaskList(true);
                        }
                        popupWindow.dismiss();
                    });
                    unstart.setOnClickListener(v -> {
                        if (mPresenter != null) {
                            mPresenter.setState(0);
                            mPresenter.requestTaskList(true);
                        }
                        popupWindow.dismiss();
                    });
                    doing.setOnClickListener(v -> {
                        if (mPresenter != null) {
                            mPresenter.setState(1);
                            mPresenter.requestTaskList(true);
                        }
                        popupWindow.dismiss();
                    });
                    complete.setOnClickListener(v -> {
                        if (mPresenter != null) {
                            mPresenter.setState(2);
                            mPresenter.requestTaskList(true);
                        }
                        popupWindow.dismiss();
                    });
                })
                .build();
        initRecyclerView();
        recyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerView);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            //跳转到详情列表页
            Task task = (Task) adapter.getItem(position);
            if (task != null) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ScoreListActivity.class);
                intent.putExtra(EventBusTags.TASKID, task.taskId);
                intent.putExtra(EventBusTags.TEMPLATEID, task.templateId);
                ArmsUtils.startActivity(getActivity(), intent);
            }
        });

    }


    private void initRecyclerView() {
        refresh.setOnRefreshListener(this);
        ArmsUtils.configRecyclerView(recyclerView, linearLayoutManager);
    }

    @Override
    public void showLoading() {
        refresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        refresh.setRefreshing(false);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
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

    @Override
    public void onClickBack(View v) {
        ArmsUtils.startActivity(UserCenterActivity.class);
    }

    @OnClick({R.id.tv_right_top, R.id.tv_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right_top:
                //    popupWindow.showAsDropDown(tvTitle);
                break;
            case R.id.tv_title:
                popupWindow.showDown();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (mPresenter != null) {
            mPresenter.requestTaskList(true);
        }
    }

    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    @Override
    public void endLoadMore() {
        isLoadingMore = false;
        mAdapter.loadMoreComplete();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RecyclerView getRecylerView() {
        return recyclerView;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - backPressedTime) > 2000L) {
            ArmsUtils.snackbarText(getString(R.string.exitapp));
            backPressedTime = System.currentTimeMillis();
        } else {
            new StorageUtils<User>(User.class, this).clear();
            super.onBackPressed();
        }
    }
}
