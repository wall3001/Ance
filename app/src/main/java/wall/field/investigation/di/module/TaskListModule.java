package wall.field.investigation.di.module;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.widget.CustomPopupWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

import wall.field.investigation.mvp.contract.TaskListContract;
import wall.field.investigation.mvp.model.TaskListModel;
import wall.field.investigation.mvp.model.entity.Task;
import wall.field.investigation.mvp.ui.adapter.TaskListAdapter;


@Module
public class TaskListModule {
    private TaskListContract.View view;

    /**
     * 构建TaskListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public TaskListModule(TaskListContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    TaskListContract.View provideTaskListView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    TaskListContract.Model provideTaskListModel(TaskListModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    RxPermissions provideRxPermissions() {
        return new RxPermissions(view.getActivity());
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getActivity());
    }

    @ActivityScope
    @Provides
    TaskListAdapter provideTaskListAdapter(List<Task> list) {
        return new TaskListAdapter(list);
    }

    @ActivityScope
    @Provides
    List<Task> provideTaskList() {
        return new ArrayList<>();
    }


}