package wall.field.investigation.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import wall.field.investigation.R;
import wall.field.investigation.app.EventBusTags;
import wall.field.investigation.di.component.DaggerScoreListComponent;
import wall.field.investigation.di.module.ScoreListModule;
import wall.field.investigation.mvp.contract.ScoreListContract;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.TaskBaseInfo;
import wall.field.investigation.mvp.presenter.ScoreListPresenter;
import wall.field.investigation.mvp.ui.adapter.ScoreListAdapter;
import wall.field.investigation.mvp.ui.view.ShowDelete;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class ScoreListActivity extends BaseActivity<ScoreListPresenter> implements ScoreListContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mSwipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_score)
    TextView tvScore;

    @Inject
    ScoreListAdapter adapter;
    @Inject
    RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.tv_right_top)
    TextView tvRightTop;

    private String taskID;
    private String templateID;
    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerScoreListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .scoreListModule(new ScoreListModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_score_list;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        tvRightTop.setText(R.string.add_score);
        tvRightTop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvRightTop.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        taskID = getIntent().getStringExtra(EventBusTags.TASKID);
        templateID = getIntent().getStringExtra(EventBusTags.TEMPLATEID);
        initRecyclerView();
        if (mPresenter != null && !TextUtils.isEmpty(taskID)) {
            mPresenter.getTaskBaseInfo(taskID);
            mPresenter.requestScoreList(true, taskID);
            mPresenter.setLoadMore(taskID);
        }
    }

    private void initRecyclerView() {
        mSwipeRefresh.setOnRefreshListener(this);
        ArmsUtils.configRecyclerView(mRecyclerView, layoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(mRecyclerView);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            ScoreItem item = (ScoreItem) adapter.getItem(position);
            toScoreItemDetailActivity(item);
        });
        adapter.setOnItemLongClickListener((adapter, view, position) ->
                {
                    ScoreItem item = (ScoreItem) adapter.getItem(position);
                    if (item != null && !TextUtils.isEmpty(item.scoreId)) {
                        deleteScoreItem(taskID, item.scoreId);
                    }
                    return true;
                }
        );
    }

    private void deleteScoreItem(String taskID, String scoreId) {

        ShowDelete.getInstance().ShowDelete(this, mRecyclerView, () -> {
            if (mPresenter != null) {
                mPresenter.deleteScoreItem(taskID, scoreId);
            }
        });

    }

    private void toScoreItemDetailActivity(ScoreItem item) {
        if (item != null) {
            Intent intent = new Intent();
            intent.setClass(this, ScoreItemDetailActivity.class);
            intent.putExtra(EventBusTags.TASKID, taskID);
            intent.putExtra(EventBusTags.ISADD, false);
            intent.putExtra(EventBusTags.SCOREID, item.scoreId);
            intent.putExtra(EventBusTags.TEMPLATEID, templateID);
            ArmsUtils.startActivity(this, intent);
        }
    }

    @Override
    public void showLoading() {
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefresh.setRefreshing(false);
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

    @Override
    public void updateBaseInfo(TaskBaseInfo data) {
        tvTitle.setText(data.address);
        tvAddress.setText(data.address);
        tvScore.setText(data.totalScore);
        tvState.setText(getState(data.complete));
    }

    @Override
    public void startLoadMore() {

    }

    @Override
    public void endLoadMore() {
        adapter.loadMoreComplete();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private CharSequence getState(String complete) {

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

    @Override
    public void onRefresh() {
        if (!TextUtils.isEmpty(taskID) && mPresenter != null) {
            mPresenter.requestScoreList(true, taskID);
        }
    }

    @OnClick(R.id.tv_right_top)
    public void onClick() {
        //新增评分项
        Intent intent = new Intent();
        intent.putExtra(EventBusTags.TASKID, taskID);
        intent.putExtra(EventBusTags.ISADD, true);
        intent.putExtra(EventBusTags.TEMPLATEID, templateID);
        intent.setClass(this, ScoreItemDetailActivity.class);
        ArmsUtils.startActivity(intent);


    }
}
