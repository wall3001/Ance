package wall.field.investigation.di.module;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jess.arms.di.scope.ActivityScope;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

import wall.field.investigation.mvp.contract.ScoreListContract;
import wall.field.investigation.mvp.model.ScoreListModel;
import wall.field.investigation.mvp.model.entity.ScoreItem;
import wall.field.investigation.mvp.model.entity.Task;
import wall.field.investigation.mvp.ui.adapter.ScoreListAdapter;
import wall.field.investigation.mvp.ui.adapter.TaskListAdapter;


@Module
public class ScoreListModule {
    private ScoreListContract.View view;

    /**
     * 构建ScoreListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ScoreListModule(ScoreListContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ScoreListContract.View provideScoreListView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ScoreListContract.Model provideScoreListModel(ScoreListModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getActivity());
    }

    @ActivityScope
    @Provides
    ScoreListAdapter provideScoreListAdapter(List<ScoreItem> list) {
        return new ScoreListAdapter(list);
    }

    @ActivityScope
    @Provides
    List<ScoreItem> provideScoreList() {
        return new ArrayList<>();
    }

}