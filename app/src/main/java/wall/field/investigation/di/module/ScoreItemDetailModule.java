package wall.field.investigation.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

import wall.field.investigation.mvp.contract.ScoreItemDetailContract;
import wall.field.investigation.mvp.model.ScoreItemDetailModel;
import wall.field.investigation.mvp.model.entity.Deduct;
import wall.field.investigation.mvp.model.entity.Standard;
import wall.field.investigation.mvp.model.entity.TemplateDetail;
import wall.field.investigation.mvp.ui.adapter.ImageAdapter;


@Module
public class ScoreItemDetailModule {


    private ScoreItemDetailContract.View view;

    /**
     * 构建ScoreItemDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ScoreItemDetailModule(ScoreItemDetailContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ScoreItemDetailContract.View provideScoreItemDetailView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ScoreItemDetailContract.Model provideScoreItemDetailModel(ScoreItemDetailModel model) {
        return model;
    }

    @ActivityScope
    @Provides
    List<TemplateDetail> provideTemplateDetailList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    List<Standard> provideStandardList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    List<Deduct> provideDeductList() {
        return new ArrayList<>();
    }

    @ActivityScope
    @Provides
    RxPermissions provideRxPermissions() {
        return new RxPermissions(view.getActivity());
    }

    @ActivityScope
    @Provides
    ImageAdapter provideImageAdapter() {
        return new ImageAdapter(null);
    }
}