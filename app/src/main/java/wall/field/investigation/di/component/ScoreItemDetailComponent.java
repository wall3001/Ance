package wall.field.investigation.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import wall.field.investigation.di.module.ScoreItemDetailModule;

import com.jess.arms.di.scope.ActivityScope;

import wall.field.investigation.mvp.ui.activity.ScoreItemDetailActivity;

@ActivityScope
@Component(modules = ScoreItemDetailModule.class, dependencies = AppComponent.class)
public interface ScoreItemDetailComponent {
    void inject(ScoreItemDetailActivity activity);
}