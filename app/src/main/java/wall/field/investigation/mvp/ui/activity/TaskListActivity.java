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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.DeviceUtils;
import com.jess.arms.widget.CustomPopupWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import wall.field.investigation.R;
import wall.field.investigation.app.EventBusTags;
import wall.field.investigation.app.utils.StorageUtils;
import wall.field.investigation.app.utils.UserUtils;
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

    /**
     * 复制任务的内容提示
     */
    private TextView tvContent;

    private EditText editRemark;

    private EditText editAddress;

    private boolean isLoadingMore;

    private CustomPopupWindow copyCpw;

    /**
     * 0 未开始 1 进行中 2 已完成 3 全部 默认1
     */
    private int state = 1;


    /**
     * 搜索名
     */
    private String searchName = null;

    /**
     * 0 由近及远 1 由远及近 默认 0
     */
    private int distance = 0;


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
        tvRightTop.setText(R.string.filter);
        imgLeftTop.setImageResource(R.drawable.ic_user_center);
        popupWindow = CustomPopupWindow
                .builder()
                .parentView(tvTitle)
                .isWrap(false)
                .isHeightWrap(false)
                .contentView(LayoutInflater.from(getBaseContext()).inflate(R.layout.ppw_filter, null))
                .customListener((contentView, customPopupWindow) -> {
                    View all = contentView.findViewById(R.id.tv_all);
                    View unstart = contentView.findViewById(R.id.tv_unStart);
                    View doing = contentView.findViewById(R.id.tv_doing);
                    View complete = contentView.findViewById(R.id.tv_complete);
                    EditText editName = contentView.findViewById(R.id.edit_name);
                    View tvNear = contentView.findViewById(R.id.tv_near);
                    View tvFar = contentView.findViewById(R.id.tv_far);
                    View tvReset = contentView.findViewById(R.id.tv_reset);
                    View tvSure = contentView.findViewById(R.id.tv_sure);
                    View tvCancel = contentView.findViewById(R.id.tv_cancel);
                    all.setOnClickListener(v -> {
                        state = 3;
                        all.setSelected(true);
                        unstart.setSelected(false);
                        doing.setSelected(false);
                        complete.setSelected(false);
                    });
                    unstart.setOnClickListener(v -> {
                        state = 0;
                        all.setSelected(false);
                        unstart.setSelected(true);
                        doing.setSelected(false);
                        complete.setSelected(false);
                    });
                    doing.setOnClickListener(v -> {
                        state = 1;
                        all.setSelected(false);
                        unstart.setSelected(false);
                        doing.setSelected(true);
                        complete.setSelected(false);
                    });
                    complete.setOnClickListener(v -> {
                        state = 2;
                        all.setSelected(false);
                        unstart.setSelected(false);
                        doing.setSelected(false);
                        complete.setSelected(true);
                    });
                    tvCancel.setOnClickListener(v -> popupWindow.dismiss());
                    tvReset.setOnClickListener(v -> {
                        editName.setText(null);
                        doing.performClick();
                        tvNear.performClick();
                    });

                    tvSure.setOnClickListener(v -> {
                        if (mPresenter != null) {
                            searchName = editName.getText().toString();
                            mPresenter.setParams(searchName, state, distance);
                            mPresenter.requestTaskList(true);
                        }
                        popupWindow.dismiss();
                    });
                    tvNear.setOnClickListener(v -> {
                        distance = 0;
                        tvNear.setSelected(true);
                        tvFar.setSelected(false);
                    });

                    tvFar.setOnClickListener(v -> {
                        distance = 1;
                        tvNear.setSelected(false);
                        tvFar.setSelected(true);
                    });
                    tvReset.performClick();
                })
                .build();

        copyCpw = CustomPopupWindow
                .builder()
                .parentView(tvTitle)
                .isWrap(false)
                .isHeightWrap(false)
                .contentView(LayoutInflater.from(getBaseContext()).inflate(R.layout.ppw_copy_task, null))
                .customListener((contentView, customPopupWindow) -> {
                    editRemark = contentView.findViewById(R.id.edit_remark);
                    tvContent = contentView.findViewById(R.id.tv_content);
                    editAddress = contentView.findViewById(R.id.edit_address);
                    TextView tvCancel = contentView.findViewById(R.id.tv_cancel);
                    TextView tvSure = contentView.findViewById(R.id.tv_sure);
                    tvCancel.setOnClickListener(v -> copyCpw.dismiss());
                    tvSure.setOnClickListener(v -> {
                        if (editRemark.isShown()) {
                            if (editRemark.getText().toString().trim().length() > 0 && editAddress.getText().toString().trim().length() > 0) {
                                disMissCopyCpw();
                                if (mPresenter != null) {
                                    DeviceUtils.hideSoftKeyboard(this, editAddress);
                                    mPresenter.copyTaskAndMain(String.valueOf(tvContent.getHint()), editRemark.getText().toString(), editAddress.getText().toString());
                                }
                            } else {
                                ArmsUtils.snackbarText(getString(R.string.add_address_remark));
                            }
                        } else {
                            editRemark.setVisibility(View.VISIBLE);
                            editAddress.setVisibility(View.VISIBLE);
                        }
                    });
                    customPopupWindow.setOnDismissListener(() -> {
                        if (editRemark != null) {
                            editRemark.setText(null);
                            editRemark.setVisibility(View.GONE);
                        }
                        if (editAddress != null) {
                            editAddress.setText(null);
                            editAddress.setVisibility(View.GONE);
                        }
                        if (tvContent != null) {
                            tvContent.setText(null);
                            tvContent.setHint(null);
                        }

                    });
                }).build();
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
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (UserUtils.getCurrentRole(this).equals("0") || UserUtils.getCurrentRole(this).equals("2")) {
                Task task = (Task) adapter.getItem(position);
                if (task != null) {
                    tvContent.setText(task.address);
                    tvContent.setHint(task.taskId);
                    copyCpw.show();
                }
            }
            return true;
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
                popupWindow.showAsDropDown(tvTitle);
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

    @Override
    public void disMissCopyCpw() {
        if (copyCpw != null) {
            copyCpw.dismiss();
        }
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
